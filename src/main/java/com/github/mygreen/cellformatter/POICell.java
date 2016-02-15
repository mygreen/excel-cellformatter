package com.github.mygreen.cellformatter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.TimeZone;

import org.apache.poi.hssf.model.InternalWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mygreen.cellformatter.lang.ArgUtils;


/**
 * POIのセル
 * @version 0.6
 * @since 0.4
 * @author T.TSUCHIE
 *
 */
public class POICell implements CommonCell {
    
    private static Logger logger = LoggerFactory.getLogger(POICell.class);
    
    private final Cell cell;
    
    public POICell(final Cell cell) {
        ArgUtils.notNull(cell, "cell");
        this.cell = cell;
    }
    
    public Cell getCell() {
        return cell;
    }
    
    @Override
    public short getFormatIndex() {
        final short formatIndex = getCell().getCellStyle().getDataFormat();
        return formatIndex;
    }
    
    @Override
    public String getFormatPattern() {
        final DataFormat dataFormat = cell.getSheet().getWorkbook().createDataFormat();
        final short formatIndex = getFormatIndex();
        
        String formatPattern = dataFormat.getFormat(formatIndex);
        if(formatPattern == null) {
            formatPattern = "";
        }
        
        return formatPattern;
        
    }
    
    @Override
    public boolean isText() {
        return cell.getCellType() == Cell.CELL_TYPE_STRING;
    }
    
    @Override
    public String getTextCellValue() {
        return cell.getStringCellValue();
    }
    
    @Override
    public boolean isBoolean() {
        return cell.getCellType() == Cell.CELL_TYPE_BOOLEAN;
    }
    
    @Override
    public boolean getBooleanCellValue() {
        return cell.getBooleanCellValue();
    }
    
    @Override
    public boolean isNumber() {
        return cell.getCellType() == Cell.CELL_TYPE_NUMERIC;
    }
    
    @Override
    public double getNumberCellValue() {
        return cell.getNumericCellValue();
    }
    
    @Override
    public Date getDateCellValue() {
        final Date date = cell.getDateCellValue();
        
        // タイムゾーン分、引かれているので調整する。
        return new Date(date.getTime() + TimeZone.getDefault().getRawOffset());
    }
    
    @Override
    public boolean isDateStart1904() {
        
        final Workbook workbook = cell.getSheet().getWorkbook();
        if(workbook instanceof HSSFWorkbook) {
            try {
                Method method = HSSFWorkbook.class.getDeclaredMethod("getWorkbook");
                method.setAccessible(true);
                
                InternalWorkbook iw = (InternalWorkbook) method.invoke(workbook);
                return iw.isUsing1904DateWindowing();
                
            } catch(NoSuchMethodException | SecurityException e) {
                logger.warn("fail access method HSSFWorkbook.getWorkbook.", e);
                return false;
            } catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                logger.warn("fail invoke method HSSFWorkbook.getWorkbook.", e);
                return false;
            }
            
        } else if(workbook instanceof XSSFWorkbook) {
            try {
                Method method = XSSFWorkbook.class.getDeclaredMethod("isDate1904");
                method.setAccessible(true);
                
                boolean value = (boolean) method.invoke(workbook);
                return value;
                
            } catch(NoSuchMethodException | SecurityException e) {
                logger.warn("fail access method XSSFWorkbook.isDate1904.", e);
                return false;
            } catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                logger.warn("fail invoke method XSSFWorkbook.isDate1904.", e);
                return false;
            }
            
        } else {
            logger.warn("unknown workbook type.", workbook.getClass().getName());
        }
        
        return false;
    }
    
    @Override
    public String getCellAddress() {
        return CellReference.convertNumToColString(cell.getColumnIndex()) + String.valueOf(cell.getRowIndex()+1);
    }
    
}
