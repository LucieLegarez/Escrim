package view;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.BDD;

/**
 * Classe représentant la vue du logisticien.
 */
public class MedecinView extends Stage {

	private Label errorLabel; // To display error messages
	private Stage primaryStage;
	private final String[] nomsColonnes = { "Lieu", "Tot_blesses", "Pers_à_soigner", "date_evenement" };
	private final BDD bdd;
	private ObservableList<String[]> listeAttentats;
	private List<String> lieu;
	private List<String> tot_blesses;
	private List<String> Pers_à_soigner;
	private Label successMessageLabel;

	/**
	 * Constructeur de la vue du logisticien.
	 *
	 * @param primaryStage La fenêtre principale de l'application.
	 */
	public MedecinView(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.bdd = new BDD();
		this.lieu = new ArrayList<>();
		this.tot_blesses = new ArrayList<>();
		this.Pers_à_soigner = new ArrayList<>();
		errorLabel = new Label(); // Initialize the error label
		errorLabel.setTextFill(Color.RED); // Set error text color
	}

	/**
	 * Affiche la vue du logisticien.
	 */
	public void afficheVueMedecin() {
		Label titleLabel = new Label("Écran de gestion du médecin");
		titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
		titleLabel.setAlignment(Pos.CENTER);

		GridPane mainPane = createMainPane();
		mainPane.getChildren().clear();
		addBackButton(mainPane);

		List<String[]> AttentatsList = bdd.recupererListeAttentat();

		addButton(mainPane);

		// Ajout du titre et du panneau de boutons au GridPane
		mainPane.add(titleLabel, 0, 15); // Ajout du titre en première ligne
		GridPane.setHalignment(titleLabel, HPos.CENTER);
		GridPane.setMargin(titleLabel, new Insets(10, 0, 0, 0)); // Espacement autour du titre
		ImageView imageMedicament = new ImageView(new Image("file:ressources/Medicaments.png"));
		imageMedicament.setFitWidth(200);
		imageMedicament.setFitHeight(200);
		GridPane.setColumnSpan(imageMedicament, GridPane.REMAINING);
		GridPane.setHalignment(imageMedicament, HPos.CENTER);
		mainPane.add(imageMedicament, 0, 20);
		showScene(mainPane, "Interface du médecin");
	}

	/**
	 * Affiche la vue des stocks de médicaments.
	 */
	public void afficheVueListesAttentats() {
		List<String[]> AttentatsList = bdd.recupererListeAttentat();
		listeAttentats = FXCollections.observableArrayList(AttentatsList);

		GridPane mainPane = new GridPane();
		mainPane.setAlignment(Pos.CENTER);
		mainPane.setPadding(new Insets(10));

		TableView<String[]> table = createTableView();
		mainPane.add(table, 0, 1);

		TextField searchField = createSearchField(table);
		mainPane.add(searchField, 0, 0);

		Button backButton = createBackButton();
		mainPane.add(backButton, 0, 5);

		showScene(mainPane, "Liste des Attentats");
	}

	/**
	 * Filtre les données dans le tableau en fonction du texte saisi.
	 *
	 * @param searchText Le texte saisi dans le champ de recherche.
	 * @param table      Le tableau à filtrer.
	 */
	public void filterTable(String searchText, TableView<String[]> table) {
		if (listeAttentats == null) {
			return;
		}

		if (searchText == null || searchText.isEmpty()) {
			table.setItems(listeAttentats);
			return;
		}

		List<String[]> filteredList = listeAttentats.stream()
				.filter(row -> row[0].toLowerCase().contains(searchText.toLowerCase())).collect(Collectors.toList());

		table.setItems(FXCollections.observableArrayList(filteredList));
	}

	/**
	 * Crée le panneau principal de la vue.
	 *
	 * @return Le panneau principal créé.
	 */
	public GridPane createMainPane() {
		GridPane mainPane = new GridPane();
		mainPane.setAlignment(Pos.CENTER);
		mainPane.setPadding(new Insets(10));
		mainPane.setVgap(5);

		
		return mainPane;
	}

	/**
	 * Ajoute le bouton de retour au panneau principal.
	 *
	 * @param mainPane Le panneau principal où ajouter le bouton de retour.
	 */
	public void addBackButton(GridPane mainPane) {
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

		mainPane.add(backButton, 0, 30);
		GridPane.setHalignment(backButton, HPos.LEFT);
		GridPane.setMargin(backButton, new Insets(10));
	}

