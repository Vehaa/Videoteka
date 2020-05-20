package com.example.videoteka.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.videoteka.Classes.Film;
import com.example.videoteka.Helper.MyUtils;
import com.example.videoteka.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class TicketFragment extends Fragment {

    private Film _film;

    private TextView  TV_FilmNaziv;
    private TextView  TV_FilmCijena;
    private TextView  TV_NewBalance;
    private Button BTN_ConfirmPayment;
    private ImageView IV_FilmBack;
    private ImageView IV_FilmTicket;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    double UserBalance;
    public TicketFragment() {
        // Required empty public constructor
    }
    public TicketFragment(Film f) {
        // Required empty public constructor
        _film = f;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_ticket, container, false);


        TV_FilmCijena = v.findViewById(R.id.TV_FilmCijena);
        TV_FilmNaziv = v.findViewById(R.id.TV_FilmNaziv);
        TV_NewBalance = v.findViewById(R.id.TV_NewBalance);
        BTN_ConfirmPayment = v.findViewById(R.id.BTN_Confirm);
        IV_FilmBack = v.findViewById(R.id.IV_Film_Back);
        IV_FilmTicket = v.findViewById(R.id.IV_Film_Ticket);

        Glide.with(getContext()).load(_film.ThumbnailURL).into(IV_FilmBack);
        Glide.with(getContext()).load(_film.ThumbnailURL).into(IV_FilmTicket);


        TV_FilmNaziv.setText(_film.Naziv);
        TV_FilmCijena.setText(String.format("$%s", _film.Cijena));
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection("Korisnici").document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {


                if (task.isSuccessful()) {
                    UserBalance = (double)task.getResult().get("Balance");

                    if ((double)(UserBalance - _film.Cijena)<0.0){
                        DecimalFormat df = new DecimalFormat("0.00");
                        TV_NewBalance.setText("Don't have enough money, your balance $"+ df.format(UserBalance));
                        BTN_ConfirmPayment.setClickable(false);
                        BTN_ConfirmPayment.setFocusable(false);

                    }else {
                        DecimalFormat df = new DecimalFormat("0.00");
                        String x =  df.format((double) (UserBalance - _film.Cijena));
                        TV_NewBalance.setText(String.format("NEW BALANCE $%s", x));
                    }



                } else {
                    TV_NewBalance.setText("NaN");
                }

            }
        });

        BTN_ConfirmPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BTN_ConfirmPayment.setClickable(false);
                BTN_ConfirmPayment.setFocusable(false);
                HashMap<String, Timestamp> map = new HashMap<>();
                map.put("Time",new Timestamp(new Date()));
                firebaseFirestore.collection("Korisnici").document(auth.getUid())
                        .collection("MojiFilmovi")
                        .document(_film.UID)
                        .set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e(TAG, "onSuccess: " );

                        Toast.makeText(getContext(), "Film kupljen", Toast.LENGTH_SHORT).show();

                        firebaseFirestore.collection("Korisnici").document(auth.getUid())
                                .update("Balance", (UserBalance - _film.Cijena)).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                MyUtils.zamjeniFragment(getParentFragmentManager(), R.id.RootLayout, new HomeFragment(), false);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                BTN_ConfirmPayment.setClickable(true);
                                BTN_ConfirmPayment.setFocusable(true);
                                Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();

                            }
                        });




                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onError: "+ e.getMessage() );
                        Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();

                        BTN_ConfirmPayment.setClickable(true);
                        BTN_ConfirmPayment.setFocusable(true);
                    }
                });

            }
        });
        return v;
    }
}
