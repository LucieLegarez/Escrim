package control;

import java.sql.Date;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.BDD;
import view.BlesseView;
import view.LogisticienView;
import view.MedecinView;

/**
 * Contrôleur pour la gestion de la connexion utilisateur.
 */
public class ConnexionController implements EventHandler<ActionEvent> {

    private BDD database;
    private TextField textId;
    private TextField textMdp;
    private Label erreur;
    private Stage primaryStage;

    /**
     * Constructeur du contrôleur de connexion.
     * 
     * @param textIdentifiant Champ de texte pour l'identifiant
     * @param textMdp Champ de texte pour le mot de passe
     * @param erreur Label pour afficher les erreurs
     * @param primaryStage Stage principal de l'application
     */
    public ConnexionController(TextField textIdentifiant, TextField textMdp, Label erreur, Stage primaryStage) {
        this.textId = textIdentifiant;
        this.textMdp = textMdp;
        this.erreur = erreur;
        this.primaryStage = primaryStage;
        database = new BDD();
    }

    /**
     * Méthode de gestion de l'événement de connexion.
     */
    public void handle(ActionEvent e) {
        String identifiant = this.textId.getText();
        String identifiantEnMinuscule = identifiant.toLowerCase();
        String mdp = this.textMdp.getText();
        String[] utilisateur = this.database.stockerUtilisateurParIdentifiant(identifiantEnMinuscule);

        if (utilisateur == null || utilisateur[0] == null || !utilisateur[0].equals("true")) {
            afficherErreur("Cet identifiant n'existe pas.");
        } else if (!(utilisateur[4].trim()).equals(mdp)) {
            afficherErreur("Mot de passe incorrect.");
        } else {
            authentificationReussie(utilisateur);
        }
    }

    /**
     * Affiche un message d'erreur dans l'interface.
     * 
     * @param message Message d'erreur à afficher
     */
    public void afficherErreur(String message) {
        this.erreur.setText(message);
    }

    /**
     * Traite le cas où l'authentification est réussie.
     * 
     * @param utilisateur Informations sur l'utilisateur connecté
     */
    public void authentificationReussie(String[] utilisateur) {
        afficherErreur("Authentification réussie.");
        this.erreur.setTextFill(Color.GREEN);
        String prenom = utilisateur[1].trim();
        String pnomEnMinuscule = prenom.toLowerCase();
        String nom = utilisateur[2].trim();
        String nomEnMinuscule = nom.toLowerCase();
        Date dateNaissance = Date.valueOf(utilisateur[3].trim());
        String userId = pnomEnMinuscule + "."+ nomEnMinuscule;
        SessionController.getInstance().setUserId(userId);
        
        if ((utilisateur[5].trim()).equals("Blessé")) {
            afficherVueBlesse(pnomEnMinuscule, nomEnMinuscule, dateNaissance);
        } else if ((utilisateur[5].trim()).equals("Médecin")) {
            afficherVueMedecin();
        } else if ((utilisateur[5].trim()).equals("Logisticien")) {
            afficherVueLogisticien();
        }
    }

    /**
     * Affiche la vue pour un utilisateur blessé.
     * 
     * @param prenom Prénom de l'utilisateur
     * @param nom Nom de l'utilisateur
     * @param dateNaissance Date de naissance de l'utilisateur
     */
    public void afficherVueBlesse(String prenom, String nom, Date dateNaissance) {
        Task<Void> pauseTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(1000);
                return null;
            }
        };

        pauseTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                primaryStage.close();
                BlesseView blesseView = new BlesseView(primaryStage);
                blesseView.afficheVueBlesse(prenom, nom, dateNaissance);
            }
        });

        new Thread(pauseTask).start();
    }
    
    /**
     * Affiche la vue pour un médecin.
     */
    public void afficherVueMedecin() {
        Task<Void> pauseTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(1000);
                return null;
            }
        };

        pauseTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                primaryStage.close();
                MedecinView medView = new MedecinView(primaryStage);
                medView.afficheVueMedecin();
            }
        });

        new Thread(pauseTask).start();
    }

    /**
     * Affiche la vue pour un logisticien.
     */
    public void afficherVueLogisticien() {
        Task<Void> pauseTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(1000);
                return null;
            }
        };

        pauseTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                primaryStage.close();
                LogisticienView logiView = new LogisticienView(primaryStage);
                logiView.afficheVueLogisticien();
            }
        });

        new Thread(pauseTask).start();
    }
}
