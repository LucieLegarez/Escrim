package view;

import control.ConnexionController;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.stage.Stage;

/**
 * Fenêtre de connexion à l'application.
 */
public class Connexion extends Stage {
    private TextField textIdentifiant;
    private TextField textMdp;
    private Label identifiantLabel;
    private Label mdpLabel;
    private Label motDePasseOublie;
    private Label erreur;
    private Button boutonConnexion;
    private Stage primaryStage;

    /**
     * Initialise la fenêtre de connexion.
     *
     * @param primaryStage La fenêtre principale de l'application.
     */
    public Connexion(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initialiserInterface();
    }

    /**
     * Affiche l'interface de connexion.
     */
    public void afficheVueConnexion() {
        setTitle("Connexion");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(30.0, 30.0, 30.0, 30.0));
        grid.setVgap(20.0);
        grid.setHgap(30.0);

        configurerElements(grid);
        configurerActions();

        Scene scene = new Scene(grid);
        setScene(scene);
        show();
    }

    /**
     * Récupère l'identifiant saisi.
     *
     * @return L'identifiant saisi.
     */
    public String getIdentifiant() {
        return textIdentifiant.getText();
    }

    /**
     * Récupère le mot de passe saisi.
     *
     * @return Le mot de passe saisi.
     */
    public String getMdp() {
        return textMdp.getText();
    }

    /**
     * Initialise les éléments de l'interface.
     */
    public void initialiserInterface() {
        textIdentifiant = new TextField();
        textMdp = new TextField();
        identifiantLabel = new Label("Identifiant :");
        mdpLabel = new Label("Mot de passe :");
        motDePasseOublie = new Label("Mot de passe oublié");
        erreur = new Label();
        boutonConnexion = new Button("Se connecter");
    }

    /**
     * Configure les éléments de l'interface.
     *
     * @param grid Le panneau de grille où placer les éléments.
     */
    public void configurerElements(GridPane grid) {
        identifiantLabel.setFont(new Font("Arial", 25.0));
        textIdentifiant.setPromptText("Saisissez votre identifiant");
        textIdentifiant.setFont(Font.font("Arial", FontPosture.ITALIC, 16.0));
        grid.add(identifiantLabel, 0, 0);
        grid.add(textIdentifiant, 1, 0);

        Label identifiantInfo = new Label("(il s'agit de : prénom.nom)");
        identifiantInfo.setFont(new Font("Arial", 13.0));
        GridPane.setColumnSpan(identifiantInfo, GridPane.REMAINING);
        GridPane.setMargin(identifiantInfo, new Insets(50, 0, 0, 0));
        grid.add(identifiantInfo, 0, 0);

        mdpLabel.setFont(new Font("Arial", 25.0));
        textMdp.setPromptText("Saisissez votre mot de passe");
        textMdp.setFont(Font.font("Arial", FontPosture.ITALIC, 16.0));
        grid.add(mdpLabel, 0, 1);
        grid.add(textMdp, 1, 1);

        boutonConnexion.setFont(new Font("Arial", 25.0));
        boutonConnexion.setStyle("-fx-background-color: purple; -fx-text-fill: white;");
        GridPane.setHalignment(boutonConnexion, HPos.RIGHT);
        grid.add(boutonConnexion, 1, 2);

        motDePasseOublie.setFont(new Font("Arial", 13.0));
        motDePasseOublie.setStyle("-fx-text-fill: blue; -fx-font-style: italic; -fx-underline: true;");
        GridPane.setColumnSpan(motDePasseOublie, GridPane.REMAINING);
        GridPane.setMargin(motDePasseOublie, new Insets(10, 0, 0, 100));
        grid.add(motDePasseOublie, 1, 3);

        ImageView imageView = new ImageView(new Image("file:ressources/flèche.png"));
        imageView.setFitWidth(30);
        imageView.setFitHeight(30);

        Button backButton = new Button();
        backButton.setGraphic(imageView);

        Logiciel logiciel = new Logiciel(primaryStage);
        backButton.setOnAction(event -> {
            logiciel.afficheVueAccueil();
            close();
        });

        grid.add(backButton, 0, 3);

        erreur.setStyle("-fx-text-fill: red;");
        GridPane.setColumnSpan(erreur, GridPane.REMAINING);
        GridPane.setMargin(erreur, new Insets(0, 0, 0, 60));
        grid.add(erreur, 0, 3);
    }

    /**
     * Configure les actions des éléments de l'interface.
     */
    public void configurerActions() {
        boutonConnexion.setOnAction((event) -> {
            ConnexionController connexionController = new ConnexionController(textIdentifiant, textMdp,
                    erreur, this);
            connexionController.handle(event);
        });

        ChangerMdp changerMdp = new ChangerMdp(primaryStage, this);
        motDePasseOublie.setOnMouseClicked(event -> {
            changerMdp.afficheVueMdp();
            close();
        });
    }
}
