package model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.h2.jdbcx.JdbcDataSource;

/**
 * Cette classe gère les interactions avec la base de données.
 */
public class BDD {
	private Connection dbConnection;
	private PreparedStatement insertionUtilisateur;
	private PreparedStatement selectionUtilisateurParNom;

	/**
	 * Constructeur de la classe BDD. Initialise la connexion à la base de données
	 * et prépare les requêtes SQL.
	 */
	public BDD() {
		openDBConnection();
		prepareStatements();
	}

	/**
	 * Ouvre une connexion à la base de données.
	 */
	public void openDBConnection() {
		JdbcDataSource dataSource = new JdbcDataSource();
		dataSource.setURL("jdbc:h2:tcp://localhost/~/test");
		dataSource.setUser("sa");
		dataSource.setPassword("");

		try {
			dbConnection = dataSource.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Prépare les requêtes SQL à exécuter.
	 */
	public void prepareStatements() {
		try {
			insertionUtilisateur = dbConnection.prepareStatement(
					"INSERT INTO personne (identifiant, prénom, nom, date_naissance, mdp, statut) VALUES (?, ?, ?, ?, ?, ?)");
			selectionUtilisateurParNom = dbConnection.prepareStatement("SELECT * FROM personne WHERE identifiant = ?");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Insère un nouvel utilisateur dans la base de données.
	 * 
	 * @param identifiant   Identifiant de l'utilisateur.
	 * @param prenom        Prénom de l'utilisateur.
	 * @param nom           Nom de l'utilisateur.
	 * @param dateNaissance Date de naissance de l'utilisateur.
	 * @param mdp           Mot de passe de l'utilisateur.
	 * @param statut        Statut de l'utilisateur.
	 */
	public void insererUtilisateur(String identifiant, String prenom, String nom, LocalDate dateNaissance, String mdp,
			String statut) {
		try {
			insertionUtilisateur.setString(1, identifiant);
			insertionUtilisateur.setString(2, prenom);
			insertionUtilisateur.setString(3, nom);
			insertionUtilisateur.setDate(4, Date.valueOf(dateNaissance));
			insertionUtilisateur.setString(5, mdp);
			insertionUtilisateur.setString(6, statut);
			
			insertionUtilisateur.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Stocke les informations d'un utilisateur identifié par son identifiant.
	 * 
	 * @param identifiant Identifiant de l'utilisateur à rechercher.
	 * @return Un tableau de String contenant les informations de l'utilisateur s'il
	 *         existe, sinon un tableau vide.
	 */
	public String[] stockerUtilisateurParIdentifiant(String identifiant) {
		String[] res = new String[6];
		try {
			PreparedStatement selectionUtilisateurParIdentifiant = dbConnection.prepareStatement(
					"SELECT identifiant, `prénom`, nom,  date_naissance, mdp, statut FROM personne WHERE identifiant = ?");
			selectionUtilisateurParIdentifiant.setString(1, identifiant);
			ResultSet rs = selectionUtilisateurParIdentifiant.executeQuery();
			boolean existe = false;
			while (rs.next()) {
				existe = true;
				res[1] = rs.getString("prénom");
				res[2] = rs.getString("nom");
				res[3] = rs.getDate("date_naissance").toString();
				res[4] = rs.getString("mdp");
				res[5] = rs.getString("statut");
				
			}
			res[0] = String.valueOf(existe);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * Stocke les informations d'un utilisateur à partir de son nom.
	 * 
	 * @param nom Nom de l'utilisateur à rechercher.
	 * @return Un tableau de String contenant les informations de l'utilisateur s'il
	 *         existe, sinon un tableau vide.
	 */
	public String[] stockerUtilisateurAPartirNom(String nom) {
		String[] res = new String[4];
		try {
			selectionUtilisateurParNom.setString(1, nom);
			ResultSet rs = selectionUtilisateurParNom.executeQuery();
			boolean existe = false;
			while (rs.next()) {
				existe = true;
				res[1] = rs.getString("prénom");
				res[2] = rs.getString("mdp");
				res[3] = rs.getString("statut");
			}
			res[0] = String.valueOf(existe);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * Vérifie l'existence d'un identifiant et d'une date de naissance dans la base
	 * de données.
	 * 
	 * @param identifiant   Identifiant de l'utilisateur.
	 * @param dateNaissance Date de naissance de l'utilisateur.
	 * @return True si l'identifiant et la date de naissance correspondent à un
	 *         utilisateur dans la base de données, sinon False.
	 */
	public boolean verifierIdentifiantEtDateNaissance(String identifiant, Date dateNaissance) {
		try {
			PreparedStatement statement = dbConnection
					.prepareStatement("SELECT COUNT(*) FROM personne WHERE identifiant = ? AND date_naissance = ?");
			statement.setString(1, identifiant);
			statement.setDate(2, dateNaissance);
			ResultSet resultSet = statement.executeQuery();
			resultSet.next();
			int count = resultSet.getInt(1);
			return count > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Récupère les stocks de médicaments depuis la base de données.
	 * 
	 * @return Une liste de tableaux de String, où chaque tableau représente les
	 *         informations d'un médicament.
	 */
	public List<String[]> recupererStocksMedicaments() {
		List<String[]> stocksMedicaments = new ArrayList<>();
		try {
			PreparedStatement statement = dbConnection.prepareStatement(
					"SELECT PRODUIT, DCI, DOSAGE, DLU, QUANTITÉ, LOT, CLASSE, NUM_CAISSE, CAISSE FROM médicament");
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				String[] medicament = new String[9];
				for (int i = 0; i < 9; i++) {
					medicament[i] = resultSet.getString(i + 1);
				}
				stocksMedicaments.add(medicament);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return stocksMedicaments;
	}
	
	public List<String[]> recupererStocksAvions(){
		List<String[]> stocksAvion = new ArrayList<>();
		try {
			PreparedStatement statement = dbConnection.prepareStatement(
					"SELECT NOM, CONSTRUCTEUR, TYPE_MOTEUR, TYPE_DE_VOL,TONNE_MAX,TAILLE_PORTE_CM,DIMENSIONS_SOUTE_CM,VOLUME_UTILISABLE_M3,EXIGENCE_PISTE_M,PORTEE_CHARGE_KM,PORTEE_VIDE_KM,VITESSE_CROISIERE_KMH,CONSOMMATION_CARBURANT_LH,POSITIONS_PALETTES,etat,lieu_attentat,date_attentat FROM avion");
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				String[] avion = new String[17];
				for (int i = 0; i < 17; i++) {
					avion[i] = resultSet.getString(i + 1);
				}
				stocksAvion.add(avion);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return stocksAvion;
	}
	
	public int getMedicamentStock(String nom, String dosage, LocalDate dlu) {
	    try {
	        PreparedStatement statement = dbConnection.prepareStatement(
	            "SELECT QUANTITÉ FROM médicament WHERE PRODUIT = ? AND DOSAGE = ? AND DLU = ?");
	        statement.setString(1, nom);
	        statement.setString(2, dosage);
	        statement.setDate(3, Date.valueOf(dlu));
	        ResultSet resultSet = statement.executeQuery();
	        if (resultSet.next()) {
	            return resultSet.getInt("QUANTITÉ");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return -1; // Retourne -1 en cas d'erreur ou si le médicament n'est pas trouvé
	}
	
	public boolean updateMedicamentStock(String nom, String dosage, LocalDate dlu, int newQuantity) {
	    try {
	        PreparedStatement statement = dbConnection.prepareStatement(
	            "UPDATE médicament SET QUANTITÉ = ? WHERE PRODUIT = ? AND DOSAGE = ? AND DLU = ?");
	        statement.setInt(1, newQuantity);
	        statement.setString(2, nom);
	        statement.setString(3, dosage);
	        statement.setDate(4, Date.valueOf(dlu));
	        int affectedRows = statement.executeUpdate();
	        return affectedRows > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}

	public boolean updateAvion(String avionNom, String etat, String lieuAttentat, LocalDate dateAttentat) {
	    try {
	        // Prépare la requête SQL pour mettre à jour les informations de l'avion
	        PreparedStatement statement = dbConnection.prepareStatement(
	            "UPDATE avion SET etat = ?, lieu_attentat = ?, date_attentat = ? WHERE nom = ?");

	        // Associe les valeurs aux paramètres de la requête
	        statement.setString(1, etat);
	        statement.setString(2, lieuAttentat);
	        statement.setDate(3, Date.valueOf(dateAttentat));
	        statement.setString(4, avionNom);

	        // Exécute la mise à jour et vérifie si les lignes sont affectées
	        int affectedRows = statement.executeUpdate();
	        return affectedRows > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}

	
	/**
	 * Modifie le mot de passe d'un utilisateur dans la base de données.
	 * 
	 * @param identifiant Identifiant de l'utilisateur dont le mot de passe doit
	 *                    être modifié.
	 * @param nouveauMdp  Nouveau mot de passe.
	 * @return True si la modification a réussi, sinon False.
	 */
	public boolean modifierMotDePasse(String identifiant, String nouveauMdp) {
		try {
			PreparedStatement statement = dbConnection
					.prepareStatement("UPDATE personne SET mdp = ? WHERE identifiant = ?");
			statement.setString(1, nouveauMdp);
			statement.setString(2, identifiant);
			int rowsAffected = statement.executeUpdate();
			return rowsAffected > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Permet d'insérer un nouveau médicament dans la base de données.
	 * 
	 * @param les nouvelles informations du produits rentrées dans la base de données
	 * @void Ajoute l'élément à la base de données
	 */
	public void insererMedicament(String produit, String dci, String dosage, LocalDate dateLimite, int quantity, String lot, String classe, int numCaisse, String caisse) {
	    try {
	        PreparedStatement insertionMedicament = dbConnection.prepareStatement(
	            "INSERT INTO médicament (PRODUIT, DCI, DOSAGE, DLU, QUANTITÉ, LOT, CLASSE, NUM_CAISSE, CAISSE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
	        insertionMedicament.setString(1, produit);
	        insertionMedicament.setString(2, dci);
	        insertionMedicament.setString(3, dosage);
	        insertionMedicament.setDate(4, Date.valueOf(dateLimite));
	        insertionMedicament.setInt(5, quantity);
	        insertionMedicament.setString(6, lot);
	        insertionMedicament.setString(7, classe);
	        insertionMedicament.setInt(8, numCaisse);
	        insertionMedicament.setString(9, caisse);

	        System.out.println("Executing query: " + insertionMedicament);
	        int result = insertionMedicament.executeUpdate();
	        System.out.println("Result: " + result);
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	/**
	 * Permet d'insérer les informations de l'attentat dans la base de données.
	 * 
	 * @param les nouvelles informations de l'attentat sont rentrées dans la base de données
	 * @void Ajoute l'évènement à la base de données
	 */
	
	public void insererAttentat(String lieu, int tot_blesses, int pers_à_soigner, LocalDate date_evenement) {
	    try {
	        PreparedStatement insertionAttentat = dbConnection.prepareStatement(
	            "INSERT INTO Attentat (lieu, Tot_blessés, Pers_à_soigner, date_evenement) VALUES (?, ?, ?, ?)");
	        insertionAttentat.setString(1, lieu);
	        insertionAttentat.setInt(2, tot_blesses);
	        insertionAttentat.setInt(3, pers_à_soigner);
	        insertionAttentat.setDate(4, Date.valueOf(date_evenement));

	        System.out.println("Executing query: " + insertionAttentat);
	        int result = insertionAttentat.executeUpdate();
	        System.out.println("Result: " + result);
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	public String insererPrescription(String prenom, String nom, String nom_medicament, int quantity, String id_med, String infoAttentat) {
	    try {
	        // Supposons que les détails du médicament sont correctement extraits ici
	        String[] details = nom_medicament.split(" ; ");
	        String produit = details[0];
	        String dosage = details[1];
	        LocalDate dlu = LocalDate.parse(details[2]);
	        String[] info = infoAttentat.split(" ; ");
	        String lieuAttentat = info[0];
	        LocalDate dateAttentat=  LocalDate.parse(info[1]);

	        int currentStock = getMedicamentStock(produit, dosage, dlu);
	        if (currentStock >= quantity) {
	            updateMedicamentStock(produit, dosage, dlu, currentStock - quantity);
	            
	            PreparedStatement insertionPrescription = dbConnection.prepareStatement(
	                "INSERT INTO prescription (PRéNOM, NOM, Id_MEDECIN, NOM_MEDICAMENT, QUANTITÉ, DATE_PRESCRIPTION, lieu_Attentat, date_Attentat) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
	            insertionPrescription.setString(1, prenom);
	            insertionPrescription.setString(2, nom);
	            insertionPrescription.setString(3, id_med);
	            insertionPrescription.setString(4, nom_medicament);
	            insertionPrescription.setInt(5, quantity);
	            insertionPrescription.setDate(6, Date.valueOf(LocalDate.now()));
	            insertionPrescription.setString(7, lieuAttentat);
	            insertionPrescription.setDate(8, Date.valueOf(dateAttentat));
	            insertionPrescription.executeUpdate();
	 
	            decrementBlessesRestants(lieuAttentat, dateAttentat);
	            return "Success";
	        } else {
	           return "Il n'y a que " + currentStock+ " produit(s) disponibles";
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return "Erreur de base de données.";
	    }
	}
	
	
	public void decrementBlessesRestants(String lieuAttentat, LocalDate dateAttentat) throws SQLException {
	    PreparedStatement updateAttentat = dbConnection.prepareStatement(
	        "UPDATE attentat SET Pers_à_soigner = Pers_à_soigner - 1 WHERE lieu = ? AND date_evenement = ?");
	    updateAttentat.setString(1, lieuAttentat);
	    updateAttentat.setDate(2, Date.valueOf(dateAttentat));
	    updateAttentat.executeUpdate();
	}
	

	public List<String[]> recupererListeAttentat() {
		List<String[]> listeAttentats = new ArrayList<>();
		try {
			PreparedStatement statement = dbConnection.prepareStatement(
					"SELECT lieu, Tot_blessés, Pers_à_soigner, date_evenement FROM attentat");
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				String[] attentat = new String[4];
				for (int i = 0; i < 4; i++) {
					attentat[i] = resultSet.getString(i + 1);
				}
				listeAttentats.add(attentat);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return listeAttentats;
	}
	

	
	public List<String[]> recupererListePrescription(){
		List<String[]> listePrescriptions = new ArrayList<>();
		try {
			PreparedStatement statement = dbConnection.prepareStatement(
					"SELECT prénom, nom, ID_MEDECIN, nom_medicament, quantité, date_prescription, lieu_Attentat, date_Attentat FROM prescription");
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				String[] prescription = new String[8];
				for (int i = 0; i < 8; i++) {
					prescription[i] = resultSet.getString(i + 1);
				}
				listePrescriptions.add(prescription);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return listePrescriptions;
	}
	
	public List<String[]> recupererPrescriptionsParPatient(String prenom, String nom) {
	    List<String[]> prescriptions = new ArrayList<>();
	    try {
	        PreparedStatement statement = dbConnection.prepareStatement(
	            "SELECT DATE_PRESCRIPTION, Id_MEDECIN, NOM_MEDICAMENT, QUANTITÉ, lieu_Attentat, date_Attentat FROM prescription WHERE PRéNOM = ? AND NOM = ?");
	        statement.setString(1, prenom);
	        statement.setString(2, nom);
	        ResultSet resultSet = statement.executeQuery();
	        while (resultSet.next()) {
	            String[] prescription = new String[6];
	            prescription[0] = resultSet.getDate("DATE_PRESCRIPTION").toString();
	            prescription[1] = resultSet.getString("Id_MEDECIN");
	            prescription[2] = resultSet.getString("NOM_MEDICAMENT");
	            prescription[3] = String.valueOf(resultSet.getInt("QUANTITÉ"));
	            prescription[4] = resultSet.getString("lieu_Attentat");
	            prescription[5] = resultSet.getDate("date_Attentat").toString();
	            prescriptions.add(prescription);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return prescriptions;
	}

	public boolean prescriptionExiste(String prenom, String nom) {
	    try {
	        PreparedStatement statement = dbConnection.prepareStatement(
	            "SELECT COUNT(*) FROM prescription WHERE PRéNOM = ? AND NOM = ? ");
	        statement.setString(1, prenom);
	        statement.setString(2, nom);
	        ResultSet resultSet = statement.executeQuery();
	        if (resultSet.next()) {
	            return resultSet.getInt(1) > 0;  // retourne vrai si au moins une entrée existe
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;  // retourne faux si aucune entrée trouvée ou erreur
	}


	
}
