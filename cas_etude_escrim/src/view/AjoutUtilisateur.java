package view;

import control.AjouterUtilisateurController;
import javafx.scene.control.DatePicker;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.stage.Stage;

/**
 * Cette classe représente la vue pour ajouter un utilisateur.
 */
public class AjoutUtilisateur extends Stage {
    private TextField textPrenom;
    private TextField textNom;
    private TextField textMdp;
    private DatePicker datePicker;
    private ComboBox<String> statutComboBox;
    private Label prenom;
    private Label nom;
    private Label mdp;
    private Label mdpInfo;
    private Label dateNaissance;
    private Label dateInfo;
    private Label statut;
    private Label erreur;
    private Button boutonAjouter;
    private Stage primaryStage;
    
    /**
     * Constructeur de la classe AjoutUtilisateur.
     * @param primaryStage La fenêtre principale de l'application.
     */
    public AjoutUtilisateur(Stage primaryStage) {
        this.primaryStage = primaryStage;
        textPrenom = new TextField();
        textNom = new TextField();
        textMdp = new TextField();
        datePicker = new DatePicker();
        statutComboBox = new ComboBox<>();
        prenom = new Label("Prénom :");
        nom = new Label("Nom :");
        mdp = new Label("Mot de passe :");
        mdpInfo = new Label("(doit contenir au moins 5 caractères)");
        dateNaissance = new Label("Date de naissance :");
        dateInfo = new Label("(au format DD/MM/YYYY)");
        statut = new Label("Statut :");
        erreur = new Label();
        boutonAjouter = new Button("Ajouter");
        afficheVueAjoutUtilisateur();
    }
    
    /**
     * Affiche la vue pour ajouter un utilisateur.
     */
    public void afficheVueAjoutUtilisateur() {
        this.setTitle("Ajout d'utilisateur");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(30.0, 30.0, 30.0, 30.0));
        grid.setVgap(20.0);
        grid.setHgap(30.0);

        setupCommonFields(grid); // Setup common fields such as prenom, nom, etc.

        

        setupBackButton(grid); // Setup the back button
        setupAddButton(grid); // Setup the add button

        Scene scene = new Scene(grid);
        setScene(scene);
        show();
    }
    
    /**
     * Configure les champs communs dans une grille spécifiée.
     * 
     * @param grid La grille dans laquelle configurer les champs communs.
     */
    public void setupCommonFields(GridPane grid) {
    	prenom.setFont(new Font("Arial", 25.0));
		textPrenom.setPromptText("Saisir le prénom de l'utilisateur");
		textPrenom.setFont(Font.font("Arial", FontPosture.ITALIC, 16.0));
		grid.add(prenom, 0, 0);
		grid.add(textPrenom, 1, 0);

		nom.setFont(new Font("Arial", 25.0));
		textNom.setPromptText("Saisir le nom de l'utilisateur");
		textNom.setFont(Font.font("Arial", FontPosture.ITALIC, 16.0));
		grid.add(nom, 0, 1);
		grid.add(textNom, 1, 1);

		dateNaissance.setFont(new Font("Arial", 25.0));
		datePicker.setPromptText("Saisir votre date de naissance");
		datePicker.setStyle("-fx-font-family: Arial; -fx-font-style: italic; -fx-font-size: 16;");
		grid.add(dateNaissance, 0, 2);
		grid.add(datePicker, 1, 2);

		dateInfo.setFont(new Font("Arial", 13.0));
		GridPane.setMargin(dateInfo, new Insets(50, 0, 0, 0));
		grid.add(dateInfo, 0, 2);

		mdp.setFont(new Font("Arial", 25.0));
		textMdp.setPromptText("Saisir le mot de passe");
		textMdp.setFont(Font.font("Arial", FontPosture.ITALIC, 16.0));
		grid.add(mdp, 0, 3);
		grid.add(textMdp, 1, 3);

		mdpInfo.setFont(new Font("Arial", 13.0));
		GridPane.setMargin(mdpInfo, new Insets(50, 0, 0, 0));
		grid.add(mdpInfo, 0, 3);

		statut.setFont(new Font("Arial", 25.0));
		if (statutComboBox.getItems().isEmpty()) {
			statutComboBox.getItems().addAll("Blessé", "Médecin", "Logisticien");
			statutComboBox.setValue("Blessé");
			statutComboBox.setStyle("-fx-font-size: 16.0;");
		}
		grid.add(statut, 0, 4);
		grid.add(statutComboBox, 1, 4);
    }
    

    /**
     * Configure le bouton de retour avec une flèche dans une grille spécifiée.
     * 
     * @param grid La grille dans laquelle configurer le bouton de retour.
     */

    public void setupBackButton(GridPane grid) {
    	ImageView imageView = new ImageView(new Image("file:ressources/flèche.png"));
		imageView.setFitWidth(30);
		imageView.setFitHeight(30);

		Button backButton = new Button();
		backButton.setGraphic(imageView);

		Logiciel logiciel = new Logiciel(primaryStage);
		backButton.setOnAction(event -> {
			logiciel.afficheVueAccueil();
			this.close();
		});

		grid.add(backButton, 0, 8);

		erreur.setStyle("-fx-text-fill: red;");
		GridPane.setColumnSpan(erreur, GridPane.REMAINING);
		GridPane.setMargin(erreur, new Insets(0, 0, 0, 60));
		grid.add(erreur, 0, 8); 
    }
		
    /**
     * Configure le bouton d'ajout dans une grille spécifiée.
     * 
     * @param grid La grille dans laquelle configurer le bouton d'ajout.
     */
	
    public void setupAddButton(GridPane grid) {
    	boutonAjouter.setFont(new Font("Arial", 25.0));
		boutonAjouter.setStyle("-fx-background-color: blue; -fx-text-fill: white;");
		GridPane.setHalignment(this.boutonAjouter, HPos.RIGHT);
		grid.add(boutonAjouter, 1, 7);

		boutonAjouter.setOnAction((event) -> {
			AjouterUtilisateurController ajoutController = new AjouterUtilisateurController(textPrenom,
					textNom, textMdp, datePicker, statutComboBox,  erreur, this);
			ajoutController.handle(event);
		});

	}
    
		
}