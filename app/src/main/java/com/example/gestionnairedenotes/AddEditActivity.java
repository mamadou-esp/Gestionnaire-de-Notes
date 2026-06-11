package com.example.gestionnairedenotes;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddEditActivity extends AppCompatActivity {

    private EditText etNoteTitle;
    private EditText etNoteContent;
    private Button btnSaveNote;

    private View viewColorWhite, viewColorRed, viewColorBlue, viewColorYellow, viewColorGreen, viewColorGrey, viewColorOrange;

    // Blanc par défaut, mais sera écrasé par la couleur reçue de l'écran précédent
    private String selectedColor = "#FFFFFF";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        etNoteTitle = findViewById(R.id.etNoteTitle);
        etNoteContent = findViewById(R.id.etNoteContent);
        btnSaveNote = findViewById(R.id.btnSaveNote);

        viewColorWhite = findViewById(R.id.colorWhite);
        viewColorRed = findViewById(R.id.colorRed);
        viewColorBlue = findViewById(R.id.colorBlue);
        viewColorYellow = findViewById(R.id.colorYellow);
        viewColorGreen = findViewById(R.id.colorGreen);
        viewColorGrey = findViewById(R.id.colorGrey);
        viewColorOrange = findViewById(R.id.colorOrange);

        // --- Récupération de la couleur envoyée depuis le menu déroulant ---
        String incomingColor = getIntent().getStringExtra("SELECTED_COLOR_FROM_MAIN");
        if (incomingColor != null) {
            selectedColor = incomingColor;
        }

        setupColorListeners();
        btnSaveNote.setOnClickListener(v -> validateAndSave());
    }

    private void setupColorListeners() {
        if (viewColorWhite != null) {
            viewColorWhite.setOnClickListener(v -> selectedColor = "#FFFFFF");
        }
        if (viewColorRed != null) {
            viewColorRed.setOnClickListener(v -> selectedColor = "#FF5252");
        }
        if (viewColorBlue != null) {
            viewColorBlue.setOnClickListener(v -> selectedColor = "#448AFF");
        }
        if (viewColorYellow != null) {
            viewColorYellow.setOnClickListener(v -> selectedColor = "#FFEB3B");
        }
        if (viewColorGreen != null) {
            viewColorGreen.setOnClickListener(v -> selectedColor = "#4CAF50");
        }
        if (viewColorGrey != null) {
            viewColorGrey.setOnClickListener(v -> selectedColor = "#828282");
        }
        if (viewColorOrange != null) {
            viewColorOrange.setOnClickListener(v -> selectedColor = "#F2994A");
        }
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

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        // Création de la note avec la couleur (soit celle cliquée dans le menu, soit changée via pastille)
        Note targetNote = new Note(title, content, selectedColor, currentDate, false);

        NoteDatabase.databaseWriteExecutor.execute(() -> {
            NoteDatabase.getInstance(AddEditActivity.this).noteDao().insertNote(targetNote);

            runOnUiThread(() -> {
                Toast.makeText(AddEditActivity.this, "Note enregistrée avec succès !", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }
}