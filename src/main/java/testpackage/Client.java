package testpackage;


import java.net.Socket;
import java.net.InetSocketAddress;
import java.io.OutputStream;

public class Client {

    public static void main(String[] args) {
        System.out.print("Hello broski\n");

        // Preparing connection to server
        InetSocketAddress targetAddress = new InetSocketAddress("localhost", 6971);
        Socket clientsocket = new Socket();

        try {
            clientsocket.connect(targetAddress);
            System.out.print("Connected to the server\n");

            OutputStream outstream = clientsocket.getOutputStream();
            for (int i=49; i<69; i++) {
                outstream.write(i);
            }

            System.out.println("Transmission complete..sleeping..");
            Thread.sleep(30000);
            System.out.println("Back from sleep.  Closing client.");

            clientsocket.close();

        }
        catch (Exception e) {
            System.out.print("heyo couldnt' connect for some reason\n");
        }
    }
}
