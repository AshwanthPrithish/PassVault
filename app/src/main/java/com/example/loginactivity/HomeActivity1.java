package com.example.loginactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.w3c.dom.Text;

public class HomeActivity1 extends AppCompatActivity {

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    Button Logout;
    TextView Name, Mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home1);
        Logout = (Button) findViewById(R.id.logout);
        Name = (TextView)findViewById(R.id.name);
        Mail = (TextView)findViewById(R.id.email);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

        gsc = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if(account!=null){
            String name = account.getDisplayName();
            String mail = account.getEmail();

            Name.setText(name);
            Mail.setText(mail);
        }

        Logout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(android.view.View view) {
                SignOut();
            }
        });
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
}