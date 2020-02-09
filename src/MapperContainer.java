import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class MapperContainer implements Runnable {


  private String hostIP;
  private int mapperID;
  private CountDownLatch latch;

  public MapperContainer(String hostIP, int mapperID, CountDownLatch latch) {
    this.hostIP = hostIP;
    this.mapperID = mapperID;
    this.latch = latch;
  }

  @Override
  public void run() {
    try {
      Process process = Runtime.getRuntime()
          .exec("shellScripts/runMapper.sh " + mapperID + " " + hostIP);
      process.waitFor();
      latch.countDown();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

  }
}
