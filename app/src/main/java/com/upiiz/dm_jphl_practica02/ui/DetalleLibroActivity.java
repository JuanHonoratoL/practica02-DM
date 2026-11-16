package com.upiiz.dm_jphl_practica02.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;
import com.upiiz.dm_jphl_practica02.MainActivity;
import com.upiiz.dm_jphl_practica02.R;
import com.upiiz.dm_jphl_practica02.adapters.LibroCatalogoAdapter;
import com.upiiz.dm_jphl_practica02.api.RetrofitClient;
import com.upiiz.dm_jphl_practica02.db.AppDatabase;
import com.upiiz.dm_jphl_practica02.db.LibroDao;
import com.upiiz.dm_jphl_practica02.db.PreferenciasManager;
import com.upiiz.dm_jphl_practica02.models.LibroDto;
import com.upiiz.dm_jphl_practica02.models.LibroEntity;
import com.upiiz.dm_jphl_practica02.models.LibroRequest;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetalleLibroActivity extends AppCompatActivity {

    public static final String EXTRA_LIBRO_DTO = "extra_libro_dto";
    public static final String EXTRA_LIBRO_ID = "extra_libro_id";

    private View viewDetalleSpine;
    private TextView tvDetalleId;
    private TextView tvDetalleDisponibilidad;
    private TextView tvDetalleTitulo;
    private TextView tvDetalleAutor;
    private TextView tvDetalleEditorial;
    private TextView tvDetallePaginas;
    private TextView tvDetalleEstadoFavorito;
    private TextView tvSubtituloDetalle;
    private MaterialButton btnToggleFavoritoDetalle;
    private ProgressBar progressDetalle;

    private LibroDao libroDao;
    private PreferenciasManager preferenciasManager;
    private LibroDto libroActual;
    private int libroId = -1;
    private boolean esFavoritoActual = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferenciasManager = new PreferenciasManager(this);
        preferenciasManager.aplicarTemaGuardado();
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_detalle_libro);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainDetalle), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        libroDao = AppDatabase.getInstance(this).libroDao();

        inicializarVistas();
        obtenerDatosIntent();
    }

    private void inicializarVistas() {
        ImageButton btnVolverDetalle = findViewById(R.id.btnVolverDetalle);
        ImageButton btnRefrescarDetalle = findViewById(R.id.btnRefrescarDetalle);
        viewDetalleSpine = findViewById(R.id.viewDetalleSpine);
        tvDetalleId = findViewById(R.id.tvDetalleId);
        tvDetalleDisponibilidad = findViewById(R.id.tvDetalleDisponibilidad);
        tvDetalleTitulo = findViewById(R.id.tvDetalleTitulo);
        tvDetalleAutor = findViewById(R.id.tvDetalleAutor);
        tvDetalleEditorial = findViewById(R.id.tvDetalleEditorial);
        tvDetallePaginas = findViewById(R.id.tvDetallePaginas);
        tvDetalleEstadoFavorito = findViewById(R.id.tvDetalleEstadoFavorito);
        tvSubtituloDetalle = findViewById(R.id.tvSubtituloDetalle);
        btnToggleFavoritoDetalle = findViewById(R.id.btnToggleFavoritoDetalle);
        MaterialButton btnEditarLibroApi = findViewById(R.id.btnEditarLibroApi);
        MaterialButton btnEliminarLibroApi = findViewById(R.id.btnEliminarLibroApi);
        progressDetalle = findViewById(R.id.progressDetalle);

        LinearLayout navCatalogoDetalle = findViewById(R.id.navCatalogoDetalle);
        LinearLayout navFavoritosDetalle = findViewById(R.id.navFavoritosDetalle);
        LinearLayout navConfiguracionDetalle = findViewById(R.id.navConfiguracionDetalle);

        btnVolverDetalle.setOnClickListener(v -> finish());
        btnRefrescarDetalle.setOnClickListener(v -> {
            if (libroId != -1) {
                consultarDetalleEnServidor(libroId, true);
            }
        });

        btnToggleFavoritoDetalle.setOnClickListener(v -> alternarFavorito());
        btnEditarLibroApi.setOnClickListener(v -> mostrarDialogoEditarLibro());
        btnEliminarLibroApi.setOnClickListener(v -> confirmarEliminacionLibro());

        navCatalogoDetalle.setOnClickListener(v -> {
            Intent intent = new Intent(this, CatalogoActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        navFavoritosDetalle.setOnClickListener(v -> {
            Intent intent = new Intent(this, FavoritosActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        navConfiguracionDetalle.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(MainActivity.EXTRA_MODO_EDICION, true);
            startActivity(intent);
        });
    }

    private void obtenerDatosIntent() {
        if (getIntent() != null) {
            Object serializable = getIntent().getSerializableExtra(EXTRA_LIBRO_DTO);
            if (serializable instanceof LibroDto) {
                libroActual = (LibroDto) serializable;
                libroId = libroActual.getId();
            } else {
                libroId = getIntent().getIntExtra(EXTRA_LIBRO_ID, -1);
            }
        }

        if (libroActual != null) {
            mostrarDatosLibro(libroActual);
        }

        if (libroId != -1) {
            preferenciasManager.guardarUltimoLibroId(libroId);
            verificarEstadoFavorito(libroId);
            consultarDetalleEnServidor(libroId, false);
        } else {
            Toast.makeText(this, "No se encontró el ejemplar solicitado", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void mostrarDatosLibro(LibroDto libro) {
        if (libro == null) return;

        viewDetalleSpine.setBackgroundColor(LibroCatalogoAdapter.obtenerColorLomo(libro.getId()));
        tvDetalleId.setText(String.format(Locale.getDefault(), "Ejemplar N.º %d", libro.getId()));
        tvDetalleTitulo.setText(libro.getTitulo());
        tvDetalleAutor.setText(libro.getAutor());
        tvDetalleEditorial.setText(libro.getEditorial());
        tvDetallePaginas.setText(String.format(Locale.getDefault(), "%d páginas", libro.getPaginas()));

        if (libro.isDisponible()) {
            tvDetalleDisponibilidad.setText("Disponible");
            tvDetalleDisponibilidad.setBackgroundResource(R.drawable.bg_badge_disponible);
            tvDetalleDisponibilidad.setTextColor(
                    ContextCompat.getColor(this, R.color.status_available_text)
            );
        } else {
            tvDetalleDisponibilidad.setText("Agotado");
            tvDetalleDisponibilidad.setBackgroundResource(R.drawable.bg_badge_no_disponible);
            tvDetalleDisponibilidad.setTextColor(
                    ContextCompat.getColor(this, R.color.status_unavailable_text)
            );
        }
    }

    private void verificarEstadoFavorito(int id) {
        AppDatabase.databaseExecutor.execute(() -> {
            LibroEntity entidadLocal = libroDao.obtenerFavoritoPorId(id);
            final boolean existeEnColeccion = (entidadLocal != null);
            runOnUiThread(() -> {
                esFavoritoActual = existeEnColeccion;
                if (libroActual == null && entidadLocal != null) {
                    libroActual = LibroDto.fromEntity(entidadLocal);
                    mostrarDatosLibro(libroActual);
                }
                actualizarVistaFavorito();
            });
        });
    }

    private void actualizarVistaFavorito() {
        if (esFavoritoActual) {
            tvDetalleEstadoFavorito.setText("Guardado en tu colección de favoritos");
            btnToggleFavoritoDetalle.setText("Retirar de Mis Favoritos");
        } else {
            tvDetalleEstadoFavorito.setText("Este libro aún no está en tu colección de favoritos");
            btnToggleFavoritoDetalle.setText("Agregar a Mis Favoritos");
        }
    }

    private void consultarDetalleEnServidor(int id, boolean mostrarToastExito) {
        progressDetalle.setVisibility(View.VISIBLE);
        tvSubtituloDetalle.setText("Consultando información del ejemplar...");

        RetrofitClient.getApiService().obtenerLibroPorId(id).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<LibroDto> call, @NonNull Response<LibroDto> response) {
                progressDetalle.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    libroActual = response.body();
                    mostrarDatosLibro(libroActual);
                    tvSubtituloDetalle.setText("Información actualizada del catálogo");
                    if (mostrarToastExito) {
                        Toast.makeText(DetalleLibroActivity.this,
                                "Ficha actualizada", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String mensajeError = RetrofitClient.obtenerMensajeError(response);
                    tvSubtituloDetalle.setText("Mostrando información guardada");
                    Toast.makeText(DetalleLibroActivity.this,
                            mensajeError, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LibroDto> call, @NonNull Throwable t) {
                progressDetalle.setVisibility(View.GONE);
                tvSubtituloDetalle.setText("Sin conexión • Mostrando ejemplar guardado");
                if (mostrarToastExito || libroActual == null) {
                    Toast.makeText(DetalleLibroActivity.this,
                            "Sin conexión en este momento. Mostrando datos guardados.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void alternarFavorito() {
        if (libroActual == null) {
            Toast.makeText(this, "No hay información del ejemplar", Toast.LENGTH_SHORT).show();
            return;
        }

        AppDatabase.databaseExecutor.execute(() -> {
            if (esFavoritoActual) {
                libroDao.eliminarFavoritoPorId(libroActual.getId());
                esFavoritoActual = false;
                runOnUiThread(() -> {
                    actualizarVistaFavorito();
                    Toast.makeText(this,
                            "Retirado de tu colección de favoritos", Toast.LENGTH_SHORT).show();
                });
            } else {
                LibroEntity nuevoFavorito = libroActual.toEntity();
                libroDao.insertarFavorito(nuevoFavorito);
                esFavoritoActual = true;
                runOnUiThread(() -> {
                    actualizarVistaFavorito();
                    Toast.makeText(this,
                            "Guardado en tu colección de favoritos",
                            Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void mostrarDialogoEditarLibro() {
        if (libroActual == null) return;

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_formulario_libro, null);
        TextInputEditText etTitulo = view.findViewById(R.id.etFormTitulo);
        TextInputEditText etAutor = view.findViewById(R.id.etFormAutor);
        TextInputEditText etEditorial = view.findViewById(R.id.etFormEditorial);
        TextInputEditText etPaginas = view.findViewById(R.id.etFormPaginas);
        MaterialSwitch switchDisponible = view.findViewById(R.id.switchFormDisponible);

        etTitulo.setText(libroActual.getTitulo());
        etAutor.setText(libroActual.getAutor());
        etEditorial.setText(libroActual.getEditorial());
        etPaginas.setText(String.format(Locale.getDefault(), "%d", libroActual.getPaginas()));
        switchDisponible.setChecked(libroActual.isDisponible());

        new AlertDialog.Builder(this)
                .setTitle("Editar Ejemplar N.º " + libroActual.getId())
                .setView(view)
                .setPositiveButton("Guardar cambios", (dialog, which) -> {
                    String titulo = etTitulo.getText() != null ? etTitulo.getText().toString().trim() : "";
                    String autor = etAutor.getText() != null ? etAutor.getText().toString().trim() : "";
                    String editorial = etEditorial.getText() != null ? etEditorial.getText().toString().trim() : "";
                    String paginasStr = etPaginas.getText() != null ? etPaginas.getText().toString().trim() : "0";

                    if (titulo.isEmpty() || autor.isEmpty() || editorial.isEmpty() || paginasStr.isEmpty()) {
                        Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int paginas;
                    try {
                        paginas = Integer.parseInt(paginasStr);
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Número de páginas inválido", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    LibroRequest request = new LibroRequest(
                            titulo, autor, editorial, paginas, switchDisponible.isChecked()
                    );
                    ejecutarActualizacionLibro(libroActual.getId(), request);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void ejecutarActualizacionLibro(int id, LibroRequest request) {
        progressDetalle.setVisibility(View.VISIBLE);
        RetrofitClient.getApiService().actualizarLibro(id, request).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<LibroDto> call, @NonNull Response<LibroDto> response) {
                progressDetalle.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    libroActual = response.body();
                    mostrarDatosLibro(libroActual);

                    if (esFavoritoActual) {
                        AppDatabase.databaseExecutor.execute(() -> {
                            LibroEntity entity = libroDao.obtenerFavoritoPorId(id);
                            if (entity != null) {
                                entity.setTitulo(libroActual.getTitulo());
                                entity.setAutor(libroActual.getAutor());
                                entity.setEditorial(libroActual.getEditorial());
                                entity.setPaginas(libroActual.getPaginas());
                                entity.setDisponible(libroActual.isDisponible());
                                libroDao.actualizarFavorito(entity);
                            }
                        });
                    }
                    Toast.makeText(DetalleLibroActivity.this,
                            "Datos del ejemplar actualizados", Toast.LENGTH_SHORT).show();
                } else {
                    String errorMsg = RetrofitClient.obtenerMensajeError(response);
                    Toast.makeText(DetalleLibroActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LibroDto> call, @NonNull Throwable t) {
                progressDetalle.setVisibility(View.GONE);
                Toast.makeText(DetalleLibroActivity.this,
                        "Sin conexión para guardar los cambios en el catálogo", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void confirmarEliminacionLibro() {
        if (libroActual == null) return;

        new AlertDialog.Builder(this)
                .setTitle("Retirar libro del catálogo")
                .setMessage("¿Deseas retirar permanentemente \"" + libroActual.getTitulo() + "\" del catálogo general?")
                .setPositiveButton("Retirar", (dialog, which) -> ejecutarEliminacionLibro(libroActual.getId()))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void ejecutarEliminacionLibro(int id) {
        progressDetalle.setVisibility(View.VISIBLE);
        RetrofitClient.getApiService().eliminarLibro(id).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                progressDetalle.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(DetalleLibroActivity.this,
                            "Ejemplar retirado del catálogo",
                            Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String errorMsg = RetrofitClient.obtenerMensajeError(response);
                    Toast.makeText(DetalleLibroActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                progressDetalle.setVisibility(View.GONE);
                Toast.makeText(DetalleLibroActivity.this,
                        "No se pudo conectar para retirar el ejemplar",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
