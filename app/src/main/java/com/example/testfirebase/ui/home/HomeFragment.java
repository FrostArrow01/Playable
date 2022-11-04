package com.example.testfirebase.ui.home;


import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.testfirebase.R;
import com.example.testfirebase.databinding.FragmentHomeBinding;
import com.example.testfirebase.models.Cancion;
import com.example.testfirebase.models.CancionDocument;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private TextView emailT;
    private FirebaseFirestore db;
    private String  emailPre, providerPre, usuario, nombre, apellidos, biografia;
    private final static int CERRAR_POPUP = 101;
    private SharedPreferences preferences;
    private QuerySnapshot albumes;
    private ArrayList albumesCanciones;
    private ImageView imagenAlbum;
    private List<Cancion> cancionesList;
    private TextView albumtitle;
    private ProgressBar progressBar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();

        getAlbums();



        preferences = (SharedPreferences) this.getActivity().getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE);
        emailPre = preferences.getString("email", null);
        providerPre = preferences.getString("provider", null);



        emailT = view.findViewById(R.id.emailT);
        emailT.setText(emailPre);
    }

    public void getAlbums(){
        progressBar = getView().findViewById(R.id.progressBar);
        albumtitle = getView().findViewById(R.id.albumtitle0);
        imagenAlbum = getView().findViewById(R.id.imagenAlbum);
        albumtitle.setVisibility(View.INVISIBLE);
        imagenAlbum.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        db.collection("albumes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            albumes = task.getResult();
                            for(int i =0;i<albumes.size();i++) {
                                Log.d("Album", albumes.getDocuments().get(i).get("tituloAlbum").toString() +": "+
                                albumes.getDocuments().get(i).get("caratula").toString());

                                cancionesList = albumes.getDocuments().get(i).toObject(CancionDocument.class).getCanciones();
                                for (int j = 0; j < cancionesList.size(); j++)
                                    Log.d("Cancion",cancionesList.get(j).titulo +": " + cancionesList.get(j).url);
                            }


                            albumtitle.setText(albumes.getDocuments().get(0).get("tituloAlbum").toString());


                            Glide.with(getActivity())
                                    .load(albumes.getDocuments().get(0).get("caratula"))
                                    .into(imagenAlbum);

                            progressBar.setVisibility(View.INVISIBLE);
                            albumtitle.setVisibility(View.VISIBLE);
                            imagenAlbum.setVisibility(View.VISIBLE);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            Snackbar.make(getView(), "Error recogiendo los albumes", Snackbar.LENGTH_LONG).show();
                        }
                    }
                });

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}