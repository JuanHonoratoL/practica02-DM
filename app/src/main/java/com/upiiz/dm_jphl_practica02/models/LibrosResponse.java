package com.upiiz.dm_jphl_practica02.models;

import com.google.gson.annotations.SerializedName;
import java.util.Collections;
import java.util.List;

public class LibrosResponse {

    @SerializedName("_embedded")
    private EmbeddedLibros embedded;

    @SerializedName("_links")
    private Links links;

    public LibrosResponse() {
    }

    public EmbeddedLibros getEmbedded() {
        return embedded;
    }

    public void setEmbedded(EmbeddedLibros embedded) {
        this.embedded = embedded;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public List<LibroDto> getLibros() {
        if (embedded != null && embedded.getLibroDtoList() != null) {
            return embedded.getLibroDtoList();
        }
        return Collections.emptyList();
    }
}
