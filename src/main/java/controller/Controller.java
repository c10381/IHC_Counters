package controller;

import ij.ImagePlus;
import ij.measure.Measurements;
import ij.measure.ResultsTable;
import ij.plugin.filter.ParticleAnalyzer;
import ij.process.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.Settings;
//import org.controlsfx.control.RangeSlider;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Controller implements Initializable {


    private Settings settings = Settings.builder().build();
    private List<String> filePaths = new ArrayList<>();
    private String selected = "";

    @FXML private TableView<String> table;
    @FXML private TableColumn<String, String> filePathColumn;
    @FXML private ImageView image;
    @FXML private ChoiceBox<String> colorChoiceBox;
//    @FXML private RangeSlider ThresholdSlider;
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
        if(!table.getItems().isEmpty()){
        selected = table.getSelectionModel().getSelectedItem();
            image.setImage(this.retrievePic(selected));
        }
    }

    /**
     * onAction 按 開始分析 取得 前端設定 的資料以及所有 上傳檔案的路徑 並轉成ImagePlus格式
     */
    public void runAnalysis(){
        settings.setFilePaths(filePaths);
        //若有一堆圖要分析 目前 button按了只會分析選的那張圖
        //可以思考把Settings.class刪除 沒有用到
        var images = settings.getFilePaths().stream().map(filepath-> new ImagePlus(filepath, SwingFXUtils.fromFXImage(this.retrievePic(filepath),null))).collect(Collectors.toList());
        analyzeImage(images.get(0));
        var map = new HashMap<String,Integer>();
        map.put("z", 10);
        map.put("b", 5);
        map.put("a", 6);
        map.put("c", 20);
        map.put("d", 1);
        map.put("e", 7);
        map.put("y", 8);
        map.put("n", 99);
        map.put("g", 50);
        map.put("m", 2);
        var mapper = map.entrySet().stream().sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    /**
     * 分析圖片 吐出分析後照
     * @param imagePlus
     */
    private void analyzeImage(ImagePlus imagePlus){
        System.out.println(colorChoiceBox.getValue());
        ImageConverter imageConverter = new ImageConverter(imagePlus);
        ColorSpaceConverter colorSpaceConverter = new ColorSpaceConverter();
        ColorProcessor colorProcessor = new ColorProcessor(imagePlus.getBufferedImage());
        ImageProcessor imageProcessor =null;
        //顏色分析
        System.out.println(imagePlus);
        switch(colorChoiceBox.getValue()){
            case "8-bit":
                imageConverter.convertToGray8();
                imageProcessor = imagePlus.getProcessor();
                break;
            case "16-bit":
                imageConverter.convertToGray16();
                imageProcessor = imagePlus.getProcessor();
                break;
            case "32-bit":
                imageConverter.convertToGray32();
                imageProcessor = imagePlus.getProcessor();
                break;
            case "Red":
                imageConverter.convertToRGBStack();
                imageProcessor = imagePlus.getStack().getProcessor(1);
                break;
            case "Green":
                imageConverter.convertToRGBStack();
                imageProcessor = imagePlus.getStack().getProcessor(2);
                break;
            case "Blue":
                imageConverter.convertToRGBStack();
                imageProcessor = imagePlus.getStack().getProcessor(3);
                break;
            case "Hue":
                imageProcessor = colorProcessor.getHSBStack().getProcessor(1);
                break;
            case "Saturation":
                imageProcessor = colorProcessor.getHSBStack().getProcessor(2);
                break;
            case "Brightness":
                imageProcessor = colorProcessor.getHSBStack().getProcessor(3);
                break;
            case "L*":
                //imageProcessor = colorSpaceConverter.RGBToLab(imagePlus).getStack().getProcessor(1);
                imageConverter.convertToLab();
                imageProcessor = imagePlus.getImageStack().getProcessor(1);
                imageConverter.convertToGray8();
                break;
            case "a*":
                //imageProcessor = colorSpaceConverter.RGBToLab(imagePlus).getStack().getProcessor(2);
                imageConverter.convertToLab();
                imageProcessor = imagePlus.getImageStack().getProcessor(2);
                imageConverter.convertToGray8();
                break;
            case "b*":
                //imageProcessor = colorSpaceConverter.RGBToLab(imagePlus).getStack().getProcessor(3);
                imageConverter.convertToLab();
                imageProcessor = imagePlus.getImageStack().getProcessor(3);
                imageConverter.convertToGray8();
                break;
        }

        imageProcessor.setThreshold(Double.parseDouble(lowThreshold.getText()), Double.parseDouble(upperThreshold.getText()), imageProcessor.getLutUpdateMode());
        ResultsTable rt = new ResultsTable();
        var imgAfterColorChange = new ImagePlus(imagePlus.getTitle(), imageProcessor);
        System.out.println(imagePlus);
        //ParticleAnalyzer.SHOW_MASKS --> 顯示圖片 一塊一塊黑
        //ParticleAnalyzer.SHOW_OUTLINES --> 顯示圖片 圈起來
        //ParticleAnalyzer.SHOW_ROI_MASKS --> 好問題還沒跑過
        ParticleAnalyzer pa = new ParticleAnalyzer(ParticleAnalyzer.SHOW_MASKS, Measurements.PERIMETER, rt,
            Double.parseDouble(lowerSize.getText()), Double.parseDouble(higherSize.getText()), Double.parseDouble(lowerCircularity.getText()), Double.parseDouble(upperCircularity.getText()));
        //不要圖片跳出來
//        pa.setHideOutputImage(true);
        pa.analyze(imgAfterColorChange);
        System.out.println(rt.getCounter());

//        image.setImage(SwingFXUtils.toFXImage(pa.getOutputImage().getBufferedImage(), null));
        image.setImage(SwingFXUtils.toFXImage(pa.getOutputImage().getBufferedImage(), null));
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
        lowThresholdSlider.valueProperty().addListener(setValue(lowThreshold,true));
        upperThresholdSilder.valueProperty().addListener(setValue(upperThreshold,true));
        lowerSizeSlider.valueProperty().addListener(setValue(lowerSize,true));
        higherSizeSlider.valueProperty().addListener(setValue(higherSize,true));
        lowerCircularitySlider.valueProperty().addListener(setValue(lowerCircularity,false));
        upperCircularitySlider.valueProperty().addListener(setValue(upperCircularity,false));
    }
    /**
     * Slider ChangeListener
     * @param textField 顯示文字欄位id
     * */
    ChangeListener<Number> setValue(TextField textField ,Boolean integer) {
        return new ChangeListener<Number>() {
            @Override
            public void changed(
                    ObservableValue<? extends Number> observable,
                    Number oldValue, Number newValue) {
                String v = String.format("%d", newValue.intValue());
                if(!integer){
                    v=String.format("%.2f", newValue.doubleValue());
                }
                textField.setText(v);
            }
        };
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        SliderInit();
    }
}
