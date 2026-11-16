package com.upiiz.dm_jphl_practica02.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class LibroDto implements Serializable {

    @SerializedName("id")
    private int id;

    @SerializedName("titulo")
    private String titulo;

    @SerializedName("autor")
    private String autor;

    @SerializedName("editorial")
    private String editorial;

    @SerializedName("paginas")
    private int paginas;

    @SerializedName("disponible")
    private boolean disponible;

    @SerializedName("_links")
    private Links links;

    public LibroDto() {
    }

    public LibroDto(int id, String titulo, String autor, String editorial, int paginas, boolean disponible) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.editorial = editorial;
        this.paginas = paginas;
        this.disponible = disponible;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo != null ? titulo : "Sin título";
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor != null ? autor : "Autor desconocido";
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getEditorial() {
        return editorial != null ? editorial : "Sin editorial";
    }

    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    public int getPaginas() {
        return paginas;
    }

    public void setPaginas(int paginas) {
        this.paginas = paginas;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public LibroEntity toEntity() {
        return new LibroEntity(id, getTitulo(), getAutor(), getEditorial(), paginas, disponible);
    }

    public static LibroDto fromEntity(LibroEntity entity) {
        if (entity == null) {
            return null;
        }
        return new LibroDto(
                entity.getId(),
                entity.getTitulo(),
                entity.getAutor(),
                entity.getEditorial(),
                entity.getPaginas(),
                entity.isDisponible()
        );
    }
}
