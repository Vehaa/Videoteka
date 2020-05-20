package com.example.videoteka.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.videoteka.Classes.Film;
import com.example.videoteka.Helper.MyUtils;
import com.example.videoteka.R;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment {

    public DetailsFragment() {
        // Required empty public constructor
    }

    public DetailsFragment(Film f) {
        // Required empty public constructor
        _film = f;
    }
    Film _film;
    TextView  FilmNaziv;
    TextView  FilmCijena;
    TextView  FilmZanr;
    TextView  FilmOpis;
    Button Iznajmi;
    ImageView  FilmSlika;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_details, container, false);

        FilmNaziv = v.findViewById(R.id.TV_FilmNaziv);
        FilmCijena = v.findViewById(R.id.TV_FilmCijena);
        FilmOpis = v.findViewById(R.id.TV_Opis);
        Iznajmi = v.findViewById(R.id.BTN_Iznajmi);
        FilmSlika = v.findViewById(R.id.IV_FilmSlika);
        FilmZanr = v.findViewById(R.id.TV_Zanr);


        if (_film!=null)
        {
            FilmNaziv.setText(_film.Naziv.toString().toUpperCase());
            FilmCijena.setText("$"+_film.Cijena);
            FilmOpis.setText(_film.Opis);
            FilmZanr.setText(_film.Zanr.get(0).toUpperCase());
            Glide.with(Objects.requireNonNull(getContext())).load(_film.ThumbnailURL).into(FilmSlika);
            Iznajmi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyUtils.zamjeniFragment(getFragmentManager(), R.id.RootLayout, new TicketFragment(_film), true);
                }
            });
        }

        return v;
    }
}
