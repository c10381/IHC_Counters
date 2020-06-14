package controller;

import com.sun.glass.events.MouseEvent;
import ij.ImagePlus;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
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
    @FXML private Slider lowThresholdSlider;
    @FXML private Slider upperThresholdSilder;
    @FXML private Slider lowerSizeSlider;
    @FXML private Slider higherSizeSlider;
    @FXML private Slider lowerCircularitySlider;
    @FXML private Slider upperCircularitySlider;
    @FXML private TextField lowThreshold;
    @FXML private TextField upperThreshold;
    @FXML private TextField lowerSize;
    @FXML private TextField higherSize;
    @FXML private TextField lowerCircularity;
    @FXML private TextField upperCircularity;
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
    * Slider initialize
    *
    * */
    public void SliderInit(){
        lowThresholdSlider.valueProperty().addListener(setValue(lowThreshold));
        upperThresholdSilder.valueProperty().addListener(setValue(upperThreshold));
        lowerSizeSlider.valueProperty().addListener(setValue(lowerSize));
        higherSizeSlider.valueProperty().addListener(setValue(higherSize));
        lowerCircularitySlider.valueProperty().addListener(setValue(lowerCircularity));
        upperCircularitySlider.valueProperty().addListener(setValue(upperCircularity));
    }
    /**
     * Slider ChangeListener
     * @param textField 顯示文字欄位id
     * */
    ChangeListener<Number> setValue(TextField textField) {
        return new ChangeListener<Number>() {
            @Override
            public void changed(
                    ObservableValue<? extends Number> observable,
                    Number oldValue, Number newValue) {
                String v = String.format("%d", newValue.intValue());
                textField.setText(v);
            }
        };
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        SliderInit();
    }
}
