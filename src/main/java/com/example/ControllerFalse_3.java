package com.example;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ControllerFalse_3 {

    @FXML
    private TextArea lyrics;

    @FXML
    private RadioButton impSong, impAlbum, impLyrics, fromFile, fromLink, manually;

    @FXML
    private Button imp, impSongFile, impAlbumFile, impLyricsFile, importPage;

    @FXML
    private TextField singerN, songN, albumN, albumY, albumT;

    @FXML
    private void goToMainPage() throws IOException {
        App.setRoot("false_1");
    }

    @FXML
    private void goToQueryPage() throws IOException {
        App.setRoot("false_4");
    }

    @FXML
    private void exitApp() throws IOException {
        System.exit(0);
    }

    @FXML
    private void importStuff() throws IOException {
        if (impSong.isSelected()) {
            if (fromLink.isSelected()) {
                App.getManager().addSongsFromLink(singerN.getText(), songN.getText());
            } else if (fromFile.isSelected() != true) {
                App.getManager().addSong(singerN.getText(), songN.getText(), albumN.getText(), albumY.getText(),
                        albumT.getText(), lyrics.getText());
            }
        } else if (impLyrics.isSelected()) {
            if (fromLink.isSelected()) {
                App.getManager().addSongsFromLink(singerN.getText(), songN.getText());
            } else if (fromFile.isSelected() != true) {
                App.getManager().addSong(singerN.getText(), songN.getText(), albumN.getText(), albumY.getText(),
                        albumT.getText(), lyrics.getText());
            }
        } else if (impAlbum.isSelected()) {
            if (fromFile.isSelected() != true) {
                App.getManager().addAlbum(singerN.getText(), albumN.getText(), albumY.getText(), albumT.getText());
            }
        }

        singerN.setText("");
        songN.setText("");
        albumN.setText("");
        albumT.setText("");
        albumY.setText("");
        lyrics.setText("");
    }

    @FXML
    private void impCheck() {
        if (singerN.getText().length() == 0 && songN.getText().length() == 0 && lyrics.getText().length() == 0
                && albumN.getText().length() == 0 && albumY.getText().length() == 0 && albumT.getText().length() == 0) {
            imp.setDisable(true);
        } else {
            imp.setDisable(false);
        }
    }

    @FXML
    private void radioButtonBehavior() {
        if (impSong.isSelected()) {
            singerN.setDisable(false);
            songN.setDisable(false);
            lyrics.setDisable(false);
            fromLink.setDisable(false);
            albumN.setDisable(false);
            albumT.setDisable(false);
            albumY.setDisable(false);
            impSongFile.setDisable(false);
            impAlbumFile.setDisable(true);
            impLyricsFile.setDisable(true);
        } else if (impAlbum.isSelected()) {
            singerN.setDisable(false);
            songN.setDisable(true);
            lyrics.setDisable(true);
            fromLink.setDisable(true);
            albumN.setDisable(false);
            albumT.setDisable(false);
            albumY.setDisable(false);
            impAlbumFile.setDisable(false);
            impSongFile.setDisable(true);
            impLyricsFile.setDisable(true);
        } else {
            singerN.setDisable(false);
            songN.setDisable(false);
            lyrics.setDisable(false);
            fromLink.setDisable(false);
            albumN.setDisable(false);
            albumT.setDisable(false);
            albumY.setDisable(false);
            impLyricsFile.setDisable(false);
            impSongFile.setDisable(true);
            impAlbumFile.setDisable(true);
        }

        if (fromFile.isSelected()) {
            singerN.setDisable(true);
            songN.setDisable(true);
            lyrics.setDisable(true);
            albumN.setDisable(true);
            albumT.setDisable(true);
            albumY.setDisable(true);
            imp.setDisable(true);
        } else if (fromLink.isSelected()) {
            singerN.setDisable(false);
            songN.setDisable(false);
            lyrics.setDisable(true);
            albumN.setDisable(true);
            albumT.setDisable(true);
            albumY.setDisable(true);
            impSongFile.setDisable(true);
            impLyricsFile.setDisable(true);
            impAlbumFile.setDisable(true);
        } else {
            if (impAlbum.isSelected() != true) {
                singerN.setDisable(false);
                songN.setDisable(false);
                lyrics.setDisable(false);
                albumN.setDisable(false);
                albumT.setDisable(false);
                albumY.setDisable(false);
            }
            impSongFile.setDisable(true);
            impLyricsFile.setDisable(true);
            impAlbumFile.setDisable(true);
        }
    }

    public void reset() {
        impSong.setSelected(false);
        impAlbum.setSelected(false);
        impLyrics.setSelected(false);
        fromFile.setSelected(false);
        fromLink.setSelected(false);
        manually.setSelected(false);
        singerN.setDisable(true);
        songN.setDisable(true);
        albumN.setDisable(true);
        albumT.setDisable(true);
        albumY.setDisable(true);
        lyrics.setDisable(true);
        imp.setDisable(true);
        impSongFile.setDisable(true);
        impAlbumFile.setDisable(true);
        impLyricsFile.setDisable(true);
        importPage.setDisable(true);
        singerN.setText("");
        songN.setText("");
        albumN.setText("");
        albumT.setText("");
        albumY.setText("");
        lyrics.setText("");
    }

    @FXML
    private void importSongFile() throws IOException {
        Stage stage = App.getStage();
        final FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser);
        List<File> list = openMultipleFiles(fileChooser, stage);
        if (list != null) {
            for (File file : list) {
                App.getManager().addSongsFromFile(file);
            }
        }
    }

    @FXML
    private void importAlbumFile() throws IOException {
        Stage stage = App.getStage();
        final FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser);
        List<File> list = openMultipleFiles(fileChooser, stage);
        if (list != null) {
            for (File file : list) {
                App.getManager().addAlbumsFromFile(file);
            }
        }
    }

    @FXML
    private void importLyricsFile() throws IOException {
        Stage stage = App.getStage();
        final FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser);
        List<File> list = openMultipleFiles(fileChooser, stage);
        // System.out.println("Debug ========");
        if (list != null) {
            for (File file : list) {
                App.getManager().addLyricsFromFile(file);
            }
        }
    }

    private static void configureFileChooser(final FileChooser fileChooser) {
        fileChooser.setTitle("Find Files");
        fileChooser.getInitialFileName();
    }

    private static List<File> openMultipleFiles(FileChooser fileChooser, Stage stage) {
        List<File> list = fileChooser.showOpenMultipleDialog(stage);
        return list;
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
