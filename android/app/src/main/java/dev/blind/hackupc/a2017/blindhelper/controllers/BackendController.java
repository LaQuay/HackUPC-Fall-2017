package dev.blind.hackupc.a2017.blindhelper.controllers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dev.blind.hackupc.a2017.blindhelper.model.Question;
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
    POST http://8239eda1.ngrok.io/answer/<username>/<questionId>
       body={text=<answer>} (edited)

   IMAGES
   GET http://8239eda1.ngrok.io/image/<imageId>
   POST http://8239eda1.ngrok.io/image
       body={image=<imagefile>}
 */

public class BackendController {
    public static final String TAG = BackendController.class.getSimpleName();
    private static final String BASE_URL = "http://4a3280a4.ngrok.io/";
    // Users
    public static final String GET_USERS_URL = BASE_URL + "users";
    public static final String GET_USER_URL = BASE_URL + "user/";
    public static final String ADD_USER_URL = BASE_URL + "user";
    // Questions
    public static final String GET_QUESTIONS_URL = BASE_URL + "questions";
    public static final String GET_QUESTION_URL = BASE_URL + "question/";
    public static final String ADD_QUESTION_URL = BASE_URL + "question/";
    // Answers
    public static final String GET_ANSWER_URL = BASE_URL + "answers/";
    public static final String ADD_ANSWER_URL = BASE_URL + "answer/";
    // Images
    public static final String GET_IMAGE_URL = BASE_URL + "image/";
    public static final String ADD_IMAGE_URL = BASE_URL + "image";

    public static void addQuestion(String userName, String questionToUpload, String imageURIToUpload, ResponseServerCallback responseServerCallback) {
        Log.e(TAG, "Sending to server -addQuestion-: " + userName + ", " + questionToUpload + ", " + imageURIToUpload);
        PhotoToServerAsyncTask photoToServerAsyncTask = new PhotoToServerAsyncTask();
        photoToServerAsyncTask.execute(ADD_QUESTION_URL, responseServerCallback, userName, questionToUpload, imageURIToUpload);
    }

    public static void addImage(String imageURIToUpload, ResponseServerCallback responseServerCallback) {
        Log.e(TAG, "Sending to server -addImage-: " + imageURIToUpload);
        PhotoToServerAsyncTask photoToServerAsyncTask = new PhotoToServerAsyncTask();
        photoToServerAsyncTask.execute(ADD_IMAGE_URL, responseServerCallback, null, null, imageURIToUpload);
    }

    public static void addAnswer(Context context, String username, String questionid, String answer, ResponseServerCallback responseServerCallback) {
        Log.e(TAG, "Sending to server -addAnswer-: " + username + ", " + questionid + ", " + answer);
        testPost(context, responseServerCallback, ADD_ANSWER_URL, username, questionid, answer);
    }

    private static void testPost(Context context, ResponseServerCallback responseServerCallback, String url, String username, String questionid, final String answer) {
        final StringRequest postRequest = new StringRequest(Request.Method.POST, url + username + "/" + questionid,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.getMessage());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("text", answer);

                return params;
            }
        };
        VolleyController.getInstance(context).addToQueue(postRequest);
    }

    public static void getAnswer(final Context context, String questionID, final ResponseServerCallback responseServerCallback) {
        JsonObjectRequest request = new JsonObjectRequest(GET_ANSWER_URL + questionID, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    Log.e(TAG, String.valueOf(jsonObject));
                    JSONObject jsonResult = jsonObject.getJSONObject("result");
                    JSONArray jsonArrayAnswers = jsonResult.getJSONArray("answers");

                    responseServerCallback.onResponseGetAnswer(jsonArrayAnswers);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleyController.getInstance(context).onConnectionFailed(volleyError.toString());
            }
        });
        VolleyController.getInstance(context).addToQueue(request);
    }

    public static ArrayList<Question> getQuestionsJSON(JSONObject jsonObject) {
        try {
            JSONArray results = jsonObject.getJSONArray("result");
            ArrayList<Question> questions = new ArrayList<>();
            for (int i = 0; i < results.length(); ++i) {
                Question question = new Question();
                JSONObject questionJSONObject = results.getJSONObject(i);
                Log.e("TT", questionJSONObject.toString());

                question.setQuestionText(questionJSONObject.getString("text"));
                question.setId(questionJSONObject.getString("_id"));

                questions.add(question);
            }
            return questions;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface ResponseServerCallback {
        void onResponseServer(String petition, String id, String text);

        void onResponseGetAnswer(JSONArray jsonArray);
    }

    private static class PhotoToServerAsyncTask extends AsyncTask<Object, Void, String> {
        private ResponseServerCallback callback;
        private String petitionTask;

        @Override
        protected String doInBackground(Object... params) {
            try {
                petitionTask = params[0].toString();
                callback = (ResponseServerCallback) params[1];
                switch (petitionTask) {
                    case (ADD_QUESTION_URL):
                        String userName = params[2].toString();
                        String question = params[3].toString();
                        String uriName = params[4].toString();

                        MultipartUtils multipart = new MultipartUtils(ADD_QUESTION_URL + userName, "UTF-8");
                        multipart.addFormField("text", question);
                        multipart.addFilePart("image", new File(uriName));

                        return multipart.finish();
                    case (ADD_IMAGE_URL):
                        uriName = params[4].toString();

                        multipart = new MultipartUtils(ADD_IMAGE_URL, "UTF-8");
                        multipart.addFilePart("image", new File(uriName));

                        return multipart.finish();
                    case (ADD_ANSWER_URL):
                        String username = params[2].toString();
                        String questionid = params[3].toString();
                        String answer = params[4].toString();

                        multipart = new MultipartUtils(ADD_ANSWER_URL + username + "/" + questionid, "UTF-8");
                        multipart.addFormField("text", answer);

                        return multipart.finish();
                }
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
                    JSONObject jsonResult = jsonObj.getJSONObject("result");
                    callback.onResponseServer(petitionTask, jsonResult.getString("_id"), jsonResult.getString("text"));
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
