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
    private List<Note> notesListFull = new ArrayList<>(); // Source de vérité (copie brute de la BDD)

    // États des filtres en cours
    private String currentSearchText = "";
    private boolean currentShowOnlyFavorites = false;

    // Met à jour la liste principale et réapplique les filtres en cours
    public void setNotes(List<Note> notes) {
        this.notesListFull = new ArrayList<>(notes);
        applyFilters();
    }

    /**
     * Centralisation du filtrage combiné (Texte + Favoris)
     */
    private void applyFilters() {
        List<Note> filteredList = new ArrayList<>();
        String filterPattern = currentSearchText.toLowerCase().trim();

        for (Note item : notesListFull) {
            // Étape A : Vérification du critère Favoris
            boolean matchesFavorite = !currentShowOnlyFavorites || item.isFavori();

            // Étape B : Vérification du critère Texte (Titre ou Contenu)
            boolean matchesText = filterPattern.isEmpty() ||
                    (item.getTitre() != null && item.getTitre().toLowerCase().contains(filterPattern)) ||
                    (item.getContenu() != null && item.getContenu().toLowerCase().contains(filterPattern));

            // Si la note valide les deux filtres, on l'affiche
            if (matchesFavorite && matchesText) {
                filteredList.add(item);
            }
        }

        this.notesList = filteredList;
        notifyDataSetChanged();
    }

    // Appelé par la barre de recherche (Lab 10)
    public void filter(String text) {
        this.currentSearchText = text != null ? text : "";
        applyFilters();
    }

    // Appelé par le bouton Favoris
    public void filterFavorites(boolean showOnlyFavorites) {
        this.currentShowOnlyFavorites = showOnlyFavorites;
        applyFilters();
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
        holder.tvDate.setText(currentNote.getDate());

        try {
            holder.cardBackground.setBackgroundColor(Color.parseColor(currentNote.getCouleur()));
        } catch (Exception e) {
            holder.cardBackground.setBackgroundColor(Color.parseColor("#828282"));
        }

        if (currentNote.isFavori()) {
            holder.ivFavoriteStar.setVisibility(View.VISIBLE);
        } else {
            holder.ivFavoriteStar.setVisibility(View.GONE);
        }

        // --- GESTION DU DOUBLE-CLIC ET CLIC SIMPLE (Lab 9) ---
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            private int clickCount = 0;
            private final Handler handler = new Handler(Looper.getMainLooper());

            private final Runnable singleClickRunnable = new Runnable() {
                @Override
                public void run() {
                    clickCount = 0;
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
                    handler.postDelayed(singleClickRunnable, 300);
                } else if (clickCount == 2) {
                    handler.removeCallbacks(singleClickRunnable);
                    clickCount = 0;

                    boolean nouvelEtatFavori = !currentNote.isFavori();
                    currentNote.setFavori(nouvelEtatFavori);

                    NoteDatabase db = NoteDatabase.getInstance(v.getContext());
                    NoteDatabase.databaseWriteExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            db.noteDao().update(currentNote);
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
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