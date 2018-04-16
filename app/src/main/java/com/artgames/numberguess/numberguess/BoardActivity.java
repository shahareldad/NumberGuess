package com.artgames.numberguess.numberguess;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import java.util.Arrays;
import java.util.Random;

public class BoardActivity extends AppCompatActivity implements RewardedVideoAdListener {

    private static int _videoAdCounter = 5;
    private static boolean _gameOver = false;
    private static int _gameRounds = 0;
    private RewardedVideoAd _rewardedVideoAd;
    private boolean _showGuessLimits;
    private int _randomNumber;
    private int _topLimit;
    private TextView _bottomBorderView;
    private TextView _bottomBorderViewTitle;
    private TextView _topBorderView;
    private TextView _topBorderViewTitle;
    private EditText _userInput;
    private TextView _lastGuessResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        MobileAds.initialize(this, "ca-app-pub-8402023979328526~8946845838");

        AdView boardAdView = findViewById(R.id.boardAdView);
        AdRequest requestTop = new AdRequest.Builder().build();
        boardAdView.loadAd(requestTop);

        _rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        _rewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();

        _showGuessLimits = getIntent().getBooleanExtra(MainActivity.guessLimitsParamName, false);
        _topLimit = getIntent().getIntExtra(MainActivity.topLimitParamName, 100);

        _bottomBorderView = findViewById(R.id.bottomBorderView);
        _bottomBorderViewTitle = findViewById(R.id.bottomBorderViewTitle);
        _topBorderView = findViewById(R.id.topBorderView);
        _topBorderViewTitle = findViewById(R.id.topBorderViewTitle);
        _userInput = findViewById(R.id.userInput);
        _lastGuessResult = findViewById(R.id.lastGuessResult);
        Button _userInputSubmit = findViewById(R.id.userInputSubmit);
        _userInputSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userMadeGuess();
            }
        });

        if (_showGuessLimits){
            _topBorderView.setVisibility(View.VISIBLE);
            _topBorderViewTitle.setVisibility(View.VISIBLE);
            _bottomBorderView.setVisibility(View.VISIBLE);
            _bottomBorderViewTitle.setVisibility(View.VISIBLE);
        }else{
            _topBorderView.setVisibility(View.INVISIBLE);
            _topBorderViewTitle.setVisibility(View.INVISIBLE);
            _bottomBorderView.setVisibility(View.INVISIBLE);
            _bottomBorderViewTitle.setVisibility(View.INVISIBLE);
        }

        initNewGame();
    }

    private void userMadeGuess() {
        if (_gameOver){
            initNewGame();
            return;
        }

        _gameRounds++;
        String userInputString = String.valueOf(_userInput.getText());
        if (userInputString.equals(""))
            return;

        int userGuess = Integer.valueOf(userInputString);
        String userGuessString = String.valueOf(userGuess);
        if (userGuess > _randomNumber){
            showMessageToUser(getString(R.string.tooBig));
            _topBorderView.setText(userGuessString);
        }else{
            if (userGuess < _randomNumber){
                showMessageToUser(getString(R.string.tooSmall));
                _bottomBorderView.setText(userGuessString);
            }else{
                String goodGuess = getString(R.string.goodGuess).replace("[N]", String.valueOf(_gameRounds));
                showMessageToUser(goodGuess);
                gameOver(userGuessString, goodGuess);
            }
        }
        _userInput.setText("");
    }

    private void gameOver(String userGuessString, String goodGuessMsg) {
        _videoAdCounter--;
        showDialogToUser(goodGuessMsg);
        _topBorderView.setText(userGuessString);
        _bottomBorderView.setText(userGuessString);
        _gameOver = true;
    }

    private void showDialogToUser(String goodGuessMsg) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(goodGuessMsg);
        final String[] items = new String[]{
                getString(R.string.newGame),
                getString(R.string.backButton)
        };
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String selection = Arrays.asList(items).get(i);

                if (_videoAdCounter == 0){
                    _videoAdCounter = 5;
                    showVideoAd();
                }

                if (selection.equals(getString(R.string.newGame))){
                    initNewGame();
                }
                if (selection.equals(getString(R.string.backButton))){
                    finish();
                }
            }
        });

        builder.create().show();
    }

    private void showMessageToUser(String msgToUser) {
        _lastGuessResult.setText(msgToUser);
    }

    private void initNewGame() {
        _randomNumber = new Random().nextInt(_topLimit) + 1;
        _bottomBorderView.setText("0");
        _topBorderView.setText(String.valueOf(_topLimit));
        _userInput.setText("");
        _lastGuessResult.setText("");
        _gameRounds = 0;
        _gameOver = false;
    }

    @Override
    protected void onResume() {
        _rewardedVideoAd.resume(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        _rewardedVideoAd.pause(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        _rewardedVideoAd.destroy(this);
        super.onDestroy();
    }

    private void showVideoAd() {
        if (_rewardedVideoAd.isLoaded()) {
            _rewardedVideoAd.show();
        }else{
            loadRewardedVideoAd();
            if (_rewardedVideoAd.isLoaded()) {
                _rewardedVideoAd.show();
            }
        }
    }

    private void loadRewardedVideoAd() {
        // demo video ad: ca-app-pub-3940256099942544/5224354917
        // real video ad: ca-app-pub-8402023979328526/6016117080
        _rewardedVideoAd.loadAd("ca-app-pub-8402023979328526/6016117080",
                new AdRequest.Builder().build());
    }

    @Override
    public void onRewardedVideoAdLoaded() {

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        loadRewardedVideoAd();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        // some reward???
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        loadRewardedVideoAd();
    }
}
