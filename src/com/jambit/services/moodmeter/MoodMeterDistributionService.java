package com.jambit.services.moodmeter;

import com.jambit.DatabaseConnection;
import com.jambit.domain.MoodEntry;
import com.jambit.services.Service;
import java.sql.SQLException;
import java.util.ArrayList;

public class MoodMeterDistributionService implements Service {

  private float time = 0;
  private static MoodMeterDistributionService instance = null;

  private MoodMeterDistributionService() {}

  public Object run() throws SQLException {
    DatabaseConnection db = DatabaseConnection.getInstance();
    return getDistribution(db.fetchMoodEntries(time));
  }

  public static MoodMeterDistributionService getInstance() {
    if (instance == null) {
      instance = new MoodMeterDistributionService();
    }
    return instance;
  }

  private int[] getDistribution(ArrayList<MoodEntry> moodEntries) {
    int[] distro = new int[10];
    for (MoodEntry moodEntry : moodEntries) {
      if (moodEntry.vote > 0 && moodEntry.vote <= 10) {
        distro[moodEntry.vote - 1]++;
      }
    }
    return distro;
  }

  public void setTime(float time) {
    this.time = time;
  }
}
