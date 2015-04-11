package com.github.mygreen.cellformatter;

import java.util.Locale;

import jxl.Cell;
import jxl.CellType;

import com.github.mygreen.cellformatter.lang.ArgUtils;
import com.github.mygreen.cellformatter.lang.JXLUtils;
import com.github.mygreen.cellformatter.lang.Utils;


/**
 * JExcel APIのセルのフォーマッタ。
 * @version 0.3
 * @author T.TSUCHIE
 *
 */
public class JXLCellFormatter {
    
    private FormatterResolver formatterResolver = new FormatterResolver();
    
    /**
     * パースしたフォーマッタをキャッシングするかどうか。
     */
    private boolean cache = true;
    
    /**
     * セルの値をフォーマットし、文字列として取得する
     * @param cell フォーマット対象のセル
     * @param isStartDate1904 ファイルの設定が1904年始まりかどうか。
     *        {@link JXLUtils#isDateStart1904(jxl.Sheet)}で値を調べます。
     * @return フォーマットしたセルの値。
     * @throws IllegalArgumentException cell is null.
     */
    public String formatAsString(final Cell cell, final boolean isStartDate1904) {
        return formatAsString(cell, Locale.getDefault(), isStartDate1904);
    }
    
    /**
     * ロケールを指定してセルの値をフォーマットし、文字列として取得する
     * @param cell フォーマット対象のセル
     * @param locale フォーマットしたロケール。nullでも可能。
     *        ロケールに依存する場合、指定したロケールにより自動的に切り替わります。
     * @param isStartDate1904 ファイルの設定が1904年始まりかどうか。
     *        {@link JXLUtils#isDateStart1904(jxl.Sheet)}で値を調べます。
     * @return フォーマットしたセルの値。
     * @throws IllegalArgumentException cell is null.
     */
    public String formatAsString(final Cell cell, final Locale locale, final boolean isStartDate1904) {        
        ArgUtils.notNull(cell, "cell");
        
        return format(cell, locale, isStartDate1904).getText();
        
    }
    
    /**
     * セルの値をフォーマットする。
     * @since 0.3
     * @param cell フォーマット対象のセル
     * @param isStartDate1904 ファイルの設定が1904年始まりかどうか。
     *        {@link JXLUtils#isDateStart1904(jxl.Sheet)}で値を調べます。
     * @return フォーマットしたセルの値。
     * @throws IllegalArgumentException cell is null.
     */
    public CellFormatResult format(final Cell cell, final boolean isStartDate1904) {        
        ArgUtils.notNull(cell, "cell");
        return format(cell, Locale.getDefault(), isStartDate1904);
    }
    
    /**
     * ロケールを指定してセルの値をフォーマットする。
     * @since 0.3
     * @param cell フォーマット対象のセル
     * @param locale フォーマットしたロケール。nullでも可能。
     *        ロケールに依存する場合、指定したロケールにより自動的に切り替わります。
     * @param isStartDate1904 ファイルの設定が1904年始まりかどうか。
     *        {@link JXLUtils#isDateStart1904(jxl.Sheet)}で値を調べます。
     * @return フォーマットしたセルの値。
     * @throws IllegalArgumentException cell is null.
     */
    public CellFormatResult format(final Cell cell, final Locale locale, final boolean isStartDate1904) {        
        ArgUtils.notNull(cell, "cell");
        
        if(cell.getType() == CellType.EMPTY) {
            return CellFormatResult.noFormatResult("");
            
        } else if(cell.getType() == CellType.LABEL || cell.getType() == CellType.STRING_FORMULA) {
            return getOtherCellValue(cell, locale, isStartDate1904);
            
        } else if(cell.getType() == CellType.BOOLEAN || cell.getType() == CellType.BOOLEAN_FORMULA) {
            return getOtherCellValue(cell, locale, isStartDate1904);
        
        } else if(cell.getType() == CellType.ERROR || cell.getType() == CellType.FORMULA_ERROR) {
            return CellFormatResult.noFormatResult("");
            
        } else if(cell.getType() == CellType.DATE || cell.getType() == CellType.DATE_FORMULA) {
            return getNumericCellValue(cell, locale, isStartDate1904);
            
        } else if(cell.getType() == CellType.NUMBER || cell.getType() == CellType.NUMBER_FORMULA) {
            return getNumericCellValue(cell, locale, isStartDate1904);
            
        } else {
            return CellFormatResult.noFormatResult(cell.getContents());
        }
        
    }
    
    /**
     * 数値型以外のセルの値を取得する
     * @param cell
     * @param locale
     * @param isStartDate1904
     * @return
     */
    private CellFormatResult getOtherCellValue(final Cell cell, final Locale locale, final boolean isStartDate1904) {
        
        final JXLCell jxlCell = new JXLCell(cell, isStartDate1904);
        final short formatIndex = jxlCell.getFormatIndex();
        final String formatPattern = jxlCell.getFormatPattern();
        
        if(formatterResolver.canResolve(formatIndex)) {
            final CellFormatter cellFormatter = formatterResolver.getFormatter(formatIndex);
            return cellFormatter.format(jxlCell, locale);
            
        } else if(formatterResolver.canResolve(formatPattern)) {
            final CellFormatter cellFormatter = formatterResolver.getFormatter(formatPattern);
            return cellFormatter.format(jxlCell, locale);
            
        } else if(Utils.isNotEmpty(formatPattern)) {
            final CellFormatter cellFormatter = formatterResolver.createFormatter(formatPattern) ;
            if(isCache()) {
                formatterResolver.registerFormatter(formatPattern, cellFormatter);
            }
            return cellFormatter.format(jxlCell, locale);
            
        } else {
            // 書式を持たない場合は、そのまま返す。
            return CellFormatResult.noFormatResult(jxlCell.getTextCellValue());
        }
    }
    
    /**
     * 数値型のセルの値を取得する。
     * @param cell
     * @param locale
     * @param isStartDate1904
     * @return
     */
    private CellFormatResult getNumericCellValue(final Cell cell, final Locale locale, final boolean isStartDate1904) {
        
        final JXLCell jxlCell = new JXLCell(cell, isStartDate1904);
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
    
    /**
     * パースしたフォーマッタをキャッシュするかどうか。
     * @return
     */
    public boolean isCache() {
        return cache;
    }
    
    /**
     * パースしたフォーマッタをキャッシュするかどうか設定する。
     * @param cache true:キャッシュする。
     */
    public void setCache(boolean cache) {
        this.cache = cache;
    }
    
}
