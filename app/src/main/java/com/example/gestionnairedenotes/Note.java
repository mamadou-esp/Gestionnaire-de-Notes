package com.example.gestionnairedenotes;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "table_notes")
public class Note implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String titre;
    private String contenu;
    private String couleur;
    private String date;
    private boolean isFavoris;

    // Constructeur complet
    public Note(String titre, String contenu, String couleur, String date, boolean isFavoris) {
        this.titre = titre;
        this.contenu = contenu;
        this.couleur = couleur;
        this.date = date;
        this.isFavoris = isFavoris;
    }

    // Constructeur vide (requis par Room)
    public Note() {
    }

    // --- Garde exactement les mêmes Getters et Setters que tu as déjà ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }
    public String getCouleur() { return couleur; }
    public void setCouleur(String couleur) { this.couleur = couleur; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public boolean isFavoris() { return isFavoris; }
    public void setFavoris(boolean favoris) { this.isFavoris = favoris; }
}