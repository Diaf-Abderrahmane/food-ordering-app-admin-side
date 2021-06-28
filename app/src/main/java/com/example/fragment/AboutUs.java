package com.example.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
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
import androidx.cardview.widget.CardView;

import de.hdodenhof.circleimageview.CircleImageView;

public class AboutUs extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;
    private ImageView back;
    private Uri imageUri;
    private ProgressBar progressBar ;
    private CircleImageView restaurantlogo;
    private CardView phone,instagramAccount,facebookPage,email,restaurantName,workTime;
    private String logoUrl,StrEmail,StrFP,StrIA,StrPH,StrRN,fromDay,toDay,fromTime,toTime;
    DatabaseReference firebaseDatabase= FirebaseDatabase.getInstance().getReference().child("About_Us");
    DatabaseReference mImageRef = firebaseDatabase.child("LogoUrl");
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance() ;
    StorageReference storageReference =firebaseStorage.getReference().child("Settings/");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        email = (CardView) findViewById(R.id.EmailOfContact);
        restaurantName = (CardView) findViewById(R.id.RestaurantName);
        facebookPage = (CardView) findViewById(R.id.Facebook);
        instagramAccount = (CardView) findViewById(R.id.Instagram);
        phone = (CardView) findViewById(R.id.Phone);
        workTime = (CardView) findViewById(R.id.WorkTime);
        restaurantlogo = (CircleImageView) findViewById(R.id.Logo);
        back = (ImageView)findViewById(R.id.back);
        progressBar = (ProgressBar) findViewById(R.id.LogoProgress);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(AboutUs.this, MainActivity.class);
                intent.putExtra("key", 4);
                startActivity(intent);


            }
        });
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
                for (DataSnapshot snap:snapshot.getChildren()){
                    switch (snap.getKey()){
                        case "Email":
                            StrEmail =snap.getValue().toString();
                            break;
                        case "FacebookPage":
                            StrFP =snap.getValue().toString();
                            break;
                        case "InstagramAccount":
                            StrIA =snap.getValue().toString();
                            break;
                        case "Phone":
                            StrPH =snap.getValue().toString();
                            break;
                        case "RestaurantName":
                            StrRN =snap.getValue().toString();
                            break;

                    }

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

                showDialog(AboutUs.this,"Email Edit",StrEmail);
            }
        });
        facebookPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 showDialog(AboutUs.this,"FacebookPage Edit",StrFP);
                }
        });
        instagramAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(AboutUs.this,"InstagramAccount Edit",StrIA);
            }
        });
        restaurantName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(AboutUs.this,"RestaurantName Edit",StrRN);
            }
        });
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(AboutUs.this,"Phone Edit",StrPH);
            }
        });
        workTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(AboutUs.this,WorkTime.class);
                startActivity(intent);}
        });

    }
    public void showDialog(Activity activity, String msg, String hint){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
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
            case "Email Edit":
                newString.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                editIcon.setImageDrawable(getDrawable(R.drawable.ic_baseline_email1));
                break;
            case "FacebookPage Edit":
                newString.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
                editIcon.setImageDrawable(getDrawable(R.drawable.ic_facebook));
                break;
            case "InstagramAccount Edit":
                newString.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
                editIcon.setImageDrawable(getDrawable(R.drawable.ic_instagram));
                break;
            case "RestaurantName Edit":
                newString.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                editIcon.setImageDrawable(getDrawable(R.drawable.ic_baseline_person));
                break;
            case "Phone Edit":
                newString.setInputType(InputType.TYPE_CLASS_NUMBER);
                newString.setFilters(new InputFilter[] {new InputFilter.LengthFilter(10)});
                editIcon.setImageDrawable(getDrawable(R.drawable.ic_baseline_phone));
                break;
        }
        newEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (msg){
                    case "Email Edit":
                        String finalNewEmail = newString.getText().toString();
                        if(finalNewEmail.equals("")){
                            Toast.makeText(AboutUs.this, "Nothing is changed", Toast.LENGTH_SHORT).show();
                        }else {
                        firebaseDatabase.child("Email").setValue(finalNewEmail);
                        Toast.makeText(AboutUs.this, "Data is changed", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "FacebookPage Edit":
                        String finalNewFacebook = newString.getText().toString();
                        if(finalNewFacebook.equals("")){
                            Toast.makeText(AboutUs.this, "Nothing is changed", Toast.LENGTH_SHORT).show();
                        }else {
                        firebaseDatabase.child("FacebookPage").setValue(finalNewFacebook);
                            Toast.makeText(AboutUs.this, "Data is changed", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "InstagramAccount Edit":
                        String finalNewInstagramAccount = newString.getText().toString();
                        if(finalNewInstagramAccount.equals("")){
                            Toast.makeText(AboutUs.this, "Nothing is changed", Toast.LENGTH_SHORT).show();
                        }else {
                        firebaseDatabase.child("InstagramAccount").setValue(finalNewInstagramAccount);
                            Toast.makeText(AboutUs.this, "Data is changed", Toast.LENGTH_SHORT).show();
                        }

                        break;
                    case "RestaurantName Edit":
                        String finalNewRestaurantName = newString.getText().toString();
                        if(finalNewRestaurantName.equals("")){
                            Toast.makeText(AboutUs.this, "Nothing is changed", Toast.LENGTH_SHORT).show();
                        }else {
                        firebaseDatabase.child("RestaurantName").setValue(finalNewRestaurantName);
                            Toast.makeText(AboutUs.this, "Data is changed", Toast.LENGTH_SHORT).show();
                        }

                        break;
                    case "Phone Edit":
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