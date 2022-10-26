package com.example.testfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    private TextView emailT, passwordT, providerT;
    private EditText usuarioE;
    private SharedPreferences.Editor editor;
    private Button saveButton, popupB;
    private FirebaseFirestore db;
    private String email, provider, usuario;
    private DocumentReference usuariosC;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTitle("Playable");

        db = FirebaseFirestore.getInstance();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_perfil:
                abrirPopupUsuario();
                break;
            case R.id.action_logout:
                logOut();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void abrirPopupUsuario(){
        Intent popupWindow = new Intent(HomeActivity.this, PopUpUsuario.class);
        popupWindow.putExtra("email", email);
        db.collection("users").document(email).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot it) {
                        popupWindow.putExtra("usuario", it.get("usuario").toString());
                        usuario = it.get("usuario").toString();
                        popupWindow.putExtra("nombre", it.get("nombre").toString());
                        popupWindow.putExtra("apellidos", it.get("apellidos").toString());
                        popupWindow.putExtra("edad", it.get("edad").toString());
                        startActivity(popupWindow);
                    }
                });
    }

    public void guardarUsuario(View view){ //Funcion apuntes para guardar usuario y campos



        //Con update podemos editar campos individualmente
        db.collection("users").document(email);




    }

    public void recuperarUsuario(){ //Funcion apuntes para recuperar usuario
        db.collection("users").document(email).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot it) {
                        usuarioE.setText(it.get("usuario").toString());

                    }
                });
    }

    public void borrarUsuario(View view){ //Funcion apuntes para borrar usuario
        db.collection("users").document(email).delete();
    }

    public void logOut(){
        FirebaseAuth.getInstance().signOut();
        editor.clear();
        editor.apply();
        onBackPressed();
    }
}