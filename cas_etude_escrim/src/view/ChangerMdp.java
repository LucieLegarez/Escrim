package view;

import control.ChangerMdpController;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.stage.Stage;

/**
 * Classe de l'interface pour changer de mot de passe.
 */
public class ChangerMdp extends Stage {

	private TextField textIdentifiant;
	private DatePicker textDateNaissance;
	private TextField textMdp;
	private Label identifiant;
	private Label identifiantInfo;
	private Label dateNaissance;
	private Label dateInfo;
	private Label mdp;
	private Label erreur;
	private Button boutonChangerMdp;
	private Connexion connexion;

	/**
	 * Constructeur de la classe
	 * 
	 * @param primaryStage La fenêtre principale de l'application.
	 * @param connexion
	 */
	public ChangerMdp(Stage primaryStage, Connexion connexion) {
		this.connexion = connexion;
		textIdentifiant = new TextField();
		textDateNaissance = new DatePicker();
		textMdp = new TextField();
		identifiant = new Label("Identifiant :");
		identifiantInfo = new Label("(il s'agit de : prénom.nom)");
		dateNaissance = new Label("Date de naissance :");
		dateInfo = new Label("(au format DD-MM-YYYY)");
		mdp = new Label("Mot de passe :");
		erreur = new Label();
		boutonChangerMdp = new Button("Changer de mot de passe");
	}

	/**
	 * Affiche la vue de changement de mot de passe.
	 */
	public void afficheVueMdp() {
		this.setTitle("Changement de mot de passe");

		GridPane grid = new GridPane();
		grid.setPadding(new Insets(30.0, 30.0, 30.0, 30.0));
		grid.setVgap(20.0);
		grid.setHgap(30.0);

		addComponentsToGrid(grid);

		boutonChangerMdp.setOnAction((event) -> {
			ChangerMdpController changerController = new ChangerMdpController(textIdentifiant, textDateNaissance,
					textMdp, erreur, this);
			changerController.handle(event);
		});

		addBackButtonToGrid(grid);

		Scene scene = new Scene(grid);
		setScene(scene);
		show();
	}

	/**
	 * Ajoute les composants au GridPane.
	 * 
	 * @param grid Le GridPane auquel ajouter les composants.
	 */
	public void addComponentsToGrid(GridPane grid) {
		// Identifiant
		identifiant.setFont(new Font("Arial", 25.0));
		textIdentifiant.setPromptText("Saisir votre identifiant");
		textIdentifiant.setFont(Font.font("Arial", FontPosture.ITALIC, 16.0));
		grid.add(identifiant, 0, 0);
		grid.add(textIdentifiant, 1, 0);

		// Info sur l'identifiant
		identifiantInfo.setFont(new Font("Arial", 13.0));
		GridPane.setColumnSpan(identifiantInfo, GridPane.REMAINING);
		GridPane.setMargin(identifiantInfo, new Insets(50, 0, 0, 0));
		grid.add(identifiantInfo, 0, 0);

		// Date de naissance
		dateNaissance.setFont(new Font("Arial", 25.0));
		textDateNaissance.setPromptText("Saisir votre date de naissance");
		textDateNaissance.setStyle("-fx-font-family: Arial; -fx-font-style: italic; -fx-font-size: 16;");
		grid.add(dateNaissance, 0, 1);
		grid.add(textDateNaissance, 1, 1);

		// Info sur la date de naissance
		dateInfo.setFont(new Font("Arial", 13.0));
		GridPane.setMargin(dateInfo, new Insets(50, 0, 0, 0));
		grid.add(dateInfo, 0, 1);

		// Mot de passe
		mdp.setFont(new Font("Arial", 25.0));
		textMdp.setPromptText("Saisir votre nouveau mot de passe");
		textMdp.setFont(Font.font("Arial", FontPosture.ITALIC, 16.0));
		grid.add(mdp, 0, 2);
		grid.add(textMdp, 1, 2);

		// Erreur
		erreur.setStyle("-fx-text-fill: red;");
		GridPane.setColumnSpan(erreur, GridPane.REMAINING);
		GridPane.setMargin(erreur, new Insets(0, 0, 0, 60));
		grid.add(erreur, 0, 4);

		// Bouton de changement de mot de passe
		boutonChangerMdp.setFont(new Font("Arial", 25.0));
		boutonChangerMdp.setStyle("-fx-background-color: purple; -fx-text-fill: white;");
		GridPane.setHalignment(boutonChangerMdp, HPos.RIGHT);
		grid.add(boutonChangerMdp, 1, 3);
	}

	/**
	 * Ajoute le bouton de retour au GridPane.
	 * 
	 * @param grid Le GridPane auquel ajouter le bouton de retour.
	 */
	public void addBackButtonToGrid(GridPane grid) {
		ImageView imageView = new ImageView(new Image("file:ressources/flèche.png"));
		imageView.setFitWidth(30);
		imageView.setFitHeight(30);

		Button backButton = new Button();
		backButton.setGraphic(imageView);

		backButton.setOnAction(event -> {
			connexion.afficheVueConnexion();
			this.close();
		});

		grid.add(backButton, 0, 4);
	}
}
