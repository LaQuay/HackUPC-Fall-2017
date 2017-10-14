package dev.blind.hackupc.a2017.blindhelper;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import dev.blind.hackupc.a2017.blindhelper.controllers.OCRController;
import dev.blind.hackupc.a2017.blindhelper.utils.ImageUtils;

public class MainActivityFragment extends Fragment implements BackendController.ResponseServerCallback, dev.blind.hackupc.a2017.blindhelper.controllers.OCRController.OCRResolvedCallback {
    private static final String TAG = MainActivityFragment.class.getSimpleName();
    private static final int CAMERA_PHOTO_CODE_EYES = 100;
    private static final int CAMERA_PHOTO_CODE_READ = 101;
    private static final int PHOTO_SCALED_WIDTH = 854;
    private static final int PHOTO_SCALED_HEIGHT = 480;
    private static final String OCR_LANGUAGE = "eng"; // En ISO 639-2/B
    private View rootview;
    private Button buttonWhereIAm;
    private Button buttonAroundMe;
    private Button buttonBeMyEyes;
    private Button buttonReadForMe;

    private Uri outputFileUri;
    private String cameraDirectory;
    private ImageController imageController;
    private OCRController OCRController;

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
        OCRController = new OCRController(getContext(), OCR_LANGUAGE);

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
                Intent intent = new Intent(getActivity(), WhereIAmActivity.class);
                startActivity(intent);
            }
        });

        buttonAroundMe.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AroundMeActivity.class);
                startActivity(intent);
            }
        });

        buttonBeMyEyes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                makePhotoCamera(CAMERA_PHOTO_CODE_EYES);
            }
        });

        buttonReadForMe.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                makePhotoCamera(CAMERA_PHOTO_CODE_READ);
            }
        });
    }

    public void makePhotoCamera(int intentForResult) {
        outputFileUri = imageController.getUriCameraPhoto(cameraDirectory);
        if (outputFileUri != null) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

            startActivityForResult(cameraIntent, intentForResult);
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
                        uploadQuestionToAPI(outputFileUri.getPath());
                    } else {
                        OCRController.imageOCRRequest("http://6666df63.ngrok.io/question/59e22b9fb55e9f388c124f48", this);
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

    public void uploadQuestionToAPI(String uriToUpload) {
        Toast.makeText(getActivity(), "Uploading photo", Toast.LENGTH_SHORT).show();

        BackendController.addQuestion("usuario123", "Pregunta de prueba", uriToUpload, this);
    }

    private void createModal(String value) {
        final AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Results")
                .setMessage(value)
                .setPositiveButton("Accept", null)
                .setNegativeButton("Repeat", null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.e(TAG, "Aquí debería volver a sonar lo que hay dentro");
                    }
                });
            }
        });

        dialog.show();

        Log.e(TAG, "Aquí debería sonar lo que hay en value");
    }

    private Bitmap readImageFromResources(Uri uriToRead) throws IOException {
        return MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uriToRead);
    }

    @Override
    public void onResponseServer(String message) {
        Log.e(TAG, "RESPONSE FROM SERVER: " + message);
        // La idea podria ser aquí esperar en un bucle a que el servidor tuviera algun valor escrito
        // y entonces hacer el createModal con ese valor
    }

    @Override
    public void onImageOCRResolved(String message) {
        Log.e(TAG, "RESPONSE FROM OCR: " + message);
        createModal(message);
    }
}
