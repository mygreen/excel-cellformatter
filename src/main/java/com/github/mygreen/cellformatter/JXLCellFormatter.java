package com.github.mygreen.cellformatter;

import java.util.Locale;

import jxl.Cell;
import jxl.CellType;

import com.github.mygreen.cellformatter.lang.ArgUtils;


/**
 * JExcel APIのセルのフォーマッタ
 * @author T.TSUCHIE
 *
 */
public class JXLCellFormatter {
    
    private FormatterResolver formatterResolver = new FormatterResolver();
    
    public String format(final Cell cell) {
        return format(cell, Locale.getDefault());
    }
    
    public String format(final Cell cell, final Locale locale) {
        
        ArgUtils.notNull(cell, "cell");
        
        if(cell.getType() == CellType.EMPTY) {
            return "";
            
        } else if(cell.getType() == CellType.LABEL || cell.getType() == CellType.STRING_FORMULA) {
            return cell.getContents();
            
        } else if(cell.getType() == CellType.BOOLEAN || cell.getType() == CellType.BOOLEAN_FORMULA) {
            return cell.getContents().toUpperCase();
        
        } else if(cell.getType() == CellType.ERROR || cell.getType() == CellType.FORMULA_ERROR) {
            return "";
            
        } else if(cell.getType() == CellType.DATE || cell.getType() == CellType.DATE_FORMULA) {
            return getFormatCellValue(cell, locale);
            
        } else if(cell.getType() == CellType.NUMBER || cell.getType() == CellType.NUMBER_FORMULA) {
            return getFormatCellValue(cell, locale);
            
        } else {
            return cell.getContents();
        }
        
    }
    
    /**
     * 数値型のセルの値を取得する。
     * @param cell
     * @param locale
     * @return
     */
    private String getFormatCellValue(final Cell cell, final Locale locale) {
        
        final JXLCell jxlCell = new JXLCell(cell);
        final short formatIndex = jxlCell.getFormatIndex();
        final String formatPattern = jxlCell.getFormatPattern();
        
        if(formatterResolver.canResolve(formatIndex)) {
            final CellFormatter cellFormatter = formatterResolver.getFormatter(formatIndex);
            return cellFormatter.format(jxlCell, locale);
            
        } else if(formatterResolver.canResolve(formatPattern)) {
            final CellFormatter cellFormatter = formatterResolver.getFormatter(formatPattern);
            return cellFormatter.format(jxlCell, locale);
            
        } else {
            // キャッシュに登録する。
            final CellFormatter cellFormatter = formatterResolver.createFormatter(formatPattern) ;
            formatterResolver.registerFormatter(formatPattern, cellFormatter);
            return cellFormatter.format(jxlCell, locale);
            
        }
        
    }
    
    public FormatterResolver getFormatterResolver() {
        return formatterResolver;
    }
    
    public void setFormatterResolver(FormatterResolver formatterResolver) {
        this.formatterResolver = formatterResolver;
    }
    
}
