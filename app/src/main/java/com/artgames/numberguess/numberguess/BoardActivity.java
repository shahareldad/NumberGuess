package com.artgames.numberguess.numberguess;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import java.util.Random;

public class BoardActivity extends AppCompatActivity implements RewardedVideoAdListener {

    private static final String FILENAME_GAME_DATA = "number_guess_game_data";
    private static int _videoAdCounter = 5;
    private static boolean _gameOver = false;
    private static int _gameRounds = 0;
    private RewardedVideoAd _rewardedVideoAd;
    private int _timeSeconds;
    private boolean _showGuessLimits;
    private int _randomNumber;
    private int _topLimit;
    private TextView _bottomBorderView;
    private TextView _topBorderView;
    private EditText _userInput;
    private TextView _lastGuessResult;
    private Button _userInputSubmit;

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

        _timeSeconds = getIntent().getIntExtra(MainActivity.timeParamName, 0);
        _showGuessLimits = getIntent().getBooleanExtra(MainActivity.guessLimitsParamName, false);
        _topLimit = getIntent().getIntExtra(MainActivity.topLimitParamName, 100);

        _bottomBorderView = findViewById(R.id.bottomBorderView);
        _topBorderView = findViewById(R.id.topBorderView);
        _userInput = findViewById(R.id.userInput);
        _lastGuessResult = findViewById(R.id.lastGuessResult);
        _userInputSubmit = findViewById(R.id.userInputSubmit);
        _userInputSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userMadeGuess();
            }
        });

        if (_showGuessLimits){
            _topBorderView.setVisibility(View.VISIBLE);
            _bottomBorderView.setVisibility(View.VISIBLE);
        }else{
            _topBorderView.setVisibility(View.INVISIBLE);
            _bottomBorderView.setVisibility(View.INVISIBLE);
        }

        initNewGame();
    }

    private void userMadeGuess() {
        if (_gameOver){
            initNewGame();
            return;
        }

        _gameRounds++;
        int userGuess = Integer.valueOf(String.valueOf(_userInput.getText()));
        if (userGuess > _randomNumber){
            showMessageToUser(getString(R.string.tooBig));
        }else{
            if (userGuess < _randomNumber){
                showMessageToUser(getString(R.string.tooSmall));
            }else{
                String goodGuess = getString(R.string.goodGuess).replace("[N]", String.valueOf(_gameRounds));
                showMessageToUser(goodGuess);
                gameOver();
            }
        }
    }

    private void gameOver() {
        _userInputSubmit.setText(R.string.startOverSubmit);
        _gameOver = true;
        _videoAdCounter--;
        if (_videoAdCounter == 0){
            _videoAdCounter = 5;
            showVideoAd();
        }
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
        _userInputSubmit.setText(R.string.guessSubmit);
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
