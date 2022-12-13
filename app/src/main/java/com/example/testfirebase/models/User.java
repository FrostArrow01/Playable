package com.example.testfirebase.models;

public class User {
    public String albumes;
    public String apellidos;
    public String biografia;
    public String email;
    public String foto;
    public String nombre;
    public String provider;
    public String usuario;

    public String getAlbumes() {
        return albumes;
    }

    public void setAlbumes(String albumes) {
        this.albumes = albumes;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getBiografia() {
        return biografia;
    }

    public String getEmail() {
        return email;
    }

    public String getFoto() {
        return foto;
    }

    public String getNombre() {
        return nombre;
    }

    public String getUsuario() {
        return usuario;
    }

    public User() {
    }

    public User(String apellidos, String biografia, String email, String foto, String nombre, String provider, String usuario) {
        this.apellidos = apellidos;
        this.biografia = biografia;
        this.email = email;
        this.foto = foto;
        this.nombre = nombre;
        this.provider = provider;
        this.usuario = usuario;
    }
}
