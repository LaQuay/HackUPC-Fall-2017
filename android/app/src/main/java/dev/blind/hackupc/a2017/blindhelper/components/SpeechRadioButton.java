package dev.blind.hackupc.a2017.blindhelper.components;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import dev.blind.hackupc.a2017.blindhelper.controllers.TextToSpeechController;

/**
 * Created by LaQuay on 14/10/2017.
 */

public class SpeechRadioButton extends AppCompatRadioButton implements View.OnLongClickListener {
    public SpeechRadioButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public SpeechRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SpeechRadioButton(Context context) {
        super(context);
        init();
    }

    private void init() {
        setOnLongClickListener(this);
    }

    @Override
    public boolean onLongClick(View view) {
        Log.e("CustomButton", "AUDIO: " + getText());
        TextToSpeechController.getInstance(getContext()).speak(getText(), TextToSpeech.QUEUE_FLUSH);
        return true;
    }
}
