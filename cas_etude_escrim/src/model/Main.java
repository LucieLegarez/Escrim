package model;

import javafx.application.Application;
import javafx.stage.Stage;
import view.Logiciel;

public class Main extends Application {
    private Logiciel logiciel;

    public void start(Stage primaryStage) {
        try {
            this.logiciel = new Logiciel(primaryStage);
            this.logiciel.afficheVueAccueil();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
