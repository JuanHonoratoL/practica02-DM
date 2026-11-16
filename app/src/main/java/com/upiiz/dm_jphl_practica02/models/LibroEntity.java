package com.upiiz.dm_jphl_practica02.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "libros_favoritos")
public class LibroEntity implements Serializable {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "titulo")
    private String titulo;

    @ColumnInfo(name = "autor")
    private String autor;

    @ColumnInfo(name = "editorial")
    private String editorial;

    @ColumnInfo(name = "paginas")
    private int paginas;

    @ColumnInfo(name = "disponible")
    private boolean disponible;

    @ColumnInfo(name = "nota_personal")
    private String notaPersonal;

    @ColumnInfo(name = "fecha_guardado")
    private long fechaGuardado;

    public LibroEntity() {
    }

    @Ignore
    public LibroEntity(int id, String titulo, String autor, String editorial, int paginas, boolean disponible) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.editorial = editorial;
        this.paginas = paginas;
        this.disponible = disponible;
        this.notaPersonal = "";
        this.fechaGuardado = System.currentTimeMillis();
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

    public String getNotaPersonal() {
        return notaPersonal != null ? notaPersonal : "";
    }

    public void setNotaPersonal(String notaPersonal) {
        this.notaPersonal = notaPersonal;
    }

    public long getFechaGuardado() {
        return fechaGuardado;
    }

    public void setFechaGuardado(long fechaGuardado) {
        this.fechaGuardado = fechaGuardado;
    }
}
