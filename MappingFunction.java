import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MappingFunction {

  public static void main(String[] args) throws IOException {
    File file = new File("/usr/src/app/java/volume/toBeMapped.txt");
    FileInputStream fileInputStream = new FileInputStream(file);
    InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

    Map<String, ArrayList<String>> map = map(bufferedReader);

    try {
      Socket socket = new Socket(args[0], 8082);
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
      objectOutputStream.writeObject(map);
      objectOutputStream.flush();
      objectOutputStream.close();
      socket.close();
    } catch (Exception e) {
      System.out.println(e);
    }
  }public static Map map(BufferedReader bufferedReader) throws IOException {
    Pattern pattern = Pattern.compile("[a-zA-Z]+");
    Map<String, ArrayList<String>> map = new HashMap<>();
    Matcher matcher;
    String str = bufferedReader.readLine();
    while (str != null) {
      if (!str.equals("")) {
        matcher = pattern.matcher(str);
        while (matcher.find()) {
          String word = matcher.group();
          if (!map.containsKey(word)) {
            map.put(word, new ArrayList<>());
            map.get(word).add(1 + "");
          } else
            map.get(word).add(1 + "");
        }
      }
      str = bufferedReader.readLine();
    }
    return map;

  }
}