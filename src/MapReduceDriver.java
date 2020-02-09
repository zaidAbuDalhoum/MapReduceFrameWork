import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class MapReduceDriver extends Application {

  List<Map<String, ArrayList<String>>> mapped = new ArrayList<>();
  List<Map<String, ArrayList<String>>> toBeReduced = null;
  List<Map<String, String>> result = new ArrayList<>();

  Label error = new Label();


  ServerSocket serverForMappers;
  ServerSocket serverForReducers;

  MapReduceOperation operation = null;
  Utility utility = new Utility();

  {
    try {
      serverForMappers = new ServerSocket(8082);
      serverForReducers = new ServerSocket(8090);

    } catch (IOException e) {
      error.setTextFill(Color.RED);
      error.setText("One Of the Ports for the Servers is Busy.");
    }
  }

  @Override
  public void start(Stage primaryStage){

    String hostIp = utility.getHostIP();

    File image = new File("Loading.gif");

    ImageView loading = new ImageView(new Image(image.toURI().toString()));

    GridPane pane = new GridPane();

    TextField numOfMappers = new TextField();

    Label mappers = new Label("Number Of Mappers : ");

    TextField numOfReducers = new TextField();

    Label reducers = new Label("Number Of Reducers : ");

    Label path = new Label("Path to the File : ");

    Label instructions = new Label("Before Starting please run this command to build\n"
        + "the mapreduce docker image : \n"
        + "docker image build -t mapreduce .");

    TextField filePath = new TextField();

    Button btNext = new Button("Next");

    Button btNext2 = new Button("Next");

    Button btNext3 = new Button("Next");

    Button btProcess = new Button("Begin Process");

    TextArea mappingFunction = new TextArea();

    TextArea reducingFunction = new TextArea();
    pane.add(instructions, 1, 1);
    pane.add(new Label(), 0, 2);
    pane.add(mappers, 0, 4);
    pane.add(numOfMappers, 1, 4);
    pane.add(reducers, 0, 5);
    pane.add(numOfReducers, 1, 5);
    pane.add(path, 0, 6);
    pane.add(filePath, 1, 6);
    pane.add(btNext, 4, 9);
    pane.add(error, 1, 11);

    GridPane secPane = new GridPane();
    btNext.setOnAction(e -> {

      if (numOfMappers.getText().isEmpty() || numOfReducers.getText().isEmpty() ||
          filePath.getText().isEmpty()) {
        error.setTextFill(Color.RED);
        error.setText("Missing Information !");
      } else {

        File input = new File(filePath.getText());
        if (!input.exists()) {
          error.setTextFill(Color.RED);
          error.setText("File Not Found! \nPlease make sure to write the full path of the File");
        } else {
          operation = new MapReduceOperation(Integer.parseInt(numOfMappers.getText()),
              Integer.parseInt(numOfReducers.getText()), input);

          mappingFunction.setPrefWidth(350);
          mappingFunction.setPrefHeight(300);

          secPane.add(new Label(), 0, 0);
          secPane.add(new Label(), 0, 1);
          secPane.add(new Label("Insert your Mapping Function with the Following Conditions :"
              + "\n 1- The Name Of The Function is map(). "
              + "\n 2- The Argument for the function is a BufferedReader. "
              + "\n 3- The Output for the function is a Map<String, ArrayList<String>>. \n"
              + "The Example below is for a Word Count Application : "), 0, 3);

          secPane.add(new Label(), 0, 4);
          secPane.add(new Label(), 0, 6);
          secPane.add(mappingFunction, 0, 7);
          secPane.add(btNext2, 5, 8);
          secPane.add(error, 0, 10);
          secPane.setAlignment(Pos.CENTER);
          mappingFunction.setText(new FunctionExample().getMapExample());

          Scene scene = new Scene(secPane, 750, 750);
          primaryStage.setScene(scene);
          primaryStage.setTitle("Map-Reduce Framework");
          primaryStage.show();
        }
      }
    });

    GridPane thirdPane = new GridPane();
    btNext2.setOnAction(event -> {
      error.setText("");

      if (mappingFunction.getText().isEmpty()) {

        error.setTextFill(Color.RED);
        error.setText("Please write the function before proceeding!");

      } else {
        StringBuilder function = new StringBuilder(mappingFunction.getText());
        function.append("\n}");

        utility.writFunction("MappingFunction.java", function.toString(), 925);

        String compileOutput = utility.compile("MappingFunction.java");

        if (!(compileOutput
            .equals("Note: MappingFunction.java uses unchecked or unsafe operations."))) {
          error.setTextFill(Color.RED);
          error.setText(compileOutput);
        } else {
          reducingFunction.setPrefWidth(350);
          reducingFunction.setPrefHeight(300);
          reducingFunction.setText(new FunctionExample().getReduceExample());

          thirdPane.add(new Label(), 0, 0);
          thirdPane.add(new Label(), 0, 1);
          thirdPane.add(new Label("Insert your Reducing Function with the Following Conditions : "
              + "           \n 1- The Name Of The Function is reduce()."
              + "           \n 2- The Argument for the function is a Map<String, ArrayList<String>>. "
              + "           \n 3- The Output for the function is a Map<String,String>.\n"
              + "The Example Below is for a Word Count Application : "), 0, 3);

          thirdPane.add(new Label(), 0, 4);
          thirdPane.add(new Label(), 0, 6);
          thirdPane.add(reducingFunction, 0, 7);
          thirdPane.add(btNext3, 2, 8);
          thirdPane.add(new Label(), 4, 9);
          thirdPane.add(loading, 0, 12);
          thirdPane.add(error, 0, 11);
          loading.setFitHeight(100);
          loading.setFitWidth(100);
          loading.visibleProperty().bind(btNext3.pressedProperty());

          thirdPane.setAlignment(Pos.CENTER);

          Scene scene = new Scene(thirdPane, 750, 750);

          primaryStage.setScene(scene);
          primaryStage.setTitle("Map-Reduce Framework");
          primaryStage.show();

        }
      }
    });

    GridPane fourthPane = new GridPane();

    btNext3.setOnAction(e -> {
      error.setText("");

      if (reducingFunction.getText().isEmpty()) {

        error.setTextFill(Color.RED);
        error.setText("Please write the function before proceeding!");

      } else {

        StringBuilder function = new StringBuilder(reducingFunction.getText());
        function.append("\n}");

        utility.writFunction("ReducingFunction.java", function.toString(), 977);

        String compileOutput = utility.compile("ReducingFunction.java");

        if (!(compileOutput
            .equals("Note: ReducingFunction.java uses unchecked or unsafe operations."))) {
          error.setTextFill(Color.RED);
          error.setText(compileOutput);
        } else {

          utility.getEnvReady(operation);

          ExecutorService executor = Executors.newCachedThreadPool();

          CountDownLatch latch = new CountDownLatch(operation.getNumOfReducers());

          for (int i = 0; i < operation.getNumOfReducers(); i++) {
            executor.execute(new ReducerContainer(hostIp, i, latch));
          }

          try {
            latch.await();
          } catch (InterruptedException ex) {
            ex.printStackTrace();
          }

          executor.shutdown();

          fourthPane.add(btProcess, 3, 3);
          fourthPane.add(new Label(), 3, 4);
          fourthPane.add(loading, 3, 5);
          fourthPane.setAlignment(Pos.CENTER);

          loading.visibleProperty().bind(btProcess.pressedProperty());

          Scene scene = new Scene(fourthPane, 750, 750);
          primaryStage.setScene(scene);
          primaryStage.setTitle("Map-Reduce Framework");
          primaryStage.show();
        }
      }
    });

    btProcess.setOnAction(e -> {

      final boolean[] open = {true};
      Thread mappersThread = new Thread(() -> {

        while (open[0]) {

          try {
            Socket socket = serverForMappers.accept();
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            Map<String, ArrayList<String>> map = (Map<String, ArrayList<String>>) objectInputStream
                .readObject();
            synchronized (mapped) {
              mapped.add(map);
            }
            objectInputStream.close();
            socket.close();
            if (mapped.size() == operation.getNumOfMappers()) {
              open[0] = false;
              socket.close();
              serverForMappers.close();
            }
          } catch (
              IOException e1) {
            e1.printStackTrace();
          } catch (
              ClassNotFoundException e1) {
            e1.printStackTrace();
          }
        }
      });

      mappersThread.start();

      ExecutorService mappersExecutor = Executors.newCachedThreadPool();
      CountDownLatch latch = new CountDownLatch(operation.getNumOfMappers());

      for (int i = 0; i < operation.getNumOfMappers(); i++) {
        mappersExecutor.execute(new MapperContainer(hostIp, i, latch));
      }

      try {
        latch.await();
      } catch (InterruptedException ex) {
        ex.printStackTrace();
      }

      mappersExecutor.shutdown();

      try {
        mappersThread.join();
      } catch (InterruptedException ex) {
        ex.printStackTrace();
      }

      toBeReduced = utility.shuffle(mapped, operation.getNumOfReducers());

      boolean[] open1 = {true};

      Thread reducersThread = new Thread(() -> {

        while (open1[0]) {

          try {
            Socket socket = serverForReducers.accept();
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            Map<String, String> map = (Map<String, String>) objectInputStream
                .readObject();
            synchronized (result) {
              result.add(map);
            }
            objectInputStream.close();
            socket.close();
            if (result.size() == operation.getNumOfReducers()) {
              open1[0] = false;
              serverForReducers.close();
            }
          } catch (
              IOException e2) {
            e2.printStackTrace();
          } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
          }
        }
      });

      String[] arrayOfIP = utility.getReducersIP(operation.getNumOfReducers());

      reducersThread.start();

      ExecutorService reducerSocketExecutor = Executors.newCachedThreadPool();

      int i = 0;
      for (String ip : arrayOfIP) {
        reducerSocketExecutor.execute(new ReducerSocket(toBeReduced.get(i), ip));
        i++;
      }

      try {
        reducersThread.join();
      } catch (InterruptedException ex) {
        ex.printStackTrace();
      }

      utility.produceOutPut(result);

      utility.cleanUp(operation.getNumOfMappers(), operation.getNumOfReducers());

      GridPane fifthPane = new GridPane();

      Label label = new Label("All Done! \nYour output File is Ready");

      fifthPane.add(label, 0, 0);
      label.setTextFill(Color.TOMATO);
      label.setFont(Font.font("Times New Roman",
          FontWeight.BOLD, FontPosture.REGULAR, 22));

      fifthPane.setAlignment(Pos.CENTER);

      Scene scene = new Scene(fifthPane, 750, 750);

      primaryStage.setScene(scene);
      primaryStage.setTitle("Map-Reduce Framework");
      primaryStage.show();

    });

    pane.setAlignment(Pos.CENTER);
    Scene scene = new Scene(pane, 750, 750);

    primaryStage.setScene(scene);
    primaryStage.setTitle("Map-Reduce Framework");
    primaryStage.show();
  }
}
