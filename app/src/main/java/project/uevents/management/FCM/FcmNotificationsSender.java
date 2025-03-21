package project.uevents.management.FCM;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FcmNotificationsSender {

    private String userFcmToken;
    private String title;
    private String body;
    private Context mContext;
    private final String postUrl = "https://fcm.googleapis.com/v1/projects/uems-4c42d/messages:send";

    public FcmNotificationsSender(String userFcmToken, String title, String body, Context mContext) {
        this.userFcmToken = userFcmToken;
        this.title = title;
        this.body = body;
        this.mContext = mContext;
    }

    public void SendNotifications() {
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        JSONObject mainObj = new JSONObject();

        try {
            // Create the message object
            JSONObject messageObject = new JSONObject();

            // Token of the target device
            messageObject.put("token", userFcmToken);

            // Notification content
            JSONObject notificationObject = new JSONObject();
            notificationObject.put("title", title);
            notificationObject.put("body", body);

            // Custom data payload
            JSONObject dataObject = new JSONObject();
            dataObject.put("key1", "value1");
            dataObject.put("key2", "value2");

            // Attach the notification and data to the message object
            messageObject.put("notification", notificationObject);
            messageObject.put("data", dataObject);

            // Attach the message to the root JSON
            mainObj.put("message", messageObject);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, postUrl, mainObj,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("Notification", "Response: " + response.toString());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (error.networkResponse != null) {
                                int statusCode = error.networkResponse.statusCode;
                                String errorBody = new String(error.networkResponse.data);
                                Log.e("Notification", "Error Status Code: " + statusCode);
                                Log.e("Notification", "Error Body: " + errorBody);
                            }
                            Log.e("Notification", "Volley Error", error);
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    AccessToken accessToken = new AccessToken();
                    String accessTokenString = null;
                    try {
                        accessTokenString = accessToken.getAccessToken();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Log.d("FCM_TOKEN", "Access Token: " + accessTokenString);

                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", "Bearer " + accessTokenString);
                    return headers;
                }
            };

            requestQueue.add(request);
            Log.d("FcmNotificationsSender", "Notification sent successfully");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
