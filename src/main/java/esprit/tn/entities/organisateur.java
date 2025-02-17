package esprit.tn.entities;
import java.sql.Date;
import java.time.LocalDate;

public class organisateur extends user{
    private String workField;
    private String workEmail;

    public organisateur( String nom, String prenom, String email, String motDePasse, LocalDate dateNaissance, String adresse, int telephone, LocalDate dateInscription, String workField, String workEmail) {
        super(nom, prenom, email, motDePasse, dateNaissance, adresse, telephone, dateInscription);
        this.workField = workField;
        this.workEmail = workEmail;
    }

    public String getWorkEmail() {
        return workEmail;
    }

    public void setWorkEmail(String workEmail) {
        this.workEmail = workEmail;
    }

    public String getWorkField() {
        return workField;
    }

    public void setWorkField(String workField) {
        this.workField = workField;
    }
}
