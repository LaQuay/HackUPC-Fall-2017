package dev.blind.hackupc.a2017.blindhelper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyImagesOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ImageClassification;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassifier;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;

import dev.blind.hackupc.a2017.blindhelper.components.SpeechTextView;
import dev.blind.hackupc.a2017.blindhelper.controllers.BackendController;
import dev.blind.hackupc.a2017.blindhelper.controllers.ImageController;
import dev.blind.hackupc.a2017.blindhelper.utils.ImageUtils;

/**
 * Created by LaQuay on 14/10/2017.
 */

public class LabelObjectActivity extends AppCompatActivity implements BackendController.ResponseServerCallback {
    private static final String TAG = LabelObjectActivity.class.getSimpleName();
    private static final int CAMERA_PHOTO_CODE = 100;
    private static final int PHOTO_SCALED_WIDTH = 854;
    private static final int PHOTO_SCALED_HEIGHT = 480;
    private ImageView preview;
    private SpeechTextView detection1;
    private SpeechTextView detection2;
    private SpeechTextView detection3;
    private SpeechTextView detection4;
    private SpeechTextView detection5;
    private VisualRecognition vrClient;
    private Uri outputFileUri;
    private String cameraDirectory;
    private ImageController imageController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label_object);

        setUpElements();

        setUpFolderPhotos();

        // Initialize Visual Recognition client
        vrClient = new VisualRecognition(
                VisualRecognition.VERSION_DATE_2016_05_20,
                getString(R.string.watson_key)
        );

        imageController = new ImageController(this);

        // Take picture
        makePhoto();
    }

    private void setUpElements() {
        preview = findViewById(R.id.preview_image);
        detection1 = findViewById(R.id.detected_objects_1);
        detection2 = findViewById(R.id.detected_objects_2);
        detection3 = findViewById(R.id.detected_objects_3);
        detection4 = findViewById(R.id.detected_objects_4);
        detection5 = findViewById(R.id.detected_objects_5);
    }

    public void setUpFolderPhotos() {
        cameraDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/hackupc2017f/";
        File newdir = new File(cameraDirectory);
        if (!newdir.isDirectory()) {
            if (!newdir.mkdirs()) {
                Toast.makeText(this, "Problem creating IMAGES FOLDER", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void makePhoto() {
        outputFileUri = imageController.getUriCameraPhoto(cameraDirectory);
        if (outputFileUri != null) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

            startActivityForResult(cameraIntent, CAMERA_PHOTO_CODE);
        }
    }

    public void uploadImageToAPI(String uriToUpload) {
        Toast.makeText(this, "Uploading photo", Toast.LENGTH_SHORT).show();

        BackendController.addImage(uriToUpload, this);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_PHOTO_CODE) {
                try {
                    Bitmap selectedImage = readImageFromResources(outputFileUri);

                    Bitmap scaledBitmap = ImageUtils.scaleBitmap(selectedImage, PHOTO_SCALED_WIDTH, PHOTO_SCALED_HEIGHT);
                    ImageUtils.compressBitmap(new File(outputFileUri.getPath()), scaledBitmap);

                    preview.setImageBitmap(scaledBitmap);

                    uploadImageToAPI(outputFileUri.getPath());
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap readImageFromResources(Uri uriToRead) throws IOException {
        return MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriToRead);
    }

    private void setVisualRecognitionData(final String imageURL) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                VisualClassification response =
                        vrClient.classify(
                                new ClassifyImagesOptions.Builder()
                                        .url(imageURL)
                                        .build()
                        ).execute();

                ImageClassification classification = response.getImages().get(0);

                final VisualClassifier classifier = classification.getClassifiers().get(0);

                Collections.sort(classifier.getClasses(), new CustomComparator());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (classifier.getClasses().size() > 4) {
                            detection5.setText(classifier.getClasses().get(4).getName()
                                    + " - " + classifier.getClasses().get(4).getScore());
                        }
                        if (classifier.getClasses().size() > 3) {
                            detection4.setText(classifier.getClasses().get(3).getName()
                                    + " - " + classifier.getClasses().get(3).getScore());
                        }
                        if (classifier.getClasses().size() > 2) {
                            detection3.setText(classifier.getClasses().get(2).getName()
                                    + " - " + classifier.getClasses().get(2).getScore());
                        }
                        if (classifier.getClasses().size() > 1) {
                            detection2.setText(classifier.getClasses().get(1).getName()
                                    + " - " + classifier.getClasses().get(1).getScore());
                        }
                        if (classifier.getClasses().size() > 0) {
                            detection1.setText(classifier.getClasses().get(0).getName()
                                    + " - " + classifier.getClasses().get(0).getScore());
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onResponseServer(String petition, String message) {
        String url = BackendController.IMAGE_URL + "/" + message;
        Log.e(TAG, "URL to VisualRecognition: " + url);
        setVisualRecognitionData(url);
    }

    private class CustomComparator implements Comparator<VisualClassifier.VisualClass> {
        @Override
        public int compare(VisualClassifier.VisualClass o1, VisualClassifier.VisualClass o2) {
            return o2.getScore().compareTo(o1.getScore());
        }
    }
}
