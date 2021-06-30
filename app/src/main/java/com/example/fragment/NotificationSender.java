package com.example.fragment;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

public class NotificationSender {
    private String Id;
    private String Title;
    private String Text;
    private String ImgName = "";


    public static String Path;

    interface NotificationI {
        void isSent(Boolean ok);
    }

    public NotificationSender() {
    }

    public NotificationSender(String title, String text, String imgName) {
        this.Title = title;
        this.Text = text;
        this.ImgName = imgName;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public String getImgName() {
        return ImgName;
    }

    public void setImgName(String imgName) {
        ImgName = imgName;
    }


    public static void addNotification(NotificationSender notification, NotificationI notificationI) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Notification").push();
        notification.setId(ref.getKey());
        ref.setValue(notification).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    FirebaseStorage.getInstance().getReference().child("Notification/" + notification.getImgName())
                            .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {


                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUri = uri;
                            Path = downloadUri.toString();

                            notificationI.isSent(true);
                        }
                    });

                }
            }
        });
    }

    public static void sendNotification(Context context, NotificationSender notification, NotificationI notificationI) {
        addNotification(notification, new NotificationI() {
            @Override
            public void isSent(Boolean ok) {
                if (ok) {
                    RequestQueue mRequestQue = Volley.newRequestQueue(context);
                    final String URL = "https://fcm.googleapis.com/fcm/send";
                    JSONObject json = new JSONObject();
                    try {
                        json.put("to", "/topics/" + "Notification");
                        JSONObject notificationObj = new JSONObject();
                        notificationObj.put("title", notification.getTitle());
                        notificationObj.put("body", notification.getText());
                        notificationObj.put("image", Path);
                        json.put("notification", notificationObj);
                        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL,
                                json,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        notificationI.isSent(true);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        notificationI.isSent(false);
                                    }
                                }) {
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> header = new HashMap<>();
                                header.put("content-type", "application/json");
                                header.put("authorization", "key=AAAAYO1D3cg:APA91bFblmwrhf7dg-RjXhhY0yvfriavjq_nJMGoiseYTz5pYr1VX0fhT7R00bWvYb6_HWrq8Z0CHCHNkzvumncpXyuns518PXfJLayfv3Mh3veTEl_Q2g2icM3yFFqQOJiNHtPCk7zv\t\n");
                                return header;
                            }
                        };
                        mRequestQue.add(request);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else notificationI.isSent(false);
            }
        });
    }

}
