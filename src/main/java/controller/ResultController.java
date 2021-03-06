package controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;
import lombok.SneakyThrows;
import main.Main;
import models.Output;
import models.Settings;

import java.io.File;
import java.io.PrintWriter;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ResultController implements Initializable {
    private Main application;
    @FXML private TableView<Output> results;
    @FXML private TableColumn<Output, Number> counterColumn;
    @FXML private TableColumn<Output, String> sliceColumn;
    @FXML private Text colorName;
    @FXML private Text threaholdRange;
    @FXML private Text sizeRange;
    @FXML private Text circularityRange;

    private List<Output> outputs;
    private Settings setting;

    public void setApp(Main application, List<Output> output, Settings settings){
        this.application = application;
        sliceColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSliceName()));
        counterColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCounter()));
        results.setItems(FXCollections.observableList(output));
        outputs = output;
        setting = settings;
        showSettingValue();
    }
    public void showSettingValue(){
        colorName.setText(setting.getColor());
        threaholdRange.setText(setting.getLowThresholdLevel()+"-"+setting.getUpperThresholdLevel());
        sizeRange.setText(setting.getLowerSize()+"-"+setting.getHigherSize());
        circularityRange.setText(setting.getLowerCircularity()+"-"+setting.getUpperCircularity());

    }
    /**
     * onAction 輸出CSV
     */
    @FXML
    @SneakyThrows
    public void outputCSV(){
        var output = outputs.stream()
                .map(eachOutput -> eachOutput.getSliceName() + "," +
                        eachOutput.getCounter()).collect(Collectors.toList());
        var headings = "slice" + "," + "counter";
        var filePath = setting.getFilePaths().get(0).substring(0,setting.getFilePaths().get(0).lastIndexOf("/")+1);
        File csvOutputFile = new File(filePath+"results.csv");
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            pw.println(headings);
            output.stream().forEach(pw::println);
        }
    }

    /**
     * 按 addBtn 跳出選擇上傳的圖片
     */
    @FXML
    public void reAnalysis(){
        application.goSelectSamplePage(setting);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

}
