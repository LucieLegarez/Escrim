package control;

/**
 * Contrôleur de session utilisateur.
 */
public class SessionController {
    private static SessionController instance = new SessionController();
    private String userId;

    private SessionController() {}

    /**
     * Obtient l'instance unique du contrôleur de session.
     * 
     * @return Instance unique du contrôleur de session
     */
    public static SessionController getInstance() {
        return instance;
    }

    /**
     * Obtient l'identifiant de l'utilisateur actuellement connecté.
     * 
     * @return Identifiant de l'utilisateur actuellement connecté
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Définit l'identifiant de l'utilisateur actuellement connecté.
     * 
     * @param userId Identifiant de l'utilisateur actuellement connecté
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }
}
