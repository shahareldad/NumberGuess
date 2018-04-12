package com.artgames.numberguess.numberguess;

public class SettingsData {
    private boolean _showGuessLimits;
    private int _topLimit;

    public boolean getShowGuessLimits() {
        return _showGuessLimits;
    }

    public void setShowGuessLimits(boolean _showGuessLimits) {
        this._showGuessLimits = _showGuessLimits;
    }

    public int getTopLimit() {
        return _topLimit;
    }

    public void setTopLimit(int _topLimit) {
        this._topLimit = _topLimit;
    }
}
