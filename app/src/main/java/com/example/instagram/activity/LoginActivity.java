package com.example.instagram.activity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.instagram.R;
import com.example.instagram.models.User;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class LoginActivity extends AppCompatActivity {

    public static String TAG = "LoginActivity";
    public static final String KEY_PROFILE="profile";


    EditText etUsername;
    EditText etPassword;
    Button btnSignin, btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        etUsername = findViewById(R.id.username);
        etPassword = findViewById(R.id.passwod);
        btnSignin = findViewById(R.id.btnSignin);
        btnSignUp = findViewById(R.id.btnSignup);

        if(ParseUser.getCurrentUser() != null){
            goMainActivity();

        }


        // user clicks to login
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = etUsername.getText().toString();
                String passWord = etPassword.getText().toString();
                loginUser(userName, passWord);
            }
        });


        // user clicks to signup
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = etUsername.getText().toString();
                String passWord = etPassword.getText().toString();
                SignUp(userName, passWord);
            }
        });

    }

    //get default image from assets
    private Bitmap getBitmapFromAsset()
    {
        AssetManager assetManager = getAssets();
        InputStream istr = null;
        try {
            istr = assetManager.open("profile.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(istr);
        return bitmap;
    }

    // convert image to parseFile

    public ParseFile conversionBitmapParseFile(Bitmap imageBitmap){
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
        byte[] imageByte = byteArrayOutputStream.toByteArray();
        ParseFile parseFile = new ParseFile("image_file.png",imageByte);
        return parseFile;
    }

    // signUp methode
    private void SignUp(String userName, String passWord) {
        ParseFile photo = conversionBitmapParseFile(getBitmapFromAsset());
        // Create the ParseUser
        ParseUser user = new ParseUser();
        // Set core properties
        user.setUsername(userName);
        user.setPassword(passWord);
        // Set custom properties
        photo.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                // If successful add file to user and signUpInBackground
                if(null == e)
                    user.put(KEY_PROFILE,photo);

            }
        });
        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.
                    goMainActivity();

                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    Log.e(TAG, "Issue with signup", e);
                    Toast.makeText(LoginActivity.this, "Issue with login", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    //login methode
    private void loginUser(String userName, String passWord) {
        ParseUser.logInInBackground(userName, passWord, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with login", e);
                    Toast.makeText(LoginActivity.this, "Issue with login", Toast.LENGTH_SHORT).show();
                    return;
                }
                goMainActivity();
                Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}