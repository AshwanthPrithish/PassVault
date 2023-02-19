package com.example.loginactivity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Scanner;
public class HomeActivity extends AppCompatActivity implements OnClickListener{

    EditText Website, UserName, Password;
    Button Insert, Delete, Update, View, ViewAll, Logout;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    SQLiteDatabase db;
    Pass pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Website = (EditText) findViewById(R.id.Website);
        UserName = (EditText) findViewById(R.id.UserName);
        Password = (EditText) findViewById(R.id.Password);
        Insert = (Button) findViewById(R.id.Insert);
        Delete = (Button) findViewById(R.id.Delete);
        Update = (Button) findViewById(R.id.Update);
        View = (Button) findViewById(R.id.View1);
        ViewAll = (Button) findViewById(R.id.ViewAll);
        Logout = (Button) findViewById(R.id.logout);


        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

        gsc = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        Logout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(android.view.View view) {
                SignOut();
            }
        });

        Insert.setOnClickListener(this);
        Delete.setOnClickListener(this);
        Update.setOnClickListener(this);
        View.setOnClickListener(this);
        ViewAll.setOnClickListener(this);
        // Creating database and table
        db = openOrCreateDatabase("UserDB", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS user(website VARCHAR, uname VARCHAR, upass VARCHAR);");
    }

    private void SignOut() {

        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

    }

    public void onClick(View view)
    {
        // Inserting a record to the Student table
        if (view == Insert)
        {
            // Checking for empty fields
            if (Website.getText().toString().trim().length() == 0 || UserName.getText().toString().trim().length() == 0 || Password.getText().toString().trim().length() == 0)
            {
                showMessage("Error", "Please enter all values");
                return;
            }
            try {
                db.execSQL("INSERT INTO user VALUES('" + Website.getText() + "','" + UserName.getText() +"','" + pass.encryptPassword(String.valueOf(Password.getText())) + "');");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            showMessage("Success", "Record added");
            clearText();
        }
        // Deleting a record from the Student table
        if (view == Delete)
        {
            // Checking for empty roll number
            if (Website.getText().toString().trim().length() == 0)
            {
                showMessage("Error", "Please enter Website");
                return;
            }
            Cursor c = db.rawQuery("SELECT * FROM user WHERE website='" + Website.getText() + "'", null);
            if (c.moveToFirst())
            {
                db.execSQL("DELETE FROM user WHERE website='" + Website.getText() + "'");
                showMessage("Success", "Record Deleted");
            }
            else
            {
                showMessage("Error", "Invalid Website");
            }
            clearText();
        }
        // Updating a record in the Student table
        if (view == Update)
        {
            // Checking for empty roll number
            if (Website.getText().toString().trim().length() == 0)
            {
                showMessage("Error", "Please enter Website");
                return;
            }
            Cursor c = db.rawQuery("SELECT * FROM user WHERE website='" + Website.getText() + "'",null);
            if (c.moveToFirst())
            {
                db.execSQL("UPDATE user SET uname='" + UserName.getText() + "',upass='" + Password.getText() +"' WHERE website='" + Website.getText() + "'");
                showMessage("Success", "Record Modified");
            }
            else
            {
                showMessage("Error", "Invalid Website");
            }
            clearText();
        }
        // Display a record from the Student table
        if (view == View)
        {
            // Checking for empty roll number
            if (Website.getText().toString().trim().length() == 0)
            {
                showMessage("Error", "Please enter Website");
                return;
            }
            Cursor c = db.rawQuery("SELECT * FROM user WHERE website='" + Website.getText() + "'", null);
            if (c.moveToFirst())
            {
                UserName.setText(c.getString(1));
                try {
                    Password.setText(pass.decryptPassword(c.getString(2)));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            else
            {
                showMessage("Error", "Invalid Website");
                clearText();
            }
        }
        // Displaying all the records
        if (view == ViewAll)
        {
            Cursor c = db.rawQuery("SELECT * FROM user", null);
            if (c.getCount() == 0)
            {
                showMessage("Error", "No records found");
                return;
            }
            StringBuffer buffer = new StringBuffer();
            while (c.moveToNext())
            {
                buffer.append("Website: " + c.getString(0) + "\n");
                buffer.append("UserName: " + c.getString(1) + "\n");
                try {
                    buffer.append("Password: " + pass.decryptPassword(c.getString(2)) + "\n\n");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            showMessage("User Details", buffer.toString());
        }
    }
    public void showMessage(String title, String message)
    {
        Builder builder = new Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
    public void clearText()
    {
        Website.setText("");
        UserName.setText("");
        Password.setText("");
        Website.requestFocus();
    }
}



class Pass {
    private static final String ALGORITHM = "AES";
    private static final String SECRET_KEY = "secretkey1234567";


    public static String encryptPassword(String password) throws Exception {
        Key key = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encryptedBytes = cipher.doFinal(password.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }


    public static String decryptPassword(String encryptedPassword) throws Exception {
        Key key = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] decodedBytes = Base64.getDecoder().decode(encryptedPassword);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes);
    }
}


