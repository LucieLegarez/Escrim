package model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
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
					"INSERT INTO personne (identifiant, prénom, nom, date_naissance, mdp, statut ) VALUES (?, ?, ?, ?, ?, ?)");
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

	public void insererMedicament(String produit, String dci, String dosage, LocalDate dateLimite, int quantity, String lot, String classe, String numCaisse, String caisse) {
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
	        insertionMedicament.setString(8, numCaisse);
	        insertionMedicament.setString(9, caisse);

	        System.out.println("Executing query: " + insertionMedicament);
	        int result = insertionMedicament.executeUpdate();
	        System.out.println("Result: " + result);
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}


}
