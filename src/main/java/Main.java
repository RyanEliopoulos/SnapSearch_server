import testpackage.SnapListener;
import testpackage.DBInterface;


public class Main {

    public static void main(String[] args) {
        // Connect to database
        DBInterface iface = new DBInterface("latest.db");
        iface.connect();

        // Establish daemon
        SnapListener sl = new SnapListener();
        sl.listen();
   }
}
