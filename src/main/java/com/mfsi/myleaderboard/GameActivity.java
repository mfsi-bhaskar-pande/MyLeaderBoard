package com.mfsi.myleaderboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Bhaskar Pande on 2/20/2017.
 */
public abstract class GameActivity  extends AppCompatActivity implements IGameView {

    private GamePresenter mGamePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());

        initWidgets();
        configureMvp();
        addListeners();

        mGamePresenter.onGameViewInitialized(this);

    }


    public GamePresenter getGamePresenter(){
        return GamePresenter.getInstance();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mGamePresenter.onGooglePlayResponse(this, requestCode, resultCode);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGamePresenter.gameViewReady();

    }



    @Override
    protected void onStop() {
        super.onStop();
        mGamePresenter.gameViewClosed();
    }



    @Override
    public void configureMvp() {

        mGamePresenter = GamePresenter.getInstance();
        mGamePresenter.setGameView(this);
    }




}
