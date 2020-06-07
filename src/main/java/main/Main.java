package main;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.Settings;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;


public class Main extends Application {

    Settings settings = Settings.builder().build();

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/view/main.fxml"));
        primaryStage.setTitle("IHC Counter");
        //primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * 按 addBtn 跳出選擇上傳的圖片
     */
    @FXML public void handleFileChooserAction(){
        FileChooser chooser = new FileChooser();
        Stage stage = new Stage();
        List<File> files = chooser.showOpenMultipleDialog(stage);
        if(files != null){
            var filePaths = files.stream().map(File::getAbsolutePath).collect(Collectors.toList());
            settings.setFilePaths(filePaths);
        }
    }
}
