package com.example.videoteka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkLogedUser();
    }

    private void checkLogedUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null){
            Intent i = new Intent(this,LoginActivity.class);
            startActivity(i);
            finish();

        }
        else
        {
            Intent i = new Intent(this,RootActivity.class);
            startActivity(i);
            finish();

        }
    }
}
