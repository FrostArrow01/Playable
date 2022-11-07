package com.example.testfirebase.models;

import java.util.List;

public class CancionDocument {
    List<Cancion> canciones;

    public List<Cancion> getCanciones() {
        return canciones;
    }

    public void setCanciones(List<Cancion> canciones) {
        this.canciones = canciones;
    }

    public CancionDocument() {

    }

    public CancionDocument(List<Cancion> canciones) {
        this.canciones = canciones;
    }
}
