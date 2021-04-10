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

        try {
            InputStream instream = this.activeSocket.getInputStream();
            OutputStream ostream = this.activeSocket.getOutputStream();

            int counter = 0;
            while (true) {
                int nextbyte = instream.read();

                if (nextbyte == -1) {
                    System.out.println("The connection with the client has been gracefully closed.");
                    break;
                }
                else {
                    counter++;
                    System.out.println("Printing the incoming byte. Counter is: " + counter);
                    System.out.println((char) nextbyte);
                }

            }
            this.activeSocket.close();
        }
        catch (Exception IOException) {
            System.out.println("Connection with the client shutdown ungracefully..");
        }
    }
}