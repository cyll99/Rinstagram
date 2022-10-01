package com.example.instagram.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.instagram.R;
import com.example.instagram.adapter.CommentAdapter;
import com.example.instagram.helper.Constants;
import com.example.instagram.helper.TimeFormatter;
import com.example.instagram.models.Comment;
import com.example.instagram.models.Post;
import com.example.instagram.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "DetailActivity";
    TextView tvUsername,tvCreatedAt,tvDescription;

    ImageView ivPhoto, ivProfile;

    TextView icon_heart,icon_save,icon_comment,icon_send,icon_heart_red,tvNumLikes;

    RelativeLayout container;
    RecyclerView rvComments;


    ParseUser currentUser = ParseUser.getCurrentUser();
    List<String> likers;
    int numlikes;
    List<Comment> allComments;
    protected List<String> comments;
    CommentAdapter commentAdapter;
    Context context;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        ImageButton btnReturn = findViewById(R.id.iconBack);
        setSupportActionBar(toolbar);

        tvUsername = findViewById(R.id.username);
        tvCreatedAt = findViewById(R.id.createdAt);
        tvDescription = findViewById(R.id.description);
        ivPhoto = findViewById(R.id.photo);
        ivProfile = findViewById(R.id.profile);
        icon_heart = findViewById(R.id.heart);
        icon_heart_red = findViewById(R.id.heart_red);
        icon_save = findViewById(R.id.save);
        icon_comment = findViewById(R.id.comment);
        icon_send = findViewById(R.id.share);
        tvNumLikes = findViewById(R.id.numlikes);
        container = findViewById(R.id.container);
        rvComments = findViewById(R.id.rvcomments);


        Post post = Parcels.unwrap(getIntent().getParcelableExtra(Constants.DATA));

        try {
            likers = Post.fromJsonArray(post.getLikers()); // list of likers
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // list of comments
        try {
            comments= Comment.fromJsonArray(post.getListComment());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        allComments = new ArrayList<>();

        commentAdapter = new CommentAdapter(DetailActivity.this, allComments);
        rvComments.setAdapter(commentAdapter); //set the adapter to the recycle view

        rvComments.setLayoutManager(new LinearLayoutManager(context)); // set the layout for the adapter

        String username = post.getUser().getUsername();
        String description = post.getDescription();
        String picture_url = post.getImage().getUrl();
        String profile_url = post.getUser().getParseFile(User.KEY_PROFILE).getUrl();
        String timestamp =TimeFormatter.getTimeStamp(post.getCreatedAt().toString());


        Constants.display_heart(icon_heart,icon_heart_red,likers); // display a filled or empty heart (methode in constants)

        tvDescription.setText(description);
        tvUsername.setText(username);
        tvCreatedAt.setText(timestamp);
        numlikes = likers.size();
        tvNumLikes.setText(String.valueOf(numlikes));

        // when user likes
        icon_heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.UserLikes(icon_heart,icon_heart_red,currentUser,post,TAG,DetailActivity.this,likers,tvNumLikes);
            }
        });
        // user clicks to return to the main page
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DetailActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivityIfNeeded(i, 0);
            }
        });
        // when user unlikes
        icon_heart_red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.UserDislikes(icon_heart,icon_heart_red,post,likers,currentUser, tvNumLikes);
            }
        });

        // user can tap on a picture to see it
        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 2 Navigate on new activity on tap

                Intent i =  new Intent(DetailActivity.this, PictureActivity.class);
                i.putExtra(Constants.DATA, Parcels.wrap(post));


                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation((Activity) DetailActivity.this, ivPhoto, Constants.TRANSITION);

                DetailActivity.this.startActivity(i, options.toBundle());

            }
        });


        Glide.with(DetailActivity.this).load(profile_url)
                .transform(new RoundedCorners(Constants.ROUNDED_PROFILE)).into(ivProfile);// display profile
        Glide.with(DetailActivity.this).load(picture_url)
                .transform(new RoundedCorners(Constants.ROUNDED_PICTURE)).into(ivPhoto); // display picture posted

        queryComments(); //query all comments about the post

    }

    // Methode for displaying comments
    protected void queryComments() {
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        query.include(Comment.KEY_USER);
        query.whereContainedIn("objectId", comments);
        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> commentList, ParseException e) {
                if (e != null){
                    Log.e(TAG, "Issue with getting Posts", e);
                    Toast.makeText(DetailActivity.this, "Issue with getting Posts", Toast.LENGTH_SHORT).show();
                    return;
                }

                allComments.addAll(commentList);
                commentAdapter.notifyDataSetChanged();

            }
        });
    }
}