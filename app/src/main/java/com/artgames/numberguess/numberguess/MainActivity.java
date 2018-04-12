package com.artgames.numberguess.numberguess;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    public static String timeParamName = "timeSeconds";
    public static String soundsParamName = "sounds";
    public static String guessLimitsParamName = "guessLimits";
    public static String topLimitParamName = "topLimit";
    public static String SETTINGS_FILENAME = "NUMBER_GUESS_SETTINGS";
    public static AppCompatActivity _activity = null;
    private SettingsData _settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _activity = this;

        MobileAds.initialize(this, "ca-app-pub-8402023979328526~8946845838");

        AdView adViewTop = findViewById(R.id.adViewTop);
        AdRequest requestTop = new AdRequest.Builder().build();
        adViewTop.loadAd(requestTop);

        AdView adViewBottom = findViewById(R.id.adViewBottom);
        AdRequest requestBottom = new AdRequest.Builder().build();
        adViewBottom.loadAd(requestBottom);

        Button noTime = findViewById(R.id.noTime);
        Button seconds15 = findViewById(R.id.seconds15);
        Button seconds30 = findViewById(R.id.seconds30);
        Button seconds60 = findViewById(R.id.seconds60);
        Button gameSettings = findViewById(R.id.gameSettings);

        noTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNewGameByLevel(0);
            }
        });

        seconds15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNewGameByLevel(15);
            }
        });

        seconds30.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNewGameByLevel(30);
            }
        });

        seconds60.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNewGameByLevel(60);
            }
        });

        LoadSettingsData();
        final boolean[] checked = new boolean[3];
        checked[0] = _settings.getShowGuessLimits();
        final SettingsData settings = new SettingsData();
        settings.setShowGuessLimits(_settings.getShowGuessLimits());
        settings.setTopLimit(_settings.getTopLimit());
        gameSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(_activity);
                builder.setTitle(R.string.settings);
                final String[] items = new String[]{
                        getString(R.string.showCurrentGuessLimits)
                };
                builder.setMultiChoiceItems(items, checked, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int indexSelected, boolean isChecked) {
                        UpdateSettingssData(indexSelected, isChecked, items, settings);
                    }
                });
                builder.setPositiveButton(R.string.okButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SaveSettingsData(settings);
                        _settings.setShowGuessLimits(settings.getShowGuessLimits());
                        _settings.setTopLimit(settings.getTopLimit());
                    }
                });
                builder.create().show();
            }
        });

    }

    private void SaveSettingsData(SettingsData settings) {
        Gson gson = new GsonBuilder().create();
        String result = gson.toJson(settings);

        FileOutputStream fos = null;
        try{
            fos = openFileOutput(MainActivity.SETTINGS_FILENAME, Context.MODE_PRIVATE);
            fos.write(result.getBytes());
            fos.close();
        }
        catch (FileNotFoundException ex){
        }
        catch (IOException ex){
        }
        finally {
            if (fos != null){
                try {
                    fos.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void UpdateSettingssData(int indexSelected, boolean isChecked, String[] items, SettingsData settings) {
        String selection = Arrays.asList(items).get(indexSelected);
        if (selection.equals(getString(R.string.showCurrentGuessLimits))){
            settings.setShowGuessLimits(isChecked);
        }
        if (selection.equals(getString(R.string.topLimitSettingsText))){
            settings.setTopLimit(100); // TODO: make this dynamic
        }
    }

    private void LoadSettingsData() {
        BufferedReader br = null;
        StringBuilder builder = null;
        InputStream stream = null;

        try{
            stream = openFileInput(MainActivity.SETTINGS_FILENAME);
            br = new BufferedReader(new InputStreamReader(stream));
            builder = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null){
                builder.append(line);
            }
            stream.close();
        }
        catch (IOException ex){
        }
        finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        _settings = new SettingsData();
        _settings.setShowGuessLimits(false);
        _settings.setTopLimit(100);
        Gson gson = new GsonBuilder().create();
        if (builder != null) {
            _settings = gson.fromJson(builder.toString(), new TypeToken<SettingsData>() {}.getType());
        }
    }

    private void startNewGameByLevel(int timeSeconds) {
        Intent i = new Intent(MainActivity.this, BoardActivity.class);
        i.putExtra(timeParamName, timeSeconds);
        i.putExtra(guessLimitsParamName, _settings.getShowGuessLimits());
        i.putExtra(topLimitParamName, _settings.getTopLimit());
        startActivity(i);
    }
}
