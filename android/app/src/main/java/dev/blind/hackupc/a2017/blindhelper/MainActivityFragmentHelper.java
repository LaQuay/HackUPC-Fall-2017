package dev.blind.hackupc.a2017.blindhelper;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
    }

    private void setUpListeners() {
        buttonPhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PhotoAnswerActivity.class);
                startActivity(intent);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }
}
