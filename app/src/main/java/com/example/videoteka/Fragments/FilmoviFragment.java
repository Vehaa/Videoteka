package com.example.videoteka.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.videoteka.Adapters.FilmoviAdapter;
import com.example.videoteka.Classes.Film;
import com.example.videoteka.Helper.FragmentTip;
import com.example.videoteka.Helper.MyUtils;
import com.example.videoteka.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FilmoviFragment extends Fragment {

    public FilmoviFragment() {
        // Required empty public constructor
    }
    public FilmoviFragment(FragmentTip fragmentTip) {
        // Required empty public constructor
        _fragmentTip = fragmentTip;

    }
    private ArrayList<Film> mDataNaslovna;
    private FirebaseFirestore db ;
    private RecyclerView recyclerViewFilmovi;
    private SwipeRefreshLayout SRL_Filmovi;
    private FilmoviAdapter filmoviAdapter;
    private ArrayList<Film> mData;
    private TextView TV_FragmentTitle;

    private FragmentTip _fragmentTip;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_filmovi, container, false);
        TV_FragmentTitle = v.findViewById(R.id.TV_FragmentTitle);
        TV_FragmentTitle.setText(MyUtils.FragmentTipString[_fragmentTip.ordinal()]);
        mData = new ArrayList<>();
        mDataNaslovna = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        SRL_Filmovi = v.findViewById(R.id.SRL_Filmovi);
        recyclerViewFilmovi = v.findViewById(R.id.RV_Filmovi);
        filmoviAdapter = new FilmoviAdapter(mData,getContext(),getParentFragmentManager(),_fragmentTip);
        recyclerViewFilmovi.setHasFixedSize(true);
        recyclerViewFilmovi.setAdapter(filmoviAdapter);
        readData();

        SRL_Filmovi.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mData.clear();
                mDataNaslovna.clear();
                readData();
            }
        });
        return v;
    }
    private void readData() {

        if (_fragmentTip == FragmentTip.MOJI_FILMOVI)
        {

            db.collection("Korisnici").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("MojiFilmovi")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        private static final String TAG = "log:" ;

                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                int y = 0;
                                List<String> FilmUIDs = new ArrayList<>();

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    FilmUIDs.add(document.getId());
                                }
                                getFilmovi(FilmUIDs);


                            } else {
                                Log.w(TAG, "Error getting documents.", task.getException());

                            }
                        }
                    });
        }
        if (_fragmentTip == FragmentTip.SPREMLJENI_FILMOVI)
        {

            db.collection("Korisnici").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("Favoriti")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        private static final String TAG = "log:" ;

                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                int y = 0;
                                List<String> FilmUIDs = new ArrayList<>();

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                   FilmUIDs.add(document.getId());
                                }
                                getFilmovi(FilmUIDs);

                            } else {
                                Log.w(TAG, "Error getting documents.", task.getException());

                            }
                        }
                    });

        }


    }
    private void getFilmovi(List<String> FilmUIDs){

        if (!FilmUIDs.isEmpty()){

        db.collection("Filmovi")
                .whereIn("UID",FilmUIDs)
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

    }
}
