package testpackage;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;


public class DBInstantiator {

    Connection conn = null;

    public void instantiate(String dbname) {
        //this.tutorialVersion(dbname);
        this.fileVersion(dbname);
        this.insrtFile();
        this.selectFile();
    }

    public void selectFile() {
        try {
            System.out.println("Attempting to pull file data");
            String sql = "SELECT * FROM photos";
            Statement stmt = this.conn.createStatement();
            stmt.setQueryTimeout(5);
            ResultSet rs = stmt.executeQuery(sql);
            InputStream instream = rs.getBinaryStream("filedata");

            byte[] buffer = new byte[1024];
            while (instream.read(buffer) > 0) {
                for(byte singlebyte : buffer) {
                    System.out.println("About to see a byte:" + (char) singlebyte);
                }
            }
        }
        catch (SQLException | IOException e){
            System.out.println("SQL exception: " + e);
        }
    }


    private void insrtFile() {

        String sql = "INSERT INTO photos (userid, filedata)"
                + "VALUES (1, ?)";

        try {
            // Placeholder "file"
            byte[] placeholder = new byte[2];
            placeholder[0] = (byte) 69;
            placeholder[1] = (byte) 70;

            // Asking table to receive a new file
            PreparedStatement pstmt = this.conn.prepareStatement(sql);
            pstmt.setBytes(1, placeholder);
            pstmt.executeUpdate();
            System.out.println("Blob data stored");

        }
        catch (SQLException e) {
            System.out.println("SQL exception: " + e);
        }
    }
    private void fileVersion(String dbname) {
        try {

            // Establishing connection
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbname);
            this.conn = conn;
            // Building statement
            System.out.println("Here's our conn: " + conn);

            DatabaseMetaData mdata = conn.getMetaData();
            System.out.println("Database metadata: " + mdata.getDriverName());

            String sql = "DROP TABLE IF EXISTS photos";
            Statement statement = conn.createStatement();
            statement.setQueryTimeout(10);
            statement.executeUpdate(sql);

            // Creating the table
            sql = "CREATE TABLE IF NOT EXISTS photos (\n"
                    + "photoid integer PRIMARY KEY, \n"
                    + "userid integer,"
                    + "filedata blob);";
            statement.executeUpdate(sql);

            statement.executeUpdate(sql);
        }
        catch (SQLException e) {
            System.out.println("Caught SQL exception - " + e);
        }
    }

    public void closeConnection() {
        try {
            if (this.conn != null) {
                this.conn.close();
            }
        }
        catch (SQLException e) {
            System.out.println("Exception closing database");
        }
    }

    private void tutorialVersion(String dbname) {

        Connection conn = null;

        try {

            // Establishing connection
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbname);
            // Building statement
            System.out.println("Here's our conn: " + conn);

            DatabaseMetaData mdata = conn.getMetaData();
            System.out.println("Database metadata: " + mdata.getDriverName());


            String sql = "DROP TABLE IF EXISTS users";
            Statement statement = conn.createStatement();
            statement.setQueryTimeout(10);
            statement.executeUpdate(sql);

            // Creating the table
            sql = "CREATE TABLE IF NOT EXISTS users (\n"
                    + "userid integer PRIMARY KEY, \n"
                    + "name string);";
            statement.executeUpdate(sql);

            // Inserting into the table
            sql = "INSERT INTO users (name)"
                    + "VALUES ('TANJA'), ('MIGUEL')";

            statement.executeUpdate(sql);

            // Retrieving newly minted user
            ResultSet rs = statement.executeQuery("SELECT * FROM users");
            while (rs.next()) {
                System.out.println("userid: " +rs.getInt("userid"));
                System.out.println("name: " + rs.getString("name"));
            }

        }
        catch (SQLException e) {
            System.out.println("Caught SQL exception - " + e);
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (SQLException e) {
                System.out.println("Exception closing database");
            }
        }
    }
}
