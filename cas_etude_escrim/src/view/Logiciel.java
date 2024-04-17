package view;

import javafx.geometry.Pos;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * La classe Logiciel représente le logiciel principal de l'application ESCRIM.
 * Elle gère l'affichage de la page d'accueil et la navigation vers d'autres vues.
 */
public class Logiciel {
    private Stage stage;
    private Label bienvenue;
    private Image imagePresentation;
    private ImageView vueImage;
    private Button boutonIdentifier;
    private Button boutonAjouterUtilisateur;

    /**
     * Constructeur de la classe Logiciel.
     *
     * @param secondaryStage Le stage principal de l'application.
     */
    public Logiciel(Stage secondaryStage) {
        this.stage = secondaryStage;
        bienvenue = new Label("Bienvenue sur le logiciel d'ESCRIM ! ");
        imagePresentation = new Image("file:ressources/attentat.jpg");
        vueImage = new ImageView(imagePresentation);
        boutonIdentifier = new Button("S'identifier");
        boutonAjouterUtilisateur = new Button("Créer un nouvel utilisateur");
    }

    /**
     * Affiche la page d'accueil de l'application.
     * Cette méthode configure les éléments de l'interface utilisateur et les ajoute à la scène.
     */
    public void afficheVueAccueil() {
        stage.setTitle("Page d'accueil ESCRIM");

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-image: url('file:ressources/attentat.jpg'); " + "-fx-background-size: cover;");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(50.0));
        grid.setAlignment(Pos.CENTER);
        grid.setVgap(30.0);
        grid.setHgap(50.0);

        vueImage.setFitWidth(400.0);
        vueImage.setFitHeight(200.0);

        bienvenue.setFont(Font.font("Arial", FontWeight.BOLD, 50.0));
        bienvenue.setTextFill(Color.WHITE);
        GridPane.setMargin(bienvenue, new Insets(50, 0, 0, 0));
        grid.add(bienvenue, 0, 5);

        boutonIdentifier.setFont(new Font("Arial", 25.0));
        boutonIdentifier.setStyle("-fx-background-color: white ; -fx-text-fill: black;");
        GridPane.setHalignment(boutonIdentifier, HPos.CENTER);
        grid.add(boutonIdentifier, 0, 10);
        boutonIdentifier.setOnAction((e) -> {
            allerVueConnexion();
        });

        boutonAjouterUtilisateur.setFont(new Font("Arial", 15.0));
        boutonAjouterUtilisateur.setStyle("-fx-background-color: grey ; -fx-text-fill: white;");
        GridPane.setHalignment(boutonAjouterUtilisateur, HPos.CENTER);
        grid.add(boutonAjouterUtilisateur, 0, 11);
        boutonAjouterUtilisateur.setOnAction((e) -> {
            allerVueAjoutUtilisateur();
        });

        root.setCenter(grid);
        Scene scene = new Scene(root, 1000.0, 600.0);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Redirige vers la vue de connexion.
     * Ferme la fenêtre actuelle et ouvre la vue de connexion.
     */
    public void allerVueConnexion() {
        stage.close();
        Connexion pageConnexion = new Connexion(stage);
        pageConnexion.afficheVueConnexion();
    }

    /**
     * Redirige vers la vue d'ajout d'utilisateur.
     * Ferme la fenêtre actuelle et ouvre la vue d'ajout d'utilisateur.
     */
    public void allerVueAjoutUtilisateur() {
        stage.close();
        AjoutUtilisateur ajoutUtilisateur = new AjoutUtilisateur(stage);
        ajoutUtilisateur.afficheVueAjoutUtilisateur();
    }
}
