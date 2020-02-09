import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;

public class ReducerSocket implements Runnable {

  private Map<String, ArrayList<String>> map;
  private String ip;

  public ReducerSocket(Map<String, ArrayList<String>> map, String ip) {
    this.map = map;
    this.ip = ip;
  }

  @Override
  public void run() {
    try {
      Socket socket = new Socket(ip, 8080);
      ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
      out.writeObject(map);
      out.flush();
      out.close();
      socket.close();
    } catch (IOException e1) {
      e1.printStackTrace();
    }
  }

}

