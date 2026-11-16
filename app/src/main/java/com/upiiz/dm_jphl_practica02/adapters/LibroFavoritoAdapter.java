package com.upiiz.dm_jphl_practica02.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.upiiz.dm_jphl_practica02.R;
import com.upiiz.dm_jphl_practica02.models.LibroEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LibroFavoritoAdapter extends RecyclerView.Adapter<LibroFavoritoAdapter.FavoritoViewHolder> {

    public interface OnFavoritoInteractionListener {
        void onVerDetalleFavorito(LibroEntity favorito);
        void onEliminarFavorito(LibroEntity favorito);
    }

    private final List<LibroEntity> listaFavoritos = new ArrayList<>();
    private final OnFavoritoInteractionListener listener;

    public LibroFavoritoAdapter(OnFavoritoInteractionListener listener) {
        this.listener = listener;
    }

    public void actualizarFavoritos(List<LibroEntity> nuevosFavoritos) {
        listaFavoritos.clear();
        if (nuevosFavoritos != null) {
            listaFavoritos.addAll(nuevosFavoritos);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FavoritoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_libro_favorito, parent, false);
        return new FavoritoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritoViewHolder holder, int position) {
        LibroEntity libro = listaFavoritos.get(position);
        Context context = holder.itemView.getContext();

        holder.viewFavoritoSpine.setBackgroundColor(LibroCatalogoAdapter.obtenerColorLomo(libro.getId()));
        holder.tvIdFavorito.setText(String.format(Locale.getDefault(), "N.º %d", libro.getId()));
        holder.tvTituloFavorito.setText(libro.getTitulo());
        holder.tvAutorFavorito.setText(libro.getAutor());
        holder.tvEditorialFavorito.setText(libro.getEditorial());
        holder.tvPaginasFavorito.setText(String.format(Locale.getDefault(), "%d págs.", libro.getPaginas()));

        if (libro.isDisponible()) {
            holder.tvDisponibilidadFavorito.setText("Disponible");
            holder.tvDisponibilidadFavorito.setBackgroundResource(R.drawable.bg_badge_disponible);
            holder.tvDisponibilidadFavorito.setTextColor(
                    ContextCompat.getColor(context, R.color.status_available_text)
            );
        } else {
            holder.tvDisponibilidadFavorito.setText("Agotado");
            holder.tvDisponibilidadFavorito.setBackgroundResource(R.drawable.bg_badge_no_disponible);
            holder.tvDisponibilidadFavorito.setTextColor(
                    ContextCompat.getColor(context, R.color.status_unavailable_text)
            );
        }

        holder.btnDetalleFavorito.setOnClickListener(v -> {
            if (listener != null) {
                listener.onVerDetalleFavorito(libro);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onVerDetalleFavorito(libro);
            }
        });

        holder.btnEliminarFavorito.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEliminarFavorito(libro);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaFavoritos.size();
    }

    static class FavoritoViewHolder extends RecyclerView.ViewHolder {
        final View viewFavoritoSpine;
        final TextView tvIdFavorito;
        final TextView tvDisponibilidadFavorito;
        final TextView tvTituloFavorito;
        final TextView tvAutorFavorito;
        final TextView tvEditorialFavorito;
        final TextView tvPaginasFavorito;
        final TextView btnDetalleFavorito;
        final ImageButton btnEliminarFavorito;

        FavoritoViewHolder(@NonNull View itemView) {
            super(itemView);
            viewFavoritoSpine = itemView.findViewById(R.id.viewFavoritoSpine);
            tvIdFavorito = itemView.findViewById(R.id.tvIdFavorito);
            tvDisponibilidadFavorito = itemView.findViewById(R.id.tvDisponibilidadFavorito);
            tvTituloFavorito = itemView.findViewById(R.id.tvTituloFavorito);
            tvAutorFavorito = itemView.findViewById(R.id.tvAutorFavorito);
            tvEditorialFavorito = itemView.findViewById(R.id.tvEditorialFavorito);
            tvPaginasFavorito = itemView.findViewById(R.id.tvPaginasFavorito);
            btnDetalleFavorito = itemView.findViewById(R.id.btnDetalleFavorito);
            btnEliminarFavorito = itemView.findViewById(R.id.btnEliminarFavorito);
        }
    }
}
