package com.audiostenographyspreadspectrum;

import com.audiostenographyspreadspectrum.spreadspectrum.Decoder;
import com.audiostenographyspreadspectrum.spreadspectrum.Encoder;
import com.audiostenographyspreadspectrum.audio.AudioDevice;
import com.audiostenographyspreadspectrum.audio.Common;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.StageStyle;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class AudioStenographyApplication extends Application {

    Stage currentState;

    public AudioStenographyApplication() {
    }

    void drawWaveForm(GraphicsContext gc, File file, ArrayList<Float> spread) throws Exception {
        float width = 1000.0F;
        int height = 80;
        int halfHeight = height / 2;
        float f = 1.0F;
        ArrayList<Float> wave = new ArrayList<>();
        if (file != null) {
            wave = Common.getWaveFromAudio(file);
            f = width / (float)wave.size();
        }
        if (spread != null) {
            f = 2.0F;
            wave = spread;
        }
        gc.setStroke(Color.RED);

        gc.clearRect(0.0D, 0.0D, (double)width, (double)height);
        gc.setLineWidth(1.0D);
        float x = 0.0F;
        
        for(int i = 0; i < wave.size() - 500; ++i) {
            gc.strokeLine((double)x, (double)halfHeight, (double)x, (double)((float)halfHeight + (Float)wave.get(i) * (float) halfHeight));
            x += f;
        }

    }

    Tab initTabEncode() throws Exception {
        Pane root = new Pane();
        root.setStyle("-fx-background-color: #282c34");
        Label passwordLabel = new Label("Nhập khóa: ");
        passwordLabel.setStyle("-fx-text-fill:white");
        passwordLabel.setLayoutX(480.0D);
        passwordLabel.setLayoutY(70.0D);
        final TextField passTextField = new TextField();
        passTextField.setLayoutX(480.0D);
        passTextField.setLayoutY(90.0D);
        passTextField.setPrefWidth(140.0D);
        Label messageLabel = new Label("Nhập tin nhắn: ");
        messageLabel.setStyle("-fx-text-fill:white");
        messageLabel.setLayoutX(20.0D);
        messageLabel.setLayoutY(70.0D);
        final TextArea textArea = new TextArea();
        textArea.setLayoutX(20.0D);
        textArea.setLayoutY(90.0D);
        textArea.setPrefWidth(400.0D);
        textArea.setPrefHeight(100.0D);
        final Label fileLabel = new Label("Chưa chọn file nào");
        fileLabel.setStyle("-fx-text-fill:white");
        fileLabel.setLayoutX(20.0D);
        fileLabel.setLayoutY(20.0D);
        Button selectFileBtn = new Button("Chọn file");
        selectFileBtn.setLayoutX(20.0D);
        selectFileBtn.setLayoutY(40.0D);
        final Button encodeButton = new Button("Mã hóa tệp tin");
        encodeButton.setLayoutX(480.0D);
        encodeButton.setLayoutY(140.0D);
        encodeButton.setDisable(true);
        final Button originalSoundSignalBtn = new Button("Phát âm thanh gốc");
        originalSoundSignalBtn.setLayoutX(20.0D);
        originalSoundSignalBtn.setLayoutY(220.0D);
        originalSoundSignalBtn.setOpacity(0.0D);
        final Button spreadSoundSignalBtn = new Button("Phát tín hiệu chèn");
        spreadSoundSignalBtn.setLayoutX(20.0D);
        spreadSoundSignalBtn.setLayoutY(330.0D);
        spreadSoundSignalBtn.setOpacity(0.0D);
        final Button encodeSoundSignalBtn = new Button("Phát âm thanh mã hóa");
        encodeSoundSignalBtn.setLayoutX(20.0D);
        encodeSoundSignalBtn.setLayoutY(450.0D);
        encodeSoundSignalBtn.setOpacity(0.0D);
        Canvas originalSoundCanvas = new Canvas(800.0D, 80.0D);
        originalSoundCanvas.setLayoutX(20.0D);
        originalSoundCanvas.setLayoutY(240.0D);
        Canvas spreadSignalCanvas = new Canvas(800.0D, 80.0D);
        spreadSignalCanvas.setLayoutX(20.0D);
        spreadSignalCanvas.setLayoutY(360.0D);
        Canvas encodeSignalCanvas = new Canvas(800.0D, 80.0D);
        encodeSignalCanvas.setLayoutX(20.0D);
        encodeSignalCanvas.setLayoutY(470.0D);
        GraphicsContext gc_originalSignal = originalSoundCanvas.getGraphicsContext2D();
        GraphicsContext gc_spreadSignal = spreadSignalCanvas.getGraphicsContext2D();
        GraphicsContext gc_encodeSignal = encodeSignalCanvas.getGraphicsContext2D();
        final File[] selectedFile = new File[1];
        selectFileBtn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                FileChooser fil_chooser = new FileChooser();
                final File file = fil_chooser.showOpenDialog(AudioStenographyApplication.this.currentState);
                if (file != null) {
                    fileLabel.setText("Selected: " + file.getAbsolutePath());
                    selectedFile[0] = file;
                    originalSoundSignalBtn.setOpacity(1.0D);
                    spreadSoundSignalBtn.setOpacity(0.0D);
                    encodeSoundSignalBtn.setOpacity(0.0D);
                    gc_spreadSignal.clearRect(0.0D, 0.0D, 800.0D, 80.0D);
                    gc_encodeSignal.clearRect(0.0D, 0.0D, 800.0D, 80.0D);
                    originalSoundSignalBtn.setOnAction(new EventHandler<ActionEvent>() {
                        public void handle(ActionEvent event) {
                            try {
                                AudioDevice.playSample(Common.getWaveFromAudio(file));
                            } catch (Exception var3) {
                                var3.printStackTrace();
                            }

                        }
                    });
                    if (!passTextField.getText().isEmpty() && !textArea.getText().isEmpty()) {
                        encodeButton.setDisable(false);
                    }

                    try {
                        AudioStenographyApplication.this.drawWaveForm(gc_originalSignal, file, null);
                    } catch (Exception var5) {
                        var5.printStackTrace();
                    }
                }

            }
        });
        passTextField.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.isEmpty() && selectedFile[0] != null) {
                    encodeButton.setDisable(false);
                }
                if(!newValue.isEmpty()&&!isDigit(newValue)){
                    passTextField.deleteNextChar();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Nhập sai định dạng khóa");
                    alert.setHeaderText("Khóa chỉ nhận dạng số tự nhiên");
                    alert.setContentText("Vui lòng nhập lại khóa!");
                    alert.showAndWait();
                }

            }
        });
        textArea.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.isEmpty() && selectedFile[0] != null) {
                    encodeButton.setDisable(false);
                }
                if(!newValue.isEmpty()&&isDigit(String.valueOf(newValue.charAt(newValue.length() - 1)))){
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Cảnh báo");
                    alert.setContentText("Định dạng số sẽ làm sai lệnh mã hóa gói tin");
                    alert.showAndWait();
                }

            }
        });
        encodeButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                String message = textArea.getText();
                long key = Long.parseLong(passTextField.getText());
                File file = selectedFile[0];
                final Encoder encoder = new Encoder(message, key, file);

                try {
                    encoder.encode();
                    final String encodeFile = encoder.outputAudio(AudioStenographyApplication.this.currentState);
                    if (encodeFile != null && !encodeFile.isEmpty()) {
                        spreadSoundSignalBtn.setOpacity(1.0D);
                        encodeSoundSignalBtn.setOpacity(1.0D);
                        spreadSoundSignalBtn.setOnAction(new EventHandler<ActionEvent>() {
                            public void handle(ActionEvent event) {
                                try {
                                    AudioDevice.playSample(encoder.getSpreadSignal());
                                } catch (Exception var3) {
                                    var3.printStackTrace();
                                }

                            }
                        });
                        encodeSoundSignalBtn.setOnAction(new EventHandler<ActionEvent>() {
                            public void handle(ActionEvent event) {
                                try {
                                    AudioDevice.playSample(Common.getWaveFromAudio(new File(encodeFile)));
                                } catch (Exception var3) {
                                    var3.printStackTrace();
                                }

                            }
                        });
                        AudioStenographyApplication.this.drawWaveForm(gc_spreadSignal, (File)null, encoder.getSpreadSignal());
                        AudioStenographyApplication.this.drawWaveForm(gc_encodeSignal, new File(encodeFile), null);
                    }
                } catch (Exception var7) {
                    var7.printStackTrace();
                }

            }
        });
        root.getChildren().addAll(messageLabel, textArea, fileLabel, selectFileBtn, encodeButton, originalSoundCanvas, passwordLabel, passTextField, originalSoundSignalBtn, spreadSignalCanvas, encodeSignalCanvas, spreadSoundSignalBtn, encodeSoundSignalBtn);
        return new Tab("MÃ HÓA", root);
    }
    boolean isDigit(String x){
        try{
            int i = Integer.parseInt(x);
            return true;
        }catch (NumberFormatException e){
            return false;
        }
    }
    Tab initTabDecode() {
        Pane root = new Pane();
        root.setStyle("-fx-background-color: #282c34");
        final Label fileLabel = new Label("Chưa chọn file nào");
        fileLabel.setStyle("-fx-text-fill:white");
        fileLabel.setLayoutX(20.0D);
        fileLabel.setLayoutY(20.0D);
        Button selectFileBtn = new Button("Chọn file");
        selectFileBtn.setLayoutX(20.0D);
        selectFileBtn.setLayoutY(40.0D);
        final Label passwordLabel = new Label("Nhập khóa: ");
        passwordLabel.setStyle("-fx-text-fill:white");
        passwordLabel.setLayoutX(20.0D);
        passwordLabel.setLayoutY(70.0D);
        final TextField passTextField = new TextField();
        passTextField.setMinWidth(100.0D);
        passTextField.setLayoutX(20.0D);
        passTextField.setLayoutY(90.0D);
        final TextArea textArea = new TextArea();
        textArea.setLayoutX(20.0D);
        textArea.setLayoutY(150.0D);
        textArea.setPrefWidth(700.0D);
        textArea.setPrefHeight(350.0D);
        final Button decodeButton = new Button("Giải mã tệp tin");
        decodeButton.setLayoutX(20.0D);
        decodeButton.setLayoutY(120.0D);
        decodeButton.setDisable(true);
        passTextField.setDisable(true);
        textArea.setOpacity(0.0D);
        final File[] selectedFile = new File[1];
        selectFileBtn.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                FileChooser fil_chooser = new FileChooser();
                final File file = fil_chooser.showOpenDialog(AudioStenographyApplication.this.currentState);
                if (file != null) {
                    selectedFile[0] = file;
                    fileLabel.setText("Selected: " + file.getAbsolutePath());
                    passTextField.setDisable(false);
                    decodeButton.setDisable(false);
                }

            }
        });
        passTextField.textProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.isEmpty() && selectedFile[0] != null) {
                    decodeButton.setDisable(false);
                }
                if(!newValue.isEmpty()&&!isDigit(newValue)){
                    passTextField.deleteNextChar();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Nhập sai định dạng khóa");
                    alert.setHeaderText("Khóa chỉ nhận dạng số tự nhiên");
                    alert.setContentText("Vui lòng nhập lại khóa!");
                    alert.showAndWait();
                }
            }
        });
        decodeButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                File file= selectedFile[0];
                long key = Long.parseLong(passTextField.getText());
                Decoder decoder = new Decoder(file, key);
                try {
                    decoder.decode();
                    textArea.setText(decoder.getDecodedMessage());
                    textArea.setOpacity(1.0D);
                    textArea.setEditable(false);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

            }
        });
        root.getChildren().addAll(fileLabel, selectFileBtn, passwordLabel, passTextField, decodeButton, textArea);
        return new Tab("GIẢI MÃ", root);
    }

    public void start(Stage primaryStage) throws Exception {
        this.currentState = primaryStage;
        TabPane tabPane = new TabPane();
        Tab tab1 = this.initTabEncode();
        Tab tab2 = this.initTabDecode();
        tab1.setClosable(false);
        tab2.setClosable(false);
        tabPane.getTabs().add(tab1);
        tabPane.getTabs().add(tab2);
        Scene scene = new Scene(tabPane, 892, 600);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("style.css")).toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.initStyle(StageStyle.DECORATED);
        primaryStage.setTitle("Giấu tin trong tệp tin âm thanh - Hỗ trợ đinh dạng Wav - tần số 44100 Hz - Kênh stereo hoặc mono - 16-bit tín hiệu");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
