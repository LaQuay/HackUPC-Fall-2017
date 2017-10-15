package dev.blind.hackupc.a2017.blindhelper.controllers;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import dev.blind.hackupc.a2017.blindhelper.R;

/**
 * Created by LaQuay on 14/10/2017.
 */

public class ImageController {
    private final String TAG = ImageController.class.getSimpleName();
    private final Context context;

    public ImageController(Context context) {
        this.context = context;
    }

    public Uri getUriCameraPhoto(String cameraDir) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            String file = cameraDir + System.currentTimeMillis() + ".jpg";
            File newfile = new File(file);
            try {
                if (!newfile.createNewFile()) {
                    Toast.makeText(context, "Problem creating IMAGE", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return getUrifromFile(newfile);
        }

        return null;
    }

    private Uri getUrifromFile(File newfile) {
        Uri uri;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            uri = FileProvider.getUriForFile(context, context.getString(R.string.file_provider_authority), newfile);
        } else {
            uri = Uri.fromFile(newfile);
        }

        return uri;
    }
}
