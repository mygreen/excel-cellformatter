package com.github.mygreen.cellformatter;

import java.util.Date;

/**
 * テスト用の数値用のセル。
 * @since 0.3
 * @author T.TSUCHIE
 *
 */
public class TestNumberCell implements CommonCell {
    
    private final double value;
    
    private final short formatIndex;
    
    private final String formatPattern;
    
    public TestNumberCell(final double value, final short formatIndex, final String formatPattern) {
        this.value = value;
        this.formatIndex = formatIndex;
        this.formatPattern = formatPattern;
        
    }
    
    @Override
    public short getFormatIndex() {
        return formatIndex;
    }
    
    @Override
    public String getFormatPattern() {
        return formatPattern;
    }
    
    @Override
    public boolean isText() {
        return false;
    }
    
    @Override
    public String getTextCellValue() {
        return "";
    }
    
    @Override
    public boolean isBoolean() {
        return false;
    }
    
    @Override
    public boolean getBooleanCellValue() {
        return false;
    }
    
    @Override
    public boolean isNumber() {
        return true;
    }
    
    @Override
    public double getNumberCellValue() {
        return value;
    }
    
    @Override
    public Date getDateCellValue() {
        return null;
    }
    
    @Override
    public boolean isDateStart1904() {
        return false;
    }
    
    @Override
    public String getCellAddress() {
        return "A1";
    }
}
