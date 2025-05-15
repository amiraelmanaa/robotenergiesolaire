public abstract class RobotConnecte extends Robot  implements Connectable{
     protected boolean connecte = false;
     protected String reseauConnecte=null;

     public RobotConnecte(String id, int x, int y, int e, int h,boolean c,String res) {
        super(id, x, y, e, h);
        this.connecte=c;
        this.reseauConnecte=res;

    }

    public void connecter ( String reseau ) throws RobotException {
        if (this.connecte) {
            throw new RobotException("Le robot est déjà connecté à un réseau.");
        } else if (!(verifierEnergie(5))) {
            throw new RobotException("Pas assez d'énergie pour se connecter.");
        } else  if (!(verifierMaintenance())) {
            throw new MaintenanceRequiseException("Maintenance requise avant de se connecter.");
        } else {
            consommerEnergie(5); 
            this.reseauConnecte = reseau;
            this.connecte = true;
            ajouterHistorique(id + " : Connecté au réseau " + reseau);
        }
    }
    public void deconnecter () {
        if (this.connecte) {
            this.connecte = false;
            this.reseauConnecte = null;
            ajouterHistorique(id + " : Déconnecté du réseau ");
        } else {
            ajouterHistorique(id + " : Le robot n'est pas connecté à un réseau.");
        }
    }
    public void envoyerDonnees( String donnees) throws RobotException {
        if (!this.connecte) {
            throw new RobotException("Le robot n'est pas connecté à un réseau.");

        } else if (!(verifierEnergie(3))) {
            throw new EnergieinsuffisanteException("Pas assez d'énergie pour envoyer des données.");
            
        } else if (!(verifierMaintenance())) {
            throw new MaintenanceRequiseException("Maintenance requise avant d'envoyer des données.");
        } else {
            consommerEnergie(3); 
            ajouterHistorique(id + " : Données envoyées : " + donnees + " au réseau " + this.reseauConnecte);
        }
    }
    
}
