module com.project.testray {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires javafx.graphics;

    opens com.project.testray to javafx.fxml;
    exports com.project.testray;
    exports com.project.testray.entyties;
    opens com.project.testray.entyties to javafx.fxml;
    exports com.project.testray.render;
    opens com.project.testray.render to javafx.fxml;
}