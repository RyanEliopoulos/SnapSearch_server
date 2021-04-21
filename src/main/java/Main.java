import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import testpackage.Photo;
import testpackage.SnapListener;
import testpackage.DBInstantiator;
import testpackage.DBInterface;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        Gson gson = new Gson();


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
//            if (iface.insertPhoto(1, filedata, (float) 420.60, (float) 69.420) == -1) {
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

            This block converts the Photo data into the final JSON string
            and then translates back to the Photo objects before writing them to files
         */
        // Pulling photo data from the databse
//        System.out.println("Attempting to load photos");
//        HashMap<String, Photo> hm = iface.getPhotos(1);
//
//        // Converting Photo objects to json string
//        HashMap<String, String> jsonmap = new HashMap<String, String>();
//        for (String key : hm.keySet()) {
//            Photo tmpPhoto = hm.get(key);
//            String photoString = gson.toJson(tmpPhoto);
//            jsonmap.put(key, photoString);
//            //System.out.println(photoString);
//        }
//
//
//        // Now to jsonify the jsonmap into a single string
//        Type JsonPhotoMap = new TypeToken<HashMap<String, String>>() {}.getType();
//        String finalString = gson.toJson(jsonmap, JsonPhotoMap);
//        System.out.println("Drumroll, please!");
//        //System.out.println(finalString);
//
//        // Attempting to reverse the process now
//        HashMap<String, String> reverseJsonMap = gson.fromJson(finalString, JsonPhotoMap);
//        HashMap<String, Photo> reverseHm = new HashMap<String, Photo>();
//        Base64.Decoder decoder = Base64.getDecoder();
//        for (String key : reverseJsonMap.keySet()) {
//            String photoString = reverseJsonMap.get(key);
//            Photo phto = gson.fromJson(photoString, Photo.class);
//            byte[] filebinary = decoder.decode(phto.encodedFileblob);
//            try {
//                FileOutputStream fos = new FileOutputStream("C:/Users/Ryan/" + key + "baby.png");
//                fos.write(filebinary);
//            }
//            catch (IOException e) {
//                e.printStackTrace();
//            }
//            //System.out.println("Photo blob: " + phto.encodedFileblob);
//        }



        // Can I now extract the binary data unperturbed?

//        // Need to translate it to a <String, String> map where the values are JSON representations
//        HashMap<String, String> jsonmap = new HashMap<String, String>();
//        //Gson gson = new Gson();
//        //Type PhotoMap = new TypeToken<HashMap<String, String>>() {}.getType();
//        Base64.Encoder b64 = Base64.getEncoder();
//
//        for(String key: hm.keySet()) {
//            // Encoding binary blob
//            Photo tmp = hm.get(key);
/// /            tmp.fileblob = b64.encodeToString(tmp.fileblob.getBytes(StandardCharsets.UTF_8));
//            String jsonized = gson.toJson(tmp);
//            System.out.println("Condition of the jsonized photo object: " + jsonized);
//            jsonmap.put(key, jsonized);
//        }
        /*

            This block is for pulling photos from the table. Works.


//         */



//        HashMap<String, byte[]> hm = iface.getPhotos(1);
//
//
//
//        // Attempting to translate to a JSON string
//        // beginning with b64 encoding
//        Base64.Encoder b64 = Base64.getEncoder();
//        //String encoded_filedata = b64.encodeToString(hm.get("2"));
//        //System.out.println(encoded_filedata);
//
//        // Creating new hashmap with encoded files
//        HashMap<String, String> encoded_map = new HashMap<String, String>();
//        Set st = hm.keySet();
//        String[] sa = (String[]) st.toArray(new String[0]);  // Intellij says must cast. Docs don't..
//        for (String key : sa) {
//            String encoded_filedata = b64.encodeToString(hm.get(key));
//            encoded_map.put(key, encoded_filedata);
//        }
//        Type photoMapping = new TypeToken<HashMap<String, String>>() {}.getType();
//        String jasonized = gson.toJson(encoded_map, photoMapping);
//        System.out.println(jasonized);

//
//
//
//        // Attempting to write one of the photos to a file
//        try {
//            FileOutputStream fos = new FileOutputStream("C:/Users/Ryan/NewMouse.png");
//            byte[] filedata = hm.get("2");
//            fos.write(filedata);
//
//        }
//        catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        catch (IOException e ) {
//            e.printStackTrace();
//        }


        SnapListener sl = new SnapListener();
        sl.listen();

//      DBInstantiator dbi = new DBInstantiator();
//      dbi.instantiate("latest.db");
//      dbi.closeConnection();
   }
}
