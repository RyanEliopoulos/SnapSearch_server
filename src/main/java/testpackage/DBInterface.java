package testpackage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.HashMap;


public class DBInterface {
    String dbname;
    Connection conn = null;

    public DBInterface(String dbname) {
        this.dbname = dbname;
    }

    public int connect() {
        try {
            this.conn = DriverManager.getConnection("jdbc:sqlite:" + this.dbname);
        }
        catch (SQLException e) {
            System.out.println("Encountered sql exception connecting to database");
            return -1;
        }
        return 0;
    }

    public void directcontrol() {
        try {
            String sql = "SELECT * FROM PHOTOS";
            Statement stmt = this.conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                System.out.println(rs.getInt("photoid"));
                System.out.println(rs.getInt("userid"));
            }
        }
        catch (SQLException e) {
            System.out.println("Problem direct controlling");
        }

    }

    public HashMap<String, Photo> getPhotos(int userid) {
        // Pulls all picture data for the given userid.

        // Querying database
        String sql = "SELECT * FROM photos " +
                "WHERE userid=(?)";
        try {
            // Executing statement
            PreparedStatement pstmt = this.conn.prepareStatement(sql);
            pstmt.setInt(1, userid);
            ResultSet rs = pstmt.executeQuery();

            // Structuring data for caller
            HashMap<String, Photo> hm = new HashMap<>();
            while (rs.next()) {
                // pulling the binary data
                InputStream instream = rs.getBinaryStream("filedata");
                ByteArrayOutputStream bout_stream = new ByteArrayOutputStream();
                try {
                    bout_stream.writeBytes(instream.readAllBytes());
                }
                catch (IOException e ) {
                    System.out.println("Error reading photo data from database");
                    return null;
                }
                byte[] photobytes = bout_stream.toByteArray();
                Float longitude = rs.getFloat("longitude");
                Float latitude = rs.getFloat("latitude");

                // Pulling primary key
                Integer photoid = rs.getInt("photoid");
                Photo phto = new Photo(photoid, userid, longitude, latitude, photobytes);

                hm.put(photoid.toString(), phto);
            }
            return hm;
        }
        catch (SQLException e) {
            System.out.println("Failed to create statement with database");
            e.printStackTrace();
            return null;
        }
    }


    public int insertPhoto(int userid, byte[] filedata, float longitude, float latitude) {
        // puts the photo data into the database. Works.

            // Schema:
            // photoid (PRIMARY KEY)
            // userid (int)
            // filedata (blob)
            // longitude (real)
            // latitude (real)

            String sql = "INSERT INTO photos (userid, filedata, longitude, latitude)"
                    + "VALUES (?, ?, ?, ?)";  //
            try {
                PreparedStatement pstmt = this.conn.prepareStatement(sql);
                pstmt.setInt(1, userid);
                pstmt.setBytes(2, filedata);
                pstmt.setFloat(3, longitude);
                pstmt.setFloat(4, latitude);
                pstmt.executeUpdate();
            }
            catch (SQLException e) {
                e.printStackTrace();
                return -1;
            }
            return 0;
    }

}
