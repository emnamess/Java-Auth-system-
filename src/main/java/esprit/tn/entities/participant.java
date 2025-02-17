package esprit.tn.entities;
import java.sql.Date;
import java.time.LocalDate;

public class participant extends user {
    private int nombreParticipations;

    public participant(String nom, String prenom, String email, String motDePasse, LocalDate dateNaissance, String adresse, int telephone, LocalDate dateInscription, int nombreParticipations) {
        super(nom, prenom, email, motDePasse, dateNaissance, adresse, telephone, dateInscription);
        this.nombreParticipations = nombreParticipations;
    }

    public int getNombreParticipations() {
        return nombreParticipations;
    }

    public void setNombreParticipations(int nombreParticipations) {
        this.nombreParticipations = nombreParticipations;
    }
}
