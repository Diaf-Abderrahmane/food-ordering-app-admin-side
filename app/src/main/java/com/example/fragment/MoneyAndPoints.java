package com.example.fragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MoneyAndPoints extends AppCompatActivity {
    private TextView currentPoints;
    private TextView currentMoney;
    private EditText newPoints;
    private EditText newMoney;
    private Button cancel;
    private Button newEdit;
    DatabaseReference PointsAndMoney = FirebaseDatabase.getInstance().getReference().child("About_Us");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points_e_money);
        TextView currentPoints = (TextView)findViewById(R.id.CurrentPoints);
        TextView currentMoney = (TextView)findViewById(R.id.CurrentMoney);
        EditText newPoints = (EditText)findViewById(R.id.NewPoints);
        EditText newMoney =(EditText) findViewById(R.id.NewMoney);
        Button cancel = (Button) findViewById(R.id.Cancel);
        Button newEdit = (Button) findViewById(R.id.NewEdit);
        PointsAndMoney.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    switch (snap.getKey()) {
                        case "Points":
                            currentPoints.setText(snap.getValue().toString());
                            break;
                        case "Money":
                            currentMoney.setText(snap.getValue().toString());
                            break;
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        newEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(newPoints.getText().toString().isEmpty())){
                    PointsAndMoney.child("Points").setValue(Integer.parseInt(newPoints.getText().toString()));
                }
                if (!(newMoney.getText().toString().isEmpty())){
                    PointsAndMoney.child("Money").setValue(Integer.parseInt(newMoney.getText().toString()));
                }
                Intent intent =new Intent(MoneyAndPoints.this, MainActivity.class);
                intent.putExtra("key", 4);
                startActivity(intent);

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(MoneyAndPoints.this, MainActivity.class);
                intent.putExtra("key", 4);
                startActivity(intent);
            }
        });

    }
}