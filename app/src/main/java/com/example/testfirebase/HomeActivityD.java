package com.example.testfirebase;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.testfirebase.databinding.ActivityHomeDBinding;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivityD extends AppCompatActivity  {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeDBinding binding;
    private SharedPreferences preferences;
    private TextView emailT, passwordT, providerT;
    private EditText usuarioE;
    private SharedPreferences.Editor editor;
    private FirebaseFirestore db;
    private String  email, provider, usuario, nombre, apellidos, biografia;
    private DocumentReference usuariosC;
    private Menu estemenu;
    private final static int CERRAR_POPUP = 101;
    boolean doubleBackToExitPressedOnce = false;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //Recogemos los datos de la actividad anterior
        Intent i = getIntent();
        email = i.getStringExtra("email");
        provider = i.getStringExtra("provider");


        //Shared preferences
        preferences = (SharedPreferences) getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString("email", email);
        editor.putString("provider", provider);
        editor.apply();

        //Fragments
        binding = ActivityHomeDBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarHomeActivityD.toolbar);
        binding.appBarHomeActivityD.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Próximamente: reproducir muscia aleatoria", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_ajustes, R.id.nav_salir)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home_activity_d);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        //Evento analytics
        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(this);
        Bundle b = new Bundle();
        b.putString("mensaje","Integracion de firebase funciona");
        analytics.logEvent("PantallaPrincipal", b);
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home_activity_d);

        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }



    @Override
    public void onBackPressed() { //hace falta pulsar 2veces la tecla de volver para salir
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            this.finishAffinity();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Pulsa atrás de nuevo para salir", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    public void borrarUsuario(View view){ //Funcion apuntes para borrar usuario
        db.collection("users").document(email).delete();
    }


    public void logOut(){
        FirebaseAuth.getInstance().signOut();
        editor.clear();
        editor.apply();
        finish();
    }

}