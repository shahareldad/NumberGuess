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

        Button easy = findViewById(R.id.easy);
        Button medium = findViewById(R.id.medium);
        Button hard = findViewById(R.id.hard);
        Button gameSettings = findViewById(R.id.gameSettings);

        easy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNewGameByLevel(100);
            }
        });

        medium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNewGameByLevel(1000);
            }
        });

        hard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNewGameByLevel(10000);
            }
        });

        LoadSettingsData();
        final boolean[] checked = new boolean[1];
        checked[0] = _settings.getShowGuessLimits();
        final SettingsData settings = new SettingsData();
        settings.setShowGuessLimits(_settings.getShowGuessLimits());
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
        _settings.setShowGuessLimits(true);
        Gson gson = new GsonBuilder().create();
        if (builder != null) {
            _settings = gson.fromJson(builder.toString(), new TypeToken<SettingsData>() {}.getType());
        }
    }

    private void startNewGameByLevel(int topBorder) {
        Intent i = new Intent(MainActivity.this, BoardActivity.class);
        i.putExtra(guessLimitsParamName, _settings.getShowGuessLimits());
        i.putExtra(topLimitParamName, topBorder);
        startActivity(i);
    }
}
