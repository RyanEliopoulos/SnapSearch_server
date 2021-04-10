import testpackage.SnapListener;
import testpackage.DBInstantiator;

public class Main {

   public static void main(String[] args) {

      //SnapListener sl = new SnapListener();
      //sl.listen();

      DBInstantiator dbi = new DBInstantiator();
      dbi.instantiate("latest.db");
      dbi.closeConnection();
   }


}
