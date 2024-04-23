package view;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import model.BDD;

/**
 * Classe représentant la vue du blessé.
 */
public class BlesseView extends Stage {

    private Stage primaryStage;
    private Label ficheRenseignement;
    private Label fichePatient;
    private String prenom;
    private String nom;
    private Label titre;
    private final BDD bdd;

    /**
     * Constructeur de la vue du blessé.
     *
     * @param primaryStage La fenêtre principale de l'application.
     */
    
    public BlesseView(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.ficheRenseignement = new Label("Fiche Renseignements :");
        this.fichePatient = new Label("Fiche Patient :");
        this.titre = new Label();
        this.bdd = new BDD();
    }

    /**
     * Affiche la vue de la fiche du blessé.
     * 
     * @param prenom        Le prénom du blessé.
     * @param nom           Le nom du blessé.
     * @param dateNaissance La date de naissance du blessé.
     */
    public void afficheVueBlesse(String prenom, String nom, Date dateNaissance) {
        this.prenom = prenom;
        this.nom = nom;
        this.titre.setText("Fiche de " + prenom + " " + nom);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dateNaissanceFormatted = dateNaissance.toLocalDate().format(formatter);

        GridPane infosPane = createInfosPane(dateNaissanceFormatted);
        GridPane patientPane = afficherPrescriptions(prenom, nom);
        Line line = createLine();
        GridPane mainPane = createMainPane(infosPane, patientPane, line);
        addTitleAndBackButton(mainPane);

        showScene(mainPane);
    }

    /**
     * Crée le panneau d'informations sur le blessé.
     * 
     * @param dateNaissanceFormatted La date de naissance du blessé formatée.
     * @return Le panneau d'informations créé.
     */
    public GridPane createInfosPane(String dateNaissanceFormatted) {
        GridPane infosPane = new GridPane();
        infosPane.setPadding(new Insets(10));
        infosPane.setVgap(10);
        infosPane.setHgap(5);
        infosPane.setPrefWidth(400);
        infosPane.setStyle("-fx-background-color: white;");

        ficheRenseignement.setAlignment(Pos.CENTER);
        ficheRenseignement.setFont(new Font("Arial", 20.0));
        ficheRenseignement.setStyle("-fx-underline: true;");
        GridPane.setMargin(infosPane, new Insets(50, 0, 200, 0));
        
        infosPane.add(ficheRenseignement, 0, 0, 2, 1); // Spanning two columns

        ImageView imagePersonne = new ImageView(new Image("file:ressources/personne.png"));
        imagePersonne.setFitWidth(100);
        imagePersonne.setFitHeight(100);
        GridPane.setColumnSpan(imagePersonne, GridPane.REMAINING);
        GridPane.setHalignment(imagePersonne, HPos.CENTER);
        infosPane.add(imagePersonne, 0, 1, 2, 1); // Spanning two columns

        Label prenomLabel = new Label("Prénom : " + prenom);
        prenomLabel.setFont(Font.font("Arial", 15));
        infosPane.add(prenomLabel, 0, 2, 2, 1); // Spanning two columns

        Label nomLabel = new Label("Nom : " + nom);
        nomLabel.setFont(Font.font("Arial", 15));
        infosPane.add(nomLabel, 0, 3, 2, 1); // Spanning two columns

        Label dateNaissanceLabel = new Label("Date de naissance : " + dateNaissanceFormatted);
        dateNaissanceLabel.setFont(Font.font("Arial", 15));
        infosPane.add(dateNaissanceLabel, 0, 4, 2, 1); // Spanning two columns

        return infosPane;
    }

