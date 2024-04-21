package view;

import java.time.LocalDate;
import javafx.scene.layout.ColumnConstraints;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
import model.BDD;

/**
 * Classe représentant la vue du logisticien.
 */
public class LogisticienView extends Stage {
	
    private Label errorLabel; // To display error messages
	private Stage primaryStage;
	private final String[] nomsColonnes = { "PRODUIT", "DCI", "DOSAGE", "DLU", "QUANTITÉ", "LOT", "CLASSE",
			"NUM_CAISSE", "CAISSE" };
	private final BDD bdd;
	private ObservableList<String[]> stocksMedicaments;
	private List<String> produit;
	private List<String> dci;
	private List<String> dosage;
	private Label successMessageLabel;

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
		errorLabel = new Label(); // Initialize the error label
        errorLabel.setTextFill(Color.RED); // Set error text color
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

			int rowIndex = 3;

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

	        // Initialize the error label and add it to the grid
	        Label errorLabel = new Label();
	        errorLabel.setTextFill(Color.RED);  // Set the text color to red for visibility
	        gridPane.add(errorLabel, 0, 0, 2, 1);  // Span across two columns

	        // Initialize the success label but do not add to grid yet
	        Label successLabel = new Label();
	        successLabel.setTextFill(Color.GREEN);

	        // Other UI components
	        DatePicker datePicker = new DatePicker();
	        TextField lotTextField = new TextField();
	        TextField numCaisseTextField = new TextField();
	        TextField caisseTextField = new TextField();
	        TextField classeTextField = new TextField();

	        // Add components to the grid
	        gridPane.addRow(1, new Label("Date limite :"), datePicker);
	        gridPane.addRow(2, new Label("Numéro de lot :"), lotTextField);
	        gridPane.addRow(3, new Label("Numéro de caisse :"), numCaisseTextField);
	        gridPane.addRow(4, new Label("Nom de la caisse :"), caisseTextField);
	        gridPane.addRow(5, new Label("Nom de la classe :"), classeTextField);

	        Button validerButton = new Button("Valider");
	        validerButton.setOnAction(e -> {
	            LocalDate dateLimite = datePicker.getValue();
	            String lot = lotTextField.getText();
	            String numCaisseStr = numCaisseTextField.getText();
	            String classe = classeTextField.getText();
	            String caisse = caisseTextField.getText();

	            if (validerChampsMed(dateLimite, lot, numCaisseStr, classe, caisse, errorLabel)) {
	                int numCaisse = Integer.parseInt(numCaisseStr); // Convert numCaisse here after validation
	                String produit = this.produit.get(index);
	                String dci = this.dci.get(index);
	                String dosage = this.dosage.get(index);

	                // Insert into the database
	                bdd.insererMedicament(produit, dci, dosage, dateLimite, quantity, lot, classe, numCaisse, caisse);
	                
	                // Display success message
	                successLabel.setText("Ajout du médicament " + produit + " réussi");
	                gridPane.add(successLabel, 0, 7, 2, 1); // Span across both columns

	                // Close popup after a delay
	                new Thread(() -> {
	                    try {
	                        Thread.sleep(2000);
	                        Platform.runLater(() -> {
	                            popupStage.close();
	                            afficheVueLogisticien();  // Refresh the logistician view to reflect the updated stock
	                        });
	                    } catch (InterruptedException ex) {
	                        ex.printStackTrace();
	                    }
	                }).start();
	            }
	        });

	        gridPane.addRow(6, validerButton);
	        popupStage.setScene(new Scene(gridPane, 450, 350)); // Adjust the scene size if necessary
	        popupStage.showAndWait();
	    });
	    return orderButton;
	}

	
	public boolean validerChampsMed(LocalDate dateLimite, String lot, String numCaisseStr, String classe, String caisse, Label errorLabel) {
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

	    visualiserStocksButton.setOnAction(event -> {
	        afficheVueStocksMedicaments();
	    });
	    renseignementAttentatButton.setOnAction(event -> {
	        createAttentatInfoPopup();
	    });

	    mainPane.add(visualiserStocksButton, 80, 0); // Keep existing position for one button
	    mainPane.add(renseignementAttentatButton, 80, 1); // Place the second button below the first

	    GridPane.setHalignment(visualiserStocksButton, HPos.RIGHT);
	    GridPane.setHalignment(renseignementAttentatButton, HPos.RIGHT);

	    GridPane.setMargin(visualiserStocksButton, new Insets(1));
	    GridPane.setMargin(renseignementAttentatButton, new Insets(1));
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
	    errorLabel.setTextFill(Color.RED);  // Set the text color to red for visibility
	    gridPane.add(errorLabel, 0, 0, 2, 1);  // Span across two columns at the top of the grid

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
	            gridPane.add(successLabel, 0, 6, 2, 1);  // Span across both columns

	            // Close popup after a delay
	            new Thread(() -> {
	                try {
	                    Thread.sleep(2000);
	                    Platform.runLater(() -> {
	                        popupStage.close();
	                        afficheVueLogisticien();  // Refresh the logistician view to reflect the updated stock
	                    });
	                } catch (InterruptedException ex) {
	                    ex.printStackTrace();
	                }
	            }).start();
	        }
	    });

	    gridPane.addRow(5, validerButton);
	    popupStage.setScene(new Scene(gridPane, 450, 350));  // Adjust the scene size if necessary
	    popupStage.showAndWait();
	}

	public boolean validateFields(LocalDate date, String lieu, String totalBlessesStr, String toTreatStr, Label errorLabel) {
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
	        if (totalBlesses < toTreat ) {
	            errorLabel.setText("Les nombres de blessés à soigner doit être égal ou plus petit que le total des blessés.");
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

