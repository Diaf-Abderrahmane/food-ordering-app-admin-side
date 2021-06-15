package com.example.fragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WorkTime extends AppCompatActivity {
    int minuteTime, hourTime;
    int minute, hour;
    String StrfromTime,StrtoTime;
    Spinner fromDay,toDay;
    TextView fromTime,toTime;
    Button newEdit,cancel;
    String todayselect,fromdayselect;
    DatabaseReference firebaseDatabase= FirebaseDatabase.getInstance().getReference().child("About_Us");
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.worktimedialog);
         toTime = (TextView) findViewById(R.id.ToTime);
         fromTime = (TextView) findViewById(R.id.FromTime);
         cancel = (Button) findViewById(R.id.Cancel);
         newEdit = (Button) findViewById(R.id.NewEdit);
         fromDay =(Spinner) findViewById(R.id.FromDayspinner);
         toDay =(Spinner) findViewById(R.id.ToDayspinner);
         fromDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fromdayselect = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
         toDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                todayselect = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        firebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap:snapshot.getChildren()){
                    switch (snap.getKey()){
                        case "FromTime":
                            StrfromTime =snap.getValue().toString();
                            break;
                        case "ToTime":
                            StrtoTime=snap.getValue().toString();
                            break;

                    }
                    toTime.setText(StrtoTime);
                    fromTime.setText(StrfromTime);

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
         fromTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog.OnTimeSetListener onTimeSetListener1 = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int sHour, int sMinute) {
                        hour = sHour;
                        minute =sMinute;

                        fromTime.setText((hour +":"+ minute).toString());

                    }
                };
                TimePickerDialog timePickerDialog1 = new TimePickerDialog(WorkTime.this, onTimeSetListener1, hourTime, minuteTime,true);
                timePickerDialog1.setTitle("Select Time");
                timePickerDialog1.show();
            }

        });
        toTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int sHour, int sMinute) {
                        hourTime = sHour;
                        minuteTime =sMinute;
                        toTime.setText(String.valueOf(hourTime+":"+minuteTime));

                    }
                };
                TimePickerDialog timePickerDialog = new TimePickerDialog(WorkTime.this, onTimeSetListener, hourTime, minuteTime,true);
                timePickerDialog.setTitle("Select Time");
                timePickerDialog.show();
            }

        });
        newEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseDatabase.child("FromTime").setValue(String.valueOf(hour)+":"+String.valueOf(minute));
                firebaseDatabase.child("ToTime").setValue(String.valueOf(hourTime)+":"+String.valueOf(minuteTime));
                firebaseDatabase.child("ToDay").setValue(todayselect);
                firebaseDatabase.child("FromDay").setValue(fromdayselect);
                Toast.makeText(WorkTime.this, "TimeWork is Updated", Toast.LENGTH_SHORT).show();
                intent = new Intent(WorkTime.this,AboutUs.class);
                startActivity(intent);
                finish();

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(WorkTime.this,AboutUs.class);
                startActivity(intent);
                finish();
            }
        });

    }
}