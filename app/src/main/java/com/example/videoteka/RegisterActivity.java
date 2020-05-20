package com.example.videoteka;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.videoteka.Classes.Korisnik;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {
    private EditText Email;
    private EditText Password;
    private EditText PasswordPotvrda;
    private Button Registracija;
    private ProgressBar ProgresBarRegistracija;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        Email = findViewById(R.id.ET_FName);
        Password = findViewById(R.id.ET_Password);
        PasswordPotvrda = findViewById(R.id.ET_PasswordPotvrda);
        Registracija = findViewById(R.id.BTN_Prijava);
        ProgresBarRegistracija = findViewById(R.id.PB_Login);
        TextView TV_SignIn = findViewById(R.id.TV_SignIn);

        TV_SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(i);
            }
        });

        Registracija.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!Password.getText().toString().equals("") &&
                        !PasswordPotvrda.getText().toString().equals("") &&
                        !Email.getText().toString().equals(""))
                {
                    if (PasswordPotvrda.getText().toString().equals(Password.getText().toString())){
                        ProgresBarRegistracija.setVisibility(View.VISIBLE);
                        Registracija.setEnabled(false);
                        String pass = Password.getText().toString();
                        String email = Email.getText().toString();

                        final Korisnik newKorisnik = new Korisnik("usr",Email.getText().toString().split("@",2)[0],"https://www.sackettwaconia.com/wp-content/uploads/default-profile.png",49.99);

                        final FirebaseAuth auth = FirebaseAuth.getInstance();

                        auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = auth.getCurrentUser();

                                    if (user != null){
                                        FirebaseFirestore firestore  =FirebaseFirestore.getInstance();
                                        firestore.collection("Korisnici").document(user.getUid()).set(newKorisnik).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Intent i = new Intent(getApplicationContext(),RootActivity.class);
                                                    SharedPreferences preferences = getSharedPreferences("User",getApplicationContext().MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = preferences.edit();
                                                    editor.putString("UserRole", "usr");
                                                    editor.commit();
                                                    startActivity(i);
                                                }else{
                                                    Toast.makeText(RegisterActivity.this, "Korisnik registrovan, nije spremljen u bazu", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    Registracija.setEnabled(true);
                                    ProgresBarRegistracija.setVisibility(View.GONE);
                                }
                            }
                        });

                    }else{
                        Toast.makeText(getApplicationContext(), "Lozinke se ne slazu!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(RegisterActivity.this, "Polja su prazna!", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }
}
