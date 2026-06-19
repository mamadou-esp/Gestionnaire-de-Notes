package com.example.gestionnairedenotes;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

    private View editColorGreen, editColorRed, editColorBlue, editColorYellow, editColorOrange, editColorGray;

    private String noteColor = "#FFFFFF";
    private int noteId = -1;
    private boolean isFavori = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        etNoteTitle = findViewById(R.id.etNoteTitle);
        etNoteContent = findViewById(R.id.etNoteContent);
        btnSaveNote = findViewById(R.id.btnSaveNote);
        mainContainer = findViewById(R.id.mainAddEdit);

        editColorGreen = findViewById(R.id.editColorGreen);
        editColorRed = findViewById(R.id.editColorRed);
        editColorBlue = findViewById(R.id.editColorBlue);
        editColorYellow = findViewById(R.id.editColorYellow);
        editColorOrange = findViewById(R.id.editColorOrange);
        editColorGray = findViewById(R.id.editColorGray);

        if (getIntent().hasExtra("EXTRA_ID")) {
            noteId = getIntent().getIntExtra("EXTRA_ID", -1);
            noteColor = getIntent().getStringExtra("EXTRA_COLOR");
            isFavori = getIntent().getBooleanExtra("EXTRA_IS_FAVORI", false);

            etNoteTitle.setText(getIntent().getStringExtra("EXTRA_TITLE"));
            etNoteContent.setText(getIntent().getStringExtra("EXTRA_CONTENT"));
            btnSaveNote.setText("Modifier");
        } else {
            if (getIntent().hasExtra("EXTRA_COLOR")) {
                noteColor = getIntent().getStringExtra("EXTRA_COLOR");
            }
            btnSaveNote.setText("Créer");
        }

        rafraichirCouleurEcran(noteColor);

        editColorGreen.setOnClickListener(v -> rafraichirCouleurEcran("#219653"));
        editColorRed.setOnClickListener(v -> rafraichirCouleurEcran("#EB5757"));
        editColorBlue.setOnClickListener(v -> rafraichirCouleurEcran("#2F80ED"));
        editColorYellow.setOnClickListener(v -> rafraichirCouleurEcran("#F2C94C"));
        editColorOrange.setOnClickListener(v -> rafraichirCouleurEcran("#F2994A"));
        editColorGray.setOnClickListener(v -> rafraichirCouleurEcran("#828282"));

        btnSaveNote.setOnClickListener(v -> sauvegarderNote());

        ImageButton btnShareNote = findViewById(R.id.btnShareNote);
        btnShareNote.setOnClickListener(v -> partagerNote());
    }

    private void partagerNote() {
        String title = etNoteTitle.getText().toString().trim();
        String content = etNoteContent.getText().toString().trim();

        if (TextUtils.isEmpty(title) && TextUtils.isEmpty(content)) {
            Toast.makeText(this, "La note est vide, rien à partager", Toast.LENGTH_SHORT).show();
            return;
        }

        String shareBody = "Note : " + title + "\n\n" + content;

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);

        startActivity(Intent.createChooser(shareIntent, "Partager la note via :"));
    }

    private void rafraichirCouleurEcran(String hexColor) {
        this.noteColor = hexColor;
        try {
            mainContainer.setBackgroundColor(Color.parseColor(noteColor));

            if (noteColor.equalsIgnoreCase("#F2C94C")) {
                etNoteTitle.setTextColor(Color.BLACK);
                etNoteTitle.setHintTextColor(Color.GRAY);
                etNoteContent.setTextColor(Color.BLACK);
                etNoteContent.setHintTextColor(Color.GRAY);
            } else {
                etNoteTitle.setTextColor(Color.WHITE);
                etNoteTitle.setHintTextColor(Color.parseColor("#CCCCCC"));
                etNoteContent.setTextColor(Color.WHITE);
                etNoteContent.setHintTextColor(Color.parseColor("#CCCCCC"));
            }
        } catch (IllegalArgumentException e) {
            mainContainer.setBackgroundColor(Color.WHITE);
        }
    }

    private void sauvegarderNote() {
        String title = etNoteTitle.getText().toString().trim();
        String content = etNoteContent.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "Veuillez saisir un titre", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "Veuillez écrire un contenu", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String currentDate = sdf.format(new Date());

        NoteDatabase db = NoteDatabase.getInstance(this);
        NoteDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if (noteId == -1) {
                    Note nouvelleNote = new Note(title, content, currentDate, noteColor, false);
                    db.noteDao().insert(nouvelleNote);
                } else {
                    Note noteModifiee = new Note(title, content, currentDate, noteColor, isFavori);
                    noteModifiee.setId(noteId);
                    db.noteDao().update(noteModifiee);
                }

                runOnUiThread(() -> {
                    Toast.makeText(AddEditActivity.this, "Opération réussie", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }
}