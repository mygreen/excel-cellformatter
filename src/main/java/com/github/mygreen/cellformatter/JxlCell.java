package com.github.mygreen.cellformatter;

import java.util.Date;

import com.github.mygreen.cellformatter.lang.ArgUtils;
import com.github.mygreen.cellformatter.lang.Utils;

import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;
import jxl.NumberCell;
import jxl.StringFormulaCell;
import jxl.format.CellFormat;
import jxl.format.Format;


/**
 * JExcel APIのセル
 * @author T.TSUCHIE
 *
 */
public class JxlCell implements CommonCell {
    
    private final Cell cell;
    
    public JxlCell(final Cell cell) {
        ArgUtils.notNull(cell, "cell");
        this.cell = cell;
    }
    
    public Cell getCell() {
        return cell;
    }
    
    /**
     * 常に'0'を返す。
     */
    @Override
    public short getFormatIndex() {
        return 0;
    }
    
    @Override
    public String getFormatPattern() {
        
     // セルのスタイル情報の取得
        final CellFormat cellStyle = cell.getCellFormat();
        if(cellStyle == null) {
            return "";
        }
        
        // セルのフォーマットの取得
        final Format cellFormat = cellStyle.getFormat();
        if(cellFormat == null) {
            return "";
        }
        
        final String formatPattern = cellFormat.getFormatString();
        if(Utils.isEmpty(formatPattern)) {
            return "";
        }
        
        return formatPattern;
    }
    
    @Override
    public boolean isString() {
        return cell.getType().equals(CellType.STRING_FORMULA);
    }
    
    @Override
    public String getStringCellValue() {
        
        if(isString()) {
            return ((StringFormulaCell) cell).getString();
        } else {
            return cell.getContents();
        }
    }
    
    @Override
    public double getNumberCellValue() {
        
        if(cell.getType().equals(CellType.NUMBER) || cell.getType().equals(CellType.NUMBER_FORMULA)) {
            return ((NumberCell) cell).getValue();
        } else {
            return 0;
        }
    }
    
    @Override
    public Date getDateCellValue() {
        if(cell.getType().equals(CellType.DATE) || cell.getType().equals(CellType.DATE_FORMULA)) {
            return ((DateCell) cell).getDate();
        } else {
            return new Date(0);
        }
    }
    
}
