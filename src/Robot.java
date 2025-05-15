import java.util.ArrayList;

public  abstract class Robot {
    protected String id;
    protected int x;
    protected int y;
    protected int energie;
    protected int heuresUtilisation;
    protected boolean EnMarche;
    protected ArrayList<String> historiqueActions;

//protected pour pouvoir l'acceder dans les classes filles

    public Robot(String id, int x, int y, int energie, int heuresUtilisation) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.energie = energie;
        this.heuresUtilisation = heuresUtilisation;
        this.EnMarche = false;
        this.historiqueActions = new ArrayList<String>();
        ajouterHistorique(id + " : Robot créé à la position (" + x + "," + y + ") avec " + energie + " d'énergie ");

    }

    //historique
    public void ajouterHistorique(String action) {
        historiqueActions.add( java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")) + " : " + action);
    }
    public ArrayList<String> getHistoriqueActions() {
        return historiqueActions;
    }


    //verif d'energie
    public boolean verifierEnergie(int energierequise) throws EnergieinsuffisanteException {
        if (this.energie < energierequise) {
            throw new EnergieinsuffisanteException("Pas assez d'energie ");
        } else {
            return true;
        }
       
    }

    //verif maintenance
    public boolean verifierMaintenance() throws MaintenanceRequiseException {
        if (this.heuresUtilisation > 100) {
            throw new MaintenanceRequiseException("Maintenance requise ");
        } else {
            return true;
        }
    }

    //demarrage
    public void demarrer(int energierequise) throws RobotException{
        if (this.energie <( energierequise*0.1)) {  // 10% de l'energie requise pour demarrer
        ajouterHistorique(id + " : echec de demarrage , pas assez d'énergie ");
            throw new RobotException("manque d’energie");
           
        } 
         else {
            this.EnMarche = true;
            ajouterHistorique(id + " : Robot demarré ");
        }
    }

    //arret
    public void arreter() {
        this.EnMarche = false;
        ajouterHistorique(id + " : Robot arrêté ");
    }

    //consommer d'energie
    public void consommerEnergie(int en) throws EnergieinsuffisanteException {
        if (this.energie < en) {
            throw new EnergieinsuffisanteException("Pas assez d'energie ");
        } else {
            this.energie -= en;
            ajouterHistorique(id + " : Energie consommée : " + en);
        }
    }

    //recharger
    public void recharger(int q) {
        if (this.energie + q > 100) {
            this.energie = 100;
            ajouterHistorique(id + " : Energie rechargée à 100% ");}
            else{
                this.energie += q;
                ajouterHistorique(id + " : Energie rechargée de " + q + " ");}
            }
            
    
       
    
    //deplacer  
    public abstract void  deplacer(int x,int y) throws RobotException;


//effectuer une tache
    public abstract void effectuerTache(String tache) throws RobotException, EnergieinsuffisanteException, MaintenanceRequiseException;


public String toString() {
        return "{" +
                "id=" + id  +
                ", Position x=" + x +
                ", y=" + y +
                ", energie=" + energie +
                "% , heuresUtilisation=" + heuresUtilisation +
                '}';
    }








    //getters et setters
    public int getX() {
        return x;
    }


    public int getY() {
        return y;
    }
    public int getEnergie() {
        return energie;
    }
    public String getId() {
        return id;
    }
    public int getHeuresUtilisation() {
        return heuresUtilisation;
    }
    public boolean isEnMarche() {
        return EnMarche;
    }
    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }
    public void setEnergie(int energie) {
        this.energie = energie;
    }
    public void setHeuresUtilisation(int heuresUtilisation) {
        this.heuresUtilisation = heuresUtilisation;
    }
    public void setEnMarche(boolean enMarche) {
        EnMarche = enMarche;
    }
    public void setId(String id) {
        this.id = id;
    }
    public void setHistoriqueActions(ArrayList<String> historiqueActions) {
        this.historiqueActions = historiqueActions;
    }
   
}