package com.example.testfirebase.models;

public class Album {
    private String caratula;
    private String titulo;

    public Album() {
    }

    public Album(String caratula) {
        this.caratula = caratula;
    }

    public String getCaratula() {
        return caratula;
    }

    public void setCaratula(String caratula) {
        this.caratula = caratula;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
}
