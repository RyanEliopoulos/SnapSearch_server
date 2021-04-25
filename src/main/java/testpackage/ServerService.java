package testpackage;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.net.ServerSocket;


public class ServerService extends Thread {

    Socket activeSocket;  // This is the command socket
    ServerSocket data_ss = null;  // null once the connection is consumed/used once
    Socket datasocket = null;   // null when no data connection is up
    DBInterface dbiface;
    ReentrantLock mutex;

    public ServerService(Socket connectedSocket, DBInterface dbiface, ReentrantLock mutex) {
        this.activeSocket = connectedSocket;
        this.dbiface = dbiface;
        this.mutex = mutex;
    }

    public void run() {
        this.manageCommands();
    }

    private void manageCommands() {
        /*

        Manages the command connection with the client.
        Commands are limited to a single character.

         */

        try {
            // Preparing read/write facilities for the command connection
            InputStream instream = this.activeSocket.getInputStream();  // Read from this
            OutputStream ostream = this.activeSocket.getOutputStream(); // Write to this

            Boolean stayalive = true;  // Updated once connection closes
            while (stayalive) {
                System.out.println("Reading from command socket");

                // Reading next commmand
                int cmd = this.readCommand(instream);
                if (cmd == -1) {
                    // Problem with data connection
                    // Terminating thread
                    break;
                }
                // Checking for valid commands
                char command = (char) cmd;
                byte[] response;
                switch(command) {
                    case 'Q': // Close connection
                        System.out.println("The client wishes to close connection");
                        stayalive = false;
                        response = "A\n".getBytes(StandardCharsets.UTF_8);
                        ostream.write(response);
                        break;
                    case 'D': // Build data connection
                        System.out.println("Client wants a new data connection");
                        if (this.datasocket != null) {
                            response = "EExistingConnection\n".getBytes(StandardCharsets.UTF_8);
                        }
                        else if (this.dataConnection() == -1) {
                            // Encountered error building the data connection
                            response = "ECould_Not_Open_Socket\n".getBytes(StandardCharsets.UTF_8);
                        }
                        else {
                            Integer port = this.data_ss.getLocalPort();
                            response = ("A" + port + "\n").getBytes(StandardCharsets.UTF_8);
                            System.out.println("For the record, port is:" + port);
                        }
                        ostream.write(response);
                        // Waiting for client to connect to the data connection
                        this.datasocket = this.data_ss.accept();
                        break;
                    case 'P':  // Transfer photos from server to client
                        System.out.println("Sending photos to client");
                        if (this.datasocket == null) {
                            response = "ENoDataConnection\n".getBytes(StandardCharsets.UTF_8);
                            ostream.write(response);
                            break;
                        }
                        else {
                            response = "A\n".getBytes(StandardCharsets.UTF_8);
                        }


                        try {
                            ostream.write(response);
                        }
                        catch (IOException e) {
                            System.out.println("Failed to write response");
                            e.printStackTrace();;
                        }

                        OutputStream dout = this.datasocket.getOutputStream();
                        this.sendPhotos(dout);
                        System.out.println("Pictures sent");

                        // Now need to clean up the data connection
                        this.datasocket.close();
                        this.datasocket = null;
                        this.data_ss.close();
                        this.data_ss = null;
                        break;
                    case 'U':
                        System.out.println("Client has requested a picture upload");
                        if (this.datasocket == null) {
                            response = "ENoDataConnection\n".getBytes(StandardCharsets.UTF_8);
                            ostream.write(response);
                        }
                        else {
                            response = "A\n".getBytes(StandardCharsets.UTF_8);
                            ostream.write(response);

                            // Reading from the socket
                            InputStream din = this.datasocket.getInputStream();
                            this.insertPhoto(din);

                            // Now need to clean up the data connection
                            this.datasocket.close();
                            this.datasocket = null;
                            this.data_ss.close();
                            this.data_ss = null;
                        }
                        break;
                }
            }
            this.activeSocket.close();
            if (this.datasocket != null) {
                this.datasocket.close();
            }
            System.out.println("Connection closed");
        }
        catch (Exception IOException) {
            System.out.println("Connection with the client shutdown ungracefully..");
        }
    }

    private int dataConnection() {
        /*
            Opening data connection
         */
        try {
            // Establishing socket
            ServerSocket data_ss = new ServerSocket(0);
            int data_port = data_ss.getLocalPort();
            //InetSocketAddress sckaddr = new InetSocketAddress("localhost", data_port);
            this.data_ss = data_ss;
        }
        catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return 1;
    }


    private int sendPhotos(OutputStream ostream) {
        /*
            Extracts the photo info for userid 1
            and sends a JSON-ified string to the client
         */

        HashMap<String, Photo> hm = this.dbiface.getPhotos(1);
        HashMap<String, String> jsonmap = new HashMap<String, String>();

        // Converting Photo objects to json string
        Gson gson = new Gson();
        for (String key : hm.keySet()) {
            Photo tmpPhoto = hm.get(key);
            String photoString = gson.toJson(tmpPhoto);
            jsonmap.put(key, photoString);
        }

        // Now to jsonify the jsonmap into a single string
        Type JsonPhotoMap = new TypeToken<HashMap<String, String>>() {}.getType();
        String finalString = gson.toJson(jsonmap, JsonPhotoMap);

        // Sending it down the pipe to the client
        try {
            ostream.write(finalString.getBytes(StandardCharsets.UTF_8));
            return 0;
        }
        catch (IOException e) {
            System.out.println("Encountered error sending the photo bytes to the client");
            e.printStackTrace();
            return -1;
        }
    }

    private int insertPhoto(InputStream instream) {
        // Read data from the connection
        CommandReader cr = new CommandReader();
        String jsonSingleton =  cr.readCommand(instream);

        Gson gson = new Gson();
        Photo phto = gson.fromJson(jsonSingleton, Photo.class);
        Base64.Decoder decoder = Base64.getDecoder();

        // Inserting into database
        // Acquiring mutex first
        this.mutex.lock();
        int ret = this.dbiface.insertPhoto(phto.userid,
                                    decoder.decode(phto.encodedFileblob),
                                    phto.longitude,
                                    phto.latitude);

        this.mutex.unlock();
        if (ret == -1) {
            System.out.println("encountered error during insertPhoto call");
            return -1;
        }
        return 0;
    }

    private int readCommand(InputStream instream) {
        // Returns the next command from the command connection
        // BufferedReader (InputStreamReader(InputStream)

        InputStreamReader inreader = new InputStreamReader(instream, StandardCharsets.UTF_8);
        try {
            return inreader.read();
        }
        catch (IOException e) {
            System.out.println("Error reading from data connection");
            e.printStackTrace();
            return -1;
        }
    }
}