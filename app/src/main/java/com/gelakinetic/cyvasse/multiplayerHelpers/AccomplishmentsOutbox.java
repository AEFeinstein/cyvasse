package com.gelakinetic.cyvasse.multiplayerHelpers;

import android.content.Context;

public class AccomplishmentsOutbox {
  public boolean mPrimeAchievement = false;
  public boolean mHumbleAchievement = false;
  public boolean mLeetAchievement = false;
  public boolean mArrogantAchievement = false;
  public int mBoredSteps = 0;
  public int mEasyModeScore = -1;
  public int mHardModeScore = -1;

  public boolean isEmpty() {
      return !mPrimeAchievement && !mHumbleAchievement && !mLeetAchievement &&
              !mArrogantAchievement && mBoredSteps == 0 && mEasyModeScore < 0 &&
              mHardModeScore < 0;
  }

  public void saveLocal(Context ctx) {
      /* TODO: This is left as an exercise. To make it more difficult to cheat,
       * this data should be stored in an encrypted file! And remember not to
       * expose your encryption key (obfuscate it by building it from bits and
       * pieces and/or XORing with another string, for instance). */
  }

  public void loadLocal(Context ctx) {
      /* TODO: This is left as an exercise. Write code here that loads data
       * from the file you wrote in saveLocal(). */
  }
}