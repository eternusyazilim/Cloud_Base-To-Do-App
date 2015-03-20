package com.example.safakesberk.todoapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by Safak Esberk on 15.3.2015.
 */
public class LoginSignupActivity extends Activity {
    Button loginbutton;
    Button signup;
    String usernametxt;
    String passwordtxt;
    EditText password;
    EditText username;
    ParseUser user;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_signup);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);

        loginbutton = (Button) findViewById(R.id.login);
        signup = (Button) findViewById(R.id.signup);

        loginbutton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                usernametxt = getUsername();
                passwordtxt = getPassword();
                logInBackground(usernametxt,passwordtxt);
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                usernametxt = getUsername();
                passwordtxt = getPassword();
                signup(usernametxt,passwordtxt);


            }
        });
    }

    private void signup(String usernametxt, String passwordtxt) {
        if (!isEmpty()){

            int numberParse = 0;
            ParseQuery<ParseUser> numberofUsers = ParseUser.getQuery();  // Note to myself : count users in parse class
            try {
                numberParse=numberofUsers.count();
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
            user = new ParseUser();
            user.setUsername(usernametxt);
            user.setPassword(passwordtxt);
            user.put("userID",numberParse+1);
            user.signUpInBackground(new SignUpCallback() {
                public void done(ParseException e) {
                    checkSignup(e);
                }
            });
        }
    }

    private void checkSignup(ParseException e) {
        if (e == null) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.successLogin),
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.signUpErr), Toast.LENGTH_LONG)
                    .show();
        }
    }

    private boolean isEmpty() {
        if(usernametxt.equals("") && passwordtxt.equals("")) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.completeForm),
                    Toast.LENGTH_LONG).show();
            return true;
        }
        else return false;
    }

    private void logInBackground(String usernametxt, String passwordtxt) {
        ParseUser.logInInBackground(usernametxt, passwordtxt,
                new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            Intent intent = new Intent( LoginSignupActivity.this,HomeActivity.class);
                            startActivity(intent);
                            Toast.makeText( getApplicationContext(),getString(R.string.successLogin), Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(),  getString(R.string.noUser), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public String getUsername() {
        return username.getText().toString();
    }
    public String getPassword() {
        return password.getText().toString();
    }
}