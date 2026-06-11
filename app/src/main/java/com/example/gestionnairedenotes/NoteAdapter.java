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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<Note> notesList = new ArrayList<>();
    private List<Note> notesListFull = new ArrayList<>(); // Source de vérité (copie brute de la BDD)

    // États des filtres et du tri en cours
    private String currentSearchText = "";
    private boolean currentShowOnlyFavorites = false;
    private int currentSortType = 0;

    // --- INTERFACE POUR LE COMPTEUR DE NOTES ---
    public interface OnNotesCountChangedListener {
        void onNotesCountChanged(int visibleCount, int totalCount);
    }

    private OnNotesCountChangedListener countListener;

    public void setOnNotesCountChangedListener(OnNotesCountChangedListener listener) {
        this.countListener = listener;
    }

    // Met à jour la liste principale et réapplique les filtres et le tri
    public void setNotes(List<Note> notes) {
        this.notesListFull = new ArrayList<>(notes);
        applyFilters();
    }

    // Permet de changer le type de tri depuis la MainActivity
    public void setSortType(int sortType) {
        this.currentSortType = sortType;
        applyFilters();
    }

    /**
     * Centralisation du filtrage combiné (Texte + Favoris) ET du Tri
     */
    private void applyFilters() {
        List<Note> filteredList = new ArrayList<>();
        String filterPattern = currentSearchText.toLowerCase().trim();

        // 1. Étape de Filtrage
        for (Note item : notesListFull) {
            boolean matchesFavorite = !currentShowOnlyFavorites || item.isFavori();

            boolean matchesText = filterPattern.isEmpty() ||
                    (item.getTitre() != null && item.getTitre().toLowerCase().contains(filterPattern)) ||
                    (item.getContenu() != null && item.getContenu().toLowerCase().contains(filterPattern));

            if (matchesFavorite && matchesText) {
                filteredList.add(item);
            }
        }

        // 2. Étape de Tri
        if (currentSortType == 0) {
            Collections.sort(filteredList, new Comparator<Note>() {
                @Override
                public int compare(Note n1, Note n2) {
                    return Integer.compare(n2.getId(), n1.getId());
                }
            });
        } else if (currentSortType == 1) {
            Collections.sort(filteredList, new Comparator<Note>() {
                @Override
                public int compare(Note n1, Note n2) {
                    String t1 = n1.getTitre() != null ? n1.getTitre() : "";
                    String t2 = n2.getTitre() != null ? n2.getTitre() : "";
                    return t1.compareToIgnoreCase(t2);
                }
            });
        }

        this.notesList = filteredList;
        notifyDataSetChanged();

        // --- ENVOI DES CHIFFRES AU COMPTEUR ---
        if (countListener != null) {
            countListener.onNotesCountChanged(filteredList.size(), notesListFull.size());
        }
    }

    // Permet à la MainActivity de récupérer la note glissée (Swipe-to-delete)
    public Note getNoteAt(int position) {
        return notesList.get(position);
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

        // Gestion du double-clic et clic simple (Lab 9)
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