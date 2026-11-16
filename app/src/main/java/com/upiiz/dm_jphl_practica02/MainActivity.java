package com.upiiz.dm_jphl_practica02;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.upiiz.dm_jphl_practica02.db.PreferenciasManager;
import com.upiiz.dm_jphl_practica02.ui.CatalogoActivity;
import com.upiiz.dm_jphl_practica02.ui.FavoritosActivity;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MODO_EDICION = "extra_modo_edicion";
    private static final String STATE_NOMBRE_ORIGINAL = "state_nombre_original";
    private static final String STATE_TEMA_ORIGINAL = "state_tema_original";

    private PreferenciasManager preferenciasManager;
    private TextView tvSaludoPersonalizado;
    private TextInputEditText etNombreUsuario;
    private RadioButton rbTemaClaro;
    private RadioButton rbTemaOscuro;
    private Button btnIngresar;
    private MaterialButton btnCancelar;

    private boolean actualizandoControles = false;
    private boolean modoEdicion = false;
    private String nombreOriginal = "";
    private boolean temaOriginal = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferenciasManager = new PreferenciasManager(this);
        preferenciasManager.aplicarTemaGuardado();
        super.onCreate(savedInstanceState);

        modoEdicion = getIntent() != null && getIntent().getBooleanExtra(EXTRA_MODO_EDICION, false);

        if (savedInstanceState != null) {
            nombreOriginal = savedInstanceState.getString(STATE_NOMBRE_ORIGINAL, "");
            temaOriginal = savedInstanceState.getBoolean(STATE_TEMA_ORIGINAL, false);
        } else {
            nombreOriginal = preferenciasManager.obtenerNombreUsuario();
            temaOriginal = preferenciasManager.esModoOscuro();
        }

        if (!modoEdicion && preferenciasManager.esConfiguracionCompletada()) {
            redirigirASeccionInicial();
            return;
        }

        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inicializarVistas();
        cargarPreferenciasEnFormulario();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_NOMBRE_ORIGINAL, nombreOriginal);
        outState.putBoolean(STATE_TEMA_ORIGINAL, temaOriginal);
    }

    private void redirigirASeccionInicial() {
        String ultimaSeccion = preferenciasManager.obtenerUltimaSeccion();
        Intent intent;
        if (PreferenciasManager.SECCION_FAVORITOS.equals(ultimaSeccion)) {
            intent = new Intent(this, FavoritosActivity.class);
        } else {
            intent = new Intent(this, CatalogoActivity.class);
        }
        startActivity(intent);
        finish();
    }

    private void inicializarVistas() {
        tvSaludoPersonalizado = findViewById(R.id.tvSaludoPersonalizado);
        etNombreUsuario = findViewById(R.id.etNombreUsuario);
        RadioGroup rgTema = findViewById(R.id.rgTema);
        rbTemaClaro = findViewById(R.id.rbTemaClaro);
        rbTemaOscuro = findViewById(R.id.rbTemaOscuro);
        btnIngresar = findViewById(R.id.btnIngresar);
        btnCancelar = findViewById(R.id.btnCancelar);

        if (modoEdicion) {
            btnIngresar.setText("Guardar e Ingresar");
            btnCancelar.setVisibility(View.VISIBLE);
        } else {
            btnIngresar.setText("Ingresar");
            btnCancelar.setVisibility(View.GONE);
        }

        rgTema.setOnCheckedChangeListener((group, checkedId) -> {
            if (actualizandoControles) {
                return;
            }
            boolean activarOscuro = (checkedId == R.id.rbTemaOscuro);
            if (preferenciasManager.esModoOscuro() != activarOscuro) {
                String nombreActual = etNombreUsuario.getText() != null
                        ? etNombreUsuario.getText().toString().trim()
                        : "";
                if (!nombreActual.isEmpty()) {
                    preferenciasManager.guardarNombreUsuario(nombreActual);
                }
                preferenciasManager.guardarModoOscuro(activarOscuro);
            }
        });

        btnIngresar.setOnClickListener(v -> guardarConfiguracionEIngresar());
        btnCancelar.setOnClickListener(v -> cancelarEdicion());
    }

    private void cargarPreferenciasEnFormulario() {
        actualizandoControles = true;

        String nombreGuardado = preferenciasManager.obtenerNombreUsuario();
        boolean esOscuro = preferenciasManager.esModoOscuro();

        if (!nombreGuardado.isEmpty()) {
            tvSaludoPersonalizado.setText(String.format("Hola, %s. Ajusta las preferencias de tu biblioteca.", nombreGuardado));
            etNombreUsuario.setText(nombreGuardado);
        } else {
            tvSaludoPersonalizado.setText("Bienvenido a tu estantería personal de lectura");
        }

        if (esOscuro) {
            rbTemaOscuro.setChecked(true);
        } else {
            rbTemaClaro.setChecked(true);
        }

        actualizandoControles = false;
    }

    private void cancelarEdicion() {
        preferenciasManager.guardarNombreUsuario(nombreOriginal);
        if (preferenciasManager.esModoOscuro() != temaOriginal) {
            preferenciasManager.guardarModoOscuro(temaOriginal);
        }
        if (isTaskRoot()) {
            redirigirASeccionInicial();
        } else {
            finish();
        }
    }

    private void guardarConfiguracionEIngresar() {
        String nombre = etNombreUsuario.getText() != null
                ? etNombreUsuario.getText().toString().trim()
                : "";

        if (nombre.isEmpty()) {
            Toast.makeText(this, "Por favor escribe tu nombre o alias para continuar", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean temaOscuroSeleccionado = rbTemaOscuro.isChecked();
        preferenciasManager.guardarNombreUsuario(nombre);
        preferenciasManager.guardarConfiguracionCompletada(true);
        preferenciasManager.guardarModoOscuro(temaOscuroSeleccionado);

        if (!modoEdicion) {
            preferenciasManager.guardarUltimaSeccion(PreferenciasManager.SECCION_CATALOGO);
            Intent intent = new Intent(this, CatalogoActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Preferencias actualizadas", Toast.LENGTH_SHORT).show();
            if (isTaskRoot()) {
                redirigirASeccionInicial();
                return;
            }
        }
        finish();
    }
}
