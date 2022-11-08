package com.example.testfirebase.ui.home;


import static android.content.ContentValues.TAG;

import android.content.ClipData;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testfirebase.Adapter;
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

    private RecyclerView albumList;
    private Adapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        setHasOptionsMenu(true);


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


        //Preferences
        preferences = (SharedPreferences) this.getActivity().getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE);
        emailPre = preferences.getString("email", null);
        providerPre = preferences.getString("provider", null);


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView1 = (SearchView) menuItem.getActionView();
        searchView1.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });

        super.onCreateOptionsMenu(menu,inflater);
    }

    //Para el recyclerview
    public void enlazarAdapter(){
        albumList = getView().findViewById(R.id.albumList);
        adapter = new Adapter(getContext(),albumes);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        albumList.setLayoutManager(gridLayoutManager);
        albumList.setAdapter(adapter);

    }

    public void getAlbums(){
        progressBar = getView().findViewById(R.id.progressBar);

        db.collection("albumes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            albumes = task.getResult();
                          /*  for(int i =0;i<albumes.size();i++) {
                                Log.d("Album", albumes.getDocuments().get(i).get("tituloAlbum").toString() + ": " +
                                        albumes.getDocuments().get(i).get("caratula").toString());

                                cancionesList = albumes.getDocuments().get(i).toObject(CancionDocument.class).getCanciones();
                                for (int j = 0; j < cancionesList.size(); j++) {
                                    Log.d("Cancion", cancionesList.get(j).titulo + ": " + cancionesList.get(j).url);

                                }

                            } */
                            progressBar.setVisibility(View.INVISIBLE);
                            enlazarAdapter();

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