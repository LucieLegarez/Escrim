package control;

public class SessionController {
    private static SessionController instance = new SessionController();
    private String userId;

    private SessionController() {}

    public static SessionController getInstance() {
        return instance;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
