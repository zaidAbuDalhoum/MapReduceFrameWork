import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ReducerContainer implements Runnable {

  private String hostIP;
  private int reducerID;
  ;
  private CountDownLatch latch;

  public ReducerContainer(String hostIP, int reducerID, CountDownLatch latch) {
    this.hostIP = hostIP;
    this.reducerID = reducerID;
    this.latch = latch;
  }

  @Override
  public void run() {
    try {
      Process process = Runtime.getRuntime()
          .exec("shellScripts/runReducer.sh " + reducerID + " " + hostIP);
      process.waitFor();
      latch.countDown();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
