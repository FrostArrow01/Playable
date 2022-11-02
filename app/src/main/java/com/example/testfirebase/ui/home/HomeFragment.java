package com.example.testfirebase.ui.home;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.testfirebase.R;
import com.example.testfirebase.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private TextView emailT;
    private FirebaseFirestore db;
    private String  emailPre, providerPre, usuario, nombre, apellidos, biografia;
    private final static int CERRAR_POPUP = 101;
    private SharedPreferences preferences;

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

        preferences = (SharedPreferences) this.getActivity().getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE);
        emailPre = preferences.getString("email", null);
        providerPre = preferences.getString("provider", null);


        getUser();
        emailT = view.findViewById(R.id.emailT);
        emailT.setText(emailPre);

    }

    public void getUser(){
        db.collection("users").document(emailPre).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot it) {
                        usuario = it.get("usuario").toString();
                        nombre = it.get("nombre").toString();
                        apellidos = it.get("apellidos").toString();
                        biografia = it.get("biografia").toString();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}