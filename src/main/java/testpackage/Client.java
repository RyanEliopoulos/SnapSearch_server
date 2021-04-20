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
                        System.out.println("Here is the string");
                        System.out.println(jsonString);


                        // Extracting decoded binary data
                        Gson gson = new Gson();
                        Type photoMap = new TypeToken<HashMap<String, String>>() {}.getType();
                        HashMap<String, String> pm = gson.fromJson(jsonString, photoMap);

                        // Checking contents of pm
                        System.out.println("pm key set" + pm.keySet());


                        Base64.Decoder dec = Base64.getDecoder();
                        System.out.println("About to decode the b64");

                        System.out.println("This is pm.get" + pm.get("2"));
                        String b64string = pm.get("2");
                        byte[] fileblob = dec.decode(b64string);

                        System.out.println("About to writ eto file");
                        // Writing to a file
                        try {
                            FileOutputStream fos = new FileOutputStream("C:/Users/Ryan/Yolo.png");
                            fos.write(fileblob);
                        }
                        catch (IOException e) {
                            System.out.println("Failed to write file blob");
                            e.printStackTrace();
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
