package dev.blind.hackupc.a2017.blindhelper.controllers;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import com.googlecode.tesseract.android.TessBaseAPI;

/**
 * Created by LaQuay on 14/10/2017.
 */

public class TessOCRController {
    private final TessBaseAPI mTess;

    // Data OCR is inside a subfolder on tesseract called 'tessdata'. Both folders, tesseract and
    // tessdata must be created starting in the root of the sd card, and training data should be inside
    public TessOCRController(Context context, String language) {
        mTess = new TessBaseAPI();
        String datapath = Environment.getExternalStorageDirectory().toString() + "/tesseract/";
        mTess.init(datapath, language);
    }

    public String getOCRResult(Bitmap bitmap) {
        mTess.setImage(bitmap);
        return mTess.getUTF8Text();
    }

    public void onDestroy() {
        if (mTess != null) mTess.end();
    }
}
