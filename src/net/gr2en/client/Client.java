package net.gr2en.client;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class Client {

  private static final String SERVER_ADDRESS = "localhost";
  private static final String SERVER_PORT = "8080";

  public static void main(String[] args) {
    displayLoginForm();
  }

  private static void displayLoginForm() {
    JFrame frame = new JFrame("Authorization");
    frame.setBounds(100,100, 240, 160);
    frame.setResizable(false);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    JTextField loginField = new JTextField("Your name");
    loginField.setBounds(30, 30, 180, 20);

    JButton ok = new JButton("CONNECT");
    ok.setBounds(30, 60, 180, 30);
    ok.addActionListener(event -> {
      frame.dispose();
      try {
        displayChatWindow(loginField.getText());
      } catch (IOException exception) {
        exception.printStackTrace();
      }
    });

    frame.setLayout(null);
    frame.add(loginField);
    frame.add(ok);
    frame.setVisible(true);
  }

  private static void displayChatWindow(String name) throws IOException {
    JFrame.setDefaultLookAndFeelDecorated(false);
    JFrame frame = new JFrame("Chat");
    frame.setBounds(400, 200, 400, 300);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    JLabel label = new JLabel();
    label.setText("<html>");

    JTextField messageField = new JTextField(30);

    InetAddress inetAddress = InetAddress.getByName(SERVER_ADDRESS);
    Socket socket = new Socket(inetAddress, Integer.parseInt(SERVER_PORT));

    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

    ServerConnection connection = new ServerConnection(reader, writer, label);
    connection.start();
    connection.sendMessage(name);

    messageField.addKeyListener(new KeyListener() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
          if (!messageField.getText().isEmpty()) {
            connection.sendMessage(messageField.getText());
            messageField.setText("");
          }
        }
      }

      @Override
      public void keyTyped(KeyEvent e) { }

      @Override
      public void keyReleased(KeyEvent e) { }
    });

    frame.add(label, BorderLayout.NORTH);
    frame.add(messageField, BorderLayout.SOUTH);
    frame.setVisible(true);
  }
}