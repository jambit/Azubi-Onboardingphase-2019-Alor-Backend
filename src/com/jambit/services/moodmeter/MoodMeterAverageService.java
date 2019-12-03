package com.jambit.services.moodmeter;

import com.jambit.domain.MoodEntry;
import java.util.ArrayList;

public class MoodMeterAverageService {

  private float time = 0;
  private static MoodMeterAverageService instance = null;

  private MoodMeterAverageService() {}

  public Object run() {
    return calculateMoodAverage(new ArrayList<MoodEntry>());
  }

  public static MoodMeterAverageService getInstance() {
    if (instance == null) {
      instance = new MoodMeterAverageService();
    }
    return instance;
  }

  private float calculateMoodAverage(ArrayList<MoodEntry> moodEntries) {
    float entryCount = moodEntries.size();
    float entrySumm = 0;
    for (int i = 0; i < entryCount; i++) {
      entrySumm += moodEntries.get(i).getVote();
    }
    float average = (float) (Math.round((entrySumm / entryCount) * 100)) / 100;
    return average;
  }

  public void setTime(float time) {
    this.time = time;
  }
}