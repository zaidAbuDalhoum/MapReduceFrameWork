import java.io.*;
import java.net.*;
import java.util.*;
import java.math.*;
import java.lang.*;
import java.nio.*;

public class ReducingFunction {

  public static void main(String[] args) throws IOException {
    try {
      ServerSocket serverSocket = new ServerSocket(8080);
      Socket socket = serverSocket.accept();
      ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
      Map<String, ArrayList<String>> map1 = (Map<String, ArrayList<String>>) objectInputStream.readObject();
      objectInputStream.close();
      socket.close();

      Map<String, String> map = reduce(map1);

      Socket socket1 = new Socket(args[0], 8090);
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket1.getOutputStream());
      objectOutputStream.writeObject(map);
      objectOutputStream.flush();
      objectOutputStream.close();
      socket1.close();
    } catch (Exception e) {
      System.out.println(e);
    }
  }
public static Map reduce(Map<String,ArrayList<String>> map) {

    Map<String, String> result = new HashMap<>();

    for (Map.Entry<String,ArrayList<String>> entry : map.entrySet()) {
      result.put(entry.getKey(),entry.getValue().size()+"");
    }

    return result;
  }
}