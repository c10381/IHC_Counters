package controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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

    private List<Output> outputs;
    private Settings setting;

    public void setApp(Main application, List<Output> output, Settings settings){
        this.application = application;
        sliceColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSlice()));
        counterColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCounter()));
        results.setItems(FXCollections.observableList(output));
        outputs = output;
        setting = settings;
    }

    /**
     * onAction 輸出CSV
     */
    @FXML
    @SneakyThrows
    public void outputCSV(){
        var output = outputs.stream()
                .map(eachOutput -> eachOutput.getSlice().replace(",", " ") + "," +
                        eachOutput.getCounter()).collect(Collectors.toList());
        var headings = "slice" + "," + "counter";
        System.out.println(setting.getSavePathText());
        File csvOutputFile = new File(setting.getSavePathText()+"\\results.csv");
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            pw.println(headings);
            output.stream().forEach(pw::println);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) { }

}
