package control;

import java.time.LocalDate;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import model.BDD;
import view.Logiciel;

/**
 * Contrôleur pour l'ajout d'un nouvel utilisateur.
 */
public class AjouterUtilisateurController implements EventHandler<ActionEvent> {
    private BDD database;
    private TextField textPrenom;
    private TextField textNom;
    private TextField textMdp;
    private DatePicker datePicker;
    private ComboBox<String> statutComboBox;
    private Label erreur;
    private Stage primaryStage;

    /**
     * Constructeur du contrôleur d'ajout d'utilisateur.
     * 
     * @param textPrenom Champ de texte pour le prénom
     * @param textNom Champ de texte pour le nom
     * @param textMdp Champ de texte pour le mot de passe
     * @param datePicker Sélecteur de date de naissance
     * @param statutComboBox Liste déroulante pour le statut de l'utilisateur
     * @param erreur Label pour afficher les erreurs
     * @param primaryStage Stage principal de l'application
     */
    public AjouterUtilisateurController(TextField textPrenom, TextField textNom, TextField textMdp,
            DatePicker datePicker, ComboBox<String> statutComboBox, Label erreur, Stage primaryStage) {
        this.textPrenom = textPrenom;
        this.textNom = textNom;
        this.textMdp = textMdp;
        this.datePicker = datePicker;
        this.statutComboBox = statutComboBox;
        this.erreur = erreur;
        this.primaryStage = primaryStage;
        database = new BDD();
    }

    /**
     * Méthode de gestion de l'événement d'ajout d'utilisateur.
     */
    public void handle(ActionEvent e) {
        String prenom = textPrenom.getText();
        String nom = textNom.getText();
        String mdp = textMdp.getText();
        String statut = statutComboBox.getValue();
        LocalDate dateNaissance = datePicker.getValue();

        if (!validerChamps(prenom, nom, mdp, dateNaissance)) {
            return;
        }

        String identifiant = genererIdentifiant(prenom, nom);
        String[] utilisateur = database.stockerUtilisateurAPartirNom(identifiant);
        
        if (!utilisateur[0].equals("false")) {
            afficherErreur("Un utilisateur avec cet identifiant existe déjà.");
        } else {
            ajouterUtilisateur(identifiant, prenom, nom, dateNaissance, mdp, statut);
        }
    }

    /**
     * Valide les champs saisis par l'utilisateur.
     * 
     * @param prenom Prénom de l'utilisateur
     * @param nom Nom de l'utilisateur
     * @param mdp Mot de passe de l'utilisateur
     * @param dateNaissance Date de naissance de l'utilisateur
     * @return true si les champs sont valides, sinon false
     */
    public boolean validerChamps(String prenom, String nom, String mdp, LocalDate dateNaissance) {
        if (prenom.isEmpty() || nom.isEmpty()) {
            afficherErreur("Le prénom et le nom sont requis.");
            return false;
        }

        if (dateNaissance == null) {
            afficherErreur("La date de naissance est requise.");
            return false;
        }

        if (mdp.contains(" ")) {
            afficherErreur("Le mot de passe ne peut pas contenir d'espaces.");
            return false;
        }

        if (mdp.length() < 5) {
            afficherErreur("Le mot de passe doit contenir au moins 5 caractères.");
            return false;
        }

        return true;
    }

    /**
     * Affiche un message d'erreur dans l'interface.
     * 
     * @param message Message d'erreur à afficher
     */
    public void afficherErreur(String message) {
        erreur.setText(message);
    }

    /**
     * Génère l'identifiant unique pour l'utilisateur.
     * 
     * @param prenom Prénom de l'utilisateur
     * @param nom Nom de l'utilisateur
     * @return Identifiant unique généré
     */
    public String genererIdentifiant(String prenom, String nom) {
        return prenom.toLowerCase() + "." + nom.toLowerCase();
    }

    /**
     * Ajoute un nouvel utilisateur à la base de données.
     * 
     * @param identifiant Identifiant unique de l'utilisateur
     * @param prenom Prénom de l'utilisateur
     * @param nom Nom de l'utilisateur
     * @param dateNaissance Date de naissance de l'utilisateur
     * @param mdp Mot de passe de l'utilisateur
     * @param statut Statut de l'utilisateur
     */
    public void ajouterUtilisateur(String identifiant, String prenom, String nom, LocalDate dateNaissance,
            String mdp, String statut) {
        database.insererUtilisateur(identifiant, prenom, nom, dateNaissance, mdp, statut);
        afficherErreur("Utilisateur " + prenom + " " + nom + " ajouté avec succès");
        erreur.setTextFill(Color.GREEN);

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
                Logiciel logiciel = new Logiciel(primaryStage);
                logiciel.afficheVueAccueil();
            }
        });

        new Thread(pauseTask).start();
    }
}
