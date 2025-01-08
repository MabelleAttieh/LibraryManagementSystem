module Hello {
    requires javafx.controls;
    requires javafx.fxml;
	requires java.sql;

    // Export the 'application' package so that JavaFX can access it
    exports application;
}
