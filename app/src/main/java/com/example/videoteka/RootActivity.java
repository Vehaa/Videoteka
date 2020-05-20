package com.example.videoteka;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.videoteka.Fragments.FilmoviFragment;
import com.example.videoteka.Fragments.HomeFragment;
import com.example.videoteka.Fragments.ProfileFragment;
import com.example.videoteka.Fragments.TicketFragment;
import com.example.videoteka.Helper.FragmentTip;
import com.example.videoteka.Helper.IUpdate;
import com.example.videoteka.Helper.MyUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import de.hdodenhof.circleimageview.CircleImageView;



public class RootActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, IUpdate {

    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private String Rola;
    private CircleImageView ProfilnaSlika;
    private FloatingActionButton FAB_Drawer;
    private IUpdate _Update;


    @Override
    protected void onStart() {
        super.onStart();
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        if (firebaseUser == null) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);

        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        _Update = this;

        MyUtils.zamjeniFragment(getSupportFragmentManager(), R.id.RootLayout, new HomeFragment(), false);
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        FAB_Drawer = findViewById(R.id.FAB_Drawer);

        FAB_Drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if (drawer.isDrawerOpen(Gravity.LEFT)){
                        drawer.closeDrawer(Gravity.LEFT);
                    }else {
                        drawer.openDrawer(Gravity.LEFT);

                    }
            }
        });

        navigationView.setNavigationItemSelectedListener(this);

        firebaseFirestore.collection("Korisnici").document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    String UserName = task.getResult().getString("KorisnickoIme");
                    final String URL = task.getResult().getString("SlikaURL");

                    UpdateHeader(URL,UserName);

                } else {
                    Rola = "nan";
                }

            }
        });
    }



    public void UpdateHeader(String url,String userName){
        NavigationView navigationView = findViewById(R.id.nav_view);

        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.UserName);
        ProfilnaSlika = headerView.findViewById(R.id.CIV_ProfilnaSlika);

        if (userName!=null)
            navUsername.setText(userName);
        if (url!=null)
            Glide.with(getApplicationContext()).load(url).into(ProfilnaSlika);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {

            MyUtils.zamjeniFragment(getSupportFragmentManager(), R.id.RootLayout, new HomeFragment(), true);
        }else if (id == R.id.nav_profile) {
            MyUtils.zamjeniFragment(getSupportFragmentManager(), R.id.RootLayout, new ProfileFragment(_Update), true);
        } else if (id == R.id.nav_favourites) {
            MyUtils.zamjeniFragment(getSupportFragmentManager(), R.id.RootLayout, new FilmoviFragment(FragmentTip.SPREMLJENI_FILMOVI), true);
        }else if (id == R.id.nav_movies) {
            MyUtils.zamjeniFragment(getSupportFragmentManager(), R.id.RootLayout, new FilmoviFragment(FragmentTip.MOJI_FILMOVI), true);
        }else if (id == R.id.nav_logut) {
            auth.signOut();

            SharedPreferences settings = getApplicationContext().getSharedPreferences("User", getApplicationContext().MODE_PRIVATE);
            settings.edit().clear().commit();
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void Update(String URL, String USERNAME) {
        UpdateHeader(URL,USERNAME);
    }
}
