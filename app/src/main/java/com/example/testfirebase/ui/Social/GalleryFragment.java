package com.example.testfirebase.ui.Social;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testfirebase.Adapter;
import com.example.testfirebase.AdapterUsuarios;
import com.example.testfirebase.R;
import com.example.testfirebase.databinding.FragmentGalleryBinding;
import com.example.testfirebase.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment {

    private FirebaseFirestore db;
    private SharedPreferences preferences;
    private String emailActual;


    QuerySnapshot usuariosSnap;
    ArrayList<User> usuarios = new ArrayList<User>();
    private RecyclerView usuariosList;
    private AdapterUsuarios adapter;


    private FragmentGalleryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();

        preferences = (SharedPreferences) this.getActivity().getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE);
        emailActual = preferences.getString("email", null);


        getUsers();
    }

    private void getUsers(){
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            usuariosSnap = task.getResult();


                            enlazarAdapter();

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            Snackbar.make(getView(), "Error recogiendo los albumes", Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void enlazarAdapter(){
        for (int i = 0; i<usuariosSnap.size(); i++) {
            //TODO
            if(usuariosSnap.getDocuments().get(i).get("email").equals(emailActual)){
                continue;
            }

            usuarios.add(usuariosSnap.getDocuments().get(i).toObject(User.class));
        }
        adapter = new AdapterUsuarios(getContext(), usuarios);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1,GridLayoutManager.VERTICAL, false);
        usuariosList = getView().findViewById(R.id.usuariosList);
        usuariosList.setLayoutManager(gridLayoutManager);
        usuariosList.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}