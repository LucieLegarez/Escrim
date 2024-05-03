package view;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.BDD;

/**
 * Classe représentant la vue du logisticien.
 */
public class LogisticienView extends Stage {

	private Label errorLabel; 
	private Stage primaryStage;
	private final String[] nomsColonnes = { "PRODUIT", "DCI", "DOSAGE", "DLU", "QUANTITÉ", "LOT", "CLASSE",
			"NUM_CAISSE", "CAISSE" };
	private final String[] nomsColonnesAvions = { "NOM", "CONSTRUCTEUR", "TYPE_MOTEUR", "TYPE_DE_VOL", "TONNE_MAX",
			"TAILLE_PORTE_CM", "DIMENSIONS_SOUTE_CM", "VOLUME_UTILISABLE_M3", "EXIGENCE_PISTE_M", "PORTEE_CHARGE_KM",
			"PORTEE_VIDE_KM", "VITESSE_CROISIERE_KMH", "CONSOMMATION_CARBURANT_LH", "POSITIONS_PALETTES", "etat",
			"lieu_attentat", "date_attentat" };
	private final BDD bdd;
	private ObservableList<String[]> stocksMedicaments;
	private ObservableList<String[]> stocksAvion;
	private List<String> produit;
	private List<String> dci;
	private List<String> dosage;

	/**
	 * Constructeur de la vue du logisticien.
	 *
	 * @param primaryStage La fenêtre principale de l'application.
	 */
	public LogisticienView(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.bdd = new BDD();
		this.produit = new ArrayList<>();
		this.dci = new ArrayList<>();
		this.dosage = new ArrayList<>();
		errorLabel = new Label(); 
		errorLabel.setTextFill(Color.RED); 
	}

	/**
	 * Affiche la vue du logisticien.
	 */
	public void afficheVueLogisticien() {
		GridPane mainPane = createMainPane();
		mainPane.getChildren().clear();
		addBackButton(mainPane);

		List<String[]> stocksMedicamentsList = bdd.recupererStocksMedicaments();
		Map<String, Integer> stockGrouped = groupStocksByProduct(stocksMedicamentsList);
		List<String> lowStockMessages = generateLowStockMessages(stockGrouped);

		displayLowStockMessages(mainPane, lowStockMessages);

		addButton(mainPane);

		showScene(mainPane, "Interface du logisticien");
	}

	/**
	 * Affiche la vue des stocks de médicaments.
	 */
	public void afficheVueStocksMedicaments() {
		List<String[]> stocksMedicamentsList = bdd.recupererStocksMedicaments();
		stocksMedicaments = FXCollections.observableArrayList(stocksMedicamentsList);

		GridPane mainPane = new GridPane();
		mainPane.setAlignment(Pos.CENTER);
		mainPane.setPadding(new Insets(10));

		TableView<String[]> table = createTableView();
		mainPane.add(table, 0, 1);

		TextField searchField = createSearchField(table);
		mainPane.add(searchField, 0, 0);

		Button backButton = createBackButton();
		mainPane.add(backButton, 0, 5);

		showScene(mainPane, "Stocks de médicaments");
	}
	
	/**
	 * Affiche la vue des stocks d'avion.
	 */

	public void afficheVueStocksAvion() {
		List<String[]> stocksAvionList = bdd.recupererStocksAvions();
		stocksAvion = FXCollections.observableArrayList(stocksAvionList);

		GridPane mainPane = new GridPane();
		mainPane.setAlignment(Pos.CENTER);
		mainPane.setPadding(new Insets(10));

		TableView<String[]> table = createTableViewAvion();
		mainPane.add(table, 0, 1);

		TextField searchField = createSearchFieldAvion(table);
		mainPane.add(searchField, 0, 0);

		Button backButton = createBackButton();
		mainPane.add(backButton, 0, 5);

		showScene(mainPane, "Stocks d'avions");
	}

	/**
	 * Crée une fenêtre contextuelle pour saisir les informations sur un avion.
	 * Cette méthode affiche une fenêtre modale contenant des champs de saisie et des contrôles pour
	 * enregistrer les informations sur l'avion, telles que la disponibilité, le nom de l'avion, etc.
	 * 
	 * La fenêtre affiche également des messages d'erreur ou de succès en fonction des validations effectuées.
	 * 
	 * @see #populateAvionComboBox(ComboBox)
	 * @see #populateAttentatComboBox(ComboBox)
	 * @see #validateFieldsAvion(String, String, String, String, Label)
	 * @see #validateFieldsAviondisp(String, String, String, Label)
	 */
	
