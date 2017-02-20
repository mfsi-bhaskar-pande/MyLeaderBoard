package com.mfsi.myleaderboard;

import android.app.Activity;

/**
 * Created by Bhaskar Pande on 2/20/2017.
 */
public interface IGameView {
    void setPlayerName(String displayName);
    void configureMvp();
    void addListeners();
    void initWidgets();

    int getContentView();

    Activity getHostActivity();

    void playerNotConnected();

    void displayCurrentScore(String score);

    int getLeaderBoardId();

    void displayCurrentScore(long mLastScore);
}
