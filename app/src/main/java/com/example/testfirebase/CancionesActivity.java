package com.example.testfirebase;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testfirebase.models.Album;
import com.example.testfirebase.models.Cancion;
import com.example.testfirebase.models.CancionDocument;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.List;

public class CancionesActivity extends AppCompatActivity {
    private String titulo;
    private String caratula;
    private FirebaseFirestore db;
    private QuerySnapshot album, albumes;
    private List<Cancion> cancionesList;
    private Album albumRandom;

    private RecyclerView cancionesRecy;
    private AdapterCanciones adapterCanciones;
    private MediaPlayer mp;
    private long currentSongLength;
    private ProgressBar progressBar, pb_loader;
    private TextView tb_title, iv_time;
    private ImageView iv_pause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canciones);

        db = FirebaseFirestore.getInstance();
        cancionesRecy = findViewById(R.id.cancionesRecy);

        //Para el toolbar
        progressBar = findViewById(R.id.pb_loader);
        tb_title = findViewById(R.id.tb_title);
        iv_pause = findViewById(R.id.iv_pause);

        Intent intent = getIntent();
        titulo =  intent.getStringExtra("tituloAlbum");
        caratula = intent.getStringExtra("caratula");

        if(titulo.equals("Random")){
            getAlbumAleatorio();
        }else{
            getAlbumSimple(titulo);
            setTitle(Html.fromHtml("<font color=\"black\">"+"Canciones de " + titulo + "</font>"));
        }


        //Para mostrar la flecha de volver
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#fbdc4c")));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void getAlbumSimple(String titulo){
            db.collection("albumes").whereEqualTo("tituloAlbum", titulo)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                album = task.getResult();
                                cancionesList = album.getDocuments().get(0).toObject(CancionDocument.class).getCanciones();
                                caratula = album.getDocuments().get(0).get("caratula").toString();
                                enlazarAdapter();
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                                Snackbar.make(findViewById(android.R.id.content), "Error recogiendo los albumes", Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
    }

    public void getAlbumAleatorio(){
        db.collection("albumes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            albumes = task.getResult();
                            int numAl = (int) (Math.random()*albumes.size());
                            cancionesList = albumes.getDocuments().get(numAl).toObject(CancionDocument.class).getCanciones();
                            setTitle(Html.fromHtml("<font color=\"black\">"+"Canciones de " + albumes.getDocuments().get(numAl).get("tituloAlbum")+ "</font>"));
                            caratula = albumes.getDocuments().get(numAl).get("caratula").toString();
                            enlazarAdapter();
                        } else {
                            Log.d(TAG, "Error getting random document: ", task.getException());
                            Snackbar.make(findViewById(android.R.id.content), "Error recogiendo los albumes", Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void enlazarAdapter(){
        adapterCanciones = new AdapterCanciones(this, cancionesList, caratula, new AdapterCanciones.RecyclerItemClickListener() {
            @Override
            public void onClickListener(Cancion cancion, Long duracion ,int position) throws IOException {
                prepareSong(cancion, duracion);

                //cambiar
                changeSelectedSong(position);
            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,1, GridLayoutManager.VERTICAL, false);
        cancionesRecy.setLayoutManager(gridLayoutManager);
        adapterCanciones.setSelectedPosition(-1);
        cancionesRecy.setAdapter(adapterCanciones);

        //Inicializacion de mediaplayer
        mp = new MediaPlayer();
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                togglePlay(mp);
            }
        });
    }

    public void prepareSong(Cancion cancion, Long duracion){
        currentSongLength = duracion;
        pb_loader.setVisibility(View.VISIBLE);
        tb_title.setVisibility(View.GONE);
        tb_title.setText(cancion.titulo);


        mp.reset();
        try {
            mp.setDataSource(cancion.url);
            mp.prepareAsync();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void togglePlay(MediaPlayer mediaPlayer){
        if(mediaPlayer.isPlaying()){
            mp.stop();
            mp.reset();
            iv_pause.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_baseline_pause_circle_24));
        }else{
            pb_loader.setVisibility(View.GONE);
            tb_title.setVisibility(View.VISIBLE);
            mp.start();
            iv_pause.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_baseline_play_circle_24));
        }
    }

    public void changeSelectedSong(int index){
        adapterCanciones.notifyItemChanged(adapterCanciones.getSelectedPosition());
        adapterCanciones.setSelectedPosition(index);
        adapterCanciones.notifyItemChanged(index);

    }

}
