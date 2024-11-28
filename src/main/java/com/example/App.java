package com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

import org.apache.lucene.document.Document;


public class App extends Application {
    private static Stage stage;
    private static Scene scene;
    private static ControllerFalse_1 false1;
    private static ControllerFalse_2 false2;
    private static ControllerFalse_3 false3;
    private static ControllerFalse_4 false4;
    private static Parent root1,root2,root3,root4;
    private static FXMLLoader fxmlLoader1;
    private static FXMLLoader fxmlLoader2 = new FXMLLoader(App.class.getResource("false_2.fxml"));
    private static FXMLLoader fxmlLoader3 = new FXMLLoader(App.class.getResource("false_3.fxml"));
    private static FXMLLoader fxmlLoader4 = new FXMLLoader(App.class.getResource("false_4.fxml"));

    private static LuceneManager manager;
    private static Document data;
    private static String docType;
    private static Boolean flag = true;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("false_1"), 1024, 600);
        false1.reset();
        stage.setTitle("False_Search_Engine");
        stage.setMinWidth(900);
        stage.setMaxWidth(1920);   
        stage.setMinHeight(500);
        stage.setMaxHeight(1080);
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        
        if(fxml.equalsIgnoreCase("false_1")){
            scene.setRoot(root1);
            if(flag){
                false1.reset();
            }
            flag = true;
        }
        else if(fxml.equalsIgnoreCase("false_2")){
            scene.setRoot(root2);
            false2.reset();
            false2.set(getDoc(),getType());
        }
        else if(fxml.equalsIgnoreCase("false_3")){
            scene.setRoot(root3);
            false3.reset();
        }
        else if(fxml.equalsIgnoreCase("false_4")){
            scene.setRoot(root4);
            false4.reset();
        }
    }

    private static Parent loadFXML(String fxml) throws IOException {
        fxmlLoader1 = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        root1 = fxmlLoader1.load();
        root2 = fxmlLoader2.load();
        root3 = fxmlLoader3.load();
        root4 = fxmlLoader4.load();
        false1 = fxmlLoader1.getController();
        false2 = fxmlLoader2.getController();
        false3 = fxmlLoader3.getController();
        false4 = fxmlLoader4.getController();
        return root1;
    }

    public static void main(String[] args) throws IOException {
        manager = new LuceneManager();
        launch();
    }

    public static LuceneManager getManager(){
        return manager;
    }

    public static Stage getStage(){
        return stage;
    }

    public static void setDoc(Document doc, String type){
        data = doc;
        docType = type;
    }

    private static Document getDoc(){
        return data;
    }

    private static String getType(){
        return docType;
    }

    public static void setFlag(Boolean b){
        flag = b;
    }

}