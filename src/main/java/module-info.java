module com.audiostenographyspreadspectrum {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;

    opens com.audiostenographyspreadspectrum to javafx.fxml;
    exports com.audiostenographyspreadspectrum;
}