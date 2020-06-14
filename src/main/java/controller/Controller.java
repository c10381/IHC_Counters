package controller;

import ij.ImagePlus;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.Settings;
import org.controlsfx.control.RangeSlider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class Controller implements Initializable {


    private Settings settings = Settings.builder().build();
    private List<String> filePaths = new ArrayList<>();
    private String selected = "";

    @FXML private TableView<String> table;
    @FXML private TableColumn<String, String> filePathColumn;
    @FXML private ImageView image;
    @FXML private ChoiceBox<String> colorChoiceBox;
    @FXML private RangeSlider ThresholdLevel;

    /**
     * 按 addBtn 跳出選擇上傳的圖片
     */
    @FXML
    public void handleFileChooserAction(){
        FileChooser chooser = new FileChooser();
        Stage stage = new Stage();
        var files = chooser.showOpenMultipleDialog(stage);
        if(files != null){
            filePaths.addAll(files.stream().map(File::getAbsolutePath).collect(Collectors.toList()));
            filePathColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()));
            table.setItems(FXCollections.observableList(filePaths));
        }
    }

    /**
     * 按 remove 移除圖片路徑
     */
    @FXML public void removeFile(){
        table.getItems().remove(selected);
    }

    /**
     * onMouseClicked 事件 點選上傳檔案路徑後 旁邊會顯示已選的圖檔
     */
    @FXML public void getSelectedFilePath() {
        selected = table.getSelectionModel().getSelectedItem();
        image.setImage(this.retrievePic(selected));
        this.runAnalysis();
    }

    /**
     * 取得 前端設定 的資料以及所有 上傳檔案的路徑 並轉成ImagePlus格式
     */
    public void runAnalysis(){
        settings.setFilePaths(filePaths);
        var images = settings.getFilePaths().stream().map(filepath-> new ImagePlus(filepath, SwingFXUtils.fromFXImage(this.retrievePic(filepath),null))).collect(Collectors.toList());
    }

    /**
     * 當使用者點選圖片位置後 到檔案去撈圖片顯示
     * @param filePath 圖片路徑
     * @return Image 圖片檔
     */
    public Image retrievePic(String filePath){
        Image image = null;
        try{
            image = new Image(new FileInputStream(filePath));
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
       return image;
    }
    /**
    * RangeSlider initialize
    *
    * */
    public void RangeSliderInit(){
        ThresholdLevel.lowValueProperty().addListener(
                new ChangeListener<Number>() {
                    @Override
                    public void changed(
                            ObservableValue<? extends Number> observable,
                            Number oldValue, Number newValue) {
                        System.out.println(newValue);
                     //   setValue(label1, newValue);
                    }
                }
        );

        ThresholdLevel.highValueProperty().addListener(
                new ChangeListener<Number>() {
                    @Override
                    public void changed(
                            ObservableValue<? extends Number> observable,
                            Number oldValue, Number newValue) {
                        System.out.println(newValue);
                     //   setValue(label2, newValue);
                    }
                }
        );

//        rangeSlider1.setHighValue(80.0);
//        rangeSlider1.setLowValue(20.0);
    }
    /**
     * 在RangeSlider移動時即時顯示的方式
     * @param label 顯示文字欄位id
     * @param number 移動的數字
     *
     */
    void setValue(Label label, Number number) {
        String v = String.format("%d",number.intValue());
        label.setText(v);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        RangeSliderInit();
    }
}
