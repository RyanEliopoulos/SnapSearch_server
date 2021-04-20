package testpackage;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class CommandReader {


    public String readCommand(InputStream instream) {
        // Returns the next command from the command connection
        // BufferedReader (InputStreamReader(InputStream)
        System.out.println("In readCommand CommandReader");
        String responseString = "";

        InputStreamReader inreader = new InputStreamReader(instream, StandardCharsets.UTF_8);
        try {
            while (true) {
                int ret = inreader.read();

                if (ret == -1 || (char) ret == '\n') {
                    break;
                }
                responseString = responseString.concat(String.valueOf( (char) ret));
            }
            return responseString;
        }
        catch (IOException e) {
            System.out.println("Error reading from data connection");
            e.printStackTrace();
            return null;
        }
    }
}
