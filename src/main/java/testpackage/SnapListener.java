package testpackage;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;
import java.sql.*;

public class SnapListener {
  // This is the top-level, primary thread listening for incoming client connections

  private ReentrantLock mutex = new ReentrantLock(); // Gatekeeper for db writes

  public void listen() {
    // Daemon for establishing client connections.
    // Each connection spins off a new thread.
    DBInterface dbiface = new DBInterface("latest.db");
    dbiface.connect();
    InetSocketAddress sadd = new InetSocketAddress("localhost", 6970);
    try (ServerSocket serversocket = new ServerSocket()) {
      serversocket.bind(sadd);
      while (true) {  // Infinite listen loop
        Socket activeSocket = serversocket.accept();
        ServerService service = new ServerService(activeSocket, dbiface, mutex);
        service.start();
      }
    } catch (Exception e) {
      System.out.print("Experienced issue in daemon thread");
      e.printStackTrace();
    }
  }
}
