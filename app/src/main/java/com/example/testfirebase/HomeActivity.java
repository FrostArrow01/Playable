package com.example.testfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.HashMap;

public class HomeActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    private TextView emailT, passwordT, providerT;
    private EditText usuario, address;
    private SharedPreferences.Editor editor;
    private Button saveButton, popupB;
    private FirebaseFirestore db;
    private String email, provider;
    private DocumentReference usuariosC;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTitle("Playable");

        db = FirebaseFirestore.getInstance();
        popupB = findViewById(R.id.popupB);



        //Recogemos los datos de la actividad anterior
        Intent i = getIntent();
        email = i.getStringExtra("email");
        provider = i.getStringExtra("provider");

        emailT = findViewById(R.id.emailT);
        providerT = findViewById(R.id.proovedor);
        emailT.setText(email);
        providerT.setText(provider);

        //Shared preferences
         preferences = (SharedPreferences) getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE);
         editor = preferences.edit();
         editor.putString("email", email);
         editor.putString("provider", provider);
         editor.apply();



        //Evento analytics
        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(this);
        Bundle b = new Bundle();
        b.putString("mensaje","Integracion de firebase funciona");
        analytics.logEvent("PantallaPrincipal", b);

    }


    public void abrirPopupUsuario(View view){
        Intent popupWindow = new Intent(HomeActivity.this, PopUpUsuario.class);
        popupWindow.putExtra("email", email);
        startActivity(popupWindow);
    }

    public void guardarUsuario(View view){ //Funcion apuntes para guardar usuario y campos



        //Con update podemos editar campos individualmente
        db.collection("users").document(email);




    }

    public void recuperarUsuario(View view){ //Funcion apuntes para recuperar usuario
        db.collection("users").document(email).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot it) {
                        usuario.setText(it.get("usuario").toString());
                        address.setText(it.get("address").toString());
                    }
                });
    }

    public void borrarUsuario(View view){ //Funcion apuntes para borrar usuario
        db.collection("users").document(email).delete();
    }

    public void logOut(View view){
        FirebaseAuth.getInstance().signOut();
        editor.clear();
        editor.apply();
        onBackPressed();
    }
}