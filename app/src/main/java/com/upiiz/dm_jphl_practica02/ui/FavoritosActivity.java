package com.upiiz.dm_jphl_practica02.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.upiiz.dm_jphl_practica02.MainActivity;
import com.upiiz.dm_jphl_practica02.R;
import com.upiiz.dm_jphl_practica02.adapters.LibroFavoritoAdapter;
import com.upiiz.dm_jphl_practica02.db.AppDatabase;
import com.upiiz.dm_jphl_practica02.db.LibroDao;
import com.upiiz.dm_jphl_practica02.db.PreferenciasManager;
import com.upiiz.dm_jphl_practica02.models.LibroDto;
import com.upiiz.dm_jphl_practica02.models.LibroEntity;

import java.util.List;
import java.util.Locale;

public class FavoritosActivity extends AppCompatActivity implements LibroFavoritoAdapter.OnFavoritoInteractionListener {

    private RecyclerView rvFavoritos;
    private LinearLayout layoutVacioFavoritos;
    private TextView tvSubtituloFavoritos;
    private TextView tvSaludoFavoritos;

    private LibroFavoritoAdapter adapter;
    private LibroDao libroDao;
    private PreferenciasManager preferenciasManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferenciasManager = new PreferenciasManager(this);
        preferenciasManager.aplicarTemaGuardado();
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_favoritos);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainFavoritos), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        preferenciasManager.guardarUltimaSeccion(PreferenciasManager.SECCION_FAVORITOS);
        libroDao = AppDatabase.getInstance(this).libroDao();

        inicializarVistas();
        actualizarSaludoLector();
        cargarFavoritosGuardados();
    }

    @Override
    protected void onResume() {
        super.onResume();
        preferenciasManager.guardarUltimaSeccion(PreferenciasManager.SECCION_FAVORITOS);
        actualizarSaludoLector();
        cargarFavoritosGuardados();
    }

    private void inicializarVistas() {
        ImageButton btnConfiguracionTopFavoritos = findViewById(R.id.btnConfiguracionTopFavoritos);
        MaterialButton btnExplorarDesdeFavoritos = findViewById(R.id.btnExplorarDesdeFavoritos);
        LinearLayout navCatalogoFav = findViewById(R.id.navCatalogoFav);
        LinearLayout navConfiguracionFav = findViewById(R.id.navConfiguracionFav);

        rvFavoritos = findViewById(R.id.rvFavoritos);
        layoutVacioFavoritos = findViewById(R.id.layoutVacioFavoritos);
        tvSubtituloFavoritos = findViewById(R.id.tvSubtituloFavoritos);
        tvSaludoFavoritos = findViewById(R.id.tvSaludoFavoritos);

        adapter = new LibroFavoritoAdapter(this);
        rvFavoritos.setLayoutManager(new GridLayoutManager(this, 2));
        rvFavoritos.setAdapter(adapter);

        View.OnClickListener irACatalogoListener = v -> {
            Intent intent = new Intent(this, CatalogoActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        };
        btnExplorarDesdeFavoritos.setOnClickListener(irACatalogoListener);
        navCatalogoFav.setOnClickListener(irACatalogoListener);

        View.OnClickListener irAConfiguracionListener = v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(MainActivity.EXTRA_MODO_EDICION, true);
            startActivity(intent);
        };
        btnConfiguracionTopFavoritos.setOnClickListener(irAConfiguracionListener);
        navConfiguracionFav.setOnClickListener(irAConfiguracionListener);
    }

    private void actualizarSaludoLector() {
        String nombre = preferenciasManager.obtenerNombreUsuario();
        if (nombre != null && !nombre.isEmpty()) {
            tvSaludoFavoritos.setText(String.format("Colección de %s", nombre));
        } else {
            tvSaludoFavoritos.setText("Colección Personal");
        }
    }

    private void cargarFavoritosGuardados() {
        AppDatabase.databaseExecutor.execute(() -> {
            List<LibroEntity> favoritos = libroDao.obtenerTodosLosFavoritos();
            runOnUiThread(() -> {
                adapter.actualizarFavoritos(favoritos);
                if (favoritos.isEmpty()) {
                    rvFavoritos.setVisibility(View.GONE);
                    layoutVacioFavoritos.setVisibility(View.VISIBLE);
                    tvSubtituloFavoritos.setText("Aún no has guardado ejemplares en tu estante");
                } else {
                    rvFavoritos.setVisibility(View.VISIBLE);
                    layoutVacioFavoritos.setVisibility(View.GONE);
                    tvSubtituloFavoritos.setText(String.format(Locale.getDefault(),
                            "%d ejemplar(es) en tu colección personal", favoritos.size()));
                }
            });
        });
    }

    @Override
    public void onVerDetalleFavorito(LibroEntity favorito) {
        LibroDto dto = LibroDto.fromEntity(favorito);
        Intent intent = new Intent(this, DetalleLibroActivity.class);
        intent.putExtra(DetalleLibroActivity.EXTRA_LIBRO_DTO, dto);
        intent.putExtra(DetalleLibroActivity.EXTRA_LIBRO_ID, favorito.getId());
        startActivity(intent);
    }

    @Override
    public void onEliminarFavorito(LibroEntity favorito) {
        new AlertDialog.Builder(this)
                .setTitle("Retirar de Favoritos")
                .setMessage("¿Deseas retirar \"" + favorito.getTitulo() + "\" de tu estante personal?")
                .setPositiveButton("Retirar", (dialog, which) ->
                        AppDatabase.databaseExecutor.execute(() -> {
                            libroDao.eliminarFavorito(favorito);
                            runOnUiThread(() -> {
                                Toast.makeText(this,
                                        "Ejemplar retirado de tu colección", Toast.LENGTH_SHORT).show();
                                cargarFavoritosGuardados();
                            });
                        }))
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
