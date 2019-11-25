package com.jambit;

import com.jambit.domain.MoodEntry;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

public class DatabaseConnection {

  private static DatabaseConnection databaseInstance = null;
  private static String PROPERTY_PATH = ("config/app.properties");
  private static Properties databaseProps = new Properties();

  private Connection activeDatabaseConnection;

  private DatabaseConnection() {}

  public static DatabaseConnection getInstance() throws IOException, SQLException {
    if (databaseInstance == null) {
      databaseInstance = new DatabaseConnection();
      databaseInstance.connect();
    }
    return databaseInstance;
  }

  /** Connects to the database */
  private void connect() throws SQLException, IOException {
    databaseProps.load(new FileInputStream(PROPERTY_PATH));

    StringBuilder connectionLink =
        new StringBuilder()
            .append("jdbc:mysql://")
            .append(databaseProps.getProperty("database.hostIP"))
            .append(":")
            .append(databaseProps.getProperty("database.port"))
            .append("/")
            .append(databaseProps.getProperty("database.databaseName"));

    System.out.println("Connecting to \"" + connectionLink + "\"");

    activeDatabaseConnection =
        DriverManager.getConnection(
            connectionLink.toString(),
            databaseProps.getProperty("database.username"),
            databaseProps.getProperty("database.password"));

    System.out.println("Connection successful!!!");
  }

  /**
   * This function returns mood entries with a chosen timespan.
   *
   * @param hours Sets size of timestamp
   * @return An ArrayList with all entries in the chosen timespan
   */
  public ArrayList<MoodEntry> fetchMoodEntries(Float hours) throws SQLException {
    long currentTime = getCurrentTimeInSeconds();

    StringBuilder query =
        new StringBuilder()
            .append("SELECT * FROM ")
            .append(databaseProps.getProperty("table.moodMeter"))
            .append(" WHERE ")
            .append(databaseProps.getProperty("table.moodMeter.time"));

    if (hours != null) {
      query
          .append(" BETWEEN ")
          .append(currentTime - (hours * 60 * 60)) /*Calculate the hours into seconds*/
          .append(" AND ")
          .append(currentTime);
    }

    query
        .append(" ORDER BY ")
        .append(databaseProps.getProperty("table.moodMeter.id"))
        .append(" ASC");

    return moodMeterSQLQuery(query.toString());
  }

  /**
   * This function returns all mood entries form the database.
   *
   * @return An ArrayList with all entries.
   */
  public ArrayList<MoodEntry> fetchAllMoodEntries() throws SQLException {
    return fetchMoodEntries(null);
  }

  /**
   * Execute SQL command on the database for the moodMeter
   *
   * @param sql SQL command you would like to use
   * @return returns ArrayList with moodMeter entries
   */
  private ArrayList<MoodEntry> moodMeterSQLQuery(String sql) throws SQLException {
    ArrayList<MoodEntry> entries = new ArrayList<>();
    Statement st = activeDatabaseConnection.createStatement();
    ResultSet rs = st.executeQuery(sql);
    while (rs.next()) {
      entries.add(
          new MoodEntry(
              rs.getInt(databaseProps.getProperty("table.moodMeter.id")),
              rs.getInt(databaseProps.getProperty("table.moodMeter.entry")),
              rs.getLong(databaseProps.getProperty("table.moodMeter.time"))));
    }

    return entries;
  }

  /**
   * Write mood entries into the database
   *
   * @param input The object to use for the entry
   */
  public void writeMoodEntry(MoodEntry input) throws SQLException {
    StringBuilder query =
        new StringBuilder()
            .append("INSERT INTO ")
            .append(databaseProps.getProperty("table.moodMeter"))
            .append("(")
            .append(databaseProps.getProperty("table.moodMeter.entry"))
            .append(",")
            .append(databaseProps.getProperty("table.moodMeter.time"))
            .append(")")
            .append(" VALUES (")
            .append(input.vote)
            .append(",")
            .append(input.time)
            .append(")");

    Statement st = activeDatabaseConnection.createStatement();
    st.executeUpdate(query.toString());
  }

  /**
   * Get the Unix timestamp in seconds
   *
   * @return Unix timestamp in seconds
   */
  private long getCurrentTimeInSeconds() {
    return System.currentTimeMillis() / 1000L;
  }

  /**
   * Changes the path to the property file
   *
   * @param path the file path to set it to
   */
  public static void setPropertyPath(String path) throws IOException {
    PROPERTY_PATH = path;
    databaseProps.load(new FileInputStream(PROPERTY_PATH));
  }
}
