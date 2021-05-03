import testpackage.SnapListener;
import testpackage.DBInterface;

public class Main {

  public static void main(String[] args) {
    // Establish daemon
    SnapListener sl = new SnapListener();
    sl.listen();
  }
}
