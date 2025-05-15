 public class RobotLivraison extends RobotConnecte {

    // Attributs
    private String ColisActuel; // string pour le nom / description du colis
    private int ColiVerif; // 0 si pas de colis, 1 si colis chargé
    private String destination;
    private boolean enLivraison;
    private static int ENERGIE_LIVRAISON = 15;
    private static int ENERGIE_CHARGEMENT = 15;
    private int energieSolaire; // Stock lnergie solaire 
    private int capaciteMaxSolaire; // Cap  max du stockage solaire
    private boolean modeSolaire; // true = utilise energie solaire  false = utilise lelectricité
    private boolean tempsEnsoleille; // true = soleil, false = nuageux
    private int tauxRechargesolaire; // % de recharge solaire  lorsque modeSolaire = true 


    // Constructeur
    public RobotLivraison(String id, int x, int y) {
        super(id, x, y, 100, 0, false, null);
        this.ColisActuel = null;
        this.destination = null;
        this.enLivraison = false;
        this.ColiVerif = 0;
        this.energieSolaire = 30;         // commence avec 30% d'energie solaire
        this.capaciteMaxSolaire = 100;    
        this.modeSolaire = true;          // par defaut energie solaire
        this.tempsEnsoleille = true;      
        this.tauxRechargesolaire = 5;     // recharge 5% d'energie solaire par heure au soleil

    }

    // Méthodes
    
    @Override
    public void effectuerTache(String tache)
            throws RobotException, EnergieinsuffisanteException, MaintenanceRequiseException {
        if (!(this.EnMarche)) {
            throw new RobotException("Le robot n'est pas en marche.");

        } else if (this.energieSolaire <ENERGIE_LIVRAISON){
            throw new EnergieinsuffisanteException("Pas assez d'énergie solaire pour effectuer la tâche.");
        }
        else if (!(verifierEnergie(ENERGIE_LIVRAISON))) {
            throw new EnergieinsuffisanteException("Pas assez d'énergie pour effectuer la tâche.");

        } else if (!(verifierMaintenance())) {
            throw new MaintenanceRequiseException("Maintenance requise avant d'effectuer la tâche.");
        } else if (this.enLivraison == true) {

            faireLivraison(x, y);

        }
    }

    public void faireLivraison(int a, int b) throws RobotException {
        if (this.ColisActuel == null) {
            throw new RobotException("Aucun colis à livrer.");
        } else {
            deplacer(a, b);
            System.out.println("Colis " + this.ColisActuel + " livré à la destination (" + x + ", " + y + ")");
            this.ColisActuel = null;
            this.enLivraison = false;
            ajouterHistorique(id + " : Livraison terminée a (" + x + ", " + y + ")");
   

        }
    }


    public void setMeteo(boolean s) {
        this.tempsEnsoleille = s;
        if (s) {
            ajouterHistorique("Météo: ensoleillé - recharge solaire activee");
        } else {
            ajouterHistorique("Météo: Nuageux - passage à l'énergie électrique");
        }
        this.modeSolaire = s ; //&& this.energieSolaire > 0; //il faut que l'enrgie solaire soit aussi positive pour activer le mode solaire
    }
 
    public void rechargerSolaire(int duree) {
        if (!this.tempsEnsoleille) {
            System.out.println("impossible de recharger lenergie solaire en temmps nuageux");
            return;
        }
        else{
        int recharge = duree * this.tauxRechargesolaire;
        int ancienval = this.energieSolaire;
        this.energieSolaire = Math.min(this.energieSolaire + recharge, this.capaciteMaxSolaire); // car on ne depasse pas la capacite max      
        ajouterHistorique("recharge solaire pendant " + duree + "h: +" + 
                          (this.energieSolaire - ancienval) + "% d'énergie solaire");
        
        System.out.println("recharge solaire: " + this.energieSolaire + "% / " + this.capaciteMaxSolaire + "%");
    }

}
public double calculerEmpreinteCarbone(double distance) {
    if (this.modeSolaire) {
        return 0.0;
    } else {
        return distance * 0.02;
    }
}


    
    @Override
    public void deplacer(int a, int b) throws RobotException {
    float distance = (float)Math.sqrt((a-this.x)*(a-this.x)+(b-this.y)*(b-this.y));
    if (distance > 100) {
        throw new RobotException("La distance a dépassé 100 unités, impossible de livrer ce colis");
    }
    
    int energienecessaire = (int)(0.3 * distance);
    
    boolean energiesuffisante = (this.modeSolaire && this.energieSolaire >= energienecessaire) || 
                              (!this.modeSolaire && verifierEnergie(energienecessaire));
    
    if (energiesuffisante && verifierMaintenance()) {
        consommerEnergie(energienecessaire);
        double empreinte = calculerEmpreinteCarbone(distance);
        String messageEmpreinte;
        if (this.modeSolaire) {
            messageEmpreinte = " Déplacement écologique - 0 emission de CO2";
        } else {
            messageEmpreinte = " Empreinte carbone: " + String.format("%.2f", empreinte) + " unités de CO2"; //calcul d epreinte carbonique lors du deplacement du robot
        }
        this.x = a;
        this.y = b;
        this.heuresUtilisation += distance/10;
        if (this.tempsEnsoleille && !this.modeSolaire) {
            int recuperation = (int)(distance * 0.01); // 1% d'énergie récupérée par 100 unités de distance
            if (recuperation > 0) {
                energieSolaire = Math.min(energieSolaire + recuperation, capaciteMaxSolaire); // on ne depasse pas la capacite max
                System.out.println(" recupeation d'énergie solaire pendant le déplacement: +" + recuperation + "%");
            }
        }
        ajouterHistorique("le robot est déplacé à ("+this.x+","+this.y+"). " + messageEmpreinte);
        System.out.println("le robot est déplacé à ("+this.x+","+this.y+").");
        System.out.println(messageEmpreinte);
    } else {
        throw new RobotException("Pas possible de se déplacer. Vérifier l'énergie ou la maintenance.");
    }
}
 









    public void chargerColis(String dest) throws RobotException {
        if (this.enLivraison) {
            throw new RobotException("Le robot est déjà  en cours de livraison.");
        } else if (this.energieSolaire < ENERGIE_CHARGEMENT) {
            throw new EnergieinsuffisanteException("Pas assez d'énergie solaire pour charger le colis.");
        } 
        else if (!(verifierEnergie(ENERGIE_CHARGEMENT))) {
            throw new EnergieinsuffisanteException("Pas assez d'énergie pour charger le colis.");
        } else if (!(verifierMaintenance())) {
            throw new MaintenanceRequiseException("Maintenance requise avant de charger le colis.");
        } else {
            this.destination = dest;
            this.enLivraison = true;
            consommerEnergie(ENERGIE_CHARGEMENT);
            ColiVerif = 1;
            ajouterHistorique(id + " : Colis " + this.ColisActuel + "chargé  a la distination: " + dest);

        }
    }

    @Override
    public String toString() {
        String r;
        if (this.reseauConnecte != null) {
            r = "Oui connecté au réseau " + this.reseauConnecte;
        } else {
            r = "Non ";
        }

        return "Robot ID: " + id + "\n" +
                "Position: (" + x + ", " + y + ")\n" +
                "Energie: " + energie + "%\n" +
                "Heures d'utilisation: " + heuresUtilisation + "\n" +
                "Colis Actuel: " + ColisActuel + "\n" +
                "Colis: " + ColiVerif + "\n" +
                "Destination: " + destination + "\n" +
                "Connecté: " + r + "\n"+
                "   Energie solaire: " + energieSolaire + "% / " + capaciteMaxSolaire + "%\n" +
                "   Mode d'énergie: " + (modeSolaire ? "Solaire " : "Électrique ") + "\n" +
                "   Météo: " + (tempsEnsoleille ? "Ensoleillé " : "Nuageux ") + "\n" +
                "   Taux de recharge solaire: " + tauxRechargesolaire + "%/h\n";

    }
    // on va surcharger la methode consommerEnergie de la classe Robot pour gerer l'energie solaire et electrique
    @Override
   public void consommerEnergie(int quantite) {
    // cas ou on utilise l'energie solaire
    if (this.modeSolaire && this.energieSolaire >= quantite) {
        this.energieSolaire -= quantite;
        ajouterHistorique("consommation d'energie solaire: -" + quantite + "% (Restant: " + energieSolaire + "%)");
        
        
        //si apres l'energie solaire devinne top basse on indique qu on passe a l'electrique 
        if (energieSolaire < 10) {
            System.out.println(" energie solaire insuff passage à  l electrique");
            modeSolaire = false; }
    } 
    else {
        if (this.modeSolaire == false) {  //nuageux
        if (this.energieSolaire >quantite) { //energie stockée suffisante
            this.energieSolaire -= quantite;
            ajouterHistorique("consommation d'energie solaire stockée : -" + quantite + "% (Restant: " + energieSolaire + "%)");
        } else {  //energie stockée insuffisante
            try {
            super.consommerEnergie(quantite);
            ajouterHistorique("consommation d'énergie électrique: -" + quantite + "% (Restant: " + energie + "%)");
            System.out.println("consommation d'énergie électrique: -" + quantite + "% (Restant: " + energie + "%)");
        } catch (EnergieinsuffisanteException e) {
            System.out.println(" energie insuffisante pour effectuer cette op");
            ajouterHistorique("echec de consommation d'énergie électrique: energie insuffisante.");
        }

    }
}
}
   }


    // getters et setters
    public String getColisActuel() {
        return ColisActuel;
    }

    public String getDestination() {
        return destination;
    }

    public boolean isEnLivraison() {
        return enLivraison;
    }

    public void setColisActuel(String colisActuel) {
        ColisActuel = colisActuel;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setEnLivraison(boolean enLivraison) {
        this.enLivraison = enLivraison;
    }

    public int getColiVerif() {
        return ColiVerif;
    }

    public void setColiVerif(int ColiVerif) {
        this.ColiVerif = ColiVerif;
    }
    public int getEnergieSolaire() {
        return energieSolaire;
    }
    
    public void setEnergieSolaire(int energieSolaire) {
        this.energieSolaire = Math.min(energieSolaire, capaciteMaxSolaire);
    }
    
    public int getCapaciteMaxSolaire() {
        return capaciteMaxSolaire;
    }
    
    public void setCapaciteMaxSolaire(int capaciteMaxSolaire) {
        this.capaciteMaxSolaire = capaciteMaxSolaire;
    }
    
    public boolean isModeSolaire() {
        return modeSolaire;
    }
    
    public void setModeSolaire(boolean modeSolaire) {
        this.modeSolaire = modeSolaire;
    }
    
    public boolean isTempsEnsoleille() {
        return tempsEnsoleille;
    }
    
    public int getTauxRechargesolaire() {
        return tauxRechargesolaire;
    }
    
    public void setTauxRechargesolaire(int tauxRechargesolaire) {
        this.tauxRechargesolaire = tauxRechargesolaire;
    }

}
