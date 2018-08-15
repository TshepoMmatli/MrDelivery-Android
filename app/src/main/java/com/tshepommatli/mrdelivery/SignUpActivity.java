package com.tshepommatli.mrdelivery;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {
    public EditText emailAddress, firstname, lastname, pW;
    String LOG = "SignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        emailAddress = (EditText) findViewById(R.id.email);
        firstname = (EditText) findViewById(R.id.firstname);
        lastname = (EditText) findViewById(R.id.lastname);
        pW = (EditText) findViewById(R.id.password);
    }

    public void btnSubmit(View view){
        Button Return = (Button) findViewById(R.id.loginReturn);
        Button createAccountBtn = (Button) findViewById(R.id.createAccountButton);
        ImageButton password = (ImageButton) findViewById(R.id.passwordHelpButton);
        //Upon being pressed, user is taken back to the login page
        Return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        String e_address = emailAddress.getText().toString();
        String fname = firstname.getText().toString();
        String lname = lastname.getText().toString();
        String passwd = pW.getText().toString();

        String type = "register";
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute(type, e_address, fname, lname, passwd);
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager=(InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
    }
}

