package view;

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
import javafx.stage.Stage;

public class Logiciel {
    private Stage stage;
    private Label bienvenue = new Label("Bienvenue sur le logiciel ESCRIM");
    private Image imagePresentation = new Image("file:ressources/guerre.jpg");
    private ImageView vueImage;
    private Button boutonLancerCalcul;
    private Button boutonIdentifier;

    public Logiciel(Stage secondaryStage) {
        this.vueImage = new ImageView(this.imagePresentation);
        this.boutonLancerCalcul = new Button("Lancer les calculs");
        this.boutonIdentifier = new Button("M'identifier");
        this.stage = secondaryStage;
    }

    public void afficheVueAccueil() {
        this.stage.setTitle("Page d'accueil ESCRIM");
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(50.0, 50.0, 50.0, 100.0));
        grid.setVgap(30.0);
        grid.setHgap(50.0);
        this.bienvenue.setFont(new Font("Arial", 25.0));
        this.bienvenue.setTextFill(Color.web("#c0001a"));
        this.boutonLancerCalcul.setFont(new Font("Arial", 25.0));
        this.boutonLancerCalcul.setStyle("-fx-background-color: white; -fx-text-fill: red;-fx-border-color: red;");
        this.boutonIdentifier.setFont(new Font("Arial", 25.0));
        this.boutonIdentifier.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        this.vueImage.setFitWidth(400.0);
        this.vueImage.setFitHeight(200.0);
        grid.add(this.bienvenue, 0, 0);
        grid.add(this.vueImage, 0, 2);
        grid.add(this.boutonLancerCalcul, 0, 5);
        grid.add(this.boutonIdentifier, 0, 6);
        GridPane.setHalignment(this.bienvenue, HPos.CENTER);
        GridPane.setHalignment(this.boutonLancerCalcul, HPos.CENTER);
        GridPane.setHalignment(this.boutonIdentifier, HPos.CENTER);
        GridPane.setHalignment(this.vueImage, HPos.CENTER);
        BorderPane root = new BorderPane();
        root.setCenter(grid);
        Scene scene = new Scene(root, 800.0, 600.0);
        this.stage.setScene(scene);
        this.stage.show();
    }
}
