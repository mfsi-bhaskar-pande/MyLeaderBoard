package com.mfsi.myleaderboard;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.Players;
import com.google.android.gms.games.leaderboard.LeaderboardScore;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.games.leaderboard.Leaderboards;

/**
 * Created by Bhaskar Pande on 2/20/2017.
 */
public class GamePresenter implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ResultCallback<Leaderboards.LoadPlayerScoreResult> {

    private GoogleApiClient mGoogleApiClient;
    private IGameView mGameView;
    private boolean mIsGameAutoLogIn = true;
    private boolean mResolvingConnectionFailure = false;
    private String mLeaderBoardId;
    private long mLastScore;
    private static final int GAMESAPI_SIGN_IN = 100;
    private static final int LEADERBOARD_REQUEST = 101;

    private static GamePresenter sGamePresenter;
    private boolean mSignInClicked;

    private GamePresenter() {



    }

    public static GamePresenter getInstance(){

        if(sGamePresenter == null){
         sGamePresenter = new GamePresenter();
        }
        return sGamePresenter;
    }




    @Override
    public void onResult(@NonNull Leaderboards.LoadPlayerScoreResult loadPlayerScoreResult) {

        if(loadPlayerScoreResult!= null && loadPlayerScoreResult.getStatus().getStatusCode() == GamesStatusCodes.STATUS_OK) {
            LeaderboardScore leaderBoard = loadPlayerScoreResult.getScore();
            if(leaderBoard != null) {
                String score = leaderBoard.getDisplayScore();
                mLastScore = leaderBoard.getRawScore();
                mGameView.displayCurrentScore(score);
            }
        }else{

        }

    }

    private boolean isConnected() {
        return mGoogleApiClient != null && mGoogleApiClient.isConnected();
    }

    public void setGameView(IGameView gameView) {
        mGameView = gameView;
    }

    /***
     * Called when a Game View is Initialized
     *
     * @param context the host Context
     */
    public void onGameViewInitialized(Context context) {

        initializeGoogleApiClient(context);
        int leaderBoardId = mGameView.getLeaderBoardId();
        initializeGameId(context,leaderBoardId);


    }

    private void initializeGameId(Context context, int leaderBoardId) {
        mLeaderBoardId = context.getResources().getString(leaderBoardId);
    }

    /***
     * CalledTo Add Score
     *
     * @param score
     */
    public void addScore(int score) {

        if (isConnected()) {
            Log.i("TAG", "LEADER BOARD SCORE: " + score);
            mLastScore += score;
            Games.Leaderboards.submitScore(mGoogleApiClient, mLeaderBoardId, mLastScore);
            mGameView.displayCurrentScore(mLastScore);
        } else {
            mGameView.playerNotConnected();
        }

    }

    public void signInClicked() {
        mSignInClicked = true;
        mGoogleApiClient.connect();
    }

    public void signOutClicked() {
        if (isConnected()) {
            Games.signOut(mGoogleApiClient);
        } else {
            mGameView.playerNotConnected();
        }
    }


    private void initializeGoogleApiClient(Context context) {

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();

    }

    public void gameViewReady() {
        mGoogleApiClient.connect();
    }

    public void gameViewClosed() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Log.i("TAG", "CONNECTION CREATED: " + bundle);

        boolean success = fetchCurrentPlayer();
        if(success) {
            loadLeaderBoardScores();
        }

    }

    private boolean fetchCurrentPlayer() {

        Player player = Games.Players.getCurrentPlayer(mGoogleApiClient);
        String displayName = player != null ? player.getDisplayName() : null;
        mGameView.setPlayerName(displayName);

        return !TextUtils.isEmpty(displayName);


    }

    @Override
    public void onConnectionSuspended(int i) {

        Log.i("TAG", "CONNECTION SUSPENDED: " + i);
        mGoogleApiClient.connect();

    }

    private void loadLeaderBoardScores() {

        if(isConnected()){
            PendingResult<Leaderboards.LoadPlayerScoreResult> results = Games.Leaderboards.loadCurrentPlayerLeaderboardScore(mGoogleApiClient,
                    mLeaderBoardId, LeaderboardVariant.TIME_SPAN_ALL_TIME, LeaderboardVariant.COLLECTION_SOCIAL);
            if(results != null){
                results.setResultCallback(this);
            }
        }
    }


    public void onConnectionResolutionResponse(Activity hostActivity, int requestCode, int resultCode) {

        if (requestCode == GAMESAPI_SIGN_IN) {
            mResolvingConnectionFailure = false;
            mSignInClicked = false;
            if (resultCode == Activity.RESULT_OK) {
                mGoogleApiClient.connect();
            } else {
                BaseGameUtils.showActivityResultError(hostActivity,
                        requestCode, resultCode, R.string.signin_failure);
            }
        }
    }

    public void displayLeaderBoard() {

        if (isConnected()) {
            Activity activity = mGameView.getHostActivity();
            activity.startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient,
                    mLeaderBoardId), LEADERBOARD_REQUEST);
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.i("TAG", "CONNECTION FAILED: " + connectionResult);

        if (mResolvingConnectionFailure) {
            return;
        }


        if (mIsGameAutoLogIn || mSignInClicked) {

            mResolvingConnectionFailure = true;

            Activity activity = mGameView.getHostActivity();
            boolean isResolutionAttemptInitiated = BaseGameUtils.
                    resolveConnectionFailure(activity, mGoogleApiClient, connectionResult, GAMESAPI_SIGN_IN,
                            R.string.connection_failure);

            if (!isResolutionAttemptInitiated) {
                mResolvingConnectionFailure = false;
            }
        }
    }

    public void onGooglePlayResponse(Activity hostActivity, int requestCode, int resultCode) {

        switch (requestCode) {
            case GAMESAPI_SIGN_IN:
                onConnectionResolutionResponse(hostActivity, requestCode, resultCode);
                break;
            case LEADERBOARD_REQUEST:
                break;
            default:
                break;
        }
    }

}
