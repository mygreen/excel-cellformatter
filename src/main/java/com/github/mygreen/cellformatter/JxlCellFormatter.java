package com.github.mygreen.cellformatter;

import java.util.Locale;

import jxl.Cell;
import jxl.CellType;

import com.github.mygreen.cellformatter.lang.ArgUtils;
import com.github.mygreen.cellformatter.lang.Utils;


/**
 * JExcel APIのセルのフォーマッタ
 * @author T.TSUCHIE
 *
 */
public class JxlCellFormatter {
    
    private FormatterResolver formatterResolver = new FormatterResolver();
    
    public String format(final Cell cell) {
        return format(cell, Locale.getDefault());
    }
    
    public String format(final Cell cell, final Locale locale) {
        
        ArgUtils.notNull(cell, "cell");
        
        if(cell.getType().equals(CellType.EMPTY)) {
            return "";
            
        } else if(cell.getType().equals(CellType.STRING_FORMULA)) {
            return cell.getContents();
            
        } else if(cell.getType().equals(CellType.BOOLEAN) || cell.getType().equals(CellType.BOOLEAN_FORMULA)) {
            return cell.getContents().toUpperCase();
        
        } else if(cell.getType().equals(CellType.ERROR) || cell.getType().equals(CellType.FORMULA_ERROR)) {
            return "";
            
        } else if(cell.getType().equals(CellType.DATE) || cell.getType().equals(CellType.DATE_FORMULA)) {
            return getFormatCellValue(cell, locale);
            
        } else if(cell.getType().equals(CellType.NUMBER) || cell.getType().equals(CellType.NUMBER_FORMULA)) {
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
        
        final JxlCell jxlCell = new JxlCell(cell);
        final String formatPattern = jxlCell.getFormatPattern();
        if(Utils.isEmpty(formatPattern)) {
            return cell.getContents();
        }
        
        if(formatterResolver.canResolve(formatPattern)) {
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
