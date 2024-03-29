package testpackage;

import java.util.Base64;

public class Photo {

  /*
     To simplify the Gson-ifying process of the photo data as nested JSON.
  */

  int photoid;
  int userid;
  double longitude;
  double latitude;
  public String encodedFileblob;  // B64 to adhere to JSON character set.

  public Photo(int photoid, int userid, double longitude, double latitude, byte[] filebytes) {
    this.photoid = photoid;
    this.userid = userid;
    this.longitude = longitude;
    this.latitude = latitude;

    Base64.Encoder b64Encoder = Base64.getEncoder();
    this.encodedFileblob = b64Encoder.encodeToString(filebytes);
  }
}
