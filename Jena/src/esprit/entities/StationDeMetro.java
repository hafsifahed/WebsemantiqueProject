package esprit.entities;

public class StationDeMetro {
    private String id;                       // Unique identifier for the metro station
    private String nomStation;               // Name of the metro station
    private String adresse;                  // Address of the metro station
    private String numeroDeTelephone;        // Phone number of the metro station
    private String horairesOuverture;        // Opening hours of the metro station
    private String horairesFermeture;        // Closing hours of the metro station
    private int capaciteMax;                 // Maximum capacity of the metro station

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNomStation() {
        return nomStation;
    }

    public void setNomStation(String nomStation) {
        this.nomStation = nomStation;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getNumeroDeTelephone() {
        return numeroDeTelephone;
    }

    public void setNumeroDeTelephone(String numeroDeTelephone) {
        this.numeroDeTelephone = numeroDeTelephone;
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