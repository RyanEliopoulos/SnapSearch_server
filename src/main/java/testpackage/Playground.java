package testpackage;


import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;

public class Playground {

    public static void main(String[] args) {


        Base64.Encoder b64encode = Base64.getEncoder();
        String encodedString = b64encode.encodeToString("yolo".getBytes(StandardCharsets.UTF_8));



    }


}


