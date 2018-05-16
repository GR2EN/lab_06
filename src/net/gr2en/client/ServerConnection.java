package net.gr2en.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.JLabel;

@SuppressWarnings("InfiniteLoopStatement")
class ServerConnection extends Thread {
  private BufferedReader reader;
  private PrintWriter writer;
  private JLabel label;

  ServerConnection(BufferedReader reader, PrintWriter writer, JLabel label) {
    this.reader = reader;
    this.writer = writer;
    this.label = label;
  }

  private void getMessage() {
    try {
      while (true) {
        String previousContent = reader.readLine() + "<br>";
        label.setText(label.getText() + previousContent);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  void sendMessage(String message) {
    writer.println(message);
  }

  @Override
  public void run() {
    while (true) {
      getMessage();
    }
  }
}
