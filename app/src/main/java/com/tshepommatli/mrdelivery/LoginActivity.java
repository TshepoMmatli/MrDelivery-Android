package com.tshepommatli.mrdelivery;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import java.io.IOException;


public class LoginActivity extends AppCompatActivity {
    public static final String PREFS = "prefFile";

    EditText UsernameEt, PasswordEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        onButtonClick();
        UsernameEt = (EditText) findViewById(R.id.edUsername);
        PasswordEt = (EditText) findViewById(R.id.edPassword);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
       // finish();

        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    public void onLogin(View view) throws IOException, InterruptedException {
        String username = UsernameEt.getText().toString();
        String password = PasswordEt.getText().toString();

        SharedPreferences preferences = getSharedPreferences(PREFS, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("username", username);
        editor.commit();

        if (!username.isEmpty() && !password.isEmpty()) {
            String type = "";
            type = "login";

            BackgroundWorker BackgroundWorker = new BackgroundWorker(this);
            BackgroundWorker.execute(type, username, password);
            UsernameEt.setText("");
            PasswordEt.setText("");
        } else
            {
            // Prompt user to enter credentials
            Toast.makeText(getApplicationContext(),
                    "Please enter the credentials!", Toast.LENGTH_LONG).show();
        }




    }

    public void onButtonClick() {
        Button signUp = (Button) findViewById(R.id.btnSignUp);
        ImageButton usernameHelpButton = (ImageButton) findViewById(R.id.imageButton1);
        ImageButton passwordHelpButton = (ImageButton) findViewById(R.id.imageButton2);

        /** If the help button next to the username textfield is pressed, a dialog box appears and
         *  informs the user to enter their username to gain access to Yardbird's menu
         */
        usernameHelpButton.setOnClickListener (new View.OnClickListener() {
                                                   @Override
                                                   public void onClick(View v) {
                                                       AlertDialog.Builder dialogBox = new AlertDialog.Builder(LoginActivity.this);
                                                       dialogBox.setMessage("Please enter your username")
                                                               .setCancelable(false)
                                                               .setNegativeButton("Okay", new DialogInterface.OnClickListener() {
                                                                   public void onClick(DialogInterface dialog, int which) {
                                                                       dialog.cancel();//closes the dialog box
                                                                   }
                                                               });

                                                       AlertDialog dialog = dialogBox.create();
                                                       dialog.setTitle("Help?");
                                                       dialog.show();
                                                   }
                                               }
        );

        /** If the help button next to the password textfield is pressed, a dialog box appears and
         *  informs the user to enter their password to gain access to Yardbird's menu
         */
        passwordHelpButton.setOnClickListener (new View.OnClickListener() {
                                                   @Override
                                                   public void onClick(View v) {
                                                       AlertDialog.Builder dialogBox = new AlertDialog.Builder(LoginActivity.this);
                                                       dialogBox.setMessage("Please enter your password")
                                                               .setCancelable(false)
                                                               .setNegativeButton("Okay", new DialogInterface.OnClickListener() {
                                                                   public void onClick(DialogInterface dialog, int which) {
                                                                       dialog.cancel();//closes the dialog box
                                                                   }
                                                               });

                                                       AlertDialog dialog = dialogBox.create();
                                                       dialog.setTitle("Help?");
                                                       dialog.show();
                                                   }
                                               }
        );

        //By pressing the Sign-Up button, the Sign-up page appears
        signUp.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(i);
            }
        });
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager=(InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
    }
}

