package com.example.soundplayer;

import android.util.Pair;
import android.view.View;
import android.widget.Button;

public class ButtonSound extends Pair<Button, Sound> {

    public ButtonSound(View button, Sound sound) {
        super((Button) button, sound);
    }

    public Button getButton(){
        return first;
    }

    public Sound getSound(){
        return second;
    }

    public String getName() {
        return getSound().getName();
    }
}
