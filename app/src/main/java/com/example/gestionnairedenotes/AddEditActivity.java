package com.example.gestionnairedenotes;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddEditActivity extends AppCompatActivity {

    private EditText etNoteTitle;
    private EditText etNoteContent;
    private Button btnSaveNote;
    private ConstraintLayout mainContainer;

    private String noteColor = "#FFFFFF"; // Couleur par défaut

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        // 1. Liaison des composants graphiques XML
        etNoteTitle = findViewById(R.id.etNoteTitle);
        etNoteContent = findViewById(R.id.etNoteContent);
        btnSaveNote = findViewById(R.id.btnSaveNote);
        mainContainer = findViewById(R.id.mainAddEdit);

        // 2. Récupération de la couleur choisie depuis la MainActivity
        if (getIntent().hasExtra("EXTRA_COLOR")) {
            noteColor = getIntent().getStringExtra("EXTRA_COLOR");
            try {
                // Application de la couleur officielle au fond de l'écran (Fidélité au design)
                mainContainer.setBackgroundColor(Color.parseColor(noteColor));

                // Si la couleur de fond est sombre (comme le vert, rouge, bleu ou gris),
                // on adapte la couleur des textes d'indication en blanc translucide pour la lisibilité
                if (!noteColor.equals("#F2C94C")) { // Sauf pour le jaune clair
                    etNoteTitle.setTextColor(Color.WHITE);
                    etNoteTitle.setHintTextColor(Color.parseColor("#CCCCCC"));
                    etNoteContent.setTextColor(Color.WHITE);
                    etNoteContent.setHintTextColor(Color.parseColor("#CCCCCC"));
                }
            } catch (IllegalArgumentException e) {
                // Repli sécurisé sur le fond blanc en cas d'erreur de format
                mainContainer.setBackgroundColor(Color.WHITE);
            }
        }

        // 3. Action du clic sur le bouton Créer
        btnSaveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sauvegarderNote();
            }
        });
    }

    /**
     * Valide les saisies, formate la date courante et insère la note dans Room en tâche de fond.
     */
    private void sauvegarderNote() {
        String title = etNoteTitle.getText().toString().trim();
        String content = etNoteContent.getText().toString().trim();

        // Validation stricte des entrées
        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "Veuillez saisir un titre", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "Veuillez écrire un contenu", Toast.LENGTH_SHORT).show();
            return;
        }

        // Génération et formatage de la date système courante (ex: "11 Juin 2026")
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        // Création de l'objet Note (par défaut, non favori à la création)
        final Note nouvelleNote = new Note(title, content, currentDate, noteColor, false);

        // Insertion asynchrone dans Room pour ne pas bloquer l'UI Thread (Critère Persistance locale)
        NoteDatabase db = NoteDatabase.getInstance(this);
        NoteDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                db.noteDao().insert(nouvelleNote);

                // Retour à l'écran précédent sur le thread principal une fois l'opération terminée
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AddEditActivity.this, "Note enregistrée avec succès", Toast.LENGTH_SHORT).show();
                        finish(); // Ferme cette activité et retourne à MainActivity
                    }
                });
            }
        });
    }
}