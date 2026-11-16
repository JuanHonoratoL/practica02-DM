package com.upiiz.dm_jphl_practica02.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.upiiz.dm_jphl_practica02.models.LibroEntity;

import java.util.List;

@Dao
public interface LibroDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertarFavorito(LibroEntity libro);

    @Query("SELECT * FROM libros_favoritos ORDER BY fecha_guardado DESC")
    List<LibroEntity> obtenerTodosLosFavoritos();

    @Query("SELECT * FROM libros_favoritos WHERE id = :id LIMIT 1")
    LibroEntity obtenerFavoritoPorId(int id);

    @Query("SELECT EXISTS(SELECT 1 FROM libros_favoritos WHERE id = :id)")
    boolean esFavorito(int id);

    @Query("SELECT id FROM libros_favoritos")
    List<Integer> obtenerIdsFavoritos();

    @Update
    void actualizarFavorito(LibroEntity libro);

    @Delete
    void eliminarFavorito(LibroEntity libro);

    @Query("DELETE FROM libros_favoritos WHERE id = :id")
    void eliminarFavoritoPorId(int id);

    @Query("SELECT COUNT(*) FROM libros_favoritos")
    int contarFavoritos();
}
