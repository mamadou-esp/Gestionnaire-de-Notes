package com.example.gestionnairedenotes;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private NoteViewModel noteViewModel;
    private NoteAdapter adapter;

    private CardView cardPaletteContainer;
    private FloatingActionButton fabAddNote;

    private boolean isFilteringFavorites = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Initialisation du RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewNotes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        // 2. Configuration de l'adaptateur
        adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);

        // 3. Liaison avec le ViewModel (Observation LiveData de Room)
        TextView tvEmptyNotes = findViewById(R.id.tvEmptyNotes);
        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                if (notes != null && !notes.isEmpty()) {
                    adapter.setNotes(notes);
                    recyclerView.setVisibility(View.VISIBLE);
                    tvEmptyNotes.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    tvEmptyNotes.setVisibility(View.VISIBLE);
                }
            }
        });

        // 4. Implémentation du Bonus : Suppression rapide (Swipe to delete)
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (adapter != null) {
                    int position = viewHolder.getAdapterPosition();
                    Note noteToDelete = adapter.getNoteAt(position);
                    noteViewModel.delete(noteToDelete);
                    Toast.makeText(MainActivity.this, "Note supprimée 🗑️", Toast.LENGTH_SHORT).show();
                }
            }
        }).attachToRecyclerView(recyclerView);


        // 5. Gestion de la Palette de couleurs
        cardPaletteContainer = findViewById(R.id.cardPaletteContainer);
        fabAddNote = findViewById(R.id.fabAdd);
        FloatingActionButton fabClosePalette = findViewById(R.id.fabClosePalette);

        fabAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardPaletteContainer.setVisibility(View.VISIBLE);
                fabAddNote.setVisibility(View.GONE);
            }
        });

        fabClosePalette.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardPaletteContainer.setVisibility(View.GONE);
                fabAddNote.setVisibility(View.VISIBLE);
            }
        });

        setupColorClick(R.id.viewColorGreen, "#4CAF50");
        setupColorClick(R.id.viewColorRed, "#F44336");
        setupColorClick(R.id.viewColorBlue, "#2196F3");
        setupColorClick(R.id.viewColorYellow, "#FFEB3B");
        setupColorClick(R.id.viewColorOrange, "#FF9800");
        setupColorClick(R.id.viewColorGray, "#9E9E9E");

        // 6. Barre de recherche dynamique (Filtre textuel en temps réel)
        EditText etSearch = findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) {
                    adapter.filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // 7. Gestion du Bouton de filtrage Favoris (Clic simple classique)
        TextView btnFavoris = findViewById(R.id.btnFavoris);
        btnFavoris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter == null) return;

                isFilteringFavorites = !isFilteringFavorites;

                if (isFilteringFavorites) {
                    adapter.filterFavorites(true);
                    btnFavoris.setText("★ Favoris");
                } else {
                    adapter.filterFavorites(false);
                    btnFavoris.setText("Favoris");
                }
            }
        });

        // 8. --- GESTION DU BOUTON DE TRI PROPRE (Respect de la maquette) ---
        // On cherche le bouton de tri dans ton XML (remplace R.id.btnTri par l'ID exact de ton bouton si besoin)
        View btnTri = findViewById(R.id.btnTri);
        if (btnTri != null) {
            btnTri.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (adapter == null) return;

                    // Création d'un menu popup élégant ancré sous le bouton
                    PopupMenu popupMenu = new PopupMenu(MainActivity.this, v);
                    popupMenu.getMenu().add(0, 0, 0, "📅 Plus récentes d'abord");
                    popupMenu.getMenu().add(0, 1, 1, "🔤 Ordre alphabétique");

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == 0) {
                                adapter.setSortType(0);
                                Toast.makeText(MainActivity.this, "Trié par date 📅", Toast.LENGTH_SHORT).show();
                                return true;
                            } else if (item.getItemId() == 1) {
                                adapter.setSortType(1);
                                Toast.makeText(MainActivity.this, "Trié par titre 🔤", Toast.LENGTH_SHORT).show();
                                return true;
                            }
                            return false;
                        }
                    });
                    popupMenu.show(); // Affichage du menu
                }
            });
        }
    }

    private void setupColorClick(int viewId, final String hexColor) {
        View colorView = findViewById(viewId);
        if (colorView != null) {
            colorView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cardPaletteContainer.setVisibility(View.GONE);
                    fabAddNote.setVisibility(View.VISIBLE);

                    Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
                    intent.putExtra("EXTRA_COLOR", hexColor);
                    startActivity(intent);
                }
            });
        }
    }
}