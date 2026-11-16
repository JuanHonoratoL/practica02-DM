package com.upiiz.dm_jphl_practica02.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class EmbeddedLibros {

    @SerializedName("libroDtoList")
    private List<LibroDto> libroDtoList;

    public EmbeddedLibros() {
    }

    public List<LibroDto> getLibroDtoList() {
        return libroDtoList;
    }

    public void setLibroDtoList(List<LibroDto> libroDtoList) {
        this.libroDtoList = libroDtoList;
    }
}
