package dev.blind.hackupc.a2017.blindhelper.controllers;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import dev.blind.hackupc.a2017.blindhelper.utils.MultipartUtils;

/**
 * Created by LaQuay on 14/10/2017.
 */

/*
    USERS
    GET http://8239eda1.ngrok.io/users
    GET http://8239eda1.ngrok.io/user/<username>
    POST http://8239eda1.ngrok.io/user
       body={username=<username>}

    QUESTIONS
    GET http://8239eda1.ngrok.io/questions
    GET http://8239eda1.ngrok.io/question/<questionid>
    POST http://8239eda1.ngrok.io/question/<username>
       body={text=<question>, image=<imagefile>}

    ANSWERS
    GET http://8239eda1.ngrok.io/answers/<questionId>
    POST http://8239eda1.ngrok.io/<username>/<questionId>
       body={text=<answer>} (edited)
 */

public class BackendController {
    public static final String TAG = BackendController.class.getSimpleName();
    private static final String BASE_URL = "http://6666df63.ngrok.io/";
    // Users
    private static final String GET_USERS_URL = BASE_URL + "users/";
    private static final String GET_USER_URL = BASE_URL + "user/";
    private static final String ADD_USER_URL = BASE_URL + "user/";
    // Questions
    private static final String GET_QUESTIONS_URL = BASE_URL + "questions/";
    private static final String GET_QUESTION_URL = BASE_URL + "question/";
    private static final String ADD_QUESTION_URL = BASE_URL + "question/";
    // Answers
    private static final String GET_ANSWER_URL = BASE_URL + "answers/";
    private static final String ADD_ANSWER_URL = BASE_URL;

    public static final String IMAGE_URL = BASE_URL + "image";

    public static void addQuestion(String userName, String questionToUpload, String imageURIToUpload, ResponseServerCallback responseServerCallback) {
        Log.e(TAG, "Sending to server -addQuestion-: " + userName + ", " + questionToUpload + ", " + imageURIToUpload);
        PhotoToServerAsyncTask photoToServerAsyncTask = new PhotoToServerAsyncTask();
        photoToServerAsyncTask.execute(userName, questionToUpload, imageURIToUpload, responseServerCallback);
    }

    public interface ResponseServerCallback {
        void onResponseServer(String message);
    }

    private static class PhotoToServerAsyncTask extends AsyncTask<Object, Void, String> {
        private ResponseServerCallback callback;

        @Override
        protected String doInBackground(Object... params) {
            try {
                String userName = params[0].toString();
                String question = params[1].toString();
                String uriName = params[2].toString();
                callback = (ResponseServerCallback) params[3];

                MultipartUtils multipart = new MultipartUtils(ADD_QUESTION_URL + userName, "UTF-8");
                multipart.addFormField("text", question);
                multipart.addFilePart("image", new File(uriName));

                return multipart.finish();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null && !response.isEmpty()) {
                try {
                    JSONObject jsonObj = new JSONObject(response);
                    callback.onResponseServer(jsonObj.getString("result"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
}
