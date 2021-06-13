package com.example.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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


public class Settings extends Fragment {
    private static final int PICK_IMAGE = 1;
    private LinearLayout sendNotification;
    private LinearLayout emailEdit;
    private LinearLayout passwordEdit;
    private LinearLayout aboutUs;
    private LinearLayout signOut;
    private ImageView restaurantImg;
    Uri imageUri;
    Intent intent;
    ProgressBar progressBar ;
    DatabaseReference firebaseDatabase= FirebaseDatabase.getInstance().getReference().child("Settings").child("RestaurantImgUrl");
    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Settings/");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.settings, container, false);
        sendNotification =(LinearLayout) view.findViewById(R.id.SendNotification);
        emailEdit =(LinearLayout) view.findViewById(R.id.EmailEdit);
        passwordEdit =(LinearLayout) view.findViewById(R.id.PasswordEdit);
        aboutUs =(LinearLayout) view.findViewById(R.id.AboutUs);
        signOut =(LinearLayout) view.findViewById(R.id.SignOut);
        restaurantImg =(ImageView) view.findViewById(R.id.RestaurantImg);
        progressBar = (ProgressBar) view.findViewById(R.id.LogoProgress);
        progressBar.getIndeterminateDrawable().setColorFilter(0xEF7505, android.graphics.PorterDuff.Mode.MULTIPLY);

        firebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String link =snapshot.getValue(String.class);
                Picasso.get().load(link).into(restaurantImg
                        , new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
            };
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //-------------
        restaurantImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImg();
            }
        });
        sendNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getActivity(),Notification.class);
                startActivity(intent);
            }
        });
        emailEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getActivity(),EmailEdit.class);
                startActivity(intent);

            }
        });
        passwordEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getActivity(),PasswordEdit.class);
                startActivity(intent);

            }
        });
        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getActivity(),AboutUs.class);
                startActivity(intent);

            }
        });
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getActivity(),Login.class);
                startActivity(intent);
                getActivity().finish();
            }
        });



        return view;
    }
    private void chooseImg() {
        Intent intent =new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE && resultCode !=0 && data != null){
            imageUri=data.getData();
            restaurantImg.setImageURI(imageUri);
            UploadImg();
        }
    }

    private void UploadImg() {
        final ProgressDialog PD = new ProgressDialog(getActivity());
        PD.setTitle("UPLoading LOGO ...");
        PD.show();
        StorageReference riversRef = storageReference.child("RestaurantImg");
        riversRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        PD.dismiss();
                        Toast.makeText(getActivity(), "Restaurant's Img is Uploaded", Toast.LENGTH_SHORT).show();
                        riversRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                String ImgUrl = task.getResult().toString();
                                firebaseDatabase.setValue(String.valueOf(ImgUrl));
                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        PD.dismiss();
                        Toast.makeText(getActivity(), "fail", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progressPercent = (100.00*snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        PD.setMessage("Progress : " +(int) progressPercent+"%");
                    }
                });
    }

}