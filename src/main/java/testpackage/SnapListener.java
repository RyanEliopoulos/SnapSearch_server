package testpackage;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;
import java.sql.*;



public class SnapListener {
    // This is the top-level, primary thread listening for incoming client connections

    private ReentrantLock mutex = new ReentrantLock();

    public void listen() {


        InetSocketAddress sadd = new InetSocketAddress("localhost", 6970);
        DBInterface dbiface = new DBInterface("latest.db");
        dbiface.connect();

        try (ServerSocket serversocket = new ServerSocket()) {
            serversocket.bind(sadd);
            System.out.print("Here\n");
            System.out.print(serversocket + "\n");

            while (true) {
                Socket activeSocket = serversocket.accept();

                // Add a new thread here to deal with the socket
                System.out.print("Client has connected to us. Giving control to the other thread\n");
                ServerService service = new ServerService(activeSocket, dbiface, mutex);
                service.start();
                System.out.println("New thread started..awaiting another call");

            }
        }
        catch (Exception e){
            System.out.print("We've got a problem, officer");
            System.out.print(e);
        }
    }

}