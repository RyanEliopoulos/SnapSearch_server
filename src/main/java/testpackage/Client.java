package testpackage;


import java.io.*;
import java.lang.reflect.Type;
import java.net.Socket;
import java.net.InetSocketAddress;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import testpackage.CommandReader;

public class Client {

    public static void main(String[] args) {
        System.out.print("Hello broski\n");

        // Preparing connection to server
        InetSocketAddress targetAddress = new InetSocketAddress("localhost", 6970);
        Socket clientsocket = new Socket();

        try {
            clientsocket.connect(targetAddress);
            System.out.print("Connected to the server\n");

            OutputStream outstream = clientsocket.getOutputStream();  // Write to
            InputStream instream = clientsocket.getInputStream();   // read from
            Scanner scanner = new Scanner(System.in);


            while (true) {
                String inputstring = scanner.next();
                if (inputstring.charAt(0) == 'Q') {
                    System.out.println("User shutdown request");
                    outstream.write((byte) 'Q');
                    break;
                }
                else if (inputstring.charAt(0) == 'D') {
                    System.out.println("Requesting data connection");
                    outstream.write((byte) 'D');
                    CommandReader cr = new CommandReader();
                    cr.readCommand(instream);

                }
                else if (inputstring.charAt(0) == 'P') {
                    System.out.println("Requesting photo data");
                    outstream.write((byte) 'D');
                    CommandReader cr = new CommandReader();
                    String response = cr.readCommand(instream); // This should be the 'A'
                    System.out.println("Data request response:" + response);


                    if (response.charAt(0) == 'E') {
                        System.out.println("Error encountered:");
                        System.out.println(response);
                    }
                    else {  // Received port of data connnection
                        // Establishing connection
                        int dataPort = Integer.parseInt(response.substring(1, response.length()));
                        Socket dataSocket = new Socket("localhost", dataPort);
                        InputStream dinstream = dataSocket.getInputStream();

                        // Sending request for photos
                        outstream.write((byte) 'P');
                        System.out.println("Photo response" + cr.readCommand(instream));

                        // Reading contents
                        CommandReader dr = new CommandReader();
                        String jsonString = dr.readCommand(dinstream);

                        // Extracting the photo data
                        Gson gson = new Gson();
                        Base64.Decoder dec = Base64.getDecoder();
                        Type JsonPhotoMap = new TypeToken<HashMap<String, String>>() {}.getType();
                        // Turning the singleton string into the first layer
                        HashMap<String, String> jsonMap = gson.fromJson(jsonString, JsonPhotoMap);
                        // Turning the nested JSON objects into Java objects
                        HashMap<String, Photo> photomap = new HashMap<String, Photo>();
                        for (String key : jsonMap.keySet()) {
                            String photoString = jsonMap.get(key);
                            Photo phto = gson.fromJson(photoString, Photo.class);
                            byte[] filedata = dec.decode(phto.encodedFileblob);
                            try {
                                FileOutputStream fos = new FileOutputStream("C:/Users/Ryan/" + key + "client.png");
                                fos.write(filedata);
                                fos.close();
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                else {
                    for (char chr : inputstring.toCharArray()) {
                        System.out.println("Typing \"" + chr + "\" to the socket");
                        outstream.write((byte) chr);
                    }
                }

            }

            clientsocket.close();

        }
        catch (Exception e) {
            System.out.print("heyo couldnt' connect for some reason\n");
            e.printStackTrace();
        }
    }


    private int readCommand(InputStream instream) {
        // Returns the next command from the command connection
        // BufferedReader (InputStreamReader(InputStream)

        InputStreamReader inreader = new InputStreamReader(instream, StandardCharsets.UTF_8);
        try {
            int ret = inreader.read();
            System.out.println("Server response: " + (char) ret);
            return ret;
        }
        catch (IOException e) {
            System.out.println("Error reading from data connection");
            e.printStackTrace();
            return -1;
        }
    }


    private void acceptPhotos() {
        // Reads from the data connection. Meant to be called by the 'P' command.
        // Expects caller to know a data connection exists







    }
}