	/**
	 * Crée un spinner pour sélectionner la quantité.
	 *
	 * @return Le spinner créé.
	 */
	public Spinner<Integer> createQuantitySpinner() {
		Spinner<Integer> quantitySpinner = new Spinner<>(0, Integer.MAX_VALUE, 0);
		quantitySpinner.setMaxWidth(70);
		quantitySpinner.setEditable(true);

		quantitySpinner.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
			if (!newValue.matches("\\d*")) {
				quantitySpinner.getEditor().setText(oldValue);
			}
		});

		return quantitySpinner;
	}

	/**
	 * Définit la disposition des éléments dans le panneau principal.
	 *
	 * @param quantitySpinner Le spinner de quantité.
	 * @param orderButton     Le bouton de commande.
	 */
	public void setButtonLayout(Spinner<Integer> quantitySpinner, Button orderButton) {
		GridPane.setColumnSpan(quantitySpinner, GridPane.REMAINING);
		GridPane.setColumnSpan(orderButton, GridPane.REMAINING);
		GridPane.setHalignment(quantitySpinner, HPos.RIGHT);
		GridPane.setHalignment(orderButton, HPos.RIGHT);
	}

	/**
	 * Ajoute le bouton pour visualiser les stocks au panneau principal. Ajoute le
	 * bouton pour ajouter les informations concernant un attentat.
	 * 
	 * @param mainPane Le panneau principal où ajouter le bouton.
	 */
	public void addButton(GridPane mainPane) {
		Button visualiserAttentatButton = new Button("Infos Attentats");
		Button renseignementPatientButton = new Button("Patient");

		// Définition de la taille et du style des boutons
		int buttonSize = 150; // Taille des boutons pour être carrés
		visualiserAttentatButton.setPrefWidth(300);
		visualiserAttentatButton.setPrefHeight(150);
		renseignementPatientButton.setPrefWidth(buttonSize);
		renseignementPatientButton.setPrefHeight(buttonSize);

		// Application de styles supplémentaires si nécessaire
		visualiserAttentatButton.setStyle("-fx-font-size: 14pt; -fx-background-radius: 5; -fx-padding: 20;");
		renseignementPatientButton.setStyle("-fx-font-size: 14pt; -fx-background-radius: 5; -fx-padding: 20;");

		// Utilisation d'une VBox pour aligner verticalement les boutons au centre
		VBox buttonBox = new VBox(10); // Espacement entre les boutons
		buttonBox.setAlignment(Pos.CENTER); // Centre les éléments dans la VBox
		buttonBox.getChildren().addAll(visualiserAttentatButton, renseignementPatientButton);

		// Ajoute la VBox au GridPane en s'assurant qu'elle est centrée
		mainPane.add(buttonBox, 0, 25); // Colonne 0, Ligne 1
		GridPane.setHalignment(buttonBox, HPos.CENTER); // Centre horizontalement dans la grille
		GridPane.setValignment(buttonBox, VPos.CENTER); // Centre horizontalement dans la grille

		visualiserAttentatButton.setOnAction(event -> {
			afficheVueListesAttentats();
		});
		renseignementPatientButton.setOnAction(event -> {
			createAttentatInfoPopup();
		});

	}

	private void createAttentatInfoPopup() {
		Stage popupStage = new Stage();
		popupStage.initModality(Modality.APPLICATION_MODAL);
		popupStage.setTitle("Saisir les informations de l'attentat");

		GridPane gridPane = new GridPane();
		gridPane.setVgap(10);
		gridPane.setHgap(10);
		gridPane.setPadding(new Insets(20));

		// Initialize the error label and add it to the grid
		Label errorLabel = new Label();
		errorLabel.setTextFill(Color.RED); // Set the text color to red for visibility
		gridPane.add(errorLabel, 0, 0, 2, 1); // Span across two columns at the top of the grid

		// Initialize the success label but do not add to grid yet
		Label successLabel = new Label();
		successLabel.setTextFill(Color.GREEN);

		// Other UI components
		DatePicker dateAttentatTextField = new DatePicker();
		TextField lieuTextField = new TextField();
		TextField totBlessesTextField = new TextField();
		TextField nbAsoignerTextField = new TextField();

		// Add components to the grid
		gridPane.addRow(1, new Label("Date de l'attentat :"), dateAttentatTextField);
		gridPane.addRow(2, new Label("Lieu de l'attentat :"), lieuTextField);
		gridPane.addRow(3, new Label("Total de blessés :"), totBlessesTextField);
		gridPane.addRow(4, new Label("Nombre à soigner :"), nbAsoignerTextField);

		Button validerButton = new Button("Valider");
		validerButton.setOnAction(e -> {
			LocalDate dateAttentat = dateAttentatTextField.getValue();
			String lieu = lieuTextField.getText();
			String totBlessesStr = totBlessesTextField.getText();
			String nbAsoignerStr = nbAsoignerTextField.getText();

			if (validateFields(dateAttentat, lieu, totBlessesStr, nbAsoignerStr, errorLabel)) {
				int totBlesses = Integer.parseInt(totBlessesStr);
				int nbAsoigner = Integer.parseInt(nbAsoignerStr);

				// Insert into the database
				bdd.insererAttentat(lieu, totBlesses, nbAsoigner, dateAttentat);

				// Display success message
				successLabel.setText("Ajout de l'attentat à " + lieu + " réussi");
				gridPane.add(successLabel, 0, 6, 2, 1); // Span across both columns

				// Close popup after a delay
				new Thread(() -> {
					try {
						Thread.sleep(2000);
						Platform.runLater(() -> {
							popupStage.close();
							afficheVueMedecin(); // Refresh the logistician view to reflect the updated stock
						});
					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}
				}).start();
			}
		});

		gridPane.addRow(5, validerButton);
		popupStage.setScene(new Scene(gridPane, 450, 350)); // Adjust the scene size if necessary
		popupStage.showAndWait();
	}

	public boolean validateFields(LocalDate date, String lieu, String totalBlessesStr, String toTreatStr,
			Label errorLabel) {
		if (date == null) {
			errorLabel.setText("La date de l'attentat est requise.");
			return false;
		}
		if (lieu.isEmpty()) {
			errorLabel.setText("Le lieu de l'attentat est requis.");
			return false;
		}
		try {
			int totalBlesses = Integer.parseInt(totalBlessesStr);
			int toTreat = Integer.parseInt(toTreatStr);
			if (totalBlesses <= 0 || toTreat <= 0) {
				errorLabel.setText("Les nombres de blessés et à soigner doivent être positifs.");
				return false;
			}
		} catch (NumberFormatException e) {
			errorLabel.setText("Les nombres de blessés et à soigner doivent être des entiers positifs.");
			return false;
		}
		return true;
	}

	/**
	 * Affiche une scène avec le panneau principal et le titre spécifiés.
	 *
	 * @param mainPane Le panneau principal à afficher dans la scène.
	 * @param title    Le titre de la scène.
	 */
	public void showScene(GridPane mainPane, String title) {
		Scene scene = new Scene(mainPane, 650, 650);
		primaryStage.setScene(scene);
		primaryStage.setTitle(title);
		primaryStage.show();
	}

	/**
	 * Crée et retourne un TableView pour afficher les stocks de médicaments.
	 *
	 * @return Le TableView créé.
	 */
	public TableView<String[]> createTableView() {
		TableView<String[]> table = new TableView<>();
		for (int i = 0; i < 4; i++) {
			TableColumn<String[], String> column = new TableColumn<>(nomsColonnes[i]);
			int columnIndex = i;
			if (i == 3) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				column.setCellValueFactory(cellData -> new SimpleStringProperty(
						formatter.format(LocalDate.parse(cellData.getValue()[columnIndex]))));
			} else {
				column.setCellValueFactory(
						cellData -> new SimpleStringProperty(cellData.getValue()[columnIndex].trim()));
			}

			column.setPrefWidth(200);

			table.getColumns().add(column);
		}
		table.getItems().addAll(listeAttentats);
		return table;
	}

	/**
	 * Crée et retourne un champ de recherche pour filtrer les données dans le
	 * TableView spécifié.
	 *
	 * @param table Le TableView à filtrer.
	 * @return Le champ de recherche créé.
	 */
	public TextField createSearchField(TableView<String[]> table) {
		TextField searchField = new TextField();
		searchField.setPromptText("Rechercher un attentat avec son lieu");

		searchField.textProperty().addListener((observable, oldValue, newValue) -> {
			filterTable(newValue, table);
		});

		searchField.setOnAction(event -> {
			String searchText = searchField.getText().trim();
			filterTable(searchText, table);
		});
		return searchField;
	}

	/**
	 * Crée et retourne un bouton de retour vers la vue du logisticien.
	 *
	 * @return Le bouton de retour créé.
	 */
	public Button createBackButton() {
		ImageView imageView = new ImageView(new Image("file:ressources/flèche.png"));
		imageView.setFitWidth(30);
		imageView.setFitHeight(30);

		Button backButton = new Button();
		backButton.setGraphic(imageView);
		backButton.setOnAction(event -> {
			afficheVueMedecin();
			this.close();
		});
		return backButton;
	}

}