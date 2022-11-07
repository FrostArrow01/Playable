package com.example.testfirebase.models;

public class Cancion {
    public String titulo;
    public String url;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Cancion() {

    }

    public Cancion(String titulo, String url) {
        this.titulo = titulo;
        this.url = url;
    }
}
