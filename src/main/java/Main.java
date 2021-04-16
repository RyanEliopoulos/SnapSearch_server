import testpackage.SnapListener;
import testpackage.DBInstantiator;
import testpackage.DBInterface;
import java.util.HashMap;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        // Connect to database
        DBInterface iface = new DBInterface("latest.db");
        iface.connect();
//        iface.directcontrol();

        // Pulling photos belonging to user 1
        HashMap<String, byte[]> hm = iface.getPhotos(1);

        Set st = hm.keySet();
        String[] sa = (String[]) st.toArray(new String[0]);  // Intellij says must cast. Docs don't..


        for (String key : sa) {
            // For each string (photoid)
            System.out.println(key);
        }



//        SnapListener sl = new SnapListener();
//        sl.listen();

//      DBInstantiator dbi = new DBInstantiator();
//      dbi.instantiate("latest.db");
//      dbi.closeConnection();
   }
}
