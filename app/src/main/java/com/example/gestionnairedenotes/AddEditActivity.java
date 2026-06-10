package com.example.gestionnairedenotes;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddEditActivity extends AppCompatActivity {
    private EditText etNoteTitle, etNoteContent;
    private Button btnSaveNote;
    private String selectedColor = "#FFFFFF"; // Couleur par défaut de la note

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        etNoteTitle = findViewById(R.id.etNoteTitle);
        etNoteContent = findViewById(R.id.etNoteContent);
        btnSaveNote = findViewById(R.id.btnSaveNote);

        setupPaletteListeners();

        btnSaveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndSave();
            }
        });
    }

    private void setupPaletteListeners() {
        findViewById(R.id.colorWhite).setOnClickListener(v -> selectedColor = "#FFFFFF");
        findViewById(R.id.colorGrey).setOnClickListener(v -> selectedColor = "#828282");
        findViewById(R.id.colorGreen).setOnClickListener(v -> selectedColor = "#219653");
        findViewById(R.id.colorRed).setOnClickListener(v -> selectedColor = "#EB5757");
        findViewById(R.id.colorBlue).setOnClickListener(v -> selectedColor = "#2F80ED");
        findViewById(R.id.colorYellow).setOnClickListener(v -> selectedColor = "#F2C94C");
        findViewById(R.id.colorOrange).setOnClickListener(v -> selectedColor = "#F2994A");
    }

    private void validateAndSave() {
        String title = etNoteTitle.getText().toString().trim();
        String content = etNoteContent.getText().toString().trim();

        if (title.isEmpty()) {
            etNoteTitle.setError("Le titre de la note ne peut pas être vide");
            etNoteTitle.requestFocus();
            return;
        }
        if (content.isEmpty()) {
            etNoteContent.setError("Le contenu de la note ne peut pas être vide");
            etNoteContent.requestFocus();
            return;
        }

        // Création de la date du jour
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault());
        String currentDate = sdf.format(new java.util.Date());

        Note targetNote = new Note(title, content, selectedColor, currentDate, false);

        // Exécution en arrière-plan via l'Executor pour ne pas bloquer l'interface
        NoteDatabase.databaseWriteExecutor.execute(() -> {
            NoteDatabase.getInstance(AddEditActivity.this).noteDao().insertNote(targetNote);

            // Retour sur le thread principal pour afficher le Toast et fermer l'activité
            runOnUiThread(() -> {
                android.widget.Toast.makeText(AddEditActivity.this, "Note enregistrée avec succès !", android.widget.Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }
}