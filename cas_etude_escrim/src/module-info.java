module cas_etude_escrim {
	requires javafx.controls;
	requires com.h2database;
	requires java.sql;
	requires javafx.base;
	requires javafx.graphics;
	
	opens model to javafx.graphics, javafx.fxml;
}
