package server;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Provides link to MySQL database
 *
 * @author Matt Ellis
 */
public class Database {
    private Connection conn;

    public Database() {
        // Create a properties object to hold database url, username, and password
        Properties properties = new Properties();

        // Read properties object to get database connection info
        try {
            FileInputStream fis = new FileInputStream("src/server/db.properties");
            properties.load(fis);
            String url = properties.getProperty("url");
            String user = properties.getProperty("user");
            String password = properties.getProperty("password");

            try {
                // Initiate the connection with the database.
                conn = DriverManager.getConnection(url, user, password);
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("There was a problem connecting to the database.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("There was a problem opening the file \"db.properties\".");
        }
    }

    public ArrayList<String> query(String query) {
        ArrayList<String> queryResults = null;
        boolean first = true; // used to initialize queryResults if there is data received from the database

        try {
            // Create a JDBC Statement object for executing the query.
            Statement statement = conn.createStatement();

            // Execute query and store result set
            ResultSet rs = statement.executeQuery(query);

            // Get metadata about the query
            ResultSetMetaData rsmd = rs.getMetaData();

            // Get the number of columns.
            int numOfColumns = rsmd.getColumnCount();

            // Process the results set.
            while (rs.next()) {
                if (first) {
                    queryResults = new ArrayList<>();
                    first = false;
                }

                // Build a comma delimited String in the form "username,password" for the user found in table.
                String row = "";
                for (int col = 1; col <= numOfColumns; col++) // note: result set starts with index 1
                {
                    if (col != numOfColumns) {
                        row += rs.getString(col) + ",";
                    } else {
                        row += rs.getString(col);
                    }
                }
                queryResults.add(row);
            }

            return queryResults;

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("There was a problem accessing the database.");
            return null;
        }
    }

    public void executeDML(String dml) throws SQLException {
        // Create a JDBC Statement object for executing the SQL Data Modification Language statement.
        Statement statement = conn.createStatement();

        // Execute the dml statement
        statement.execute(dml);
    }

    public void closeDB() throws SQLException {
        conn.close();
    }

}

