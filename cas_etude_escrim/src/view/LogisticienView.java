package view;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.BDD;

/**
 * Classe représentant la vue du logisticien.
 */
public class LogisticienView extends Stage {

	private Stage primaryStage;
	private final String[] nomsColonnes = { "PRODUIT", "DCI", "DOSAGE", "DLU", "QUANTITÉ", "LOT", "CLASSE",
			"NUM_CAISSE", "CAISSE" };
	private final BDD bdd;
	private ObservableList<String[]> stocksMedicaments;

	/**
	 * Constructeur de la vue du logisticien.
	 *
	 * @param primaryStage La fenêtre principale de l'application.
	 */
	public LogisticienView(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.bdd = new BDD();
	}

	/**
	 * Affiche la vue du logisticien.
	 */
	public void afficheVueLogisticien() {
		GridPane mainPane = createMainPane();
		addBackButton(mainPane);

		List<String[]> stocksMedicamentsList = bdd.recupererStocksMedicaments();
		Map<String, Integer> stockGrouped = groupStocksByProduct(stocksMedicamentsList);
		List<String> lowStockMessages = generateLowStockMessages(stockGrouped);

		displayLowStockMessages(mainPane, lowStockMessages);

		addVisualiserStocksButton(mainPane);

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

		mainPane.add(backButton, 0, 65);
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
				String[] keyParts = entry.getKey().split("-");
				String message = "• Le stock de " + keyParts[0] + " avec un dosage de " + keyParts[2]
						+ " est insuffisant.";
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

			int rowIndex = 3;

			for (String message : lowStockMessages) {
				Label messageLabel = new Label(message);
				messageLabel.setTextFill(Color.RED);
				messageLabel.setWrapText(true);
				mainPane.add(messageLabel, 0, rowIndex);
				GridPane.setMargin(messageLabel, new Insets(5, 10, 5, 10));

				Spinner<Integer> quantitySpinner = createQuantitySpinner();
				mainPane.add(quantitySpinner, 1, rowIndex);

				Button orderButton = createOrderButton(message, quantitySpinner);
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
	public Button createOrderButton(String message, Spinner<Integer> quantitySpinner) {
		Button orderButton = new Button("Buy");
		orderButton.setOnAction(event -> {
			int quantity = quantitySpinner.getValue();
			// A COMPLETER
		});
		return orderButton;
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
	 * Ajoute le bouton pour visualiser les stocks au panneau principal.
	 *
	 * @param mainPane Le panneau principal où ajouter le bouton.
	 */
	public void addVisualiserStocksButton(GridPane mainPane) {
		Button visualiserStocksButton = new Button("Visualiser les stocks");
		visualiserStocksButton.setOnAction(event -> {
			afficheVueStocksMedicaments();
		});
		mainPane.add(visualiserStocksButton, 80, 0);
		GridPane.setHalignment(visualiserStocksButton, HPos.RIGHT);
		GridPane.setMargin(visualiserStocksButton, new Insets(1));
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
