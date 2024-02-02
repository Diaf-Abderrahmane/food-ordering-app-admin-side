package com.example.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class Notification extends AppCompatActivity {

    String ImgName = "default.jpg";
    ProgressBar progressBar;
    Bitmap bitmap;
    ImageView Image;
    ImageView IconImage;
    ImageView IconUpload;
    ImageView IconClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        progressBar = findViewById(R.id.Icon_progressbar);
        Image = findViewById(R.id.Image);
        IconImage = findViewById(R.id.Icon_image);
        IconUpload = findViewById(R.id.Icon_upload);
        IconClear = findViewById(R.id.Icon_clear);
        EditText Title = findViewById(R.id.Title);
        EditText Text = findViewById(R.id.Text);
        Button Send = findViewById(R.id.Send);
        NotificationSender notificationSender = new NotificationSender();
        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtTitle = Title.getText().toString();
                String txtText = Text.getText().toString();
                if (!txtText.equals("") && !txtText.equals("") && !ImgName.equals("default.jpg")) {
                    notificationSender.setTitle(txtTitle);
                    notificationSender.setText(txtText);
                    notificationSender.setImgName(ImgName);
                    NotificationSender.sendNotification(Notification.this, notificationSender, new NotificationSender.NotificationI() {
                        @Override
                        public void isSent(Boolean ok) {
                            if (ok)
                                Toast.makeText(Notification.this, "sent", Toast.LENGTH_LONG).show();
                            else
                                Toast.makeText(Notification.this, "unsent", Toast.LENGTH_LONG).show();
                        }
                    });

                } else if (ImgName.equals("default.jpg")) {
                    Toast.makeText(Notification.this, "PLEASE UPLOAD AN IMAGE", Toast.LENGTH_LONG).show();

                }
            }
        });


        IconImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1);
            }
        });

        IconUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IconUpload.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                Random random = new Random();
                int r0 = random.nextInt(20);
                int r1 = random.nextInt(20) + 20;
                int r2 = random.nextInt(20) + 30;

                ImgName = "img_" + r0 + r1 + r2 + ".jpg";
                notificationSender.setImgName(ImgName);
                String imgPath = "Notification/" + ImgName;
                UploadTask uploadTask = FirebaseStorage.getInstance().getReference().child(imgPath).putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        IconUpload.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });

        IconClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImgName = "default.jpg";
                IconClear.setVisibility(View.INVISIBLE);
                Image.setImageDrawable(getDrawable(R.drawable.bg));
                IconImage.setVisibility(View.VISIBLE);
                IconUpload.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (resultCode == RESULT_OK && reqCode == 1) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap b = BitmapFactory.decodeStream(imageStream);
                bitmap = Bitmap.createScaledBitmap(b, 392, 248, false);

                Image.setImageBitmap(bitmap);
                IconImage.setVisibility(View.INVISIBLE);
                IconUpload.setVisibility(View.VISIBLE);
                IconClear.setVisibility(View.VISIBLE);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(Notification.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(Notification.this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }

}