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
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.testfirebase.Utils.Utils;
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

    //Recyclerview y toolbar
    private RecyclerView cancionesRecy;
    private AdapterCanciones adapterCanciones;
    private MediaPlayer mediaPlayer;
    private long currentSongLength;
    private int currentIndex;
    private ProgressBar pb_loader;
    private TextView tb_title, iv_time;
    private ImageView iv_pause, iv_previous, iv_next;
    private SeekBar seekBar;
    private ProgressBar progressBar2;
    private boolean firstLaunch = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canciones);

        db = FirebaseFirestore.getInstance();
        cancionesRecy = findViewById(R.id.cancionesRecy);

        //Para el toolbar
        tb_title = findViewById(R.id.tb_title);
        iv_pause = findViewById(R.id.iv_pause);
        iv_next = findViewById(R.id.iv_next);
        iv_previous = findViewById(R.id.iv_previous);
        pb_loader = findViewById(R.id.pb_loader);
        iv_time = findViewById(R.id.iv_time);
        seekBar = findViewById(R.id.seekbar);
        progressBar2 = findViewById(R.id.progressBar2);

        //Coger random o uno especifico depende de donde venga
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
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    public void getAlbumSimple(String titulo){
            db.collection("albumes").whereEqualTo("tituloAlbum", titulo)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            progressBar2.setVisibility(View.INVISIBLE);
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
                        progressBar2.setVisibility(View.INVISIBLE);
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
                currentSongLength = duracion;
                firstLaunch = false;
                prepareSong(cancion);

                //cambiar
                changeSelectedSong(position);
            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,1, GridLayoutManager.VERTICAL, false);
        cancionesRecy.setLayoutManager(gridLayoutManager);
        adapterCanciones.setSelectedPosition(-1);
        cancionesRecy.setAdapter(adapterCanciones);

        //Inicializacion de mediaplayer
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                togglePlay(mp);
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if(currentIndex + 1 < cancionesList.size()){
                    Cancion siguiente = cancionesList.get(currentIndex+1);
                    changeSelectedSong(currentIndex+1);

                    try {
                        prepareSong(siguiente);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    Cancion siguiente = cancionesList.get(0);
                    changeSelectedSong(0);
                    try {
                        prepareSong(siguiente);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        //gestion de seekbar
        handleSeekbar();

        //controles
        pushPlay();
        pushPrevious();
        pushNext();
    }

    public void prepareSong(Cancion cancion) throws IOException {
        currentSongLength = cancion.getDuration();
        pb_loader.setVisibility(View.VISIBLE);
        tb_title.setVisibility(View.GONE);
        iv_time.setVisibility(View.GONE);
        tb_title.setText(cancion.titulo + "  -  ");
        iv_time.setText(Utils.convertDuration(currentSongLength));


        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(cancion.url);
            mediaPlayer.prepareAsync();
        }catch (IOException e){
            e.printStackTrace();
        }
    } //actualiza estilos y settea la cancion que se va a reproducir

    public void handleSeekbar(){
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(mediaPlayer != null && b){
                    mediaPlayer.seekTo(i*1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    } //actualiza la musica al mover la barra

    public void togglePlay(MediaPlayer mp){
        if(mp.isPlaying()){
            mp.stop();
            mp.reset();
            //iv_pause.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_baseline_play_circle_24));
        }else{
            pb_loader.setVisibility(View.GONE);
            tb_title.setVisibility(View.VISIBLE);
            iv_time.setVisibility(View.VISIBLE);
            mp.start();
            iv_pause.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_baseline_pause_circle_24));
            Handler mHandler = new Handler();
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    seekBar.setMax((int) currentSongLength/1000);
                    int mCurrentPosition = mediaPlayer.getCurrentPosition()/1000;
                    seekBar.setProgress(mCurrentPosition);
                    iv_time.setText(Utils.convertDuration((long) mediaPlayer.getCurrentPosition()));
                    mHandler.postDelayed(this, 1000);
                }
            });

        }
    } //inicia una cancion pausando la anterior

    public void changeSelectedSong(int index){
        adapterCanciones.notifyItemChanged(adapterCanciones.getSelectedPosition());
        currentIndex = index;
        adapterCanciones.setSelectedPosition(currentIndex);
        adapterCanciones.notifyItemChanged(currentIndex);


    } //ilumina la cancion que esta sonando

    //controles play previous next
    public void pushPlay(){
        iv_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying() && mediaPlayer != null){
                    iv_pause.setImageDrawable(ContextCompat.getDrawable(CancionesActivity.this, R.drawable.ic_baseline_play_circle_24));
                    mediaPlayer.pause();
                }else{
                    if(firstLaunch){
                        Cancion cancion = cancionesList.get(0);
                        changeSelectedSong(0);
                        try {
                            prepareSong(cancion);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else{
                        mediaPlayer.start();
                        firstLaunch = false;
                    }
                    iv_pause.setImageDrawable(ContextCompat.getDrawable(CancionesActivity.this, R.drawable.ic_baseline_pause_circle_24));
                }
            }
        });
    }

    public void pushPrevious(){
        iv_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstLaunch = false;
                if(mediaPlayer != null){
                    if(currentIndex -1 >= 0){
                        Cancion previous = cancionesList.get(currentIndex-1);
                        changeSelectedSong(currentIndex-1);
                        try {
                            prepareSong(previous);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else{
                        changeSelectedSong(cancionesList.size()-1);
                        try {
                            prepareSong(cancionesList.get(cancionesList.size()-1));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        });
    }

    public void pushNext(){
        iv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstLaunch = false;
                if(mediaPlayer != null){
                    if(currentIndex +1 < cancionesList.size()){
                        Cancion siguiente = cancionesList.get(currentIndex+1);
                        changeSelectedSong(currentIndex+1);
                        try {
                            prepareSong(siguiente);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else{
                        changeSelectedSong(0);
                        try {
                            prepareSong(cancionesList.get(0));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer = new MediaPlayer();
        finish();
    }
}
