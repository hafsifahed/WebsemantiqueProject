package esprit.entities;

public class Velo {
    private String id;                       // Unique identifier for the bicycle
    private String marque;                   // Brand of the bicycle
    private String modele;                   // Model of the bicycle
    private String couleur;                  // Color of the bicycle
    private String immatriculation;          // License plate number (if applicable)
    private int annee;                       // Year of manufacture
    private double poids;                    // Weight of the bicycle

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMarque() {
        return marque;
    }

    public void setMarque(String marque) {
        this.marque = marque;
    }

    public String getModele() {
        return modele;
    }

    public void setModele(String modele) {
        this.modele = modele;
    }

    public String getCouleur() {
        return couleur;
    }

    public void setCouleur(String couleur) {
        this.couleur = couleur;
    }

    public String getImmatriculation() {
        return immatriculation;
    }

    public void setImmatriculation(String immatriculation) {
        this.immatriculation = immatriculation;
    }

    public int getAnnee() {
        return annee;
    }

    public void setAnnee(int annee) {
        this.annee = annee;
    }

    public double getPoids() {
        return poids;
    }

    public void setPoids(double poids) {
        this.poids = poids;
    }
}