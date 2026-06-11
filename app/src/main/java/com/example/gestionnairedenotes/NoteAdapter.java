package com.example.gestionnairedenotes;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<Note> notesList = new ArrayList<>();

    // Met à jour la liste des notes et rafraîchit l'affichage
    public void setNotes(List<Note> notes) {
        this.notesList = notes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // "Gonfle" le layout item_note.xml défini dans le Lab 1
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note currentNote = notesList.get(position);

        // 1. Remplissage des textes
        holder.tvTitle.setText(currentNote.getTitre());
        holder.tvContent.setText(currentNote.getContenu());
        holder.tvDate.setText(currentNote.getDate());

        // 2. Application de la couleur officielle
        try {
            holder.cardBackground.setBackgroundColor(Color.parseColor(currentNote.getCouleur()));
        } catch (Exception e) {
            holder.cardBackground.setBackgroundColor(Color.parseColor("#828282")); // Gris par défaut en cas d'erreur
        }

        // 3. Gestion de l'icône Favori (L'étoile jaune)
        // Elle ne s'affiche que si l'attribut isFavori de la note est à true
        if (currentNote.isFavori()) {
            holder.ivFavoriteStar.setVisibility(View.VISIBLE);
        } else {
            holder.ivFavoriteStar.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    // Classe interne pour lier les vues de item_note.xml
    class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent, tvDate;
        ImageView ivFavoriteStar;
        ConstraintLayout cardBackground;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvNoteTitle);
            tvContent = itemView.findViewById(R.id.tvNoteContent);
            tvDate = itemView.findViewById(R.id.tvNoteDate);
            ivFavoriteStar = itemView.findViewById(R.id.ivFavoriteStar);
            cardBackground = itemView.findViewById(R.id.layoutCardBackground);
        }
    }
}