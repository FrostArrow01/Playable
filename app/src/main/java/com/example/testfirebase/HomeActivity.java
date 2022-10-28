package com.example.testfirebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ActionMenuItemView;

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
import android.widget.Toast;

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
    private String email, provider, usuario, nombre, apellidos, edad;
    private DocumentReference usuariosC;
    private Menu esteMenu;
    private final static int CERRAR_POPUP = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTitle("Playable");


        //Recogemos los datos de la actividad anterior
        Intent i = getIntent();
        email = i.getStringExtra("email");
        provider = i.getStringExtra("provider");

        db = FirebaseFirestore.getInstance();
         //recogemos los datos del usuario

        getUser();

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
        esteMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu){
        getUserUsuario();
        esteMenu.getItem(0).setTitle(usuario);
        return true;
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


    public void getUser(){
        db.collection("users").document(email).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot it) {
                        usuario = it.get("usuario").toString();
                        nombre = it.get("nombre").toString();
                        apellidos = it.get("apellidos").toString();
                        edad = it.get("edad").toString();

                    }
                });
    }

    public void getUserUsuario(){ //se hace una petición mas simple para no sobrecargar
        db.collection("users").document(email).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot it) {
                        usuario = it.get("usuario").toString();
                    }
                });
    }


    public void abrirPopupUsuario(){
        Intent popupWindow = new Intent(HomeActivity.this, PopUpUsuario.class);
        popupWindow.putExtra("email", email);
            popupWindow.putExtra("usuario", usuario);
            popupWindow.putExtra("nombre", nombre);
            popupWindow.putExtra("apellidos", apellidos);
            popupWindow.putExtra("edad", edad);
            startActivityForResult(popupWindow, CERRAR_POPUP);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CERRAR_POPUP){
            getUserUsuario();
            esteMenu.getItem(0).setTitle(usuario);
        }
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