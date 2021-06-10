package com.example.fragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    private ImageView restopPhoto;
    private LinearLayout nav;


    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private RecyclerView RvComment;
    private CommentAdapter commentAdapter;
    private ArrayList<Comment> listComments;
    static String COMMENT_KEY = "Comments";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View view =inflater.inflate(R.layout.fragment_reviews, container, false);
        listComments = new ArrayList<>();
        RvComment = view.findViewById(R.id.rv_comment);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();


        //ini recyclerview

        iniRvComment();
        restopPhoto = view.findViewById(R.id.resto_img);
        //Glide.with(this).load(R.drawable.restop).into(restopPhoto);

        return view;
    }
  /*  private void gotoActivity(Class<?> cls){
        nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),cls);
                startActivity(intent);
                getActivity().finish();
            }
        });

    }*/


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
                commentAdapter = new CommentAdapter(getActivity().getApplicationContext(),listComments);
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