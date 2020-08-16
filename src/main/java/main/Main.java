package main;

import controller.Controller;
import controller.ResultController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Main extends Application {
    private Stage stage;
    @Override
    public void start(Stage primaryStage) throws Exception{
        stage = primaryStage;
        stage.setTitle("IHC Counter");
        primaryStage.setTitle("IHC Counter");
        //primaryStage.setScene(new Scene(root, 300, 275));
        goSelectSample();
        primaryStage.show();
    }

    public void goSelectSample(){
        try {
            Controller selectSamplePage = (Controller) replaceSceneContent("/view/main.fxml");
            selectSamplePage.setApp(this);
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void goResult(){
        try {
            ResultController resultPage = (ResultController) replaceSceneContent("/view/result.fxml");
            resultPage.setApp(this);
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Initializable replaceSceneContent(String fxml) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        InputStream in = Main.class.getResourceAsStream(fxml);
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(Main.class.getResource(fxml));
        Scene scene = new Scene(loader.load(in));
        stage.setScene(scene);
        stage.sizeToScene();
        return (Initializable) loader.getController();
    }

    public static void main(String[] args) {
        launch(args);
    }



}
