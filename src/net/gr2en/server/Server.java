package net.gr2en.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("InfiniteLoopStatement")
public class Server {

  private static final int PORT = 8080;

  private final List<Connection> connectionList = Collections.synchronizedList(new ArrayList<>());

  private Server() {
    try {
      ServerSocket serverSocket = new ServerSocket(PORT);
      while (true) {
        Socket socket = serverSocket.accept();
        Connection connection = new Connection(socket);
        connectionList.add(connection);
        connection.start();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    System.out.println("Server is working on " + PORT + " port.");
    new Server();
  }

  class Connection extends Thread {

    private BufferedReader reader;
    private PrintWriter writer;
    private Socket socket;
    private String userName;

    Connection(Socket socket) {
      this.socket = socket;
      try {
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    private void close() {
      try {
        reader.close();
        writer.close();
        socket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    @Override
    public void run() {
      try {
        userName = reader.readLine();
        synchronized (connectionList) {
          for (Connection connection : connectionList) {
            connection.writer.println("Welcome, " + userName + ".");
          }
        }
        String message;
        while (true) {
          message = reader.readLine();
          synchronized (connectionList) {
            for (Connection connection : connectionList) {
              connection.writer.println(userName + ": " + message);
            }
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        close();
        synchronized (connectionList){
          for (Connection connection : connectionList) {
            connection.writer.println(userName + " has left.");
          }
        }
      }
    }
  }
}