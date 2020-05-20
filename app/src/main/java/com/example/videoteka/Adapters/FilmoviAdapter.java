package com.example.videoteka.Adapters;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.icu.text.TimeZoneFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.videoteka.Classes.Film;
import com.example.videoteka.Fragments.DetailsFragment;
import com.example.videoteka.Fragments.HomeFragment;
import com.example.videoteka.Fragments.TicketFragment;
import com.example.videoteka.Helper.FragmentTip;
import com.example.videoteka.Helper.MyUtils;
import com.example.videoteka.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import io.grpc.internal.LogExceptionRunnable;

import static androidx.constraintlayout.widget.Constraints.TAG;

class FilmView extends RecyclerView.ViewHolder{
    private View mView;
    public ImageView IV_Film;
    public TextView TV_FilmNaziv;
    public ConstraintLayout CL_root;
    public Button BTN_Cijena;
    public FloatingActionButton FAB_Favourite;
    public FilmView(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
        IV_Film = mView.findViewById(R.id.IV_FilmSlika);
        TV_FilmNaziv = mView.findViewById(R.id.TV_FilmNaziv);
        BTN_Cijena = mView.findViewById(R.id.BTN_Cijena);
        FAB_Favourite = mView.findViewById(R.id.FAB_Favourite);
        CL_root = mView.findViewById(R.id.CL_root_one);
    }
}

public class FilmoviAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public List<Film> _data;
    public List<String> _favorits;
    private Context _ctx;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private FragmentManager _fm;
    private FragmentTip _fragmentTip;

    public FilmoviAdapter(List<Film> _data, Context _ctx, FragmentManager parentFragmentManager, FragmentTip tip) {
        this._data = _data;
        this._ctx = _ctx;
        this._fragmentTip = tip;
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        _favorits = new ArrayList<String>();
        db = FirebaseFirestore.getInstance();
        _fm = parentFragmentManager;
        db.collection("Korisnici").document(firebaseUser.getUid()).collection("Favoriti")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    private static final String TAG = "log:" ;

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int y = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String s = document.getId().replace(" ","");
                                _favorits.add(s);

                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());

                        }
                    }
                });
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType ==1){
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.one_film_layout2, parent, false);
            return new FilmView(view);

        }
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.one_film_layout, parent, false);
        return new FilmView(view);
    }

    @Override
    public int getItemViewType(int position) {

        if (position==1)
            return 1;
        return 0;
    }

    private boolean check(List<String> left,String right){
        for (String s: left) {
            if (s.equals(right)){
                return true;
            }
        }
        return false;
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        final FilmView _holder = (FilmView) holder;
        Glide.with(_ctx).load(_data.get(position).ThumbnailURL).into(_holder.IV_Film);
        _holder.TV_FilmNaziv.setText(_data.get(position).Naziv);

        //boolean x  =check(_favorits,_data.get(position).UID);
        if(_favorits.contains(_data.get(position).UID))
        {
            _holder.FAB_Favourite.setImageResource(R.drawable.ic_favorite_black_24dp);
            _holder.FAB_Favourite.setColorFilter(R.color.colorPrimary);
        }else{
            _holder.FAB_Favourite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            _holder.FAB_Favourite.setColorFilter(R.color.colorGrey);
        }

        if(_fragmentTip == FragmentTip.HOME || _fragmentTip==FragmentTip.SPREMLJENI_FILMOVI){
            _holder.BTN_Cijena.setText("$"+_data.get(position).Cijena);
            _holder.BTN_Cijena.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyUtils.zamjeniFragment(_fm, R.id.RootLayout, new TicketFragment(_data.get(position)), true);
                }
            });
        }
        if (_fragmentTip == FragmentTip.MOJI_FILMOVI){
            _holder.BTN_Cijena.setText("Detalji");
            _holder.BTN_Cijena.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyUtils.zamjeniFragment(_fm, R.id.RootLayout, new DetailsFragment(_data.get(position)), true);
                }
            });
        }


        _holder.IV_Film.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.zamjeniFragment(_fm, R.id.RootLayout, new DetailsFragment(_data.get(position)), true);
            }
        });
        _holder.TV_FilmNaziv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.zamjeniFragment(_fm, R.id.RootLayout, new DetailsFragment(_data.get(position)), true);

            }
        });
        _holder.FAB_Favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(_favorits.contains(_data.get(position).UID))
                {

                    db.collection("Korisnici")
                            .document(firebaseUser.getUid())
                            .collection("Favoriti")
                            .document(_data.get(position).UID)
                            .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Log.e(TAG, "onSuccess: " );
                                _favorits.remove(_data.get(position).UID);
                                _holder.FAB_Favourite.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                                _holder.FAB_Favourite.setColorFilter(R.color.colorGrey);
                                Toast.makeText(_ctx, "Film uklonjen iz favorita", Toast.LENGTH_SHORT).show();
                                if (_fragmentTip==FragmentTip.SPREMLJENI_FILMOVI){
                                    _data.remove(position);
                                    notifyDataSetChanged();
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "onError: "+ e.getMessage() );

                        }
                    });
                }else{
                    HashMap<String,String> map = new HashMap<>();
                    map.put("Time","T");
                    db.collection("Korisnici").document(firebaseUser.getUid())
                                                            .collection("Favoriti")
                                                            .document(_data.get(position).UID)
                                                            .set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.e(TAG, "onSuccess: " );
                            _favorits.add(_data.get(position).UID);
                            _holder.FAB_Favourite.setImageResource(R.drawable.ic_favorite_black_24dp);
                            _holder.FAB_Favourite.setColorFilter(R.color.colorPrimary);
                            Toast.makeText(_ctx, "Film dodan u favorite", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "onError: "+ e.getMessage() );

                        }
                    });


                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return _data.size();
    }
}
