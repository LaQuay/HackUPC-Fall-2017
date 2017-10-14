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

public class MainActivityFragment extends Fragment implements BackendController.ResponseServerCallback {
    private static final String TAG = MainActivityFragment.class.getSimpleName();
    private static final int CAMERA_PHOTO_CODE_EYES = 100;
    private static final int CAMERA_PHOTO_CODE_READ = 101;
    private static int PHOTO_SCALED_WIDTH = 854;
    private static int PHOTO_SCALED_HEIGHT = 480;
    private View rootview;
    private Button buttonWhereIAm;
    private Button buttonAroundMe;
    private Button buttonBeMyEyes;
    private Button buttonReadForMe;

    private Uri outputFileUri;
    private String cameraDirectory;
    private ImageController imageController;
    private TessOCRController tessOCRController;

    public static MainActivityFragment newInstance() {
        return new MainActivityFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_main_activity, container, false);

        setUpElements();
        setUpListeners();

        setUpFolderPhotos();

        imageController = new ImageController(getContext());
        tessOCRController = new TessOCRController(getContext(), "spa"); // En ISO 639-2/B

        return rootview;
    }

    private void setUpElements() {
        buttonWhereIAm = rootview.findViewById(R.id.main_fragment_where_i_am_button);
        buttonAroundMe = rootview.findViewById(R.id.main_fragment_around_me_button);
        buttonBeMyEyes = rootview.findViewById(R.id.main_fragment_be_my_eyes_button);
        buttonReadForMe = rootview.findViewById(R.id.main_fragment_read_for_me_button);
    }

    private void setUpListeners() {
        buttonWhereIAm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //makePhotoCamera();
                Intent intent = new Intent(getActivity(), WhereIAmActivity.class);
                startActivity(intent);
            }
        });

        buttonAroundMe.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //openImageChooser();
                Intent intent = new Intent(getActivity(), AroundMeActivity.class);
                startActivity(intent);
            }
        });

        buttonBeMyEyes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /*Fragment fragment = KeyboardSearchFragment.newInstance();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.main_container, fragment, KeyboardSearchFragment.TAG);
                ft.addToBackStack(null);
                ft.commit();*/
                makePhotoCamera();
            }
        });

        buttonReadForMe.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /*Fragment fragment = AudioRecognisonFragment.newInstance();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.main_container, fragment, AudioRecognisonFragment.TAG);
                ft.addToBackStack(null);
                ft.commit();*/
            }
        });
    }

    public void makePhotoCamera() {
        outputFileUri = imageController.getUriCameraPhoto(cameraDirectory);
        if (outputFileUri != null) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

            startActivityForResult(cameraIntent, CAMERA_PHOTO_CODE_EYES);
        }
    }

    public void setUpFolderPhotos() {
        cameraDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/hackupc2017f/";
        File newdir = new File(cameraDirectory);
        if (!newdir.isDirectory()) {
            if (!newdir.mkdirs()) {
                Toast.makeText(getContext(), "Problem creating IMAGES FOLDER", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_PHOTO_CODE_EYES || requestCode == CAMERA_PHOTO_CODE_READ) {
                try {
                    Bitmap selectedImage = readImageFromResources(outputFileUri);

                    Bitmap scaledBitmap = ImageUtils.scaleBitmap(selectedImage, PHOTO_SCALED_WIDTH, PHOTO_SCALED_HEIGHT);
                    ImageUtils.compressBitmap(new File(outputFileUri.getPath()), scaledBitmap);

                    if (requestCode == CAMERA_PHOTO_CODE_EYES) {
                        uploadImageToAPI(outputFileUri.getPath());
                    } else {
                        Log.e(TAG, "OCR Result");
                        String ocrResult = tessOCRController.getOCRResult(selectedImage);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            Toast.makeText(getActivity(), "You haven't picked Image", Toast.LENGTH_SHORT).show();
        }
    }

    public void uploadImageToAPI(String uriToUpload) {
        Toast.makeText(getActivity(), "Uploading photo", Toast.LENGTH_SHORT).show();

        BackendController.addQuestion("usuario123", "Pregunta de prueba", uriToUpload, this);
    }

    private Bitmap readImageFromResources(Uri uriToRead) throws IOException {
        return MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uriToRead);
    }

    @Override
    public void onResponseServer(String message) {
        Log.e(TAG, "RESPONSE FROM SERVER: " + message);
    }
}
