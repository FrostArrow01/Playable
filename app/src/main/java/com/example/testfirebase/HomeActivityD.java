package com.example.testfirebase;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.internal.NavigationMenuItemView;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivityD extends AppCompatActivity  {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeDBinding binding;
    private SharedPreferences preferences;
    private TextView usuarioD, biografiaD;
    private SharedPreferences.Editor editor;
    private FirebaseFirestore db;
    private String  email, provider, usuario, nombre, apellidos, biografia;
    private DocumentReference usuariosC;
    private ImageView imageButton;
    private final static int CERRAR_POPUP = 101;
    boolean doubleBackToExitPressedOnce = false;
    private DrawerLayout drawer;
    private NavigationMenuItemView nav_salir, nav_ajustes;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Recogemos los datos de la actividad anterior
        Intent i = getIntent();
        email = i.getStringExtra("email");
        provider = i.getStringExtra("provider");

        db = FirebaseFirestore.getInstance();
        getUserandBio();
        if(user.getPhotoUrl() != null){
            db.collection("users").document(email).update("foto", user.getPhotoUrl());
        }

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
                Intent i = new Intent(view.getContext(), CancionesActivity.class);
                i.putExtra("tituloAlbum", "Random");
                i.putExtra("caratula", "Random");
                view.getContext().startActivity(i);
            }
        });
        drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,  R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_ajustes, R.id.nav_salir)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home_activity_d);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

    }

    //Se ejecuta cada vez que se abre el drawer
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home_activity_d);
        getUserandBio();

        nav_salir = findViewById(R.id.nav_salir);
        nav_salir.setOnClickListener(new View.OnClickListener() { //funcion para el boton salir
            @Override
            public void onClick(View view) {
                logOut();
            }
        });


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

    public void getUserandBio(){
        db.collection("users").document(email).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot it) {
                        usuario = it.get("usuario").toString();
                        biografia = it.get("biografia").toString();
                    }
                }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        usuarioD = findViewById(R.id.usuarioDrawer);
                        biografiaD = findViewById(R.id.biografiaDrawer);
                        imageButton = findViewById(R.id.imagenDrawer);
                        if(usuario.matches("") || biografia.matches("")){
                            Toast.makeText(HomeActivityD.this, "Ve a la pestaña mi perfil para completar tus datos", Toast.LENGTH_SHORT).show();
                        }else{
                            usuarioD.setText(usuario);
                            biografiaD.setText(biografia);
                        }
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if(user.getPhotoUrl() != null && imageButton != null) {
                            Glide.with(getApplicationContext())
                                    .load(user.getPhotoUrl())
                                    .into(imageButton);
                        }
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
        finish();
    }

}