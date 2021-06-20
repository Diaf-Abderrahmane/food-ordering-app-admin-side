package com.example.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context mContext;
    private ArrayList<Comment> mData;



    public CommentAdapter(Context context, ArrayList<Comment> data) {
        this.mContext = context;
        this.mData = data;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.row_comment,parent,false);
        return new CommentViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {

        ///////////////////////////
        getUserPhoto(holder,position);
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mData.get(position).getKey());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.name.setText(snapshot.child("Username").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.content.setText(mData.get(position).getContent());
        holder.date.setText(timestampToString((long) mData.get(position).getTimestamp()));
        holder.commentRatingShow.setRating((int) mData.get(position).getRating());
        holder.removeComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference().child(Reviews.COMMENT_KEY).child(mData.get(position).getKey());
                commentRef.removeValue();
                mData.remove(position);
                notifyDataSetChanged();
                notifyItemRangeChanged(position, mData.size());
            }
        });
        holder.replyComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(mContext,R.style.Theme_AppCompat_Light_Dialog_Alert));
                final View replyLayout = LayoutInflater.from(mContext).inflate(R.layout.add_reply,null);
                builder.setView(replyLayout);
                builder.setTitle("Admin reply");
                EditText commentReply = replyLayout.findViewById(R.id.comment_reply_edittext);
                commentReply.setText(mData.get(position).getReply());

                builder.setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference().child(Reviews.COMMENT_KEY).child(mData.get(position).getKey()).child("reply");
                        commentRef.setValue(commentReply.getText().toString());

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder{
        ImageView userPhoto,removeComment,replyComment;
        TextView name,content,date;
        RatingBar commentRatingShow;

        public CommentViewHolder(View itemView){
            super(itemView);
            userPhoto = itemView.findViewById(R.id.comment_img);
            name = itemView.findViewById(R.id.comment_username);
            content = itemView.findViewById(R.id.comment_content);
            date = itemView.findViewById(R.id.comment_date);
            commentRatingShow = itemView.findViewById(R.id.comment_ratingbar);
            removeComment = itemView.findViewById(R.id.comment_remove);
            replyComment = itemView.findViewById(R.id.comment_add_reply);
        }
    }
    private void getUserPhoto(@NonNull CommentViewHolder holder, int position) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.child(mData.get(position).getKey()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    if (snapshot.hasChild("image")) {
                        String image = snapshot.child("image").getValue(String.class);
                        Glide.with(mContext).load(image).into(holder.userPhoto);


                    }
                    else
                        Glide.with(mContext).load(R.drawable.profile_pic).into(holder.userPhoto);
                }


            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
    private String timestampToString(long time) {

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("dd/MM/yyyy",calendar).toString();
        return date;


    }

}
