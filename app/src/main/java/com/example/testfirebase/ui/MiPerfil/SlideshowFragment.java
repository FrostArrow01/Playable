package com.example.testfirebase.ui.MiPerfil;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.testfirebase.HomeActivityD;
import com.example.testfirebase.R;
import com.example.testfirebase.databinding.FragmentSlideshowBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SlideshowFragment extends Fragment {

    private FragmentSlideshowBinding binding;
    private EditText emailE, usuarioE, nombreE, apellidosE, biografiaE;
    private Button botonG;
    private FirebaseFirestore db;
    private String  emailPre, providerPre, usuario, nombre, apellidos, biografia;
    private final static int CERRAR_POPUP = 101;
    private SharedPreferences preferences;
    private TextView usuarioDrawer, biografiaDrawer;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SlideshowViewModel slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();

        preferences = (SharedPreferences) this.getActivity().getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE);
        emailPre = preferences.getString("email", null);
        providerPre = preferences.getString("provider", null);

        db.collection("users").document(emailPre).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot it) {
                        usuario = it.get("usuario").toString();
                        nombre = it.get("nombre").toString();
                        apellidos = it.get("apellidos").toString();
                        biografia = it.get("biografia").toString();

                        emailE = view.findViewById(R.id.emailE);
                        emailE.setText(emailPre);
                        usuarioE = view.findViewById(R.id.usuarioE);
                        usuarioE.setText(usuario);
                        nombreE = view.findViewById(R.id.nombreE);
                        nombreE.setText(nombre);
                        apellidosE = view.findViewById(R.id.apellidosE);
                        apellidosE.setText(apellidos);
                        biografiaE = view.findViewById(R.id.biografiaE);
                        biografiaE.setText(biografia);

                    }
                });

        botonG = view.findViewById(R.id.guardarB);
        botonG.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(usuarioE.getText().toString().matches("") || nombreE.getText().toString().matches("") || apellidosE.getText().toString().matches("") || biografiaE.getText().toString().matches("")){
                    Snackbar.make(view, "Asegurate de rellenar todos los campos", Snackbar.LENGTH_LONG).show();
                }else{
                    try {
                        db.collection("users").document(emailPre).update("usuario", usuarioE.getText().toString());
                        db.collection("users").document(emailPre).update("nombre", nombreE.getText().toString());
                        db.collection("users").document(emailPre).update("apellidos", apellidosE.getText().toString());
                        db.collection("users").document(emailPre).update("biografia", biografiaE.getText().toString());
                        Snackbar.make(view, "Los datos se han guardado correctamente", Snackbar.LENGTH_LONG).show();

                    }catch (Exception e){
                        Snackbar.make(view, "¡Ha habido un error al guardar los datos!", Snackbar.LENGTH_LONG).show();
                    }
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