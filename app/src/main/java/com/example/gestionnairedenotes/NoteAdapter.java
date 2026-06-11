package com.example.gestionnairedenotes;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<Note> notesList = new ArrayList<>();

    public void setNotes(List<Note> notes) {
        this.notesList = notes;
        notifyDataSetChanged(); // Alerte le RecyclerView qu'il y a de nouvelles notes à afficher
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note currentNote = notesList.get(position);

        holder.tvTitle.setText(currentNote.getTitre());
        holder.tvContent.setText(currentNote.getContenu());
        holder.tvDate.setText(currentNote.getDate());

        // Application de la couleur de fond choisie par l'utilisateur
        try {
            holder.layoutBackground.setBackgroundColor(Color.parseColor(currentNote.getCouleur()));
        } catch (Exception e) {
            holder.layoutBackground.setBackgroundColor(Color.WHITE); // Couleur par défaut en cas d'erreur
        }
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent, tvDate;
        ConstraintLayout layoutBackground;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvNoteTitle);
            tvContent = itemView.findViewById(R.id.tvNoteContent);
            tvDate = itemView.findViewById(R.id.tvNoteDate);
            layoutBackground = itemView.findViewById(R.id.layoutCardBackground);
        }
    }
}