package esprit.tn.entities;

import java.sql.Date;
import java.time.LocalDate;

public class admin extends user{
    private LocalDate createdAt;

    public admin(String nom, String prenom, String email, String motDePasse, LocalDate dateNaissance, String adresse, int telephone, LocalDate dateInscription, LocalDate createdAt) {
        super(nom, prenom, email, motDePasse, dateNaissance, adresse, telephone, dateInscription);
        this.createdAt = createdAt;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }
}
