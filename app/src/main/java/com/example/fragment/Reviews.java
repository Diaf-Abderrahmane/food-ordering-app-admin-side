package com.example.fragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;


public class Reviews extends Fragment {

    private ImageView restopLogo;
    private ProgressBar logoProgressBar;


    private RecyclerView RvComment;
    private CommentAdapter commentAdapter;
    private ArrayList<Comment> listComments;
    private EditText description;
    private Button submitDescBtn;
    final static String COMMENT_KEY = "Comments",DESCRIPTION_KEY = "resto_description";


    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_reviews, container, false);
        // if you want to understand more features check client reviews code




        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();


        description = view.findViewById(R.id.resto_description);
        submitDescBtn = view.findViewById(R.id.submit_description_btn);
        logoProgressBar = view.findViewById(R.id.resto_logo_progress_bar);
        listComments = new ArrayList<>();
        RvComment = view.findViewById(R.id.rv_comment);
        restopLogo = view.findViewById(R.id.resto_logo);
        // if there is already a description in our database
        // we should retrieve it and put it in the
        // description EditText
        DatabaseReference aboutUsRef = firebaseDatabase.getReference().child("About_Us");
        aboutUsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.getChildrenCount()>0){
                    if(snapshot.hasChild(DESCRIPTION_KEY)){
                        String desc = snapshot.child(DESCRIPTION_KEY).getValue(String.class);
                        description.setText(desc);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // if the admin clicks on submit button
        // then store the description in firebase database
        submitDescBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aboutUsRef.child(DESCRIPTION_KEY).setValue(description.getText().toString());
                description.onEditorAction(EditorInfo.IME_ACTION_DONE);
            }
        });



        // get the app logo from firebase
        getLogo();

        //initialize recyclerview

        iniRvComment();


        return view;
    }

    private void getLogo(){
        DatabaseReference logoRef = firebaseDatabase.getReference().child("About_Us");
        logoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                restopLogo.setVisibility(View.VISIBLE);
                logoProgressBar.setVisibility(View.INVISIBLE);
                String logo = snapshot.child("LogoUrl").getValue(String.class);
                Glide.with(restopLogo.getContext()).load(logo).into(restopLogo);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void iniRvComment() {
        RvComment.setLayoutManager(new LinearLayoutManager(getActivity()));
        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference().child(COMMENT_KEY);
        listComments = new ArrayList<>();

        commentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                listComments.clear();
                for(DataSnapshot snap:snapshot.getChildren()){
                    Comment comment = snap.getValue(Comment.class);
                    listComments.add(comment);

                }
                Collections.reverse(listComments);
                commentAdapter = new CommentAdapter(getActivity(),listComments);
                RvComment.setAdapter(commentAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    private void showMessage(String message) {
        Toast.makeText(getActivity(),message,Toast.LENGTH_LONG).show();
    }
}