package dev.blind.hackupc.a2017.blindhelper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import dev.blind.hackupc.a2017.blindhelper.controllers.BackendController;
import dev.blind.hackupc.a2017.blindhelper.controllers.ImageController;
import dev.blind.hackupc.a2017.blindhelper.controllers.TessOCRController;
import dev.blind.hackupc.a2017.blindhelper.utils.ImageUtils;

public class MainActivityFragmentHelper extends Fragment {
    public static final String TAG = MainActivityFragmentHelper.class.getSimpleName();
    private static final int CAMERA_PHOTO_CODE_EYES = 100;
    private static final int CAMERA_PHOTO_CODE_READ = 101;
    private static int PHOTO_SCALED_WIDTH = 854;
    private static int PHOTO_SCALED_HEIGHT = 480;
    private View rootview;
    private Button buttonPhoto;
    private Button buttonVideo;

    private Uri outputFileUri;
    private String cameraDirectory;

    public static MainActivityFragmentHelper newInstance() {
        return new MainActivityFragmentHelper();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_main_helper_activity, container, false);

        setUpElements();
        setUpListeners();

        return rootview;
    }

    private void setUpElements() {
        buttonPhoto = rootview.findViewById(R.id.main_fragment_helper_photo);
        buttonVideo = rootview.findViewById(R.id.main_fragment_helper_video);
    }

    private void setUpListeners() {
        buttonPhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //makePhotoCamera();
                //Intent intent = new Intent(getActivity(), WhereIAmActivity.class);
                //startActivity(intent);
            }
        });

        buttonVideo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //openImageChooser();
                //Intent intent = new Intent(getActivity(), AroundMeActivity.class);
                //startActivity(intent);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }
}
