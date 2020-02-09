import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

public class Utility {

  public String getHostIP() {
    String output = "";
    String[] arr = null;

    try {
      Runtime runtime = Runtime.getRuntime();
      Process process = runtime.exec("hostname -I");
      BufferedReader bufferedReader = new BufferedReader(
          new InputStreamReader(process.getInputStream()));
      output = bufferedReader.readLine();
      arr = output.split(" ");
      bufferedReader.close();

    } catch (IOException e) {
      System.out.println(e);
    }

    return arr[1];
  }

  //gets the number of lines in the input text file divided by the number of mappers
  public int getNumOfLinesPerMapper(File file, int numOfMappers) {

    int numOfLines = 0;

    try (Stream<String> fileStream = Files.lines(Paths.get(file.getPath()))) {
      numOfLines = (int) fileStream.count();
    } catch (IOException e) {
      return 0;
    }
    return (int) Math.ceil(numOfLines / (numOfMappers + 0.0));
  }

  //splits the input text file to multiple text files for the mappers and builds the directories
  //to be mounted to the containers
  public void getEnvReady(MapReduceOperation operation) {

    try {
      Runtime.getRuntime().exec("shellScripts/getEnvironmentReady.sh " +
          operation.getNumOfMappers() + " " + operation.getNumOfReducers() + " " +
          new Utility().getNumOfLinesPerMapper(operation.getInput(), operation.getNumOfMappers())
          + " " + operation.getInput().getPath());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  public List<Map<String, ArrayList<String>>> shuffle(List<Map<String, ArrayList<String>>> list,
      int numOfReducers) {
    List<Map<String, ArrayList<String>>> toBeReduced = new ArrayList<>();
    Set<String> set = new HashSet<>();

    for (Map map : list) {
      set.addAll(map.keySet());
    }
    int i = 0;
    toBeReduced.add(new HashMap<>());

    for (String key : set) {
      toBeReduced.get(i).put(key, new ArrayList<>());
      for (Map map : list) {
        if (map.containsKey(key)) {
          toBeReduced.get(i).get(key).addAll((Collection<? extends String>) map.get(key));
        }
      }

      if (toBeReduced.get(i).size() >= (int) Math.ceil(set.size() / (numOfReducers + 0.0))) {
        i++;
        toBeReduced.add(new HashMap<>());
      }

    }
    return toBeReduced;
  }


  public String[] getReducersIP(int numOfReducers) {
    String[] array = new String[numOfReducers];
    try {
      Runtime runtime = Runtime.getRuntime();
      Process process = runtime.exec("shellScripts/getContainersIP.sh " + numOfReducers);
      BufferedReader bufferedReader = new BufferedReader(
          new InputStreamReader(process.getInputStream()));
      for (int i = 0; i < numOfReducers; i++) {
        array[i] = bufferedReader.readLine();
      }
      bufferedReader.close();

    } catch (IOException e) {
      System.out.println(e);
    }
    return array;
  }

  public void produceOutPut(List<Map<String, String>> list) {

    Map<String, String> result = new TreeMap<>();

    for (Map map : list) {
      result.putAll(map);
    }

    File file = new File("output.txt");

    BufferedWriter writer = null;
    try {
      writer = new BufferedWriter(new FileWriter(file));
    } catch (IOException e) {
      e.printStackTrace();
    }

    for (Map.Entry<String, String> entry : result.entrySet()) {
      try {
        writer.write(entry.getKey() + " --> " + entry.getValue());
        writer.newLine();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    try {
      writer.flush();
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void cleanUp(int numOfMappers, int numOfReducers) {

    try {
      Process process = Runtime.getRuntime().exec("shellScripts/cleanUp.sh " +
          numOfMappers + " " + numOfReducers);
      process.waitFor();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

  }

  public String compile(String javaFile) {
    String output = "";
    try {
      Process process = Runtime.getRuntime().exec("javac " + javaFile);
      BufferedReader bufferedReader = new BufferedReader(
          new InputStreamReader(process.getErrorStream()));
      output = bufferedReader.readLine();
      bufferedReader.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
    return output;
  }

  public void writFunction(String className, String function, int position) {

    RandomAccessFile file;

    try {
      file = new RandomAccessFile(className, "rw");
      FileChannel fileChannel = file.getChannel();
      fileChannel.position(position);
      ByteBuffer buff = ByteBuffer.wrap(function.getBytes(
          StandardCharsets.UTF_8));
      fileChannel.write(buff);
    } catch (FileNotFoundException e1) {
      e1.printStackTrace();
    } catch (IOException e1) {
      e1.printStackTrace();
    }
  }
}



