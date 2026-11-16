package com.upiiz.dm_jphl_practica02.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.upiiz.dm_jphl_practica02.models.LibroRequest;
import com.upiiz.dm_jphl_practica02.models.LibrosResponse;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CatalogoActivity extends AppCompatActivity implements LibroCatalogoAdapter.OnLibroInteractionListener {

    private RecyclerView rvCatalogo;
    private SwipeRefreshLayout swipeRefreshCatalogo;
    private LinearLayout layoutCargandoCatalogo;
    private LinearLayout layoutEstadoCatalogo;
    private TextView tvTituloEstadoCatalogo;
    private TextView tvMensajeEstadoCatalogo;
    private TextView tvSubtituloCatalogo;
    private TextView tvSaludoCatalogo;

    private LibroCatalogoAdapter adapter;
    private LibroDao libroDao;
    private PreferenciasManager preferenciasManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferenciasManager = new PreferenciasManager(this);
        preferenciasManager.aplicarTemaGuardado();
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_catalogo);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainCatalogo), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        preferenciasManager.guardarUltimaSeccion(PreferenciasManager.SECCION_CATALOGO);
        libroDao = AppDatabase.getInstance(this).libroDao();

        inicializarVistas();
        actualizarSaludoLector();
        sincronizarFavoritosLocales();
        consultarCatalogoRemoto(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        preferenciasManager.guardarUltimaSeccion(PreferenciasManager.SECCION_CATALOGO);
        actualizarSaludoLector();
        sincronizarFavoritosLocales();
    }

    private void inicializarVistas() {
        ImageButton btnActualizarCatalogo = findViewById(R.id.btnActualizarCatalogo);
        ImageButton btnConfiguracionTopCatalogo = findViewById(R.id.btnConfiguracionTopCatalogo);
        MaterialButton btnAgregarLibroApi = findViewById(R.id.btnAgregarLibroApi);
        MaterialButton btnReintentarCatalogo = findViewById(R.id.btnReintentarCatalogo);
        MaterialButton btnIrFavoritosDesdeCatalogo = findViewById(R.id.btnIrFavoritosDesdeCatalogo);
        EditText etBuscarCatalogo = findViewById(R.id.etBuscarCatalogo);

        LinearLayout navFavoritos = findViewById(R.id.navFavoritos);
        LinearLayout navConfiguracion = findViewById(R.id.navConfiguracion);

        rvCatalogo = findViewById(R.id.rvCatalogo);
        swipeRefreshCatalogo = findViewById(R.id.swipeRefreshCatalogo);
        layoutCargandoCatalogo = findViewById(R.id.layoutCargandoCatalogo);
        layoutEstadoCatalogo = findViewById(R.id.layoutEstadoCatalogo);
        tvTituloEstadoCatalogo = findViewById(R.id.tvTituloEstadoCatalogo);
        tvMensajeEstadoCatalogo = findViewById(R.id.tvMensajeEstadoCatalogo);
        tvSubtituloCatalogo = findViewById(R.id.tvSubtituloCatalogo);
        tvSaludoCatalogo = findViewById(R.id.tvSaludoCatalogo);

        adapter = new LibroCatalogoAdapter(this);
        rvCatalogo.setLayoutManager(new GridLayoutManager(this, 2));
        rvCatalogo.setAdapter(adapter);

        btnActualizarCatalogo.setOnClickListener(v -> consultarCatalogoRemoto(true));
        swipeRefreshCatalogo.setOnRefreshListener(() -> consultarCatalogoRemoto(true));
        btnReintentarCatalogo.setOnClickListener(v -> consultarCatalogoRemoto(true));

        View.OnClickListener irAFavoritosListener = v -> {
            startActivity(new Intent(this, FavoritosActivity.class));
            overridePendingTransition(0, 0);
        };
        btnIrFavoritosDesdeCatalogo.setOnClickListener(irAFavoritosListener);
        navFavoritos.setOnClickListener(irAFavoritosListener);

        View.OnClickListener irAConfiguracionListener = v -> abrirConfiguracion();
        btnConfiguracionTopCatalogo.setOnClickListener(irAConfiguracionListener);
        navConfiguracion.setOnClickListener(irAConfiguracionListener);

        btnAgregarLibroApi.setOnClickListener(v -> mostrarDialogoCrearLibro());

        etBuscarCatalogo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filtrar(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void actualizarSaludoLector() {
        String nombre = preferenciasManager.obtenerNombreUsuario();
        if (nombre != null && !nombre.isEmpty()) {
            tvSaludoCatalogo.setText(String.format("Biblioteca de %s", nombre));
        } else {
            tvSaludoCatalogo.setText("Biblioteca Personal");
        }
    }

    private void abrirConfiguracion() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_MODO_EDICION, true);
        startActivity(intent);
    }

    private void sincronizarFavoritosLocales() {
        AppDatabase.databaseExecutor.execute(() -> {
            List<Integer> ids = libroDao.obtenerIdsFavoritos();
            runOnUiThread(() -> adapter.actualizarIdsFavoritos(ids));
        });
    }

    private void consultarCatalogoRemoto(boolean esRecargaManual) {
        if (!swipeRefreshCatalogo.isRefreshing()) {
            layoutCargandoCatalogo.setVisibility(View.VISIBLE);
        }
        layoutEstadoCatalogo.setVisibility(View.GONE);
        tvSubtituloCatalogo.setText("Actualizando estantería...");

        RetrofitClient.getApiService().obtenerLibros().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<LibrosResponse> call,
                                   @NonNull Response<LibrosResponse> response) {
                layoutCargandoCatalogo.setVisibility(View.GONE);
                swipeRefreshCatalogo.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<LibroDto> libros = response.body().getLibros();
                    adapter.actualizarLibros(libros);

                    if (libros.isEmpty()) {
                        rvCatalogo.setVisibility(View.GONE);
                        layoutEstadoCatalogo.setVisibility(View.VISIBLE);
                        tvTituloEstadoCatalogo.setText("La estantería está vacía");
                        tvMensajeEstadoCatalogo.setText("Por el momento no hay ejemplares registrados en el catálogo.");
                        tvSubtituloCatalogo.setText("Sin ejemplares disponibles");
                    } else {
                        rvCatalogo.setVisibility(View.VISIBLE);
                        layoutEstadoCatalogo.setVisibility(View.GONE);
                        tvSubtituloCatalogo.setText(String.format(Locale.getDefault(),
                                "%d ejemplares en la estantería", libros.size()));
                        if (esRecargaManual) {
                            Toast.makeText(CatalogoActivity.this,
                                    "Estantería actualizada (" + libros.size() + " libros)",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    String mensajeError = RetrofitClient.obtenerMensajeError(response);
                    mostrarErrorCatalogo("Catálogo no disponible", mensajeError);
                }
            }

            @Override
            public void onFailure(@NonNull Call<LibrosResponse> call, @NonNull Throwable t) {
                layoutCargandoCatalogo.setVisibility(View.GONE);
                swipeRefreshCatalogo.setRefreshing(false);
                String detalle = "No fue posible cargar los ejemplares en este momento. "
                        + "Verifica tu conexión a Internet o consulta tu estante de Favoritos.";
                mostrarErrorCatalogo("Sin conexión a Internet", detalle);
            }
        });
    }

    private void mostrarErrorCatalogo(String titulo, String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
        tvSubtituloCatalogo.setText("Sin conexión • Tus favoritos siguen disponibles");
        if (adapter.getItemCount() == 0) {
            rvCatalogo.setVisibility(View.GONE);
            layoutEstadoCatalogo.setVisibility(View.VISIBLE);
            tvTituloEstadoCatalogo.setText(titulo);
            tvMensajeEstadoCatalogo.setText(mensaje);
        }
    }

    @Override
    public void onVerDetalleClick(LibroDto libro) {
        Intent intent = new Intent(this, DetalleLibroActivity.class);
        intent.putExtra(DetalleLibroActivity.EXTRA_LIBRO_DTO, libro);
        intent.putExtra(DetalleLibroActivity.EXTRA_LIBRO_ID, libro.getId());
        startActivity(intent);
    }

    @Override
    public void onToggleFavoritoClick(LibroDto libro, boolean esFavoritoActualmente) {
        AppDatabase.databaseExecutor.execute(() -> {
            if (esFavoritoActualmente) {
                libroDao.eliminarFavoritoPorId(libro.getId());
            } else {
                libroDao.insertarFavorito(libro.toEntity());
            }
            List<Integer> nuevosIds = libroDao.obtenerIdsFavoritos();
            runOnUiThread(() -> {
                adapter.actualizarIdsFavoritos(nuevosIds);
                String msg = esFavoritoActualmente
                        ? "Retirado de tus favoritos: " + libro.getTitulo()
                        : "Agregado a tus favoritos: " + libro.getTitulo();
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void mostrarDialogoCrearLibro() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_formulario_libro, null);
        TextInputEditText etTitulo = view.findViewById(R.id.etFormTitulo);
        TextInputEditText etAutor = view.findViewById(R.id.etFormAutor);
        TextInputEditText etEditorial = view.findViewById(R.id.etFormEditorial);
        TextInputEditText etPaginas = view.findViewById(R.id.etFormPaginas);
        MaterialSwitch switchDisponible = view.findViewById(R.id.switchFormDisponible);

        new AlertDialog.Builder(this)
                .setTitle("Nuevo Ejemplar")
                .setView(view)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String titulo = etTitulo.getText() != null ? etTitulo.getText().toString().trim() : "";
                    String autor = etAutor.getText() != null ? etAutor.getText().toString().trim() : "";
                    String editorial = etEditorial.getText() != null ? etEditorial.getText().toString().trim() : "";
                    String paginasStr = etPaginas.getText() != null ? etPaginas.getText().toString().trim() : "";

                    if (titulo.isEmpty() || autor.isEmpty() || editorial.isEmpty() || paginasStr.isEmpty()) {
                        Toast.makeText(this, "Completa todos los datos del libro", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int paginas;
                    try {
                        paginas = Integer.parseInt(paginasStr);
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Ingresa un número de páginas válido", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    LibroRequest nuevoLibro = new LibroRequest(
                            titulo, autor, editorial, paginas, switchDisponible.isChecked()
                    );
                    crearLibroEnServidor(nuevoLibro);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void crearLibroEnServidor(LibroRequest request) {
        swipeRefreshCatalogo.setRefreshing(true);
        RetrofitClient.getApiService().crearLibro(request).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<LibroDto> call, @NonNull Response<LibroDto> response) {
                swipeRefreshCatalogo.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(CatalogoActivity.this,
                            "Ejemplar agregado al catálogo",
                            Toast.LENGTH_SHORT).show();
                    consultarCatalogoRemoto(false);
                } else {
                    String errorMsg = RetrofitClient.obtenerMensajeError(response);
                    Toast.makeText(CatalogoActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LibroDto> call, @NonNull Throwable t) {
                swipeRefreshCatalogo.setRefreshing(false);
                Toast.makeText(CatalogoActivity.this,
                        "No se pudo agregar el libro. Verifica tu conexión.", Toast.LENGTH_LONG).show();
            }
        });
    }
}
