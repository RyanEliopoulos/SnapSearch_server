import testpackage.SnapListener;
import testpackage.DBInstantiator;
import testpackage.DBInterface;

import java.io.*;
import java.util.HashMap;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        // Connect to database
        DBInterface iface = new DBInterface("latest.db");
        iface.connect();

        /*

            This block reads the images and inserts it into the database
         */
//        try {
//            // Open file
//            FileInputStream fis = new FileInputStream("C:/Users/Ryan/Mickey_Mouse.png");
//
//            // Read file into buffer
//            ByteArrayOutputStream outstream = new ByteArrayOutputStream();
//            int bt;
//            while((bt = fis.read()) != -1) {
//                outstream.write(bt);
//            }
//
//           //
//            byte[] filedata = outstream.toByteArray();
//            if (iface.insertPhoto(1, filedata) == -1) {
//                System.out.println("Call to insert_photos failed");
//            }
//        }
//        catch (FileNotFoundException e ) {
//            e.printStackTrace();
//        }
//        catch (IOException io) {
//            io.printStackTrace();
//        }


        /*
            This block is for pulling photos from the table. Works.

         */
        HashMap<String, byte[]> hm = iface.getPhotos(1);

        Set st = hm.keySet();
        String[] sa = (String[]) st.toArray(new String[0]);  // Intellij says must cast. Docs don't..

        for (String key : sa) {
            System.out.println(key);
        }



        // Attempting to write one of the photos to a file
        try {
            FileOutputStream fos = new FileOutputStream("C:/Users/Ryan/NewMouse.png");
            byte[] filedata = hm.get("2");
            fos.write(filedata);

        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e ) {
            e.printStackTrace();
        }


//        SnapListener sl = new SnapListener();
//        sl.listen();

//      DBInstantiator dbi = new DBInstantiator();
//      dbi.instantiate("latest.db");
//      dbi.closeConnection();
   }
}
