package com.mfsi.myleaderboard;

import android.app.Activity;
import android.app.job.JobInfo;
import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends GameActivity implements View.OnClickListener{


    private TextView mScoreBoard;
    private GamePresenter mGamePresenter;
    private Button mAddScore;
    private Button mShowLeaderBoard;
    private Button mSignOut;
    private Button mSignIn;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mGamePresenter = getGamePresenter();
    }

    @Override
    public int getContentView() {
        return R.layout.activity_main;
    }


    @Override
    public void playerNotConnected() {

    }

    @Override
    public void displayCurrentScore(String score) {
        mScoreBoard.setText(score);

    }

    @Override
    public void displayCurrentScore(long lastScore) {
        mScoreBoard.setText(""+lastScore);
    }

    @Override
    public int getLeaderBoardId() {
        return R.string.leaderboard_animetrivia;
    }

    @Override
    public Activity getHostActivity() {
        return this;
    }


    @Override
    public void setPlayerName(String displayName) {
        Toast.makeText(this, ""+displayName,Toast.LENGTH_SHORT).show();
    }



    @Override
    public void addListeners() {

        mShowLeaderBoard.setOnClickListener(this);
        mAddScore.setOnClickListener(this);
        mSignOut.setOnClickListener(this);
        mSignIn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.main_but_addScore:
                mGamePresenter.addScore(10);
                break;
            case R.id.main_but_showLeaderBoard:
                mGamePresenter.displayLeaderBoard();
                break;
            case R.id.main_but_signOut:
                mGamePresenter.signOutClicked();
                break;
            case R.id.main_but_signIn:
                mGamePresenter.signInClicked();
                break;
        }

    }

    @Override
    public void initWidgets() {

        mScoreBoard = (TextView)findViewById(R.id.main_txv_score);
        mAddScore = (Button)findViewById(R.id.main_but_addScore);
        mShowLeaderBoard = (Button)findViewById(R.id.main_but_showLeaderBoard);
        mSignOut = (Button)findViewById(R.id.main_but_signOut);
        mSignIn = (Button)findViewById(R.id.main_but_signIn);

    }
}
