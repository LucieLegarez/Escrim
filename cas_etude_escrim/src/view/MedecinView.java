package view;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import control.SessionController;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
import javafx.collections.transformation.FilteredList;
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
	private final String[] nomsColonnesPres = { "prénom", "nom", "ID_MEDECIN ", "nom_medicament ", "quantité ", "date_prescription ", "lieu_Attentat ", "date_Attentat" };
	private final BDD bdd;
	private ObservableList<String[]> listeAttentats;
	private ObservableList<String[]> prescriptionList;
	private List<String> lieu;
	private List<String> tot_blesses;
	private List<String> Pers_à_soigner;
	private Label successMessageLabel;
	private final BlesseView BV;
	
	/**
	 * Constructeur de la vue du logisticien.
	 *
	 * @param primaryStage La fenêtre principale de l'application.
	 */
	public MedecinView(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.bdd = new BDD();
		this.BV = new BlesseView(primaryStage);
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

		addButton(mainPane);

		// Ajout du titre et du panneau de boutons au GridPane
		mainPane.add(titleLabel, 0, 5); // Ajout du titre en première ligne
		GridPane.setHalignment(titleLabel, HPos.CENTER);
		GridPane.setMargin(titleLabel, new Insets(0, 0, 0, 0)); // Espacement autour du titre
		ImageView imageMedicament = new ImageView(new Image("file:ressources/Medicaments.png"));
		imageMedicament.setFitWidth(200);
		imageMedicament.setFitHeight(200);
		GridPane.setColumnSpan(imageMedicament, GridPane.REMAINING);
		GridPane.setHalignment(imageMedicament, HPos.CENTER);
		mainPane.add(imageMedicament, 0, 10);
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

	public void afficheVueListesPrescriptions() {
		List<String[]> PrescriptionList = bdd.recupererListePrescription();
		
		prescriptionList = FXCollections.observableArrayList(PrescriptionList);

		GridPane mainPane = new GridPane();
		mainPane.setAlignment(Pos.CENTER);
		mainPane.setPadding(new Insets(10));

		TableView<String[]> table = createTableViewPres();
		mainPane.add(table, 0, 1);

		TextField searchField = createSearchFieldPres(table);
		mainPane.add(searchField, 0, 0);

		Button backButton = createBackButton();
		mainPane.add(backButton, 0, 5);

		showScene(mainPane, "Liste des Prescriptions");
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
	
	public void filterTablePres(String searchText, TableView<String[]> table) {
		if (prescriptionList == null) {
			return;
		}

		if (searchText == null || searchText.isEmpty()) {
			table.setItems(prescriptionList);
			return;
		}

		List<String[]> filteredList = prescriptionList.stream()
				.filter(row -> row[1].toLowerCase().contains(searchText.toLowerCase())).collect(Collectors.toList());

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

		mainPane.add(backButton, 0, 20);
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
		Button visualiserAttentatButton = new Button("Informations Attentats");
		Button renseignementPatientButton = new Button("Créer Prescription");
		Button visualiserPrescriptionButton = new Button("Consulter Prescription");

		// Définition de la taille et du style des boutons
		 // Taille des boutons pour être carrés
		visualiserAttentatButton.setPrefWidth(350);
		visualiserAttentatButton.setPrefHeight(150);
		renseignementPatientButton.setPrefWidth(350);
		renseignementPatientButton.setPrefHeight(150);
		visualiserPrescriptionButton.setPrefWidth(350);
		visualiserPrescriptionButton.setPrefHeight(150);

		// Application de styles supplémentaires si nécessaire
		visualiserAttentatButton.setStyle(" -fx-font-size: 14pt; -fx-background-radius: 5; -fx-padding: 20;");
		renseignementPatientButton.setStyle("-fx-font-size: 14pt; -fx-background-radius: 5; -fx-padding: 20;");
		visualiserPrescriptionButton.setStyle("-fx-font-size: 14pt; -fx-background-radius: 5; -fx-padding: 20;");

		// Utilisation d'une VBox pour aligner verticalement les boutons au centre
		VBox buttonBox = new VBox(10); // Espacement entre les boutons
		buttonBox.setAlignment(Pos.CENTER); // Centre les éléments dans la VBox
		buttonBox.getChildren().addAll(visualiserAttentatButton, renseignementPatientButton, visualiserPrescriptionButton);

		// Ajoute la VBox au GridPane en s'assurant qu'elle est centrée
		mainPane.add(buttonBox, 0, 15); // Colonne 0, Ligne 1
		GridPane.setHalignment(buttonBox, HPos.CENTER); // Centre horizontalement dans la grille
		GridPane.setValignment(buttonBox, VPos.CENTER); // Centre horizontalement dans la grille

		visualiserAttentatButton.setOnAction(event -> {
			afficheVueListesAttentats();
		});
		renseignementPatientButton.setOnAction(event -> {
			createPrescriptionPopUp();
		});
		
		visualiserPrescriptionButton.setOnAction(event -> {
			afficheVueListesPrescriptions();
		});

	}

	private void createPrescriptionPopUp() {
		Stage popupStage = new Stage();
		popupStage.initModality(Modality.APPLICATION_MODAL);
		Label titleLabel = new Label("Information prescription");
		titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
		titleLabel.setAlignment(Pos.CENTER);
		popupStage.setTitle("Saisir les informations de la prescription pour le patient");

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
		TextField prenomTextField = new TextField();
		TextField nomTextField = new TextField();
		
		
	    ComboBox<String> medicamentComboBox = new ComboBox<>();
	    populateMedicamentComboBox(medicamentComboBox);
	    Spinner<Integer> quantiteSpinner = createQuantitySpinner();
	    
	    ComboBox<String> AttentatComboBox = new ComboBox<>();
	    populateAttentatComboBox(AttentatComboBox);
	    
		
		// Add components to the grid
		gridPane.addRow(1, new Label("Prénom patient :"), prenomTextField);
		gridPane.addRow(2, new Label("Nom patient :"), nomTextField);
		gridPane.addRow(3, new Label("Médicament prescrit :"), medicamentComboBox);
		gridPane.addRow(4, new Label("Quantité :"), quantiteSpinner);
		gridPane.addRow(5, new Label("Attentat :"), AttentatComboBox);
		
		
		Button validerButton = new Button("Valider");
		validerButton.setStyle("-fx-background-color: linear-gradient(#8a2be2, #9370db);-fx-pref-width: 75px;-fx-pref-height: 2px; -fx-text-fill: white; -fx-font-size: 8pt; -fx-background-radius: 5; -fx-padding: 5;");
		validerButton.setOnAction(e -> {
			String pnom = prenomTextField.getText();
			String nom = nomTextField.getText();
	        String medPrescrit = medicamentComboBox.getSelectionModel().getSelectedItem();
	        String infoAttentat = AttentatComboBox.getSelectionModel().getSelectedItem();

	        int quantite = quantiteSpinner.getValue();
	        String nomEnMinuscules = nom.toLowerCase();
			String pnomEnMinuscule = pnom.toLowerCase();
			
			if (validateFields(pnomEnMinuscule, nomEnMinuscules, medPrescrit, quantite, infoAttentat, errorLabel)) {
				
				String id_med = SessionController.getInstance().getUserId();
				// Insert into the database
				String result = bdd.insererPrescription(pnomEnMinuscule, nomEnMinuscules, medPrescrit, quantite, id_med, infoAttentat);
				if ("Success".equals(result)) {
				// Display success message
					BV.afficherPrescriptions(pnom, nom);
				successLabel.setText("Ajout de la prescription de " + pnom +" "+ nom + " réussi");
				gridPane.add(successLabel, 0, 7, 2, 1); // Span across both columns
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
			}else {
			    errorLabel.setText(result); // Cela affichera le message de stock insuffisant ou toute autre erreur
			}} 
				
		});

		gridPane.addRow(6, validerButton);
		popupStage.setScene(new Scene(gridPane, 450, 350)); // Adjust the scene size if necessary
		popupStage.showAndWait();
	}
	
	private void populateMedicamentComboBox(ComboBox<String> comboBox) {
	    List<String[]> medicaments = bdd.recupererStocksMedicaments();
	    for (String[] medicament : medicaments) {
	        comboBox.getItems().add(medicament[0].trim() + " ; " + medicament[2].trim() + " ; " + medicament[3].trim());
	    }
	}

	private void populateAttentatComboBox(ComboBox<String> comboBox) {
	    List<String[]> attentat = bdd.recupererListeAttentat();
	    for (String[] Attentat : attentat) {
	        comboBox.getItems().add(Attentat[0].trim() + " ; " + Attentat[3].trim() );
	    }
	}

	// A faire : décompter les médicament utilisés
	//			Synchroniser la fiche du patient(attention à l'id)
	//			bonus (pouvoir prescrire plusieurs medicament/ pouvoir ecrire pour selectionner)

	public boolean validateFields(String pnom, String nom, String medPrescrit, int quantite, String infoAttentat,
			 Label errorLabel) {
		
		if (pnom.isEmpty()||nom.isEmpty()) {
			errorLabel.setText("Les nom et prénom du patient sont requis.");
			return false;
		}
		if (medPrescrit==null) {
			errorLabel.setText("Renseigner le médicament");
			return false;
		}
		
		if (infoAttentat==null) {
			errorLabel.setText("Renseigner un attentat");
			return false;
		}

		if (bdd.prescriptionExiste(pnom, nom)) {
	        errorLabel.setText("Une prescription pour ce patient existe déjà.");
	        return false;
	    }
		
		try {
			
			if (quantite <= 0 ) {
				errorLabel.setText("Le nombres de médicament doit être positif.");
				return false;
			}
		} catch (NumberFormatException e) {
			errorLabel.setText("Le nombres de médicament doivt être un entier positif.");
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
	
	public TableView<String[]> createTableViewPres() {
		TableView<String[]> table = new TableView<>();
		for (int i = 0; i < 8; i++) {
			TableColumn<String[], String> column = new TableColumn<>(nomsColonnesPres[i]);
			int columnIndex = i;
			
				column.setCellValueFactory(
						cellData -> new SimpleStringProperty(cellData.getValue()[columnIndex].trim()));
			

			column.setPrefWidth(200);

			table.getColumns().add(column);
		}
		table.getItems().addAll(prescriptionList);
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
	
	
	public TextField createSearchFieldPres(TableView<String[]> table) {
		TextField searchField = new TextField();
		searchField.setPromptText("Rechercher une prescription par nom");

		searchField.textProperty().addListener((observable, oldValue, newValue) -> {
			filterTablePres(newValue, table);
		});

		searchField.setOnAction(event -> {
			String searchText = searchField.getText().trim();
			filterTablePres(searchText, table);
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