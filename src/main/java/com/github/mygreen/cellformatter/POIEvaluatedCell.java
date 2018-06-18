package com.github.mygreen.cellformatter;

import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;

import com.github.mygreen.cellformatter.lang.ExcelDateUtils;

/**
 * POI用の数式の値を評価したセルのラッパークラス。
 *
 * @version 0.10
 * @since 0.8.3
 * @author T.TSUCHIE
 *
 */
public class POIEvaluatedCell extends POICell {

    private final CellValue value;

    /**
     * セルと評価した結果を
     * @param cell タイプが数式のセル。
     * @param value 数式を評価した結果。
     */
    public POIEvaluatedCell(final Cell cell, final CellValue value) {
        super(cell);
        this.value = value;

    }

    /**
     * 式を評価したセルの値を取得する。
     * @return
     */
    public CellValue getCellValue() {
        return value;
    }

    @Override
    public boolean isText() {
        return value.getCellTypeEnum() == CellType.STRING;
    }

    @Override
    public String getTextCellValue() {
        return value.getStringValue();
    }

    @Override
    public boolean isBoolean() {
        return value.getCellTypeEnum() == CellType.BOOLEAN;
    }

    @Override
    public boolean getBooleanCellValue() {
        return value.getBooleanValue();
    }

    @Override
    public boolean isNumber() {
        return value.getCellTypeEnum() == CellType.NUMERIC;
    }

    @Override
    public double getNumberCellValue() {
        return value.getNumberValue();
    }

    @Override
    public Date getDateCellValue() {
        return ExcelDateUtils.convertJavaDate(getNumberCellValue(), isDateStart1904());

    }

}
