package com.example.videoteka.Fragments;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.videoteka.Adapters.FilmoviAdapter;
import com.example.videoteka.Classes.Film;
import com.example.videoteka.Helper.FragmentTip;
import com.example.videoteka.Helper.MyUtils;
import com.example.videoteka.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    FrameLayout FL_frame;
    private Film NaslovnaTrenutniFilm ;
    private int NaslovnaFilmBrojac;
    private TextView NaslovnaFilm ;
    private ArrayList<Film> mData;
    private ConstraintLayout CL_Naslovna;
    private ArrayList<Film> mDataNaslovna;
    private FirebaseFirestore db ;
    private RecyclerView recyclerViewFilmovi;
    private SwipeRefreshLayout SRL_Filmovi;
    private FilmoviAdapter filmoviAdapter;
    private FloatingActionButton FAB_volume;
    private FloatingActionButton FAB_Search;
    private MediaPlayer _mp;
    private ImageView IV_VideoOverlay;
    private Button BTN_details;
    private EditText ET_Search;
    private boolean IsSearchOpen;
    private int dubleClick;
    boolean IsStoped;
    private int CurrentTime;
    VideoView videoView;
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onResume() {
        super.onResume();
        videoView.seekTo(CurrentTime);
        videoView.start();
    }

    @Override
    public void onPause() {
        super.onPause();

        CurrentTime  = videoView.getCurrentPosition();
        videoView.pause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v  =inflater.inflate(R.layout.fragment_home, container, false);
        ET_Search = v.findViewById(R.id.ET_Search);
        FL_frame = v.findViewById(R.id.FL_frame);
        videoView = v.findViewById(R.id.ImageView);
        FAB_volume  = v.findViewById(R.id.FAB_volume);
        FAB_Search = v.findViewById(R.id.FAB_Serch);
        CL_Naslovna = v.findViewById(R.id.CL_Naslovna);
        IV_VideoOverlay = v.findViewById(R.id.IV_VideoOverlay);
        BTN_details = v.findViewById(R.id.BTN_Detalji);
        IsSearchOpen = false;
        final MediaController mediacontroller = new MediaController(getContext());
        mediacontroller.setAnchorView(videoView);
        IsStoped = false;
        dubleClick = 0;
        NaslovnaFilm = v.findViewById(R.id.TV_Naslov);
        NaslovnaFilmBrojac = 0;
        FAB_volume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (IsStoped){
                    FAB_volume.setImageResource(R.drawable.ic_pause_black_24dp);
                    videoView.seekTo(CurrentTime);
                    videoView.start();
                }else{
                    FAB_volume.setImageResource(R.drawable.ic_play_arrow_black_24dp);

                    CurrentTime = videoView.getCurrentPosition();
                    videoView.pause();
                }
                IsStoped = !IsStoped;

            }
        });
        videoView.setSoundEffectsEnabled(IsStoped);
        videoView.setMediaController(mediacontroller);
        videoView.requestFocus();
        videoView.setMediaController(null);

        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dubleClick++;
                if (dubleClick==2){
                    updateNaslovna();
                    dubleClick=0;
                }
            }
        });
        FL_frame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dubleClick++;
                if (dubleClick==2){
                    updateNaslovna();
                    dubleClick=0;
                }
            }
        });
        //videoView.start();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                   updateNaslovna();
            }
        });

        FAB_Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (IsSearchOpen && !ET_Search.getText().toString().isEmpty()){

                    filterMovies(ET_Search.getText().toString());
                }else if(IsSearchOpen && ET_Search.getText().toString().isEmpty()){
                    IsSearchOpen = false;
                    ET_Search.setVisibility(View.GONE);

                }else{
                    IsSearchOpen = true;
                    ET_Search.setVisibility(View.VISIBLE);
                    ET_Search.setFocusable(true);
                    ET_Search.requestFocus();
                }
            }
        });
        mData = new ArrayList<>();
        mDataNaslovna = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        SRL_Filmovi = v.findViewById(R.id.SRL_Filmovi);
        recyclerViewFilmovi = v.findViewById(R.id.RV_Filmovi);
        filmoviAdapter = new FilmoviAdapter(mData,getContext(),getParentFragmentManager(), FragmentTip.HOME);
        recyclerViewFilmovi.setHasFixedSize(true);
        recyclerViewFilmovi.setAdapter(filmoviAdapter);
        SRL_Filmovi.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                IsStoped = false;
                FAB_volume.setImageResource(R.drawable.ic_pause_black_24dp);
                mData.clear();
                mDataNaslovna.clear();
                    readData();
            }
        });

        BTN_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.zamjeniFragment(getFragmentManager(), R.id.RootLayout, new DetailsFragment(NaslovnaTrenutniFilm), true);

            }
        });
        readData();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms

                IV_VideoOverlay.setVisibility(View.VISIBLE);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        IV_VideoOverlay.setVisibility(View.GONE);

                    }
                }, 3000);
            }
        }, 5000);
        return v;
    }

    private void filterMovies(String text) {
        db.collection("Filmovi")
                .whereEqualTo("Naziv",text)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    private static final String TAG = "log:" ;

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int y = 0;
                            mData.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Film p = null;
                                p = document.toObject(Film.class);
                                p.UID = document.getId();
                                mData.add(p);

                            }
                            filmoviAdapter.notifyDataSetChanged();
                            ET_Search.setVisibility(View.GONE);
                            ET_Search.setText("");
                            IsSearchOpen=false;
                            ET_Search.clearFocus();
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());

                        }
                    }
                });
    }

    private void readData() {

        db.collection("Filmovi")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    private static final String TAG = "log:" ;

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int y = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Film p = null;
                                p = document.toObject(Film.class);
                                p.UID = document.getId();
                                mData.add(p);
                                if (p.Naslovna){
                                    mDataNaslovna.add(p);
                                    if (y<1){
                                        updateNaslovna();
                                        y++;
                                    }
                                }
                            }
                            filmoviAdapter.notifyDataSetChanged();
                            SRL_Filmovi.setRefreshing(false);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                            SRL_Filmovi.setRefreshing(false);

                        }
                    }
                });
    }

    private void updateNaslovna() {
        if (mDataNaslovna.size()>=NaslovnaFilmBrojac+1){
            Film p = mDataNaslovna.get(NaslovnaFilmBrojac);
             NaslovnaFilmBrojac++;
            CL_Naslovna.setVisibility(View.VISIBLE);
            NaslovnaTrenutniFilm = p;
            NaslovnaFilm.setText(p.Naziv.toUpperCase());
            videoView.setVideoURI(Uri.parse(p.NaslovnaSlikaURL));
            videoView.setMediaController(null);
            videoView.start();
        }else{
            NaslovnaFilmBrojac =0;
            updateNaslovna();
        }
    }
}
