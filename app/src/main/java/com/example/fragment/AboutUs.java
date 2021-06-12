package com.example.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class AboutUs extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;
    Uri imageUri;
    ProgressBar progressBar ;
    CircleImageView restaurantlogo;
    ImageView phone,instagramAccount,facebookPage,email,stringEditing;
    TextView restaurantName;
    String logoUrl,StrEmail,StrFP,StrIA,StrPH,StrRN;
    DatabaseReference firebaseDatabase= FirebaseDatabase.getInstance().getReference().child("About_Us");
    DatabaseReference mImageRef = firebaseDatabase.child("LogoUrl");
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance() ;
    StorageReference storageReference =firebaseStorage.getReference().child("Settings/");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        email = (ImageView) findViewById(R.id.EmailOfContact);
        restaurantName = (TextView) findViewById(R.id.RestaurantName);
        facebookPage = (ImageView) findViewById(R.id.Facebook);
        instagramAccount = (ImageView) findViewById(R.id.Instagram);
        phone = (ImageView) findViewById(R.id.Phone);
        restaurantlogo = (CircleImageView) findViewById(R.id.Logo);
        progressBar = (ProgressBar) findViewById(R.id.LogoProgress);
        mImageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String link = snapshot.getValue(String.class);
                Picasso.get().load(link).into(restaurantlogo, new Callback() {
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
        storageReference.getPath();
        firebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i=0;
                for (DataSnapshot snap:snapshot.getChildren()){
                    switch (i){
                        case 0:
                            StrEmail =snap.getValue().toString();
                            break;
                        case 1:
                            StrFP =snap.getValue().toString();
                            break;
                        case 2:
                            StrIA =snap.getValue().toString();
                            break;
                        case 4:
                            StrPH =snap.getValue().toString();
                            break;
                        case 5:
                            StrRN =snap.getValue().toString();
                            break;
                    }
                    i++;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        restaurantlogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImg();
                 }
        });
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDialog(AboutUs.this,"email",StrEmail);
            }
        });
        facebookPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 showDialog(AboutUs.this,"facebookPage",StrFP);
                }
        });
        instagramAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(AboutUs.this,"instagramAccount",StrIA);
            }
        });
        restaurantName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(AboutUs.this,"restaurantName",StrRN);
            }
        });
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(AboutUs.this,"phone",StrPH);
            }
        });
    }
    public void showDialog(Activity activity, String msg, String hint){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.aboutuspopup);
        EditText newString = (EditText) dialog.findViewById(R.id.NewString);
        TextView dialogTilte = (TextView) dialog.findViewById(R.id.DialogTilte);
        final TextView currentText = (TextView) dialog.findViewById(R.id.Currenttext);
        Button cancel = (Button) dialog.findViewById(R.id.Cancel);
        Button newEdit = (Button) dialog.findViewById(R.id.NewEdit);
        String nString =newString.getText().toString();
        ImageView editIcon = (ImageView) dialog.findViewById(R.id.EditIcon);
        dialogTilte.setText(msg);
        newString.setHint("New");
        currentText.setText(hint);
        switch (msg){
            case "email":
                newString.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                editIcon.setImageDrawable(getDrawable(R.drawable.ic_baseline_email1));
                break;
            case "facebookPage":
                newString.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
                editIcon.setImageDrawable(getDrawable(R.drawable.ic_facebook));
                break;
            case "instagramAccount":
                newString.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
                editIcon.setImageDrawable(getDrawable(R.drawable.ic_instagram));
                break;
            case "restaurantName":
                newString.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                editIcon.setImageDrawable(getDrawable(R.drawable.ic_baseline_person));
                break;
            case "phone":
                newString.setInputType(InputType.TYPE_CLASS_NUMBER);
                newString.setFilters(new InputFilter[] {new InputFilter.LengthFilter(10)});
                editIcon.setImageDrawable(getDrawable(R.drawable.ic_baseline_phone));
                break;
        }
        newEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (msg){
                    case "email":
                        String finalNewEmail = newString.getText().toString();
                        if(finalNewEmail.equals("")){
                            Toast.makeText(AboutUs.this, "Nothing is changed", Toast.LENGTH_SHORT).show();
                        }else {
                        firebaseDatabase.child("Email").setValue(finalNewEmail);
                        Toast.makeText(AboutUs.this, "Data is changed", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "facebookPage":
                        String finalNewFacebook = newString.getText().toString();
                        if(finalNewFacebook.equals("")){
                            Toast.makeText(AboutUs.this, "Nothing is changed", Toast.LENGTH_SHORT).show();
                        }else {
                        firebaseDatabase.child("FacebookPage").setValue(finalNewFacebook);
                            Toast.makeText(AboutUs.this, "Data is changed", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "instagramAccount":
                        String finalNewInstagramAccount = newString.getText().toString();
                        if(finalNewInstagramAccount.equals("")){
                            Toast.makeText(AboutUs.this, "Nothing is changed", Toast.LENGTH_SHORT).show();
                        }else {
                        firebaseDatabase.child("InstagramAccount").setValue(finalNewInstagramAccount);
                            Toast.makeText(AboutUs.this, "Data is changed", Toast.LENGTH_SHORT).show();
                        }

                        break;
                    case "restaurantName":
                        String finalNewRestaurantName = newString.getText().toString();
                        if(finalNewRestaurantName.equals("")){
                            Toast.makeText(AboutUs.this, "Nothing is changed", Toast.LENGTH_SHORT).show();
                        }else {
                        firebaseDatabase.child("RestaurantName").setValue(finalNewRestaurantName);
                            Toast.makeText(AboutUs.this, "Data is changed", Toast.LENGTH_SHORT).show();
                        }

                        break;
                    case "phone":
                        String finalNewphone = newString.getText().toString();
                        if(finalNewphone.equals("")){
                            Toast.makeText(AboutUs.this, "Nothing is changed", Toast.LENGTH_SHORT).show();
                        }else {
                        firebaseDatabase.child("Phone").setValue(finalNewphone);
                            Toast.makeText(AboutUs.this, "Data is changed", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
    private void chooseImg() {
        Intent intent =new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE && resultCode != 0 && data != null){
            imageUri=data.getData();
            restaurantlogo.setImageURI(imageUri);
            UploadImg();}
    }

    private void UploadImg() {
        final ProgressDialog PD = new ProgressDialog(this);
        PD.setTitle("UPLoading LOGO ...");
        PD.show();
        StorageReference riversRef = storageReference.child("Logo.png");
        riversRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        PD.dismiss();
                        Toast.makeText(AboutUs.this, "Logo is Uploaded", Toast.LENGTH_SHORT).show();
                        riversRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                String LogoUrl = task.getResult().toString();
                                firebaseDatabase.child("LogoUrl").setValue(String.valueOf(LogoUrl));
                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        PD.dismiss();
                        Toast.makeText(AboutUs.this, "fail", Toast.LENGTH_SHORT).show();

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