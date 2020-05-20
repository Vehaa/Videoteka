package com.example.videoteka;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private Button login;
    private TextView registracija;
    private EditText email;
    private EditText pass;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private ProgressBar progressBarLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login = findViewById(R.id.BTN_Prijava);
        registracija = findViewById(R.id.TV_Register);
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.ET_FName);
        pass = findViewById(R.id.ET_Password);
        firebaseFirestore = FirebaseFirestore.getInstance();
        progressBarLogin = findViewById(R.id.PB_Login);
        registracija.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(i);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!email.getText().toString().equals("") && !pass.getText().toString().equals("")) {
                    login.setEnabled(false);
                    progressBarLogin.setVisibility(View.VISIBLE);
                    signIn(email.getText().toString(), pass.getText().toString());
                }else{
                    Toast.makeText(LoginActivity.this, "Polja su prazna!", Toast.LENGTH_SHORT).show();
                    login.setEnabled(true);
                    progressBarLogin.setVisibility(View.GONE);

                }
            }
        });
    }
    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("suc", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            firebaseFirestore.collection("Korisnici").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    String  Rola;
                                    if (task.isSuccessful()){
                                        Rola = task.getResult().getString("Rola");
                                    }else{
                                        Rola = "nan";
                                    }
                                    progressBarLogin.setVisibility(View.GONE);

                                    Intent i = new Intent(getApplicationContext(),RootActivity.class);


                                    SharedPreferences preferences = getSharedPreferences("User",getApplicationContext().MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString("UserRole", Rola);
                                    editor.apply();
                                    startActivity(i);
                                    finish();
                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("NotSuc", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            login.setEnabled(true);
                            progressBarLogin.setVisibility(View.GONE);

                        }

                        // ...
                    }
                });
    }
}
