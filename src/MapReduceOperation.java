import java.io.File;

public class MapReduceOperation {

  private int numOfMappers;

  private int numOfReducers;

  private File input;

  public MapReduceOperation(int numOfMappers, int numOfReducers, File input) {
    this.numOfMappers = numOfMappers;
    this.numOfReducers = numOfReducers;
    this.input = input;
  }

  public int getNumOfMappers() {
    return numOfMappers;
  }


  public int getNumOfReducers() {
    return numOfReducers;
  }

  public File getInput() {
    return input;
  }

}
