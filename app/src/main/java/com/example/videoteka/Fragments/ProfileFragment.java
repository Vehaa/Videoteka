package com.example.videoteka.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.ScrollingTabContainerView;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.videoteka.Classes.Korisnik;
import com.example.videoteka.Helper.IUpdate;
import com.example.videoteka.R;
import com.example.videoteka.RootActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int PICK_IMAGE_REQUEST = 12 ;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private IUpdate _Update;
    public ProfileFragment() {
        // Required empty public constructor

    }

    public ProfileFragment(IUpdate update) {

        _Update = update;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private AppCompatSpinner SP_spol;
    private List<String> spol;

    private EditText Ime;
    private TextView Email;
    private EditText Prezime;
    private EditText KorisnickoIme;
    private EditText DatumRodjenja;
    private CircleImageView ProfilnaSlika;
    private Button Save;
    private FirebaseFirestore firestore;
    private StorageReference mStorageRef;
    private Uri pathDoSlike;


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            pathDoSlike = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getContext()).getContentResolver(),pathDoSlike);
                ProfilnaSlika.setImageBitmap(bitmap);

                Toast.makeText(getContext(), "Image upload ...", Toast.LENGTH_SHORT).show();
                uploadImageToFirebaseStorage(pathDoSlike);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToFirebaseStorage(Uri pathDoSlike) {
        final StorageReference profilnaRef = mStorageRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()+"/profilna.jpg");

        Bitmap bitmap = ((BitmapDrawable) ProfilnaSlika.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] data = baos.toByteArray();

        profilnaRef.putBytes(data).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                if (!task.isSuccessful()){
                    throw  task.getException();
                }

                return profilnaRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){
                    Uri downloadUri = task.getResult();
                    _Update.Update(downloadUri.toString(),null);
                    FirebaseFirestore firestore  =FirebaseFirestore.getInstance();
                    firestore.collection("Korisnici").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update("SlikaURL",downloadUri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getContext(), "Image uploaded!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_profile, container, false);
        SP_spol = v.findViewById(R.id.SP_Spol);
        spol = new ArrayList<>();
        spol.add(" ");
        spol.add("Musko");
        spol.add("Zensko");


        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, spol);
        // Drop down layout style
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SP_spol.setAdapter(dataAdapter);

        Ime = v.findViewById(R.id.ET_FName);
        Prezime = v.findViewById(R.id.ET_LastName);
        KorisnickoIme = v.findViewById(R.id.ET_UserName);
        DatumRodjenja = v.findViewById(R.id.ET_BirthDate);
        ProfilnaSlika = v.findViewById(R.id.CIV_ProfilnaSlika);
        Email = v.findViewById(R.id.TV_Email);
        Save = v.findViewById(R.id.BTN_Update);
        mStorageRef = FirebaseStorage.getInstance().getReference("ProfilneSlike");
        firestore = FirebaseFirestore.getInstance();


        SP_spol.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.WHITE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        firestore.collection("Korisnici").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Korisnik k = task.getResult().toObject(Korisnik.class);
                    Ime.setText(k.Ime);
                    Prezime.setText(k.Prezime);
                    KorisnickoIme.setText(k.KorisnickoIme);
                    DatumRodjenja.setText(k.DatumRodjenja);
                    Email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

                    Glide.with(Objects.requireNonNull(getContext())).load(k.SlikaURL).into(ProfilnaSlika);
                    if (k.Spol.equals("Musko")){
                        SP_spol.setSelection(1);

                    }else if (k.Spol.equals("Zensko")){
                        SP_spol.setSelection(2);

                    }else{
                        SP_spol.setSelection(0);
                    }
                } else {

                }
            }
        });

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(DatumRodjenja.getText()) ||
                        TextUtils.isEmpty(Ime.getText()) ||
                        TextUtils.isEmpty(Prezime.getText()) ||
                        TextUtils.isEmpty(KorisnickoIme.getText()))
                {
                    Toast.makeText(getContext(), "Prazna polja!", Toast.LENGTH_SHORT).show();
                } else {

                    HashMap<String,Object> userToUpdate = new HashMap<>();
                    userToUpdate.put("Ime",Ime.getText().toString());
                    userToUpdate.put("Prezime",Prezime.getText().toString());
                    userToUpdate.put("KorisnickoIme",KorisnickoIme.getText().toString());
                    userToUpdate.put("DatumRodjenja",DatumRodjenja.getText().toString());
                    userToUpdate.put("Spol",SP_spol.getSelectedItem());

                    firestore.collection("Korisnici").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update(userToUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                _Update.Update(null,KorisnickoIme.getText().toString());
                                Toast.makeText(getContext(), "Profil uspjesno izmjenjen", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });
        ProfilnaSlika.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                galerija();
            }
        });
        return v;
    }

    private void galerija() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

}
