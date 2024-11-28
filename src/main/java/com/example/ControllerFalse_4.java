package com.example;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;

public class ControllerFalse_4 {

    private String queryText = "";

    @FXML
    private TextArea queries;

    @FXML
    private Button queryInfo, change;

    @FXML
    private void goToMainPage() throws IOException {
        App.setRoot("false_1");
    }

    @FXML
    private void goToImportPage() throws IOException {
        App.setRoot("false_3");
    }

    @FXML
    private void exitApp() throws IOException {
        System.exit(0);
    }

    @FXML
    private void fieldCheck() throws IOException {
        if (queryText.equalsIgnoreCase(queries.getText())) {
            change.setDisable(true);
        } else {
            change.setDisable(false);
        }
    }

    @FXML
    private void saveChanges() {
        try {
            File oldFile = new File("queries.txt");
            oldFile.delete();
            RandomAccessFile file2 = new RandomAccessFile("queries.txt", "rw");
            file2.writeBytes(queries.getText());
            file2.close();
            queryText = queries.getText();
            change.setDisable(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reset() {
        queryText = "";
        try {
            RandomAccessFile file = new RandomAccessFile("queries.txt", "rw");
            String line;
            file.seek(0);
            while ((line = file.readLine()) != null) {
                queryText = queryText + line;
            }
            file.close();
            queries.setText(queryText);
        } catch (IOException e) {
            e.printStackTrace();
        }
        queryInfo.setDisable(true);
        change.setDisable(true);
    }

    // MODE CHANGE
    @FXML
    private Button modebutton;

    @FXML
    private HBox parent;

    @FXML
    private void modeChange() throws IOException {
        String nightCssPath = "/com/css/nightmode.css";
        String nightCss = getClass().getResource(nightCssPath).toExternalForm();
        if (BModeManager.getMode() == 0) {
            parent.getStylesheets().add(nightCss);
            BModeManager.setMode(1);
        } else {
            parent.getStylesheets().remove(nightCss);
            BModeManager.setMode(0);
        }
    }
    // MODE CHANGE END
}
