package dev.blind.hackupc.a2017.blindhelper.components;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import dev.blind.hackupc.a2017.blindhelper.controllers.TextToSpeechController;

/**
 * Created by LaQuay on 14/10/2017.
 */

public class SpeechTextView extends AppCompatTextView implements View.OnClickListener {
    public SpeechTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public SpeechTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SpeechTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Log.e("CustomTextView", "AUDIO: " + getText());
        TextToSpeechController.getInstance(getContext()).speak(getText(), TextToSpeech.QUEUE_FLUSH);
    }
}
