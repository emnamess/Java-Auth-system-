package esprit.tn.entities;
import java.sql.Date;
import java.time.LocalDate;

public class partenaire extends user {
    private String typeService;
    private String siteWeb;
    private int nbreContrats;

    public partenaire(String nom, String prenom, String email, String motDePasse, LocalDate dateNaissance, String adresse, int telephone, LocalDate dateInscription, String typeService, String siteWeb, int nbreContrats) {
        super(nom, prenom, email, motDePasse, dateNaissance, adresse, telephone, dateInscription);
        this.typeService = typeService;
        this.siteWeb = siteWeb;
        this.nbreContrats = nbreContrats;
    }

    public String getTypeService() {
        return typeService;
    }

    public void setTypeService(String typeService) {
        this.typeService = typeService;
    }

    public String getSiteWeb() {
        return siteWeb;
    }

    public void setSiteWeb(String siteWeb) {
        this.siteWeb = siteWeb;
    }

    public int getNbreContrats() {
        return nbreContrats;
    }

    public void setNbreContrats(int nbreContrats) {
        this.nbreContrats = nbreContrats;
    }
}
