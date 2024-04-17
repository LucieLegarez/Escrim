package control;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.BDD;
import view.Connexion;

import java.sql.Date;
import java.time.LocalDate;

/**
 * Classe du contrôleur pour changer de mot de passe.
 */
public class ChangerMdpController implements EventHandler<ActionEvent> {

    private BDD database;
    private TextField textIdentifiant;
    private TextField textMdp;
    private Label erreur;
    private Stage primaryStage;
    private DatePicker textDateNaissance;

    /**
     * Constructeur du contrôleur pour le changement de mot de passe.
     *
     * @param textIdentifiant  Champ de saisie de l'identifiant.
     * @param textDateNaissance  Champ de saisie de la date de naissance.
     * @param textMdp  Champ de saisie du nouveau mot de passe.
     * @param erreur  Label pour afficher les messages d'erreur.
     * @param primaryStage  Fenêtre principale de l'application.
     */
    public ChangerMdpController(TextField textIdentifiant, DatePicker textDateNaissance, TextField textMdp, Label erreur, Stage primaryStage) {
        database = new BDD();
        this.textIdentifiant = textIdentifiant;
        this.textDateNaissance = textDateNaissance;
        this.textMdp = textMdp;
        this.erreur = erreur;
        this.primaryStage = primaryStage;
    }

    /**
     * Méthode pour gérer l'action du bouton "Changer de mot de passe".
     *
     * @param event  L'événement de clic sur le bouton.
     */
    @Override
    public void handle(ActionEvent event) {
        String identifiant = textIdentifiant.getText();
        LocalDate localDateNaissance = textDateNaissance.getValue();
        String mdp = textMdp.getText();

        if (validerChamps(identifiant, localDateNaissance, mdp)) {
            changerMotDePasse(identifiant, localDateNaissance, mdp);
        }
    }

    /**
     * Valide les champs saisis par l'utilisateur.
     * 
     * @param identifiant Identifiant saisi par l'utilisateur
     * @param dateNaissance Date de naissance saisie par l'utilisateur
     * @param mdp Mot de passe saisi par l'utilisateur
     * @return true si les champs sont valides, sinon false
     */
    public boolean validerChamps(String identifiant, LocalDate dateNaissance, String mdp) {
        if (identifiant.isEmpty()) {
            afficherErreur("Veuillez saisir un identifiant.");
            return false;
        }

        if (dateNaissance == null) {
            afficherErreur("Veuillez sélectionner une date de naissance.");
            return false;
        }

        if (mdp.length() < 5) {
            afficherErreur("Le mot de passe doit contenir au moins 5 caractères.");
            return false;
        }

        if (mdp.contains(" ")) {
            afficherErreur("Le mot de passe ne doit pas contenir d'espaces.");
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
     * Traite le changement de mot de passe.
     * 
     * @param identifiant Identifiant de l'utilisateur
     * @param dateNaissance Date de naissance de l'utilisateur
     * @param mdp Nouveau mot de passe
     */
    public void changerMotDePasse(String identifiant, LocalDate dateNaissance, String mdp) {
        Date dateNaissanceSQL = Date.valueOf(dateNaissance);
        boolean identifiantValide = database.verifierIdentifiantEtDateNaissance(identifiant, dateNaissanceSQL);

        if (!identifiantValide) {
            afficherErreur("Identifiant et/ou date de naissance incorrects.");
        } else {
            boolean mdpModifie = database.modifierMotDePasse(identifiant, mdp);

            if (mdpModifie) {
                afficherErreur("Mot de passe modifié avec succès.");
                this.erreur.setTextFill(Color.GREEN);

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
                        Connexion connexion = new Connexion(primaryStage);
                        connexion.afficheVueConnexion();
                    }
                });

                new Thread(pauseTask).start();

            } else {
                afficherErreur("Erreur lors de la modification du mot de passe.");
            }
        }
    }
}
