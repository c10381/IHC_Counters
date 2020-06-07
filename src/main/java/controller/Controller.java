package controller;

import javafx.scene.image.Image;
import models.Settings;

import java.io.FileInputStream;
import java.io.FileNotFoundException;


public class Controller {

    /**
     * 取得 前端設定 的資料以及所有 上傳檔案的路徑
     * @param settings
     */
    public void settings(Settings settings){

    }

    /**
     * 當使用者點選圖片位置後 到檔案去撈圖片顯示
     * @param filePath
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
}
