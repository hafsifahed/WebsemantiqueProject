package esprit.entities;

public class Metro {
    private String id;                       // Unique identifier for the metro
    private String nomMetro;                 // Name of the metro
    private String ligne;                    // Line of the metro
    private String couleur;                  // Color of the metro line
    private String horairesOuverture;        // Opening hours of the metro
    private String horairesFermeture;        // Closing hours of the metro
    private int capaciteMax;                 // Maximum capacity of the metro

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNomMetro() {
        return nomMetro;
    }

    public void setNomMetro(String nomMetro) {
        this.nomMetro = nomMetro;
    }

    public String getLigne() {
        return ligne;
    }

    public void setLigne(String ligne) {
        this.ligne = ligne;
    }

    public String getCouleur() {
        return couleur;
    }

    public void setCouleur(String couleur) {
        this.couleur = couleur;
    }

    public String getHorairesOuverture() {
        return horairesOuverture;
    }

    public void setHorairesOuverture(String horairesOuverture) {
        this.horairesOuverture = horairesOuverture;
    }

    public String getHorairesFermeture() {
        return horairesFermeture;
    }

    public void setHorairesFermeture(String horairesFermeture) {
        this.horairesFermeture = horairesFermeture;
    }

    public int getCapaciteMax() {
        return capaciteMax;
    }

    public void setCapaciteMax(int capaciteMax) {
        this.capaciteMax = capaciteMax;
    }
}