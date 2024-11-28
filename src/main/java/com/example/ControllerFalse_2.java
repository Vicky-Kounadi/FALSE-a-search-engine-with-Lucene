package com.example;

import java.io.IOException;

import org.apache.lucene.document.Document;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ControllerFalse_2 {

    @FXML
    TextField singerN, songN, albumN, albumY, albumT;

    @FXML
    TextArea lyrics;

    @FXML
    private void goToMainPage() throws IOException {
        App.setRoot("false_1");
    }

    @FXML
    private void goToMainPageWithData() throws IOException {
        App.setFlag(false);
        App.setRoot("false_1");
    }

    @FXML
    private void goToImportPage() throws IOException {
        App.setRoot("false_3");
    }

    @FXML
    private void goToQueryPage() throws IOException {
        App.setRoot("false_4");
    }

    @FXML
    private void exitApp() throws IOException {
        System.exit(0);
    }

    public void set(Document doc, String type) {
        if (type.equalsIgnoreCase("song")) {
            singerN.setText(doc.get("singer_name"));
            songN.setText(doc.get("song_name"));
            lyrics.setText(doc.get("lyrics"));
            if (doc.get("album_name") != null) {
                albumN.setText(doc.get("album_name"));
            }
            if (doc.get("album_year") != null) {
                albumY.setText(doc.get("album_year"));
            }
            if (doc.get("album_type") != null) {
                albumT.setText(doc.get("album_type"));
            }
        } else {
            albumN.setText(doc.get("album_name"));
            albumY.setText(doc.get("album_year"));
            albumT.setText(doc.get("album_type"));
        }
    }

    public void reset() {
        singerN.setText("");
        songN.setText("");
        albumN.setText("");
        albumY.setText("");
        albumT.setText("");
        lyrics.setText("");
    }

    // MODE CHANGE
    @FXML
    private Button modebutton;

    @FXML
    private HBox parent;

    @FXML
    private VBox insideview;

    @FXML
    private void modeChange() throws IOException {
        String nightCssPath = "/com/css/nightmode.css";
        String nightCss = getClass().getResource(nightCssPath).toExternalForm();
        String nightdetailsCssPath = "/com/css/nightmodeview.css";
        String nightdetailsCss = getClass().getResource(nightdetailsCssPath).toExternalForm();
        if (BModeManager.getMode() == 0) {
            parent.getStylesheets().add(nightCss);
            insideview.getStylesheets().add(nightdetailsCss);
            BModeManager.setMode(1);
        } else {
            parent.getStylesheets().remove(nightCss);
            insideview.getStylesheets().remove(nightdetailsCss);
            BModeManager.setMode(0);
        }
    }
    // MODE CHANGE END
}