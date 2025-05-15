import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class InterfaceRobotEcologique extends JFrame {
    
    // on commence par declarer les couleurs a utiliser
    private static final Color VERT_FORET = new Color(34, 139, 34);     
    private static final Color VERT_CLAIR = new Color(144, 238, 144);
    private static final Color BLEU_CIEL = new Color(135, 206, 235);
    private static final Color MARRON_TERRE = new Color(139, 69, 19);
    private static final Color JAUNE_SOLEIL = new Color(255, 215, 0);
    private static final Color BURGUNDY=new Color(166, 44, 44);
    private static final Color TURC=new Color(80, 118, 135);
    
    private RobotLivraison robot;  // instance de RobotLivraison

    // on declare les boutons et les panneaux a utiliser
    private JButton btnSoleil;
    private JButton btnNuage;
    private JButton btnConnecter;
    private JButton btnDeconnecter;
    private JButton btnEnvDonnees;
    private JButton btnRechargerSolaire;
    private JButton btnRechargerElectrique;
    private JButton btnDeplacer;
    private JButton btnChargerColis;
    private JButton btnLivrer;
    private JButton btnAfficherBilan;
    private JTextArea txtHistorique;
    private JPanel panelMeteo;
    private JPanel panelCommandes;
    private JPanel panelSimulation;
    private JPanel panelInfos;
    private JPanel panelHistorique;
    private SimulationPanel simulationPanel;  //classe interne pour le panneau de simulation
    private JLabel lblSoleil;        //  label pour le soleil


    
    // cette liste  est pour suivre les deplacements de robot
    private List<Point> traceDep = new ArrayList<>();    // liste de points pour le trajet du robot

  

    // constructeur de la classe
    public InterfaceRobotEcologique(RobotLivraison robot) {
        this.robot = robot;
        traceDep.add(new Point(robot.getX(), robot.getY())); // position initiale du robot
        setTitle("Robot Écologique - " + robot.getId());
        setSize(1000, 700); // taille de la fenetre
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        
        //on utilise  un borderLayout pour le panneau principal 
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(VERT_CLAIR);

        
        // Panneau meteo avec boutons soleil et nuage et un dessin de soleil
        // on utilise un flowLayout pour le panneau meteo
        panelMeteo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelMeteo.setBorder(BorderFactory.createTitledBorder(         //  un border pour le panneau 
            BorderFactory.createLineBorder(VERT_FORET, 2),    
            "Météo", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new Font("Arial", 
            Font.BOLD, 14),VERT_FORET));//decoration du panneau
        panelMeteo.setBackground(VERT_CLAIR);                    
        

        // dessin d'un soleil
        lblSoleil = new JLabel() { 
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);        // on appelle la methode paintComponent de la classe parente (expliqué dans le rapport)
                Graphics2D gdd = (Graphics2D) g; // on utilise Graphics2D pour un meilleur rendu
                gdd.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // on active l'anti-aliasing pour un rendu plus lisse
                //gdd est un Graphics2D qui permet de dessiner des formes 2D 
                
                // dessiner le soleil
                int size = Math.min(getWidth(), getHeight()) - 10;
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                
                // corps du soleil
                gdd.setColor(JAUNE_SOLEIL);
                gdd.fillOval(centerX - size/4, centerY - size/4, size/2, size/2); 
                
                // rayons du soleil
                gdd.setStroke(new BasicStroke(2)); 
                for (int i = 0; i < 12; i++) {
                    double angle = Math.toRadians(i * 30);
                    int x1 = centerX + (int)((size/4) * Math.cos(angle));
                    int y1 = centerY + (int)((size/4) * Math.sin(angle));
                    int x2 = centerX + (int)((size/2) * Math.cos(angle));
                    int y2 = centerY + (int)((size/2) * Math.sin(angle));
                    gdd.drawLine(x1, y1, x2, y2);
                }
                
                // visage du soleil si ensoleillé  souriant ou triste si nuageux 
                if (robot.isTempsEnsoleille()) {
                    gdd.setColor(Color.BLACK);
                    // yeux
                    gdd.fillOval(centerX - size/8, centerY - size/10, size/20, size/20);
                    gdd.fillOval(centerX + size/16, centerY - size/10, size/20, size/20);
                    // sourire
                    gdd.drawArc(centerX - size/12, centerY - size/16, size/6, size/8, 0, -180); // sourire avec un arc 
                } else {
                    gdd.setColor(Color.BLACK);
                    gdd.fillOval(centerX - size/8, centerY - size/10, size/20, size/20);
                    gdd.fillOval(centerX + size/16, centerY - size/10, size/20, size/20);
                    // sourcils tristes
                    gdd.drawArc(centerX - size/12, centerY, size/6, size/8, 0, 180); // arc pour le sourcil
                }
            }
        };
        //fin de la classe anonyme pour le dessin du soleil

        lblSoleil.setPreferredSize(new Dimension(60, 60)); 
        btnSoleil = new JButton(" Ensoleillé");  // bouton pour le soleil
        btnSoleil.setFont(new Font("Arial", Font.BOLD, 12));// decoration du bouton
        btnSoleil.setBackground(JAUNE_SOLEIL);
        btnSoleil.setForeground(Color.BLACK);
        btnSoleil.setFocusPainted(false); // ca c'est pour enlever le contour du bouton
         // action a faire quand on clique sur le bouton
         
        btnSoleil.addActionListener(e -> {      
            //l'utilisation du fonction lambda est pour rendre le code plus lisible et plus concis a expliquer mieux dans le rapport
            robot.setMeteo(true); // on change la meteo du robot
            // on met a jour l'interface graphique
            updateUI();     // on appelle la methode updateUI pour mettre a jour l'interface graphique
            repaint();  // on appelle repaint pour redessiner le soleil
        });

         //bouton pour le nuage
        btnNuage = new JButton("Nuageux");
        btnNuage.setFont(new Font("Arial", Font.BOLD, 12));
        btnNuage.setBackground(BLEU_CIEL);
        btnNuage.setForeground(Color.BLACK);
        btnNuage.setFocusPainted(false);
        btnNuage.addActionListener(e -> {
            robot.setMeteo(false);
            updateUI();
            repaint();
        });

        // bouton pour connecter le robot
        btnConnecter = new JButton("Connecter");
        btnConnecter.setFont(new Font("Arial", Font.BOLD, 12));
        btnConnecter.setBackground(TURC);
        btnConnecter.setForeground(Color.BLACK);
        btnConnecter.setFocusPainted(false);
        btnConnecter.addActionListener(e -> {
            String reseau = JOptionPane.showInputDialog(this, "Nom du réseau:", "Connexion", JOptionPane.QUESTION_MESSAGE);
            if (reseau != null && !reseau.trim().isEmpty()) {
                try {
                    robot.connecter(reseau);
                    updateUI();
                } catch (RobotException ex) {
                    JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), 
                                                  "Erreur de connexion", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        // bouton pour deconnecter le robot
        btnDeconnecter = new JButton("Déconnecter");
        btnDeconnecter.setFont(new Font("Arial", Font.BOLD, 12));
        btnDeconnecter.setBackground(BURGUNDY);
        btnDeconnecter.setForeground(Color.BLACK);
        btnDeconnecter.setFocusPainted(false);
        btnDeconnecter.addActionListener(e -> {
            robot.deconnecter();
            updateUI();
            
        });
        //bouton pour envoyer des donnees
        btnEnvDonnees = new JButton("Envoyer Données");
        btnEnvDonnees.setFont(new Font("Arial", Font.BOLD, 12));
        btnEnvDonnees.setBackground(Color.YELLOW);
        btnEnvDonnees.setForeground(Color.BLACK);
        btnEnvDonnees.setFocusPainted(false);
        btnEnvDonnees.addActionListener(e -> {
            String donnees = JOptionPane.showInputDialog(this, "Données à envoyer:", "Envoi de Données", JOptionPane.QUESTION_MESSAGE);
            if (donnees != null && !donnees.trim().isEmpty()) {
                try {
                    robot.envoyerDonnees(donnees);
                    updateUI();
                } catch (RobotException ex) {
                    JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), 
                                                  "Erreur d'envoi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        // on ajoute le dessin du soleil et les boutons au panneau meteo et conxection
        panelMeteo.add(lblSoleil);
        panelMeteo.add(btnSoleil);
        panelMeteo.add(btnNuage);
        panelMeteo.add(Box.createHorizontalStrut(200)); // espace entre les boutons
        panelMeteo.add(btnConnecter, BorderLayout.EAST);
        panelMeteo.add(btnDeconnecter, BorderLayout.EAST);
        panelMeteo.add(btnEnvDonnees, BorderLayout.EAST);


        
        // panneau de commandes
        panelCommandes = new JPanel();
        // on utilise un gridLayout pour le panneau de commandes
        panelCommandes.setLayout(new GridLayout(6, 1, 10, 10));
        panelCommandes.setBorder(BorderFactory.createTitledBorder( BorderFactory.createLineBorder(VERT_FORET, 2), "Commandes", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new Font("Arial", Font.BOLD, 14),VERT_FORET));
        panelCommandes.setBackground(VERT_CLAIR);
        //boutons recharger solaire et electrique
        btnRechargerSolaire = createStyledButton(" Recharger (Solaire)", JAUNE_SOLEIL);
        btnRechargerSolaire.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Nombre d'heures d'exposition au soleil:","Recharge Solaire", JOptionPane.QUESTION_MESSAGE);
              //show input dialog  est une methode de JOptionPane qui permet d'afficher une boite de dialogue pour entrer un texte
            try {
                int heures = Integer.parseInt(input);
                robot.rechargerSolaire(heures);
                updateUI();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Veuillez entrer un nombre valide.", 
                                            "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        // bouton pour recharger electrique
        // on utilise la methode createStyledButton pour creer le bouton avec les styles
        btnRechargerElectrique = createStyledButton(" Recharger (Electrique)", BLEU_CIEL);
        btnRechargerElectrique.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Quantité d'énergie à recharger:", "Recharge Électrique", JOptionPane.QUESTION_MESSAGE);
             // on affiche une boite de dialogue pour entrer la quantite d'energie a recharger
            try {
                int quantite = Integer.parseInt(input); // on convertit le texte en entier
                //input est une variable de type String qui contient le texte entre par l'utilisateur
                // on appelle la methode recharger de la classe RobotLivraison pour recharger l'energie
                robot.recharger(quantite);
                updateUI(); // on met a jour l'interface graphique
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Veuillez entrer un nombre valide.", 
                                            "Erreur", JOptionPane.ERROR_MESSAGE); // on affiche un message d'erreur si l'utilisateur entre un texte qui n'est pas un nombre
            }
        });
        
        // bouton pour deplacer le robot et son actionListener
        btnDeplacer = createStyledButton(" Déplacer", new Color(70, 130, 180));
        btnDeplacer.addActionListener(e -> {
            try {
                //inputX et inputY sont des variables qui contiennent les coordonees x et y entre par l'utilisateur
                String inputX = JOptionPane.showInputDialog(this, "Coordonnée X:", 
                                                          "Déplacement", JOptionPane.QUESTION_MESSAGE);
                String inputY = JOptionPane.showInputDialog(this, "Coordonnée Y:", 
                                                          "Déplacement", JOptionPane.QUESTION_MESSAGE);
                int x = Integer.parseInt(inputX);
                int y = Integer.parseInt(inputY);
                
                // enregistrer la position actuelle avant deplacement
                traceDep.add(new Point(robot.getX(), robot.getY()));
                
                robot.deplacer(x, y);
                
                // ajout la nouvelle position
                traceDep.add(new Point(x, y));
                
                updateUI();
                simulationPanel.repaint();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Veuillez entrer des nombres valides.", 
                                            "Erreur", JOptionPane.ERROR_MESSAGE);
            } catch (RobotException ex) {
                JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), 
                                            "Erreur de déplacement", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnChargerColis = createStyledButton("📦 Charger Colis", MARRON_TERRE);
        btnChargerColis.addActionListener(e -> {
            try {
                String colis = JOptionPane.showInputDialog(this, "Nom du colis:", 
                                                         "Chargement de colis", JOptionPane.QUESTION_MESSAGE);
                String destination = JOptionPane.showInputDialog(this, "Destination:", 
                                                              "Chargement de colis", JOptionPane.QUESTION_MESSAGE);
                robot.setColisActuel(colis);
                robot.chargerColis(destination);
                updateUI();
            } catch (RobotException ex) {
                JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), 
                                            "Erreur de chargement", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnLivrer = createStyledButton(" Livrer", new Color(46, 139, 87));
        btnLivrer.addActionListener(e -> {
            try {
                String inputX = JOptionPane.showInputDialog(this, "Coordonnée X de livraison:", 
                                                          "Livraison", JOptionPane.QUESTION_MESSAGE);
                String inputY = JOptionPane.showInputDialog(this, "Coordonnée Y de livraison:", 
                                                          "Livraison", JOptionPane.QUESTION_MESSAGE);
                int x = Integer.parseInt(inputX);
                int y = Integer.parseInt(inputY);
                
                // enregistrer la position avant livraison dans la liste de trace defini au debut
                traceDep.add(new Point(robot.getX(), robot.getY()));

                robot.faireLivraison(x, y);
                
                // ajouter la pos de livraison
                traceDep.add(new Point(x, y));
                
                updateUI();
                simulationPanel.repaint(); // repaint le panneau de simulation

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Veuillez entrer des nombres valides.", 
                                            "Erreur", JOptionPane.ERROR_MESSAGE);
            } catch (RobotException ex) {
                JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), 
                                            "Erreur de livraison", JOptionPane.ERROR_MESSAGE);
            }
        });
         // bouton pour afficher le bilan écologique
        btnAfficherBilan = createStyledButton("Bilan Ecologique", VERT_FORET);
        btnAfficherBilan.addActionListener(e -> {
            String bilan = "=== BILAN ECOLOGIQUE ===\n" +
                         "Energie solaire: " + robot.getEnergieSolaire() + "% / " + robot.getCapaciteMaxSolaire() + "%\n" +
                         "Energie électrique: " + robot.getEnergie() + "%\n" +
                         "Mode d'énergie actuel: " + (robot.isModeSolaire() ? "Solaire " : "Electrique ") + "\n" +
                         "Météo actuel: " + (robot.isTempsEnsoleille() ? "Ensoleillé" : "Nuageux") + "\n" +
                         "Empreinte écologique: " + (robot.isModeSolaire() ? "Faible" : "Moyenne") + "\n" +
                         "========================";
            
            JOptionPane.showMessageDialog(this, bilan, "Bilan Écologique", JOptionPane.INFORMATION_MESSAGE);
            updateUI();
        });
        
        // on ajoute les boutons au panneau de commandes
        panelCommandes.add(btnRechargerSolaire);
        panelCommandes.add(btnRechargerElectrique);
        panelCommandes.add(btnDeplacer);
        panelCommandes.add(btnChargerColis);
        panelCommandes.add(btnLivrer);
        panelCommandes.add(btnAfficherBilan);
        
        // Panneau de simulation

        simulationPanel = new SimulationPanel();
        simulationPanel.setPreferredSize(new Dimension(500, 300));
        // on utilise un borderLayout pour le panneau de simulation
        panelSimulation = new JPanel(new BorderLayout());
        panelSimulation.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(VERT_FORET, 2), "Simulation de Déplacement", javax.swing.border.TitledBorder.CENTER,javax.swing.border.TitledBorder.TOP,new Font("Arial", Font.BOLD, 14),VERT_FORET));
        panelSimulation.setBackground(VERT_CLAIR);
        panelSimulation.add(simulationPanel, BorderLayout.CENTER);
        
        // Panneau d'historique
        panelHistorique = new JPanel(new BorderLayout());
        // on utilise un borderLayout pour le panneau d'historique
        panelHistorique.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(VERT_FORET, 2), "Historique des Actions", 
            javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new Font("Arial", Font.BOLD, 14), VERT_FORET));
        panelHistorique.setBackground(VERT_CLAIR);
        
        // zone de texte pour afficher l'historique
        txtHistorique = new JTextArea();
        txtHistorique.setEditable(false); // zone de texte non editable car c'est un historique
        txtHistorique.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtHistorique.setBackground(new Color(253, 253, 240));
        txtHistorique.setForeground(new Color(50, 50, 50));
        txtHistorique.setLineWrap(true);  // pour que le texte s'enroule et soit mieux visibl e
        txtHistorique.setWrapStyleWord(true); // pour que le texte s'enroule par mot 

        // on utilise un JScrollPane pour permettre le deplacement du texte
        JScrollPane scrollPaneHistorique = new JScrollPane(txtHistorique);
        scrollPaneHistorique.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);  // barre de forme verticale
        panelHistorique.add(scrollPaneHistorique, BorderLayout.CENTER);
        
        // Panneau d'informations
        // on utilise un gridLayout pour le panneau d'informations
        panelInfos = new JPanel(new GridLayout(1, 2, 10, 10));
        panelInfos.setBackground(VERT_CLAIR);
        
        // Ajout des panneaux à la fenêtre 
        mainPanel.add(panelMeteo, BorderLayout.NORTH);

        //on essaye de faire un panneau a gauche pour les commandes et l'historique
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setBackground(VERT_CLAIR);
        leftPanel.add(panelCommandes, BorderLayout.NORTH);
        leftPanel.add(panelHistorique, BorderLayout.CENTER);
        
        //on ajoute le panneau de simulation au centre et celui de commandes et historique a gauche
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(panelSimulation, BorderLayout.CENTER);
        
        setContentPane(mainPanel);  // on ajoute le panneau principal a la fenetre
        
        // Mise à jour initiale des informations
        updateUI();
        
        setVisible(true);
    }
    
    // Methode pour creer un bouton avec des styles
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        return button;
    }
    
    // Methode pour mettre a jour l'interface graphique
    private void updateUI() {
        // Mise à jour de l'historique
        txtHistorique.setText(formatHistorique(robot.getHistoriqueActions()));
        
        // Mettre à jour les états des boutons selon la météo
        btnSoleil.setEnabled(!robot.isTempsEnsoleille());
        btnNuage.setEnabled(robot.isTempsEnsoleille());
        
        // Mise à jour du panneau de simulation
        simulationPanel.repaint();
        
        // Force le repaint du soleil
        lblSoleil.repaint();
    }
    
    
    private String formatHistorique(ArrayList<String> historique) {
        if (historique == null || historique.isEmpty()) {
            return "Aucune action enregistrée";
        }
        
        // Formater l'historique avec des emojis   c'est pour rendre l'historique plus lisible 
        StringBuilder formatted = new StringBuilder();
        
        for (String ligne : historique) {
            if (ligne.contains("Recharge solaire")) {
                formatted.append("🔆 ");
            } else if (ligne.contains("Recharge électrique")) {
                formatted.append("⚡ ");
            } else if (ligne.contains("déplacé")) {
                formatted.append("🚶 ");
            } else if (ligne.contains("Colis")) {
                formatted.append("📦 ");
            } else if (ligne.contains("Livraison")) {
                formatted.append("🚚 ");
            } else if (ligne.contains("Météo")) {
                formatted.append("☁️ ");
            } else if (ligne.contains("créé")) {
                formatted.append("🤖 ");
            } else if (ligne.contains("démarré")) {
                formatted.append("▶️ ");
            } else if (ligne.contains("arrêté")) {
                formatted.append("⏹️ ");
            } else if (ligne.contains("Connecté")) {
                formatted.append("📡 ");
            } else {
                formatted.append("📝 ");
            }
            
            formatted.append(ligne).append("\n\n");
        }
        
        return formatted.toString();
    }
    
    // Classe interne pour le panneau de simulation
    private class SimulationPanel extends JPanel {
        private static final int GRID_SIZE = 10;
        private static final int CELL_SIZE = 30;
        private static final int ROBOT_SIZE = 20;
        
        public SimulationPanel() {
            setBackground(new Color(240, 248, 255));
            setBorder(BorderFactory.createLoweredBevelBorder());
        }
        
        // Redéfinir la méthode paintComponent pour dessiner le robot et la grille
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D gdd = (Graphics2D) g;
            gdd.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int width = getWidth();
            int height = getHeight();
            
            // Déterminer les limites de la grille
            int maxX = Math.max(100, width / CELL_SIZE); //CELL_SIZE est la taille de chaque cellule de la grille
            // on utilise Math.max pour s'assurer que la grille a au moins 100 cellules
            int maxY = Math.max(100, height / CELL_SIZE);
            
            // Dessiner le fond
            gdd.setColor(new Color(240, 248, 255));
            gdd.fillRect(0, 0, width, height);
            
            // Dessiner la grille
            gdd.setColor(new Color(200, 200, 200));
            for (int i = 0; i <= maxX; i++) {
                gdd.drawLine(i * CELL_SIZE, 0, i * CELL_SIZE, height);      //on dessine les lignes verticales
            }
            for (int i = 0; i <= maxY; i++) {                                   // on dessine les lignes horizontales
                gdd.drawLine(0, i * CELL_SIZE, width, i * CELL_SIZE);
            }
            
            // Dessiner les coordonnées
            gdd.setColor(new Color(100, 100, 100));
            gdd.setFont(new Font("Arial", Font.PLAIN, 10));
            for (int i = 0; i <= maxX; i += 5) {
                gdd.drawString(String.valueOf(i), i * CELL_SIZE + 2, 12);
            }
            for (int i = 0; i <= maxY; i += 5) {
                gdd.drawString(String.valueOf(i), 2, i * CELL_SIZE + 12);
            }
            
            // Dessiner le trajet du robot
            if (traceDep.size() > 1) {   
                gdd.setColor(robot.isModeSolaire() ? VERT_FORET : Color.BLUE);   // si il est en mode solaire on dessine le trajet en vert sinon en bleu
                gdd.setStroke(new BasicStroke(2)); // on utilise un trait plus epais pour le trajet
                // Dessiner le trajet
                for (int i = 0; i < traceDep.size() - 1; i++) {
                    Point p1 = traceDep.get(i);
                    Point p2 = traceDep.get(i + 1);
                    gdd.drawLine(p1.x * CELL_SIZE + CELL_SIZE/2, p1.y * CELL_SIZE + CELL_SIZE/2, 
                                p2.x * CELL_SIZE + CELL_SIZE/2, p2.y * CELL_SIZE + CELL_SIZE/2);
                }
            }
            
            // Dessiner le robot
            int robotX = robot.getX() * CELL_SIZE + (CELL_SIZE - ROBOT_SIZE) / 2;
            int robotY = robot.getY() * CELL_SIZE + (CELL_SIZE - ROBOT_SIZE) / 2;
            
            // Dessiner l'icône du robot
            if (robot.isModeSolaire()) {
                // Robot en mode solaire
                gdd.setColor(JAUNE_SOLEIL);  // couleur du robot solaire
                gdd.fillOval(robotX, robotY, ROBOT_SIZE, ROBOT_SIZE);
                
                // Détails du robot solaire
                gdd.setColor(Color.BLACK);
                gdd.drawOval(robotX, robotY, ROBOT_SIZE, ROBOT_SIZE);
                // Panneau solaire sur le robot
                gdd.setColor(new Color(30, 30, 100));
                gdd.fillRect(robotX + ROBOT_SIZE/4, robotY, ROBOT_SIZE/2, ROBOT_SIZE/6);
            } else {
                // Robot en mode électrique
                gdd.setColor(BLEU_CIEL);  // couleur du robot electrique
                gdd.fillOval(robotX, robotY, ROBOT_SIZE, ROBOT_SIZE);
                
                // Détails du robot électrique
                gdd.setColor(Color.BLACK);
                gdd.drawOval(robotX, robotY, ROBOT_SIZE, ROBOT_SIZE);
                // Symbole électrique
                gdd.setColor(Color.YELLOW);
                gdd.drawLine(robotX + ROBOT_SIZE/2, robotY + ROBOT_SIZE/4, 
                            robotX + ROBOT_SIZE/2, robotY + ROBOT_SIZE*3/4);
            }
            
            // Afficher les informations du robot dans le coin inf gauche du la grille
            gdd.setColor(Color.BLACK);
            gdd.setFont(new Font("Arial", Font.BOLD, 12));
            
            // Afficher les informations d'énergie et de position
            String info = "Position: (" + robot.getX() + "," + robot.getY() + ")";
            gdd.drawString(info, 10, height - 40);
            
            // Afficher les informations d'énergie
            String energieInfo = "Énergie: " + (robot.isModeSolaire() ? 
                              "Solaire " + robot.getEnergieSolaire() + "%" : 
                              "Électrique " + robot.getEnergie() + "%");
            gdd.drawString(energieInfo, 10, height - 20);
            
            // Si le robot transporte un colis
            if (robot.getColisActuel() != null && !robot.getColisActuel().isEmpty()) {
                // Dessiner le colis
                gdd.setColor(MARRON_TERRE);
                gdd.fillRect(robotX + ROBOT_SIZE/4, robotY - ROBOT_SIZE/2, ROBOT_SIZE/2, ROBOT_SIZE/2);
                gdd.setColor(Color.BLACK);
                gdd.drawRect(robotX + ROBOT_SIZE/4, robotY - ROBOT_SIZE/2, ROBOT_SIZE/2, ROBOT_SIZE/2);
                
                // Afficher la destination
                gdd.setColor(Color.RED);
                gdd.drawString("Destination: " + robot.getDestination(), 10, height - 60);
            }
        }





    }
    
    
    public static void main(String[] args) {
        RobotLivraison robot = new RobotLivraison("ECO", 10, 10);
        
        try {
            robot.demarrer(0); 
            SwingUtilities.invokeLater(() -> new InterfaceRobotEcologique(robot)); // on lance l'interface graphique
        } catch (Exception e) {
            e.printStackTrace(); // on affiche l'erreur si il y en a une
        }
    }
}