	private void createAvionPopUp() {
		Stage popupStage = new Stage();
		popupStage.initModality(Modality.APPLICATION_MODAL);
		Label titleLabel = new Label("Information avion");
		titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
		titleLabel.setAlignment(Pos.CENTER);
		popupStage.setTitle("Saisir les informations pour l'avion");

		GridPane gridPane = new GridPane();
		gridPane.setVgap(10);
		gridPane.setHgap(10);
		gridPane.setPadding(new Insets(20));

		Label errorLabel = new Label();
		errorLabel.setTextFill(Color.RED); 
		gridPane.add(errorLabel, 0, 0, 2, 1); 

		Label successLabel = new Label();
		successLabel.setTextFill(Color.GREEN);

		ComboBox<String> etatComboBox = new ComboBox<>();
		etatComboBox.getItems().addAll("disponible", "occupé");

		ComboBox<String> avionComboBox = new ComboBox<>();
		populateAvionComboBox(avionComboBox);

		ComboBox<String> attentatComboBox = new ComboBox<>();
		populateAttentatComboBox(attentatComboBox);

		gridPane.addRow(1, new Label("Disponibilité avion :"), etatComboBox);
		gridPane.addRow(2, new Label("Avion :"), avionComboBox);

		attentatComboBox.setVisible(false);
		Label attentatLabel = new Label("Attentat :");
		attentatLabel.setVisible(false);
		gridPane.addRow(5, attentatLabel, attentatComboBox);
		etatComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
			if ("occupé".equals(newVal)) {
				attentatComboBox.setVisible(true);
				attentatLabel.setVisible(true);
			} else {
				attentatComboBox.setVisible(false);
				attentatLabel.setVisible(false);
			}
		});

		Button validerButton = new Button("Valider");
		validerButton.setStyle(
				"-fx-background-color: linear-gradient(#8a2be2, #9370db);-fx-pref-width: 75px;-fx-pref-height: 2px; -fx-text-fill: white; -fx-font-size: 8pt; -fx-background-radius: 5; -fx-padding: 5");
		validerButton.setOnAction(e -> {
			String etat = etatComboBox.getSelectionModel().getSelectedItem();
			
			String avionUt = avionComboBox.getSelectionModel().getSelectedItem();
			String[] avion = avionUt.split(" ; ");
			String nomAvion = avion[0];
			String etatAvion = avion[1];
			if (etat.equals("occupé")) {
			String infoAttentat = attentatComboBox.getSelectionModel().getSelectedItem();
			String[] info = infoAttentat.split(" ; ");
			String lieuAttentat = info[0];
			LocalDate dateAttentat = LocalDate.parse(info[1]);
			

			if (validateFieldsAvion(etat, avionUt, etatAvion, infoAttentat, errorLabel)) {

				if (bdd.updateAvion(nomAvion, etat, lieuAttentat, dateAttentat)) {
					successLabel.setText("Mise à jour de l'avion réussie");
					gridPane.add(successLabel, 0, 7, 2, 1); 
					new Thread(() -> {
						try {
							Thread.sleep(2000);
							Platform.runLater(() -> {
								popupStage.close();
								afficheVueLogisticien(); 
							});
						} catch (InterruptedException ex) {
							ex.printStackTrace();
						}
					}).start();
				} else {
					errorLabel.setText("Échec de la mise à jour de l'avion.");
				}
			}}else {
				
				String lieuAttentat = "null";
				LocalDate dateAttentat = LocalDate.parse("1111-11-11");
				

				if (validateFieldsAviondisp(etat, avionUt, etatAvion, errorLabel)) {

					if (bdd.updateAvion(nomAvion, etat, lieuAttentat, dateAttentat)) {
						successLabel.setText("Mise à jour de l'avion réussie");
						gridPane.add(successLabel, 0, 7, 2, 1); 
						new Thread(() -> {
							try {
								Thread.sleep(2000);
								Platform.runLater(() -> {
									popupStage.close();
									afficheVueLogisticien(); 
								});
							} catch (InterruptedException ex) {
								ex.printStackTrace();
							}
						}).start();
					} else {
						errorLabel.setText("Échec de la mise à jour de l'avion.");
					}
			}}
		});

		gridPane.addRow(6, validerButton);
		popupStage.setScene(new Scene(gridPane, 450, 350)); 
		popupStage.showAndWait();
	}

	/**
	 * Remplit une ComboBox avec les informations sur les attentats récupérées depuis la base de données.
	 * Cette méthode récupère une liste d'attentats à partir de la base de données et les ajoute à la ComboBox
	 * au format "lieu ; date", où lieu représente le lieu de l'attentat et date la date de l'attentat.
	 * 
	 * @param comboBox La ComboBox à remplir avec les informations sur les attentats.
	 * 
	 * @see BaseDeDonnees#recupererListeAttentat()
	 */

	private void populateAttentatComboBox(ComboBox<String> comboBox) {
		List<String[]> attentat = bdd.recupererListeAttentat();
		for (String[] Attentat : attentat) {
			comboBox.getItems().add(Attentat[0].trim() + " ; " + Attentat[3].trim());
		}
	}

	/**
	 * Remplit une ComboBox avec les informations sur les avions récupérées depuis la base de données.
	 * Cette méthode récupère une liste d'avions à partir de la base de données et les ajoute à la ComboBox
	 * au format "nom de l'avion ; état de disponibilité", où le nom de l'avion représente le nom de l'avion
	 * et l'état de disponibilité représente l'état actuel de l'avion (disponible ou occupé).
	 * 
	 * @param comboBox La ComboBox à remplir avec les informations sur les avions.
	 * 
	 * @see BaseDeDonnees#recupererStocksAvions()
	 */

	private void populateAvionComboBox(ComboBox<String> comboBox) {
		List<String[]> avion = bdd.recupererStocksAvions();
		for (String[] Avion : avion) {
			comboBox.getItems().add(Avion[0].trim() + " ; " + Avion[14].trim());
		}
	}

	/**
	 * Valide les champs relatifs à la saisie des informations sur un avion.
	 * Cette méthode vérifie si les champs obligatoires sont renseignés correctement.
	 * 
	 * @param etat L'état de disponibilité sélectionné pour l'avion.
	 * @param avionUt Les informations sur l'avion sélectionné.
	 * @param etatAvion L'état actuel de disponibilité de l'avion.
	 * @param infoAttentat Les informations sur l'attentat sélectionné, le cas échéant.
	 * @param errorLabel Le label où afficher les messages d'erreur, le cas échéant.
	 * 
	 * @return true si tous les champs sont valides, sinon false.
	 */
	
	public boolean validateFieldsAvion(String etat, String avionUt, String etatAvion, String infoAttentat, Label errorLabel) {

		if (etat == null) {
			errorLabel.setText("L'état doit être sélectionné");
			return false;
		}
		if (avionUt == null) {
			errorLabel.setText("Renseigner l'avion");
			return false;
		}

		if (infoAttentat == null) {
			errorLabel.setText("Renseigner un attentat");
			return false;
		}
		
		if (etat.equals(etatAvion)) {
			errorLabel.setText("L'avion est déjà occupé");
			return false;
		}

		return true;
	}

	/**
	 * Valide les champs relatifs à la saisie des informations sur un avion lorsque l'état est disponible.
	 * Cette méthode vérifie si les champs obligatoires sont renseignés correctement.
	 * 
	 * @param etat L'état de disponibilité sélectionné pour l'avion.
	 * @param avionUt Les informations sur l'avion sélectionné.
	 * @param etatAvion L'état actuel de disponibilité de l'avion.
	 * @param errorLabel Le label où afficher les messages d'erreur, le cas échéant.
	 * 
	 * @return true si tous les champs sont valides, sinon false.
	 */

	public boolean validateFieldsAviondisp(String etat, String avionUt, String etatAvion, Label errorLabel) {

		if (etat == null) {
			errorLabel.setText("L'état doit être sélectionné");
			return false;
		}
		if (avionUt == null) {
			errorLabel.setText("Renseigner l'avion");
			return false;
		}

		if (etat.equals(etatAvion)){
			errorLabel.setText("L'avion est déjà dans cet état");
			return false;
		}

		return true;
	}
	/**
	 * Filtre les données dans le tableau en fonction du texte saisi.
	 *
	 * @param searchText Le texte saisi dans le champ de recherche.
	 * @param table      Le tableau à filtrer.
	 */
	public void filterTable(String searchText, TableView<String[]> table) {
		if (stocksMedicaments == null) {
			return;
		}

		if (searchText == null || searchText.isEmpty()) {
			table.setItems(stocksMedicaments);
			return;
		}

		List<String[]> filteredList = stocksMedicaments.stream()
				.filter(row -> row[0].toLowerCase().contains(searchText.toLowerCase())).collect(Collectors.toList());

		table.setItems(FXCollections.observableArrayList(filteredList));
	}

	/**
	 * Filtrer les données de la table des avions en fonction du texte de recherche.
	 * Cette méthode filtre les données de la table des avions en fonction du texte de recherche spécifié.
	 * 
	 * @param searchText Le texte à rechercher dans les données de la table.
	 * @param table La table des avions à filtrer.
	 */

	public void filterTableAvion(String searchText, TableView<String[]> table) {
		if (stocksAvion == null) {
			return;
		}

		if (searchText == null || searchText.isEmpty()) {
			table.setItems(stocksAvion);
			return;
		}

		List<String[]> filteredList = stocksAvion.stream()
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

		mainPane.add(backButton, 0, 50);
		GridPane.setHalignment(backButton, HPos.LEFT);
		GridPane.setMargin(backButton, new Insets(10));
	}

	/**
	 * Regroupe les stocks de médicaments par produit.
	 *
	 * @param stocksMedicamentsList La liste des stocks de médicaments.
	 * @return Une map contenant les stocks groupés par produit.
	 */
	public Map<String, Integer> groupStocksByProduct(List<String[]> stocksMedicamentsList) {
		Map<String, Integer> stockGrouped = new HashMap<>();
		for (String[] medicament : stocksMedicamentsList) {
			String key = medicament[0].trim() + "-" + medicament[1].trim() + "-" + medicament[2].trim();
			int quantite = Integer.parseInt(medicament[4]);
			stockGrouped.merge(key, quantite, Integer::sum);
		}
		return stockGrouped;
	}

	/**
	 * Génère les messages de stock bas.
	 *
	 * @param stockGrouped La map contenant les stocks groupés par produit.
	 * @return Une liste de messages de stock bas.
	 */
	public List<String> generateLowStockMessages(Map<String, Integer> stockGrouped) {
		List<String> lowStockMessages = new ArrayList<>();
		for (Map.Entry<String, Integer> entry : stockGrouped.entrySet()) {
			if (entry.getValue() < 10) {
				int a = 10 - entry.getValue();
				String[] keyParts = entry.getKey().split("-");
				produit.add(keyParts[0]);
				dci.add(keyParts[1]);
				dosage.add(keyParts[2]);
				String message = "• Il manque au stock au moins " + a + " " + keyParts[0] + " avec un dosage de "
						+ keyParts[2];
				lowStockMessages.add(message);
			}
		}
		return lowStockMessages;
	}

	/**
	 * Affiche les messages de stock bas dans le panneau principal.
	 *
	 * @param mainPane         Le panneau principal où afficher les messages.
	 * @param lowStockMessages La liste de messages de stock bas.
	 */
	public void displayLowStockMessages(GridPane mainPane, List<String> lowStockMessages) {
		if (!lowStockMessages.isEmpty()) {
			Label nouveauxMessagesLabel = new Label("Nouveaux messages:");
			nouveauxMessagesLabel.setFont(Font.font("Arial", 18));
			nouveauxMessagesLabel.setStyle("-fx-underline: true;");
			mainPane.add(nouveauxMessagesLabel, 0, 1);
			GridPane.setMargin(nouveauxMessagesLabel, new Insets(10));

			int rowIndex = 20;

			for (int i = 0; i < lowStockMessages.size(); i++) {
				String message = lowStockMessages.get(i);
				Label messageLabel = new Label(message);
				messageLabel.setTextFill(Color.RED);
				messageLabel.setWrapText(true);
				mainPane.add(messageLabel, 0, rowIndex);
				GridPane.setMargin(messageLabel, new Insets(5, 10, 5, 10));

				Spinner<Integer> quantitySpinner = createQuantitySpinner();
				mainPane.add(quantitySpinner, 1, rowIndex);

				Button orderButton = createOrderButton(message, quantitySpinner, i); // Pass current index
				mainPane.add(orderButton, 2, rowIndex);

				setButtonLayout(quantitySpinner, orderButton);

				rowIndex++;
			}
		}
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
	 * Crée un bouton de commande.
	 *
	 * @param message         Le message associé à la commande.
	 * @param quantitySpinner Le spinner de quantité associé à la commande.
	 * @return Le bouton de commande créé.
	 */
	public Button createOrderButton(String message, Spinner<Integer> quantitySpinner, int index) {
		Button orderButton = new Button("Buy");
		orderButton.setOnAction(event -> {
			int quantity = quantitySpinner.getValue();
			Stage popupStage = new Stage();
			popupStage.initModality(Modality.APPLICATION_MODAL);
			popupStage.setTitle("Saisir les informations du médicament");

			GridPane gridPane = new GridPane();
			gridPane.setVgap(20);
			gridPane.setHgap(20);
			gridPane.setPadding(new Insets(20));

			Label errorLabel = new Label();
			errorLabel.setTextFill(Color.RED);
			gridPane.add(errorLabel, 0, 0, 2, 1); 

			Label successLabel = new Label();
			successLabel.setTextFill(Color.GREEN);

			DatePicker datePicker = new DatePicker();
			TextField lotTextField = new TextField();
			TextField numCaisseTextField = new TextField();
			TextField caisseTextField = new TextField();
			TextField classeTextField = new TextField();

			gridPane.addRow(1, new Label("Date limite :"), datePicker);
			gridPane.addRow(2, new Label("Numéro de lot :"), lotTextField);
			gridPane.addRow(3, new Label("Numéro de caisse :"), numCaisseTextField);
			gridPane.addRow(4, new Label("Nom de la caisse :"), caisseTextField);
			gridPane.addRow(5, new Label("Nom de la classe :"), classeTextField);

			Button validerButton = new Button("Valider");
			validerButton.setStyle(
					"-fx-background-color: linear-gradient(#8a2be2, #9370db);-fx-pref-width: 75px;-fx-pref-height: 2px; -fx-text-fill: white; -fx-font-size: 8pt; -fx-background-radius: 5; -fx-padding: 5");
			validerButton.setOnAction(e -> {
				LocalDate dateLimite = datePicker.getValue();
				String lot = lotTextField.getText();
				String numCaisseStr = numCaisseTextField.getText();
				String classe = classeTextField.getText();
				String caisse = caisseTextField.getText();

				if (validerChampsMed(dateLimite, lot, numCaisseStr, classe, caisse, errorLabel)) {
					int numCaisse = Integer.parseInt(numCaisseStr); 
					String produit = this.produit.get(index);
					String dci = this.dci.get(index);
					String dosage = this.dosage.get(index);

					bdd.insererMedicament(produit, dci, dosage, dateLimite, quantity, lot, classe, numCaisse, caisse);

					successLabel.setText("Ajout du médicament " + produit + " réussi");
					gridPane.add(successLabel, 0, 7, 2, 1); 

					new Thread(() -> {
						try {
							Thread.sleep(2000);
							Platform.runLater(() -> {
								popupStage.close();
								afficheVueLogisticien(); 
							});
						} catch (InterruptedException ex) {
							ex.printStackTrace();
						}
					}).start();
				}
			});

			gridPane.addRow(6, validerButton);
			popupStage.setScene(new Scene(gridPane, 450, 350)); 
			popupStage.showAndWait();
		});
		return orderButton;
	}

	/**
	 * Valider les champs pour les médicaments.
	 * Cette méthode vérifie si les champs obligatoires pour les médicaments sont remplis correctement.
	 * 
	 * @param dateLimite La date limite du produit.
	 * @param lot Le numéro de lot du produit.
	 * @param numCaisseStr Le numéro de caisse du produit sous forme de chaîne.
	 * @param classe La classe du produit.
	 * @param caisse Le nom de la caisse du produit.
	 * @param errorLabel Le label d'erreur où afficher les messages d'erreur.
	 * @return true si tous les champs sont valides, false sinon.
	 */

	public boolean validerChampsMed(LocalDate dateLimite, String lot, String numCaisseStr, String classe, String caisse,
			Label errorLabel) {
		if (lot.isEmpty()) {
			errorLabel.setText("Le numéro de lot est requis.");
			return false;
		}

		if (dateLimite == null) {
			errorLabel.setText("La date limite du produit est requise.");
			return false;
		}

		if (dateLimite.isBefore(LocalDate.now())) {
			errorLabel.setText("La date limite du produit ne doit pas être antérieure à la date du jour.");
			return false;
		}

		try {
			int numCaisse = Integer.parseInt(numCaisseStr);
			if (numCaisse <= 0) {
				errorLabel.setText("Le numéro de caisse doit être un entier positif.");
				return false;
			}
		} catch (NumberFormatException e) {
			errorLabel.setText("Le numéro de caisse doit être un nombre valide.");
			return false;
		}

		if (classe.isEmpty()) {
			errorLabel.setText("La classe du produit est requise.");
			return false;
		}

		if (caisse.isEmpty()) {
			errorLabel.setText("Le nom de la caisse est requis.");
			return false;
		}

		return true; // All checks passed
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
		Button visualiserStocksButton = new Button("Visualiser les stocks");
		Button renseignementAttentatButton = new Button("Ajouter Attentat");
		Button visualiserStockAvionButton = new Button("Visualiser avions");
		Button updateStockAvionButton = new Button("Update avions");

		updateStockAvionButton.setOnAction(event -> {
			createAvionPopUp();
		});

		visualiserStocksButton.setOnAction(event -> {
			afficheVueStocksMedicaments();
		});
		renseignementAttentatButton.setOnAction(event -> {
			createAttentatInfoPopup();
		});
		visualiserStockAvionButton.setOnAction(event -> {
			afficheVueStocksAvion();
		});
		mainPane.add(visualiserStocksButton, 80, 1);
		mainPane.add(renseignementAttentatButton, 80, 3); 
		mainPane.add(visualiserStockAvionButton, 80, 2);
		mainPane.add(updateStockAvionButton, 80, 4);

		GridPane.setHalignment(visualiserStocksButton, HPos.RIGHT);
		GridPane.setHalignment(visualiserStockAvionButton, HPos.RIGHT);
		GridPane.setHalignment(renseignementAttentatButton, HPos.RIGHT);
		GridPane.setHalignment(updateStockAvionButton, HPos.RIGHT);

		GridPane.setMargin(visualiserStocksButton, new Insets(1));
		GridPane.setMargin(renseignementAttentatButton, new Insets(1));
		GridPane.setMargin(visualiserStockAvionButton, new Insets(1));
		GridPane.setMargin(updateStockAvionButton, new Insets(1));
	}

	/**
	 * Crée une fenêtre contextuelle pour saisir les informations sur un attentat.
	 * Cette méthode affiche une fenêtre contextuelle modale pour saisir les détails d'un attentat, tels que la date, le lieu, le nombre total de blessés et le nombre à soigner.
	 */

	private void createAttentatInfoPopup() {
		Stage popupStage = new Stage();
		popupStage.initModality(Modality.APPLICATION_MODAL);
		popupStage.setTitle("Saisir les informations de l'attentat");

		GridPane gridPane = new GridPane();
		gridPane.setVgap(10);
		gridPane.setHgap(10);
		gridPane.setPadding(new Insets(20));

		Label errorLabel = new Label();
		errorLabel.setTextFill(Color.RED); 
		gridPane.add(errorLabel, 0, 0, 2, 1); 

		Label successLabel = new Label();
		successLabel.setTextFill(Color.GREEN);

		DatePicker dateAttentatTextField = new DatePicker();
		TextField lieuTextField = new TextField();
		TextField totBlessesTextField = new TextField();
		TextField nbAsoignerTextField = new TextField();

		gridPane.addRow(1, new Label("Date de l'attentat :"), dateAttentatTextField);
		gridPane.addRow(2, new Label("Lieu de l'attentat :"), lieuTextField);
		gridPane.addRow(3, new Label("Total de blessés :"), totBlessesTextField);
		gridPane.addRow(4, new Label("Nombre à soigner :"), nbAsoignerTextField);

		Button validerButton = new Button("Valider");
		validerButton.setStyle(
				"-fx-background-color: linear-gradient(#8a2be2, #9370db);-fx-pref-width: 75px;-fx-pref-height: 2px; -fx-text-fill: white; -fx-font-size: 8pt; -fx-background-radius: 5; -fx-padding: 5");
		validerButton.setOnAction(e -> {
			LocalDate dateAttentat = dateAttentatTextField.getValue();
			String lieu = lieuTextField.getText();
			String totBlessesStr = totBlessesTextField.getText();
			String nbAsoignerStr = nbAsoignerTextField.getText();

			if (validateFields(dateAttentat, lieu, totBlessesStr, nbAsoignerStr, errorLabel)) {
				int totBlesses = Integer.parseInt(totBlessesStr);
				int nbAsoigner = Integer.parseInt(nbAsoignerStr);

				bdd.insererAttentat(lieu, totBlesses, nbAsoigner, dateAttentat);

				successLabel.setText("Ajout de l'attentat à " + lieu + " réussi");
				gridPane.add(successLabel, 0, 6, 2, 1); 

				new Thread(() -> {
					try {
						Thread.sleep(2000);
						Platform.runLater(() -> {
							popupStage.close();
							afficheVueLogisticien(); 
						});
					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}
				}).start();
			}
		});

		gridPane.addRow(5, validerButton);
		popupStage.setScene(new Scene(gridPane, 450, 350)); 
		popupStage.showAndWait();
	}

	/**
	 * Valide les champs saisis pour un attentat.
	 * Cette méthode vérifie si les champs requis pour un attentat sont correctement saisis.
	 *
	 * @param date          La date de l'attentat.
	 * @param lieu          Le lieu de l'attentat.
	 * @param totalBlessesStr   Le nombre total de blessés.
	 * @param toTreatStr    Le nombre à soigner.
	 * @param errorLabel    Le label où afficher les messages d'erreur le cas échéant.
	 * @return              True si les champs sont valides, false sinon.
	 */

	public boolean validateFields(LocalDate date, String lieu, String totalBlessesStr, String toTreatStr,
			Label errorLabel) {
		if (date == null) {
			errorLabel.setText("La date de l'attentat est requise.");
			return false;
		}

		if (date.isAfter(LocalDate.now())) {
			errorLabel.setText("La date de l'attentat ne doit pas être postérieure à la date du jour.");
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
			if (totalBlesses < toTreat) {
				errorLabel.setText(
						"Les nombres de blessés à soigner doit être égal ou plus petit que le total des blessés.");
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
		for (int i = 0; i < 9; i++) {
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
		table.getItems().addAll(stocksMedicaments);
		return table;
	}

	/**
	 * Crée une TableView pour afficher les informations des avions.
	 * Cette méthode crée une TableView avec les colonnes appropriées pour afficher les informations des avions.
	 *
	 * @return              La TableView créée pour afficher les informations des avions.
	 */
	
	public TableView<String[]> createTableViewAvion() {
		TableView<String[]> table = new TableView<>();
		for (int i = 0; i < 17; i++) {
			TableColumn<String[], String> column = new TableColumn<>(nomsColonnesAvions[i]);
			int columnIndex = i;

			column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[columnIndex].trim()));

			column.setPrefWidth(200);

			table.getColumns().add(column);
		}
		table.getItems().addAll(stocksAvion);
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
		searchField.setPromptText("Rechercher un médicament");

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
	 * Crée un champ de recherche pour filtrer les données dans une TableView d'avions.
	 * Cette méthode crée un champ de texte où l'utilisateur peut saisir du texte pour filtrer les données affichées dans une TableView d'avions.
	 *
	 * @param table         La TableView d'avions à filtrer.
	 * @return              Le champ de recherche créé.
	 */

	public TextField createSearchFieldAvion(TableView<String[]> table) {
		TextField searchField = new TextField();
		searchField.setPromptText("Rechercher un Avion");

		searchField.textProperty().addListener((observable, oldValue, newValue) -> {
			filterTableAvion(newValue, table);
		});

		searchField.setOnAction(event -> {
			String searchText = searchField.getText().trim();
			filterTableAvion(searchText, table);
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
			afficheVueLogisticien();
			this.close();
		});
		return backButton;
	}

}
