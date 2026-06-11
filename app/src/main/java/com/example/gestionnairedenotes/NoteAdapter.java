package com.example.gestionnairedenotes;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note currentNote = notesList.get(position);

        // 1. Remplissage du titre et de la date (Seulement ces deux éléments pour l'accueil !)
        holder.tvTitle.setText(currentNote.getTitre());
        holder.tvDate.setText(currentNote.getDate());

        try {
            holder.cardBackground.setBackgroundColor(Color.parseColor(currentNote.getCouleur()));
        } catch (Exception e) {
            holder.cardBackground.setBackgroundColor(Color.parseColor("#828282")); // Couleur par défaut en cas d'erreur
        }

        // 2. Gestion de l'affichage de l'icône Étoile Favori
        if (currentNote.isFavori()) {
            holder.ivFavoriteStar.setVisibility(View.VISIBLE);
        } else {
            holder.ivFavoriteStar.setVisibility(View.GONE);
        }

        // --- INTERCEPTION MULTI-CLIC (CLIC SIMPLE vs DOUBLE CLIC) ---
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            private int clickCount = 0;
            private final Handler handler = new Handler(Looper.getMainLooper());

            // Action du CLIC SIMPLE (s'exécute après 300ms s'il n'y a pas de deuxième clic)
            private final Runnable singleClickRunnable = new Runnable() {
                @Override
                public void run() {
                    clickCount = 0;
                    // Ouvrir le mode modification (Écran 6)
                    Intent intent = new Intent(holder.itemView.getContext(), AddEditActivity.class);
                    intent.putExtra("EXTRA_ID", currentNote.getId());
                    intent.putExtra("EXTRA_TITLE", currentNote.getTitre());
                    intent.putExtra("EXTRA_CONTENT", currentNote.getContenu());
                    intent.putExtra("EXTRA_COLOR", currentNote.getCouleur());
                    intent.putExtra("EXTRA_IS_FAVORI", currentNote.isFavori());

                    holder.itemView.getContext().startActivity(intent);
                }
            };

            @Override
            public void onClick(View v) {
                clickCount++;

                if (clickCount == 1) {
                    // On lance le compte à rebours de 300 millisecondes
                    handler.postDelayed(singleClickRunnable, 300);
                } else if (clickCount == 2) {
                    // DOUBLE CLIC DÉTECTÉ : On annule immédiatement le clic simple !
                    handler.removeCallbacks(singleClickRunnable);
                    clickCount = 0;

                    // 1. Inverser l'état favori
                    boolean nouvelEtatFavori = !currentNote.isFavori();
                    currentNote.setFavori(nouvelEtatFavori);

                    // 2. Mise à jour asynchrone dans Room (Qualité de code & Fluidité de l'UI)
                    NoteDatabase db = NoteDatabase.getInstance(v.getContext());
                    NoteDatabase.databaseWriteExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            db.noteDao().update(currentNote);

                            // 3. Rafraîchir l'élément graphique sur le Thread UI Principal
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    // On ne redessine QUE la ligne modifiée (Gain de performances)
                                    notifyItemChanged(holder.getAdapterPosition());

                                    if (nouvelEtatFavori) {
                                        Toast.makeText(v.getContext(), "Ajouté aux favoris ⭐", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(v.getContext(), "Retiré des favoris", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    // Classe interne mappée avec tes identifiants exacts de composants
    class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDate;
        ImageView ivFavoriteStar;
        ConstraintLayout cardBackground;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDate = itemView.findViewById(R.id.tvDate);
            ivFavoriteStar = itemView.findViewById(R.id.ivFavoriteStar);
            cardBackground = itemView.findViewById(R.id.cardBackground);
        }
    }
}