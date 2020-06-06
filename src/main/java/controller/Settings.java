package controller;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 前端傳回的設定
 * @date 2020/06/06
 */
@Builder
@Data
@NoArgsConstructor
public class Settings {

    String color;
    Integer lowThresholdLevel;
    Integer upperThresholdLevel;
    Integer lowerSize;
    Integer higherSize;
    BigDecimal lowerCircularity;
    BigDecimal upperCircularity;
    Boolean saveOverlayMaskPics;
    List<String> filePaths;

}
