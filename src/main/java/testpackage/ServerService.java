package testpackage;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;


public class ServerService extends Thread {

    Socket activeSocket;
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
                switch(command) {
                    case 'Q': // Close connection
                        System.out.println("The client wishes to close connection");
                        stayalive = false;
                        break;
                    case 'I': // Client is identifying themselves

                }
                System.out.println("Printing the incoming byte");
                System.out.println((char) nextint);
            }
            this.activeSocket.close();
            System.out.println("Connection closed");
        }
        catch (Exception IOException) {
            System.out.println("Connection with the client shutdown ungracefully..");
        }
    }

    private Boolean usercontext(int userid) {
        // Called by the control loop to update current context
        // to the given userid (if it exists)
        System.out.println("Placeholder");
        return true;
    }
}