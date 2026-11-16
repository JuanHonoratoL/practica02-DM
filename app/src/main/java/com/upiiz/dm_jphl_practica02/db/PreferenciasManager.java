package com.upiiz.dm_jphl_practica02.db;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class PreferenciasManager {

    private static final String PREF_NAME = "ExploradorLibrosPrefs";
    private static final String KEY_NOMBRE_USUARIO = "nombre_usuario";
    private static final String KEY_MODO_OSCURO = "modo_oscuro";
    private static final String KEY_ULTIMA_SECCION = "ultima_seccion";
    private static final String KEY_ULTIMO_LIBRO_ID = "ultimo_libro_id";
    private static final String KEY_CONFIGURACION_COMPLETADA = "configuracion_completada";

    public static final String SECCION_BIENVENIDA = "Bienvenida";
    public static final String SECCION_CATALOGO = "Catálogo";
    public static final String SECCION_FAVORITOS = "Favoritos";
    public static final String SECCION_DETALLE = "Detalle";

    private final SharedPreferences sharedPreferences;

    public PreferenciasManager(Context context) {
        this.sharedPreferences = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void guardarNombreUsuario(String nombre) {
        sharedPreferences.edit()
                .putString(KEY_NOMBRE_USUARIO, nombre != null ? nombre.trim() : "")
                .apply();
    }

    public String obtenerNombreUsuario() {
        return sharedPreferences.getString(KEY_NOMBRE_USUARIO, "");
    }

    public void guardarModoOscuro(boolean activarModoOscuro) {
        sharedPreferences.edit()
                .putBoolean(KEY_MODO_OSCURO, activarModoOscuro)
                .apply();
        aplicarTema(activarModoOscuro);
    }

    public boolean esModoOscuro() {
        return sharedPreferences.getBoolean(KEY_MODO_OSCURO, false);
    }

    public void aplicarTemaGuardado() {
        aplicarTema(esModoOscuro());
    }

    public static void aplicarTema(boolean modoOscuro) {
        int targetMode = modoOscuro
                ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO;
        if (AppCompatDelegate.getDefaultNightMode() != targetMode) {
            AppCompatDelegate.setDefaultNightMode(targetMode);
        }
    }

    public void guardarConfiguracionCompletada(boolean completada) {
        sharedPreferences.edit()
                .putBoolean(KEY_CONFIGURACION_COMPLETADA, completada)
                .apply();
    }

    public boolean esConfiguracionCompletada() {
        return sharedPreferences.getBoolean(KEY_CONFIGURACION_COMPLETADA, false);
    }

    public void guardarUltimaSeccion(String seccion) {
        sharedPreferences.edit()
                .putString(KEY_ULTIMA_SECCION, seccion)
                .apply();
    }

    public String obtenerUltimaSeccion() {
        return sharedPreferences.getString(KEY_ULTIMA_SECCION, SECCION_CATALOGO);
    }

    public void guardarUltimoLibroId(int libroId) {
        sharedPreferences.edit()
                .putInt(KEY_ULTIMO_LIBRO_ID, libroId)
                .apply();
    }

    public int obtenerUltimoLibroId() {
        return sharedPreferences.getInt(KEY_ULTIMO_LIBRO_ID, -1);
    }
}
