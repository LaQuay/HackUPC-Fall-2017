package dev.blind.hackupc.a2017.blindhelper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.io.InputStream;

import dev.blind.hackupc.a2017.blindhelper.controllers.ImageController;
import dev.blind.hackupc.a2017.blindhelper.controllers.TessOCRController;
import dev.blind.hackupc.a2017.blindhelper.utils.UriUtils;

public class MainActivityFragment extends Fragment {
    private static final String TAG = MainActivityFragment.class.getSimpleName();
    private static final int GALLERY_PHOTO_CODE = 100;
    private static final int CAMERA_PHOTO_CODE = 101;
    private View rootview;
    private Button buttonCamera;
    private Button buttonGallery;
    private Button buttonKeyboard;
    private Button buttonMicrophone;

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
        buttonCamera = rootview.findViewById(R.id.main_fragment_camera_button);
        buttonGallery = rootview.findViewById(R.id.main_fragment_gallery_button);
        buttonKeyboard = rootview.findViewById(R.id.main_fragment_keyboard_button);
        buttonMicrophone = rootview.findViewById(R.id.main_fragment_microphone_button);
    }

    private void setUpListeners() {
        buttonCamera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                makePhotoCamera();
            }
        });

        buttonGallery.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openImageChooser();
            }
        });

        buttonKeyboard.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /*Fragment fragment = KeyboardSearchFragment.newInstance();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.main_container, fragment, KeyboardSearchFragment.TAG);
                ft.addToBackStack(null);
                ft.commit();*/
            }
        });

        buttonMicrophone.setOnClickListener(new View.OnClickListener() {
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

            startActivityForResult(cameraIntent, CAMERA_PHOTO_CODE);
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

    private void openImageChooser() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_PHOTO_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY_PHOTO_CODE) {
                try {
                    Uri imageUri = data.getData();
                    String realUri = UriUtils.getRealPathFromUri(imageUri, getContext());
                    InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);

                    Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                    //Bitmap scaledBitmap = ImageUtils.scaleBitmap(selectedImage, PHOTO_SCALED_WIDTH, PHOTO_SCALED_HEIGHT);
                    //ImageUtils.compressBitmap(new File(realUri), scaledBitmap);
                    //uploadImageToAPI(realUri);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
                }
            } else if (requestCode == CAMERA_PHOTO_CODE) {
                try {
                    Bitmap selectedImage = readImageFromResources(outputFileUri);

                    //Bitmap scaledBitmap = ImageUtils.scaleBitmap(selectedImage, PHOTO_SCALED_WIDTH, PHOTO_SCALED_HEIGHT);
                    //ImageUtils.compressBitmap(new File(outputFileUri.getPath()), scaledBitmap);
                    //uploadImageToAPI(outputFileUri.getPath());

                    String ocrResult = tessOCRController.getOCRResult(selectedImage);
                    Log.e(TAG, "OCR Result");
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            Toast.makeText(getActivity(), "You haven't picked Image", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap readImageFromResources(Uri uriToRead) throws IOException {
        return MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uriToRead);
    }
}
