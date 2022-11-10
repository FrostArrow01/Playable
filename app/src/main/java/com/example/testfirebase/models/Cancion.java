package com.example.testfirebase.models;

import android.media.MediaPlayer;

import java.io.IOException;

public class Cancion {
    public String titulo;
    public String url;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Long getDuration() throws IOException {
        MediaPlayer mp = new MediaPlayer();

            mp.setDataSource(url);
            mp.prepare();

        return Long.valueOf(mp.getDuration());
    }

    public Cancion(){

    }

    public Cancion(String titulo, String url) {
        this.titulo = titulo;
        this.url = url;
    }
}
