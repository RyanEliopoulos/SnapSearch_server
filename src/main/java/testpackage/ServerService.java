package testpackage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.locks.ReentrantLock;
import java.net.ServerSocket;


public class ServerService extends Thread {

    Socket activeSocket;
    Socket datasocket = null;   // null when no data connection is up
    ReentrantLock mutex;

    public ServerService(Socket connectedSocket, ReentrantLock mutex) {
        this.activeSocket = connectedSocket;
        this.mutex = mutex;
        System.out.println("That's right wer are here without an import statement!");
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
                System.out.println("Reading from socket");

                // Reading from socket
                int nextint = instream.read();
                if (nextint == -1) {
                    System.out.println("The connection with the client has been gracefully closed.");
                    break;
                }

                // Checking for valid commands
                char command = (char) nextint;
                byte[] response;
                switch(command) {
                    case 'Q': // Close connection
                        System.out.println("The client wishes to close connection");
                        stayalive = false;
                        response = "A".getBytes(StandardCharsets.UTF_8);
                        ostream.write(response);
                        break;
                    case 'D': // Build data connection
                        if (this.datasocket != null) {
                            response = "EExistingConnection".getBytes(StandardCharsets.UTF_8);
                        }
                        else if (this.dataConnection() == -1) {
                            // Encountered error building the data connection
                            response = "ECould_Not_Open_Socket".getBytes(StandardCharsets.UTF_8);
                        }
                        else {
                            Integer port = this.datasocket.getLocalPort();
                            response = ("A" + port).getBytes(StandardCharsets.UTF_8);
                        }
                        ostream.write(response);
                        break;
                }
                System.out.println("Printing the incoming byte");
                System.out.println((char) nextint);
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
            this.datasocket = data_ss.accept();  // Waiting for client
        }
        catch (IOException e) {
            e.printStackTrace();
            return -1;
        }

        return 1;
    }


}