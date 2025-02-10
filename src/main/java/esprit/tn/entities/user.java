package esprit.tn.entities;

import java.time.LocalDate;

public class user {
    protected int id_user;
    protected String nom;
    protected String prenom;
    protected String email;
    protected String motDePasse;
    protected LocalDate dateNaissance;
    protected String adresse;
    protected int telephone;
    protected LocalDate  dateInscription;

    public user(int id_user, String nom, String prenom, String email, String motDePasse, LocalDate  dateNaissance, String adresse, int telephone, LocalDate  dateInscription) {
        this.id_user = id_user;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.dateNaissance = dateNaissance;
        this.adresse = adresse;
        this.telephone = telephone;
        this.dateInscription = dateInscription;
    }
    public user() {
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public LocalDate  getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate  dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public int getTelephone() {
        return telephone;
    }

    public void setTelephone(int telephone) {
        this.telephone = telephone;
    }

    public LocalDate  getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(LocalDate  dateInscription) {
        this.dateInscription = dateInscription;
    }
}
