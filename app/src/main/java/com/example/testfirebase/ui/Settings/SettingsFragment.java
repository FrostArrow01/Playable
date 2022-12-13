package com.example.testfirebase.ui.Settings;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.testfirebase.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SettingsFragment extends Fragment {

    private SettingsViewModel mViewModel;
    private String emailActual;
    private SharedPreferences preferences;
    private FirebaseFirestore db;


    private LinearLayout cambiarContraS, borrarCuenta;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        preferences = (SharedPreferences) this.getActivity().getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE);
        emailActual = preferences.getString("email", null);

        borrarCuenta = view.findViewById(R.id.borrarCuenta);
        borrarCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDialogo(view);
            }
        });

        cambiarContraS = view.findViewById(R.id.cambiarContraS);
        cambiarContraS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recuperarContrasena(view);
            }
        });




    }

    public void recuperarContrasena(View view){
        if(!emailActual.matches("")){
            FirebaseAuth.getInstance().sendPasswordResetEmail(emailActual)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Snackbar.make(view, "Se ha enviado un email para recuperar tu contraseña", Snackbar.LENGTH_LONG).show();
                            }else{
                                Snackbar.make(view, "Ha habido un error.", Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
        }else{
            Snackbar.make(view, "Introduce el correo electrónico", Snackbar.LENGTH_LONG).show();
        }

    }

    public void mostrarDialogo(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("ELIMINAR CUENTA");
        builder.setMessage("¿Quieres continuar? Este cambio no es reversible")
                .setPositiveButton("Sí, estoy seguro", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        borrarUsuario();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }

    public void borrarUsuario(){ //Funcion apuntes para borrar usuario
        db.collection("users").document(emailActual).delete();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child("FotosPerfilUsuarios")
                .child(uid + ".jpeg");
        FirebaseAuth.getInstance().getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                reference.delete();
                getActivity().finish();
            }
        });

    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
    }

}