package controller;

import ij.ImagePlus;
import ij.measure.Measurements;
import ij.measure.ResultsTable;
import ij.plugin.filter.ParticleAnalyzer;
import ij.process.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.Main;
import models.Output;
import models.Settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


public class Controller implements Initializable {

    private Main application;
    private Settings settings ;
    private List<String> filePaths = new ArrayList<>();
    private String selected = "";

    @FXML private TableView<String> table;
    @FXML private TableColumn<String, String> filePathColumn;
    @FXML private ImageView beforeImage;
    @FXML private ImageView afterImage;
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
    @FXML private CheckBox saveOverlayMaskPics;
    @FXML private CheckBox realtimeChange;
    @FXML private TextArea savePathText;



    public void setApp(Main application, Settings settings){
        this.application = application;
        Optional.ofNullable(settings)
                .ifPresentOrElse(this::reAnalysisSitting,
                        () -> {
                            Settings S =Settings.builder()
                            .color("8-bit")
                            .lowThresholdLevel(0.0)
                            .upperThresholdLevel(140.0)
                            .lowerSize(20.0)
                            .higherSize(500.0)
                            .lowerCircularity(0.0)
                            .upperCircularity(1.0)
                            .saveOverlayMaskPics(false)
                            .savePathText("")
                            .build();
                            reAnalysisSitting(S);
                        });
    }
    /**
     * 重新載入上一次變更的設定值
     */
    public void reAnalysisSitting(Settings settings){
        this.settings = settings;
        Optional.ofNullable(settings.getFilePaths())
                .ifPresent(e -> {
                    this.filePaths = settings.getFilePaths();
                    filePathColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()));
                    table.setItems(FXCollections.observableList(e));
                });

        colorChoiceBox.setValue(this.settings.getColor());
        lowThresholdSlider.setValue(this.settings.getLowThresholdLevel());
        lowThreshold.setText(this.settings.getLowThresholdLevel().toString());
        upperThresholdSilder.setValue(this.settings.getUpperThresholdLevel());
        upperThreshold.setText(this.settings.getUpperThresholdLevel().toString());
        lowerSizeSlider.setValue(this.settings.getLowerSize());
        lowerSize.setText(this.settings.getLowerSize().toString());
        higherSizeSlider.setValue(this.settings.getHigherSize());
        higherSize.setText(this.settings.getHigherSize().toString());
        lowerCircularitySlider.setValue(this.settings.getLowerCircularity());
        lowerCircularity.setText(this.settings.getLowerCircularity().toString());
        upperCircularitySlider.setValue(this.settings.getUpperCircularity());
        upperCircularity.setText(this.settings.getUpperCircularity().toString());
    }
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
        if(!table.getItems().isEmpty()
                && table.getSelectionModel().getSelectedItem() != null){
            //選擇圖片路徑
        selected = table.getSelectionModel().getSelectedItem();
            beforeImage.setImage(this.retrievePic(selected));
            if(colorChoiceBox.getValue() != null){
                analyzeImage(new ImagePlus(selected, SwingFXUtils.fromFXImage(beforeImage.getImage(),null)));
            }
            zoomIn(beforeImage, afterImage);
        }
    }



    /**
     * onMouseRelease 分析單一選擇的圖
     */
    public void runSingleImageAnalysis(){
        if(!realtimeChange.isSelected())
            return;
        if(filePaths.size()==0)
            return;
        if(selected.isEmpty()){
            selected=filePaths.get(0);
            beforeImage.setImage(this.retrievePic(selected));
        }
        var image = new ImagePlus(selected, SwingFXUtils.fromFXImage(this.retrievePic(selected),null));
        analyzeImage(image);
    }

    /**
     * 分析圖片 吐出分析後照
     * @param imagePlus
     */
    private Output analyzeImage(ImagePlus imagePlus){

        ImageConverter imageConverter = new ImageConverter(imagePlus);
        ColorSpaceConverter colorSpaceConverter = new ColorSpaceConverter();
        ColorProcessor colorProcessor = new ColorProcessor(imagePlus.getBufferedImage());
        ImageProcessor imageProcessor =null;
        //顏色分析
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
                imageProcessor = colorSpaceConverter.RGBToLab(imagePlus).getStack().getProcessor(1);
                break;
            case "a*":
                imageProcessor = colorSpaceConverter.RGBToLab(imagePlus).getStack().getProcessor(2);
                break;
            case "b*":
                imageProcessor = colorSpaceConverter.RGBToLab(imagePlus).getStack().getProcessor(3);
                break;
        }

        imageProcessor.setThreshold(Double.parseDouble(lowThreshold.getText()), Double.parseDouble(upperThreshold.getText()), imageProcessor.getLutUpdateMode());
        ResultsTable rt = new ResultsTable();
        //ParticleAnalyzer.SHOW_MASKS --> 顯示圖片 一塊一塊黑
        //ParticleAnalyzer.SHOW_OUTLINES --> 顯示圖片 圈起來
        //ParticleAnalyzer.SHOW_ROI_MASKS --> 好問題還沒跑過
        ParticleAnalyzer pa = new ParticleAnalyzer(ParticleAnalyzer.SHOW_ROI_MASKS, Measurements.PERIMETER, rt,
            Double.parseDouble(lowerSize.getText()), Double.parseDouble(higherSize.getText()), Double.parseDouble(lowerCircularity.getText()), Double.parseDouble(upperCircularity.getText()));
        //不要圖片跳出來
        pa.setHideOutputImage(true);
        pa.analyze(imagePlus, imageProcessor);
        afterImage.setImage(SwingFXUtils.toFXImage(pa.getOutputImage().getBufferedImage(), null));
        //儲存設定值
        settings.setColor(colorChoiceBox.getValue());
        settings.setHigherSize(Double.parseDouble(higherSize.getText()));
        settings.setLowerSize(Double.parseDouble(lowerSize.getText()));
        settings.setLowerCircularity(Double.parseDouble(lowerCircularity.getText()));
        settings.setUpperCircularity(Double.parseDouble(upperCircularity.getText()));
        settings.setUpperThresholdLevel(Double.parseDouble(upperThreshold.getText()));
        settings.setLowThresholdLevel(Double.parseDouble(lowThreshold.getText()));
        settings.setSavePathText(savePathText.getText());

        zoomIn(afterImage, beforeImage);
        var imageTitle = imagePlus.getTitle().substring(imagePlus.getTitle().lastIndexOf("\\")+1);
        var imageName = imagePlus.getTitle().substring(imagePlus.getTitle().lastIndexOf("/")+1,imagePlus.getTitle().length());
        return Output.builder()
                .counter(rt.getCounter())
                .slice(imageTitle)
                .sliceName(imageName)
                .build();
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
     * 放大縮小照片
     * @param imageView1,imageView2
     */
    private void zoomIn(ImageView imageView1, ImageView imageView2){
        final int MIN_PIXELS = 10;
        double width = imageView1.getImage().getWidth();
        double height = imageView1.getImage().getHeight();

        imageView1.setPreserveRatio(true);
        reset(imageView1, width / 2, height / 2);

        ObjectProperty<Point2D> mouseDown = new SimpleObjectProperty<>();

        imageView1.setOnMousePressed(e -> {

            Point2D mousePress = imageViewToImage(imageView1, new Point2D(e.getX(), e.getY()));
            mouseDown.set(mousePress);
        });

        imageView1.setOnMouseDragged(e -> {
            Point2D dragPoint = imageViewToImage(imageView1, new Point2D(e.getX(), e.getY()));
            shift(imageView1, imageView2, dragPoint.subtract(mouseDown.get()));
            mouseDown.set(imageViewToImage(imageView1, new Point2D(e.getX(), e.getY())));
        });

        imageView1.setOnScroll(e -> {
            double delta = e.getDeltaY();
            Rectangle2D viewport = imageView1.getViewport();
            double scale = clamp(Math.pow(1.01, delta),

                    // don't scale so we're zoomed in to fewer than MIN_PIXELS in any direction:
                    Math.min(MIN_PIXELS / viewport.getWidth(), MIN_PIXELS / viewport.getHeight()),

                    // don't scale so that we're bigger than imageView dimensions:
                    Math.max(width / viewport.getWidth(), height / viewport.getHeight())

            );

            Point2D mouse = imageViewToImage(imageView1, new Point2D(e.getX(), e.getY()));

            double newWidth = viewport.getWidth() * scale;
            double newHeight = viewport.getHeight() * scale;

            // To keep the visual point under the mouse from moving, we need
            // (x - newViewportMinX) / (x - currentViewportMinX) = scale
            // where x is the mouse X coordinate in the imageView

            // solving this for newViewportMinX gives

            // newViewportMinX = x - (x - currentViewportMinX) * scale

            // we then clamp this value so the imageView never scrolls out
            // of the imageview:

            double newMinX = clamp(mouse.getX() - (mouse.getX() - viewport.getMinX()) * scale,
                    0, width - newWidth);
            double newMinY = clamp(mouse.getY() - (mouse.getY() - viewport.getMinY()) * scale,
                    0, height - newHeight);

            imageView1.setViewport(new Rectangle2D(newMinX, newMinY, newWidth, newHeight));
            imageView2.setViewport(new Rectangle2D(newMinX, newMinY, newWidth, newHeight));
        });

        imageView1.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                reset(imageView1, width, height);
                reset(imageView2, width, height);
            }
        });
    }

    // reset to the top left:
    private void reset(ImageView imageView, double width, double height) {
        imageView.setViewport(new Rectangle2D(0, 0, width, height));
    }

    // shift the viewport of the imageView by the specified delta, clamping so
    // the viewport does not move off the actual image:
    private void shift(ImageView imageView, ImageView imageView2, Point2D delta) {
        Rectangle2D viewport = imageView.getViewport();

        double width = imageView.getImage().getWidth() ;
        double height = imageView.getImage().getHeight() ;

        double maxX = width - viewport.getWidth();
        double maxY = height - viewport.getHeight();

        double minX = clamp(viewport.getMinX() - delta.getX(), 0, maxX);
        double minY = clamp(viewport.getMinY() - delta.getY(), 0, maxY);

        imageView.setViewport(new Rectangle2D(minX, minY, viewport.getWidth(), viewport.getHeight()));
        imageView2.setViewport(new Rectangle2D(minX, minY, viewport.getWidth(), viewport.getHeight()));
    }

    private double clamp(double value, double min, double max) {

        if (value < min)
            return min;
        if (value > max)
            return max;
        return value;
    }

    // convert mouse coordinates in the imageView to coordinates in the actual image:
    private Point2D imageViewToImage(ImageView imageView, Point2D imageViewCoordinates) {
        double xProportion = imageViewCoordinates.getX() / imageView.getBoundsInLocal().getWidth();
        double yProportion = imageViewCoordinates.getY() / imageView.getBoundsInLocal().getHeight();

        Rectangle2D viewport = imageView.getViewport();
        return new Point2D(
                viewport.getMinX() + xProportion * viewport.getWidth(),
                viewport.getMinY() + yProportion * viewport.getHeight());
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
        return (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) ->{
            String v = String.format("%d", newValue.intValue());
            if(!integer){
                v=String.format("%.2f", newValue.doubleValue());
            }
            textField.setText(v);
        };
    }

    @FXML
    public void handleSavePathChooserAction(){
        if(saveOverlayMaskPics.isSelected()) {
            DirectoryChooser dirchooser = new DirectoryChooser();
            Stage stage = new Stage();
            File files = dirchooser.showDialog(stage);
            if (!files.getPath().equals("")) {
                savePathText.setText(files.getPath());
            }
        }else{
            savePathText.setText("");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        SliderInit();
    }

    /**
     * onAction 按 開始分析 取得 前端設定 的資料以及所有 上傳檔案的路徑 並轉成ImagePlus格式
     */
    public void runAnalysis() throws IOException {
        settings.setFilePaths(filePaths);
        var images = settings.getFilePaths().stream().map(filepath-> new ImagePlus(filepath, SwingFXUtils.fromFXImage(this.retrievePic(filepath),null))).collect(Collectors.toList());
        var outputs = images.stream().map(this::analyzeImage).collect(Collectors.toList());
        application.goResultPage(outputs, settings);
    }


}
