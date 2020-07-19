package models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 前端傳回的設定
 * @date 2020/06/06
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Settings {

    String color;
    Double lowThresholdLevel;
    Double upperThresholdLevel;
    Double lowerSize;
    Double higherSize;
    Double lowerCircularity;
    Double upperCircularity;
    Boolean saveOverlayMaskPics;
    List<String> filePaths;
    String savePathText;

}
