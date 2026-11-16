package com.upiiz.dm_jphl_practica02.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.upiiz.dm_jphl_practica02.R;
import com.upiiz.dm_jphl_practica02.models.LibroDto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class LibroCatalogoAdapter extends RecyclerView.Adapter<LibroCatalogoAdapter.LibroViewHolder> {

    public interface OnLibroInteractionListener {
        void onVerDetalleClick(LibroDto libro);
        void onToggleFavoritoClick(LibroDto libro, boolean esFavoritoActualmente);
    }

    private static final int[] COLORES_LOMO = new int[]{
            Color.parseColor("#5D4037"),
            Color.parseColor("#6D3B2E"),
            Color.parseColor("#4E342E"),
            Color.parseColor("#795548"),
            Color.parseColor("#5C4033"),
            Color.parseColor("#4A3B32")
    };

    private final List<LibroDto> listaCompleta = new ArrayList<>();
    private final List<LibroDto> listaFiltrada = new ArrayList<>();
    private final Set<Integer> idsFavoritos = new HashSet<>();
    private final OnLibroInteractionListener listener;
    private String textoBusquedaActual = "";

    public LibroCatalogoAdapter(OnLibroInteractionListener listener) {
        this.listener = listener;
    }

    public static int obtenerColorLomo(int libroId) {
        int index = Math.abs(libroId) % COLORES_LOMO.length;
        return COLORES_LOMO[index];
    }

    public void actualizarLibros(List<LibroDto> nuevosLibros) {
        listaCompleta.clear();
        if (nuevosLibros != null) {
            listaCompleta.addAll(nuevosLibros);
        }
        filtrar(textoBusquedaActual);
    }

    public void actualizarIdsFavoritos(List<Integer> nuevosIds) {
        idsFavoritos.clear();
        if (nuevosIds != null) {
            idsFavoritos.addAll(nuevosIds);
        }
        notifyDataSetChanged();
    }

    public void filtrar(String query) {
        textoBusquedaActual = query != null ? query.trim().toLowerCase(Locale.ROOT) : "";
        listaFiltrada.clear();

        if (textoBusquedaActual.isEmpty()) {
            listaFiltrada.addAll(listaCompleta);
        } else {
            for (LibroDto libro : listaCompleta) {
                String idStr = String.valueOf(libro.getId());
                String titulo = libro.getTitulo().toLowerCase(Locale.ROOT);
                String autor = libro.getAutor().toLowerCase(Locale.ROOT);
                String editorial = libro.getEditorial().toLowerCase(Locale.ROOT);

                if (idStr.contains(textoBusquedaActual)
                        || titulo.contains(textoBusquedaActual)
                        || autor.contains(textoBusquedaActual)
                        || editorial.contains(textoBusquedaActual)) {
                    listaFiltrada.add(libro);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LibroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_libro_catalogo, parent, false);
        return new LibroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LibroViewHolder holder, int position) {
        LibroDto libro = listaFiltrada.get(position);
        Context context = holder.itemView.getContext();

        holder.viewBookSpine.setBackgroundColor(obtenerColorLomo(libro.getId()));
        holder.tvIdLibro.setText(String.format(Locale.getDefault(), "N.º %d", libro.getId()));
        holder.tvTituloLibro.setText(libro.getTitulo());
        holder.tvAutorLibro.setText(libro.getAutor());
        holder.tvEditorialLibro.setText(libro.getEditorial());
        holder.tvPaginasLibro.setText(String.format(Locale.getDefault(), "%d págs.", libro.getPaginas()));

        if (libro.isDisponible()) {
            holder.tvDisponibilidadLibro.setText("Disponible");
            holder.tvDisponibilidadLibro.setBackgroundResource(R.drawable.bg_badge_disponible);
            holder.tvDisponibilidadLibro.setTextColor(
                    ContextCompat.getColor(context, R.color.status_available_text)
            );
        } else {
            holder.tvDisponibilidadLibro.setText("Agotado");
            holder.tvDisponibilidadLibro.setBackgroundResource(R.drawable.bg_badge_no_disponible);
            holder.tvDisponibilidadLibro.setTextColor(
                    ContextCompat.getColor(context, R.color.status_unavailable_text)
            );
        }

        final boolean esFavorito = idsFavoritos.contains(libro.getId());
        if (esFavorito) {
            holder.btnFavoritoItem.setImageResource(R.drawable.ic_favorite_filled);
        } else {
            holder.btnFavoritoItem.setImageResource(R.drawable.ic_favorite_border);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onVerDetalleClick(libro);
            }
        });

        holder.btnFavoritoItem.setOnClickListener(v -> {
            if (listener != null) {
                listener.onToggleFavoritoClick(libro, esFavorito);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaFiltrada.size();
    }

    static class LibroViewHolder extends RecyclerView.ViewHolder {
        final View viewBookSpine;
        final TextView tvIdLibro;
        final TextView tvDisponibilidadLibro;
        final TextView tvTituloLibro;
        final TextView tvAutorLibro;
        final TextView tvEditorialLibro;
        final TextView tvPaginasLibro;
        final ImageButton btnFavoritoItem;

        LibroViewHolder(@NonNull View itemView) {
            super(itemView);
            viewBookSpine = itemView.findViewById(R.id.viewBookSpine);
            tvIdLibro = itemView.findViewById(R.id.tvIdLibro);
            tvDisponibilidadLibro = itemView.findViewById(R.id.tvDisponibilidadLibro);
            tvTituloLibro = itemView.findViewById(R.id.tvTituloLibro);
            tvAutorLibro = itemView.findViewById(R.id.tvAutorLibro);
            tvEditorialLibro = itemView.findViewById(R.id.tvEditorialLibro);
            tvPaginasLibro = itemView.findViewById(R.id.tvPaginasLibro);
            btnFavoritoItem = itemView.findViewById(R.id.btnFavoritoItem);
        }
    }
}
