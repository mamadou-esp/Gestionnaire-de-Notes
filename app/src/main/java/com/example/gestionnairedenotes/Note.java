package com.example.gestionnairedenotes;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "table_notes")
public class Note {

    @PrimaryKey(autoGenerate = true)
    private int id; // Identifiant unique généré automatiquement

    private String titre;
    private String contenu;
    private String date;
    private String couleur; // Stockera le code hexadécimal (ex: "#219653")
    private boolean isFavori; // true si favori, false sinon

    // Constructeur utilisé lors de la création d'une nouvelle note
    public Note(String titre, String contenu, String date, String couleur, boolean isFavori) {
        this.titre = titre;
        this.contenu = contenu;
        this.date = date;
        this.couleur = couleur;
        this.isFavori = isFavori;
    }

    // Getters et Setters (obligatoires pour que Room puisse lire/écrire les données)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getCouleur() { return couleur; }
    public void setCouleur(String couleur) { this.couleur = couleur; }



    public boolean isFavori() { return isFavori; }
    public void setFavori(boolean favori) { isFavori = favori; }
}