    /**
     * Crée le panneau d'informations sur le patient et gère l'affichage de longs textes.
     * 
     * @return Le panneau d'informations sur le patient créé.
     */
    public GridPane afficherPrescriptions(String prenom, String nom) {
        List<String[]> prescriptions = bdd.recupererPrescriptionsParPatient(prenom, nom);
        GridPane patientPane = new GridPane();
        patientPane.setPadding(new Insets(10));
        patientPane.setVgap(10);
        patientPane.setHgap(5);
        patientPane.setPrefWidth(450);
        patientPane.setStyle("-fx-background-color: white;");
        
        for (String[] pres : prescriptions) {
            String date = pres[0];
            String medecin = pres[1];
            String médicament = pres[2];
            String quantite = pres[3];
            String lieu = pres[4];
            String dateAttentat = pres[5];
            
            fichePatient.setAlignment(Pos.CENTER);
            fichePatient.setFont(new Font("Arial", 20.0));
            fichePatient.setStyle("-fx-underline: true;");
            GridPane.setMargin(patientPane, new Insets(50, 0, 200, 0));
            patientPane.add(fichePatient, 0, 0);
            
            ImageView imageMedicament = new ImageView(new Image("file:ressources/Medicaments.png"));
            imageMedicament.setFitWidth(100);
            imageMedicament.setFitHeight(100);
            GridPane.setColumnSpan(imageMedicament, GridPane.REMAINING);
            GridPane.setHalignment(imageMedicament, HPos.CENTER);
            patientPane.add(imageMedicament, 0, 1);
            
            Label presLabel = new Label("Date: " + date.strip() + "\nMédecin: " + medecin.strip()  + "\nMédicament: " + médicament.strip()  + "\nQuantité: " + quantite.strip()  + "\nLieu: " + lieu.strip()  + "\nDate Attentat: " + dateAttentat.strip() );
            presLabel.setFont(Font.font("Arial", 14));
            presLabel.setWrapText(true); // Enable text wrapping within the label
            presLabel.setMaxWidth(550); // Set the maximum width for the label
            patientPane.add(presLabel, 0, prescriptions.indexOf(pres)+4, 1, 1); // Add label to the grid
        }

        return patientPane;
    }

    /**
     * Crée une ligne pour séparer les panneaux.
     * 
     * @return La ligne créée.
     */
    public Line createLine() {
        Line line = new Line(0, 0, 0, 470);
        line.setStyle("-fx-stroke: grey;");
        line.setStrokeWidth(3);
        return line;
    }

    /**
     * Crée le panneau principal en assemblant les panneaux d'informations et la ligne.
     * 
     * @param infosPane   Le panneau d'informations sur le blessé.
     * @param patientPane Le panneau d'informations sur le patient.
     * @param line        La ligne séparatrice.
     * @return Le panneau principal créé.
     */
    public GridPane createMainPane(GridPane infosPane, GridPane patientPane, Line line) {
        GridPane mainPane = new GridPane();
        mainPane.setAlignment(Pos.CENTER);
        mainPane.setPadding(new Insets(10));
        mainPane.setPrefWidth(1500);

        GridPane.setMargin(infosPane, new Insets(50, 0, 0, 0));
        GridPane.setMargin(line, new Insets(50, 10, 0, 10));
        GridPane.setMargin(patientPane, new Insets(50, 0, 0, 0));
        mainPane.addColumn(0, infosPane);
        mainPane.addColumn(1, line);
        mainPane.addColumn(2, patientPane);

        titre.setFont(Font.font("Arial", FontWeight.BOLD, 30.0));
        GridPane.setColumnSpan(titre, GridPane.REMAINING);
        GridPane.setHalignment(titre, HPos.CENTER);
        GridPane.setValignment(titre, VPos.TOP);
        GridPane.setMargin(titre, new Insets(1, 0, 0, 0));
        mainPane.add(titre, 0, 0);

        return mainPane;
    }

    /**
     * Ajoute le titre et le bouton de retour au panneau principal.
     * 
     * @param mainPane Le panneau principal.
     */
    public void addTitleAndBackButton(GridPane mainPane) {
        ImageView imageView = new ImageView(new Image("file:ressources/flèche.png"));
        imageView.setFitWidth(30);
        imageView.setFitHeight(30);

        Button backButton = new Button();
        backButton.setGraphic(imageView);

        backButton.setOnAction(event -> {
            new Logiciel(primaryStage).afficheVueAccueil();
            this.close();
        });

        GridPane.setConstraints(backButton, 0, 1);
        mainPane.getChildren().add(backButton);
    }

    /**
     * Affiche la scène avec le panneau principal.
     * 
     * @param mainPane Le panneau principal à afficher.
     */
    public void showScene(GridPane mainPane) {
        Scene scene = new Scene(mainPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Fiche du blessé");
        primaryStage.show();
    }
}
