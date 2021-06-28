package com.example.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class Settings extends Fragment {
    private static final int PICK_IMAGE = 1;
    private LinearLayout sendNotification;
    private LinearLayout emailEdit;
    private LinearLayout passwordEdit;
    private LinearLayout aboutUs;
    private LinearLayout signOut;
    private LinearLayout pointsEMoney;
    private Uri imageUri;
    private Intent intent;
    private CircleImageView restaurantlogo;
    private ProgressBar progressBar;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    DatabaseReference firebaseDatabase= FirebaseDatabase.getInstance().getReference().child("About_Us");
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings, container, false);

        sendNotification = (LinearLayout) view.findViewById(R.id.SendNotification);
        emailEdit = (LinearLayout) view.findViewById(R.id.EmailEdit);
        passwordEdit = (LinearLayout) view.findViewById(R.id.PasswordEdit);
        aboutUs = (LinearLayout) view.findViewById(R.id.AboutUs);
        signOut = (LinearLayout) view.findViewById(R.id.SignOut);
        pointsEMoney = (LinearLayout) view.findViewById(R.id.PointsEMoney);
        restaurantlogo = (CircleImageView) view.findViewById(R.id.Logo);
        progressBar = (ProgressBar) view.findViewById(R.id.LogoPg);
        progressBar.getIndeterminateDrawable().setColorFilter(0xEF7505, android.graphics.PorterDuff.Mode.MULTIPLY);
        firebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap:snapshot.getChildren()){
                    switch (snap.getKey()){
                        case "LogoUrl":
                            Picasso.get().load(snap.getValue(String.class)).into(restaurantlogo, new Callback() {
                                @Override
                                public void onSuccess() {
                                    progressBar.setVisibility(View.GONE);
                                }
                                @Override
                                public void onError(Exception e) {
                                }
                            });
                            break;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        //-------------
        sendNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getActivity(), Notification.class);
                startActivity(intent);
            }
        });
        emailEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getActivity(), EmailEdit.class);
                startActivity(intent);

            }
        });
        passwordEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getActivity(), PasswordEdit.class);
                startActivity(intent);

            }
        });
        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getActivity(), AboutUs.class);
                startActivity(intent);

            }
        });
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fAuth.signOut();
                intent = new Intent(getActivity(), Login.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        pointsEMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getActivity(), MoneyAndPoints.class);
                startActivity(intent);
                getActivity();
            }
        });


        return view;
    }

}