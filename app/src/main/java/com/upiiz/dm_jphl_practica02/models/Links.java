package com.upiiz.dm_jphl_practica02.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Links implements Serializable {

    @SerializedName("self")
    private Link self;

    @SerializedName("libros")
    private Link libros;

    @SerializedName("actualizar")
    private Link actualizar;

    @SerializedName("eliminar")
    private Link eliminar;

    public Links() {
    }

    public Link getSelf() {
        return self;
    }

    public void setSelf(Link self) {
        this.self = self;
    }

    public Link getLibros() {
        return libros;
    }

    public void setLibros(Link libros) {
        this.libros = libros;
    }

    public Link getActualizar() {
        return actualizar;
    }

    public void setActualizar(Link actualizar) {
        this.actualizar = actualizar;
    }

    public Link getEliminar() {
        return eliminar;
    }

    public void setEliminar(Link eliminar) {
        this.eliminar = eliminar;
    }
}
