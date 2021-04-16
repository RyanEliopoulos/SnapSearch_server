package testpackage;


import java.net.Socket;
import java.net.InetSocketAddress;
import java.io.OutputStream;

import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        System.out.print("Hello broski\n");

        // Preparing connection to server
        InetSocketAddress targetAddress = new InetSocketAddress("localhost", 6970);
        Socket clientsocket = new Socket();

        try {
            clientsocket.connect(targetAddress);
            System.out.print("Connected to the server\n");

            OutputStream outstream = clientsocket.getOutputStream();
            Scanner scanner = new Scanner(System.in);


            while (true) {
                String inputstring = scanner.nextLine();
                if (inputstring == "Q") {
                    System.out.println("User shutdown request");
                    break;
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
        }
    }
}
