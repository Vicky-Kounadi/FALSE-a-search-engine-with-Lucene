package com.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class ControllerFalse_1 {

    private ArrayList<Document> songsAlbums = new ArrayList<>();
    private int top = 0;

    @FXML
    private ListView<String> songs_albums;

    @FXML
    private RadioButton songSearch, albumSearch;

    @FXML
    private Button mainPage, view, del, findSim, search;

    @FXML
    private TextField singerName, songName, lyricsField, albumName, albumYear, top_k;

    @FXML
    private void goToSongPage() throws IOException {
        Document view;
        if (songSearch.isSelected()) {
            view = songsAlbums.get(songs_albums.getSelectionModel().getSelectedIndex());
            App.setDoc(view, "song");
        } else {
            view = songsAlbums.get(songs_albums.getSelectionModel().getSelectedIndex());
            App.setDoc(view, "album");
        }
        App.setRoot("false_2");
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
    private void similarity() throws IOException, ParseException {
        ArrayList<Document> results;
        if (songSearch.isSelected()) {
            results = App.getManager()
                    .findSimilarSong(songsAlbums.get(songs_albums.getSelectionModel().getSelectedIndex()), top);
            songs_albums.getItems().clear();
            songsAlbums.clear();
            for (Document document : results) {
                String str1, str2, str3;
                str1 = document.get("singer_name");
                str2 = document.get("song_name");
                str3 = document.get("lyrics");
                if (document.get("lyrics").equalsIgnoreCase("undefined") == false) {
                    str3 = str3.substring(0, 50);
                }
                songsAlbums.add(document);
                songs_albums.getItems().add(str1 + "\n" + str2 + "\n" + str3);
            }
        } else {
            results = App.getManager()
                    .findSimilarAlbum(songsAlbums.get(songs_albums.getSelectionModel().getSelectedIndex()), top);
            songs_albums.getItems().clear();
            songsAlbums.clear();
            for (Document document : results) {
                String str1, str2, str3;
                str1 = document.get("singer_name");
                str2 = document.get("album_name");
                str3 = document.get("album_year");
                songsAlbums.add(document);
                songs_albums.getItems().add(str1 + "\n" + str2 + "\n" + str3);
            }
        }
    }

    @FXML
    public void delSelectedDocs() throws IOException, ParseException {
        ObservableList<Integer> numOfDocs;
        if (songSearch.isSelected()) {
            numOfDocs = songs_albums.getSelectionModel().getSelectedIndices();
            System.out.println("DEBUGG " + numOfDocs);
            for (int i = numOfDocs.size() - 1; i >= 0; i--) {
                int j = numOfDocs.get(i);
                Document doc = songsAlbums.get(j);
                System.out.println("Debug i ==== " + i);
                App.getManager().deleteSong(doc);
                songsAlbums.remove(j);
                songs_albums.getItems().remove(j);
            }
        } else {
            numOfDocs = songs_albums.getSelectionModel().getSelectedIndices();
            System.out.println("DEBUGG 2 " + numOfDocs);
            for (int i = numOfDocs.size() - 1; i >= 0; i--) {
                int j = numOfDocs.get(i);
                Document doc = songsAlbums.get(j);
                App.getManager().deleteAlbum(doc);
                songsAlbums.remove(j);
                songs_albums.getItems().remove(j);
            }
        }
    }

    @FXML
    private void searchQuery() throws IOException, ParseException {
        songs_albums.getItems().clear();
        if (songsAlbums.size() > 0) {
            songsAlbums.clear();
        }
        Map<String, String> map = new HashMap<String, String>();
        if (songSearch.isSelected()) {
            if (singerName.getText().length() > 0) {
                map.put("singer_name", singerName.getText());
            }
            if (songName.getText().length() > 0) {
                map.put("song_name", songName.getText());
            }
            if (lyricsField.getText().length() > 0) {
                map.put("lyrics", lyricsField.getText());
            }
            ArrayList<Document> results;
            try {
                top = Integer.parseInt(top_k.getText());
                results = App.getManager().searchSongQuery(map, top);
                for (Document document : results) {
                    String str1, str2, str3;
                    if (document.get("singer_name").contains(singerName.getText())
                            && singerName.getText().length() > 0) {
                        str1 = document.get("singer_name").replace(singerName.getText(),
                                "[" + singerName.getText() + "]");
                    } else {
                        str1 = document.get("singer_name");
                    }
                    if (document.get("song_name").contains(songName.getText()) && songName.getText().length() > 0) {
                        str2 = document.get("song_name").replace(songName.getText(), "[" + songName.getText() + "]");
                    } else {
                        str2 = document.get("song_name");
                    }
                    if (document.get("lyrics").contains(lyricsField.getText()) && lyricsField.getText().length() > 0) {
                        str3 = document.get("lyrics").replace(lyricsField.getText(), "[" + lyricsField.getText() + "]");
                        int startIndex = str3.indexOf("[" + lyricsField.getText() + "]");
                        str3 = str3.substring(startIndex, startIndex + 50);
                    } else {
                        str3 = document.get("lyrics");
                        if (document.get("lyrics").equalsIgnoreCase("undefined") == false) {
                            str3 = str3.substring(0, 50);
                        }
                    }
                    songsAlbums.add(document);
                    songs_albums.getItems().add(str1 + "\n" + str2 + "\n" + str3);
                }
            } catch (NumberFormatException e) {
                System.out.println("Insert Num");
            }
        } else {
            if (singerName.getText().length() > 0) {
                map.put("singer_name", singerName.getText());
            }
            if (albumName.getText().length() > 0) {
                map.put("album_name", albumName.getText());
            }
            if (albumYear.getText().length() > 0) {
                map.put("album_year", albumYear.getText());
            }
            ArrayList<Document> resultss;
            try {
                top = Integer.parseInt(top_k.getText());
                resultss = App.getManager().searchAlbumQuery(map, top);
                for (Document document : resultss) {
                    String str1, str2, str3;
                    if (document.get("singer_name").contains(singerName.getText())
                            && singerName.getText().length() > 0) {
                        str1 = document.get("singer_name").replace(singerName.getText(),
                                "[" + singerName.getText() + "]");
                    } else {
                        str1 = document.get("singer_name");
                    }
                    if (document.get("album_name").contains(songName.getText()) && songName.getText().length() > 0) {
                        str2 = document.get("album_name").replace(songName.getText(), "[" + songName.getText() + "]");
                    } else {
                        str2 = document.get("album_name");
                    }
                    if (document.get("album_year").contains(lyricsField.getText())
                            && lyricsField.getText().length() > 0) {
                        str3 = document.get("album_year").replace(lyricsField.getText(),
                                "[" + lyricsField.getText() + "]");
                    } else {
                        str3 = document.get("album_year");
                    }
                    songsAlbums.add(document);
                    songs_albums.getItems().add(str1 + "\n" + str2 + "\n" + str3);
                }
            } catch (NumberFormatException e) {
                System.out.println("Insert Num");
            }
        }
    }

    @FXML
    private void searchCheck() {
        if (singerName.getText().length() == 0 && songName.getText().length() == 0
                && lyricsField.getText().length() == 0 && albumName.getText().length() == 0
                && albumYear.getText().length() == 0) {
            search.setDisable(true);
        } else {
            search.setDisable(false);
        }
    }

    @FXML
    private void buttonBehavior() {
        if (songs_albums.getSelectionModel().getSelectedIndices().size() > 1) {
            view.setDisable(true);
            findSim.setDisable(true);
            del.setDisable(false);
        } else {
            view.setDisable(false);
            findSim.setDisable(false);
            del.setDisable(false);
        }
    }

    @FXML
    private void searchType() {
        if (songSearch.isSelected()) {
            singerName.setDisable(false);
            songName.setDisable(false);
            lyricsField.setDisable(false);
            albumName.setDisable(true);
            albumYear.setDisable(true);
            top_k.setDisable(false);
            albumName.setText("");
            albumYear.setText("");
        } else {
            singerName.setDisable(false);
            songName.setDisable(true);
            lyricsField.setDisable(true);
            albumName.setDisable(false);
            albumYear.setDisable(false);
            top_k.setDisable(false);
            singerName.setText("");
            songName.setText("");
            lyricsField.setText("");
        }
    }

    public void reset() {
        top = 0;
        songSearch.setSelected(false);
        albumSearch.setSelected(false);
        mainPage.setDisable(true);
        singerName.setDisable(true);
        songName.setDisable(true);
        lyricsField.setDisable(true);
        albumName.setDisable(true);
        albumYear.setDisable(true);
        top_k.setDisable(true);
        singerName.setText("");
        songName.setText("");
        lyricsField.setText("");
        albumName.setText("");
        albumYear.setText("");
        top_k.setText("");
        view.setDisable(true);
        del.setDisable(true);
        findSim.setDisable(true);
        search.setDisable(true);
        songs_albums.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        songs_albums.getItems().clear();
    }

    @FXML
    private void exitApp() throws IOException {
        System.exit(0);
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
        String nightlistsCssPath = "/com/css/nightlists.css";
        String nightlistsCss = getClass().getResource(nightlistsCssPath).toExternalForm();
        if (BModeManager.getMode() == 0) {
            parent.getStylesheets().add(nightCss);
            songs_albums.getStylesheets().add(nightlistsCss);
            BModeManager.setMode(1);
            System.out.println("parent styles: " + parent.getStylesheets());
            System.out.println("songs_albums styles: " + songs_albums.getStylesheets());

        } else {
            parent.getStylesheets().remove(nightCss);
            songs_albums.getStylesheets().remove(nightlistsCss);
            BModeManager.setMode(0);
        }
    }
    // MODE CHANGE END
}