package com.manager.appbanhangonline.Service;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SendNoti {
    private final String userFCMToken;
    private final String title;
    private final String body;
    private final Context context;
    private final String postURL = "https://www.googleapis.com/auth/firebase.messaging";

    public SendNoti(String userFCMToken, String title, String body, Context context) {
        this.userFCMToken = userFCMToken;
        this.title = title;
        this.body = body;
        this.context = context;
    }

    public void SendNotification() {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JSONObject mainObj = new JSONObject();
        try {
            JSONObject messageObject = new JSONObject();
            JSONObject notificationObject = new JSONObject();
            notificationObject.put("title", title);
            notificationObject.put("body", body);
            messageObject.put("token", userFCMToken);
            messageObject.put("message", messageObject);
            mainObj.put("message", messageObject);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postURL, mainObj, response -> {

            }, volleyError -> {

            }) {
                @NonNull
                @Override
                public Map<String, String> getHeaders() {
                    AccessToken accessToken = new AccessToken();
                    String accessKey = accessToken.getAccessToken();
                    Map<String, String> header = new HashMap<>();
                    header.put("Content-Type", "application/json");
                    header.put("Authorization", "Bearer " + accessKey);
                    return header;
                }
            };
            requestQueue.add(request);
        } catch (JSONException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
