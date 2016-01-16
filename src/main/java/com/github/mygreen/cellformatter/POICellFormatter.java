package com.github.mygreen.cellformatter;

import java.util.Locale;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FormulaError;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;


/**
 * POIのセルの値を文字列として取得する。
 * 参考URL
 * <ul>
 *   <li><a href="http://www.ne.jp/asahi/hishidama/home/tech/apache/poi/cell.html" target="_blank">{@code ひしだま's 技術メモページ - Apache POI Cell : Cellの値の取得}</a></li>
 *   <li><a href="http://shin-kawara.seesaa.net/article/159663314.html" target="_blank">{@code POIでセルの値をとるのは大変　日付編}</a></li>
 * </ul>
 * 
 * @version 0.4
 * @author T.TSUCHIE
 *
 */
public class POICellFormatter {
    
    private FormatterResolver formatterResolver = new FormatterResolver();
    
    /**
     * パースしたフォーマッタをキャッシングするかどうか。
     */
    private boolean cache = true;
    
    /**
     * エラーセルの値を空文字として取得するかどうか。
     */
    private boolean errorCellAsEmpty = false;
    
    /**
     * セルの値を文字列として取得する
     * @param cell 取得対象のセル
     * @return フォーマットしたセルの値。 cellがnullの場合、空文字を返す。
     */
    public String formatAsString(final Cell cell) {
        return formatAsString(cell, Locale.getDefault());
    }
    
    /**
     * ロケールを指定してセルの値を文字列として取得する
     * @param cell フォーマット対象のセル
     * @param locale locale フォーマットしたロケール。nullでも可能。
     *        ロケールに依存する場合、指定したロケールにより自動的に切り替わります。
     * @return フォーマットした文字列。cellがnullの場合、空文字を返す。
     */
    public String formatAsString(final Cell cell, final Locale locale) {
        return format(cell, locale).getText();
    }
    
    /**
     * セルの値を取得する
     * @since 0.3
     * @param cell フォーマット対象のセル
     * @return フォーマット結果。cellがnullの場合、空文字を返す。
     */
    public CellFormatResult format(final Cell cell) {
        return format(cell, Locale.getDefault());
    }
    
    
    /**
     * ロケールを指定してセルの値を取得する
     * @since 0.3
     * @param cell フォーマット対象のセル
     * @param locale locale フォーマットしたロケール。nullでも可能。
     *        ロケールに依存する場合、指定したロケールにより自動的に切り替わります。
     * @return フォーマット結果。cellがnullの場合、空文字を返す。
     */
    private CellFormatResult format(final Cell cell, final Locale locale) {
        
        if(cell == null) {
            final CellFormatResult result = new CellFormatResult();
            result.setCellType(FormatCellType.Blank);
            result.setText("");
            return result;
        }
        
        final Locale runtimeLocale = locale != null ? locale : Locale.getDefault();
        
        switch(cell.getCellType()) {
            case Cell.CELL_TYPE_BLANK:
                // 結合しているセルの場合、左上のセル以外に値が設定されている場合がある。
                return getMergedCellValue(cell, runtimeLocale);
                
            case Cell.CELL_TYPE_BOOLEAN:
                return getOtherCellValue(cell, runtimeLocale);
                
            case Cell.CELL_TYPE_STRING:
                return getOtherCellValue(cell, runtimeLocale);
                
            case Cell.CELL_TYPE_NUMERIC:
                return getNumericCellValue(cell, runtimeLocale);
                
            case Cell.CELL_TYPE_FORMULA:
                return getFormulaCellValue(cell, runtimeLocale);
                
            case Cell.CELL_TYPE_ERROR:
                return getErrorCellValue(cell, runtimeLocale);
                
            default:
                final CellFormatResult result = new CellFormatResult();
                result.setCellType(FormatCellType.Unknown);
                result.setText("");
                return result;
        }
    }
    
    /**
     * 式が設定されているセルの値を評価する。
     * @param cell
     * @param locale
     * @return
     */
    private CellFormatResult getFormulaCellValue(final Cell cell, final Locale locale) {
        
        final int cellType = cell.getCellType();
        assert cellType == Cell.CELL_TYPE_FORMULA;
        
        final Workbook workbook = cell.getSheet().getWorkbook();
        final CreationHelper helper = workbook.getCreationHelper();
        final FormulaEvaluator evaluator = helper.createFormulaEvaluator();
        
        try {
            // 再帰的に処理する
            final Cell evalCell = evaluator.evaluateInCell(cell);
            return format(evalCell, locale);
        } catch(Exception e) {
            return getErrorCellValue(cell, locale);
        }
        
    }
    
    /**
     * エラーセルの値を評価する。
     * @param cell
     * @param locale
     * @return
     */
    private CellFormatResult getErrorCellValue(final Cell cell, final Locale locale) {
        
       final int cellType = cell.getCellType();
       assert cellType == Cell.CELL_TYPE_ERROR;
       
       final FormulaError error = FormulaError.forInt(cell.getErrorCellValue());
       
       final CellFormatResult result = new CellFormatResult();
       result.setCellType(FormatCellType.Error);
       result.setValue(error.getCode());
       
       if(isErrorCellAsEmpty()) {
           result.setText("");
       } else {
           result.setText(error.getString());
       }
       
       return result;
       
    }
    
    /**
     * 結合されているセルの値の取得。
     * <p>通常は左上のセルに値が設定されているが、結合されているときは左上以外のセルの値を取得する。
     * <p>左上以外のセルに値が設定されている場合は、CellTypeがCELL_TYPE_BLANKになるため注意が必要。
     * @param cell
     * @param locale
     * @return
     */
    private CellFormatResult getMergedCellValue(final Cell cell, final Locale locale) {
        
        final Sheet sheet = cell.getSheet();
        final int size = sheet.getNumMergedRegions();
        
        for(int i=0; i < size; i++) {
            final CellRangeAddress range = sheet.getMergedRegion(i);
            if(!range.isInRange(cell.getRowIndex(), cell.getColumnIndex())) {
                continue;
            }
            
            // 非BLANKまたはnullでないセルを取得する。
            for(int rowIdx=range.getFirstRow(); rowIdx <= range.getFirstRow(); rowIdx++) {
                final Row row = sheet.getRow(rowIdx);
                if(row == null) {
                    continue;
                }
                
                for(int colIdx=range.getFirstColumn(); colIdx <= range.getLastColumn(); colIdx++) {
                    final Cell valueCell = row.getCell(colIdx);
                    if(valueCell == null || valueCell.getCellType() == Cell.CELL_TYPE_BLANK) {
                        continue;
                    }
                    
                    return format(valueCell, locale);
                }
            }
            
        }
        
        final CellFormatResult result = new CellFormatResult();
        result.setCellType(FormatCellType.Blank);
        result.setText("");
        return result;
    }
    
    /**
     * 数値以外ののフォーマット
     * @return
     */
    private CellFormatResult getOtherCellValue(final Cell cell, final Locale locale) {
        
        final int cellType = cell.getCellType();
        assert cellType == Cell.CELL_TYPE_STRING || cellType == Cell.CELL_TYPE_BOOLEAN;
        
        final POICell poiCell = new POICell(cell);
        final short formatIndex = poiCell.getFormatIndex();
        final String formatPattern = poiCell.getFormatPattern();
        
        if(formatterResolver.canResolve(formatIndex)) {
            final CellFormatter cellFormatter = formatterResolver.getFormatter(formatIndex);
            return cellFormatter.format(poiCell, locale);
            
        } else if(formatterResolver.canResolve(formatPattern)) {
            final CellFormatter cellFormatter = formatterResolver.getFormatter(formatPattern);
            return cellFormatter.format(poiCell, locale);
            
        } else {
            // キャッシュに存在しない場合
            final CellFormatter cellFormatter = formatterResolver.createFormatter(formatPattern) ;
            if(isCache()) {
                formatterResolver.registerFormatter(formatPattern, cellFormatter);
            }
            return cellFormatter.format(poiCell, locale);
            
        }
        
    }
    
    /**
     * 数値型のセルの値を取得する。
     * <p>書式付きの数字か日付のどちらかの場合がある。
     * @param cell
     * @param locale
     * @return
     */
    private CellFormatResult getNumericCellValue(final Cell cell, final Locale locale) {
        
        final int cellType = cell.getCellType();
        assert cellType == Cell.CELL_TYPE_NUMERIC;
        
        final POICell poiCell = new POICell(cell);
        final short formatIndex = poiCell.getFormatIndex();
        final String formatPattern = poiCell.getFormatPattern();
        
        if(formatterResolver.canResolve(formatIndex)) {
            final CellFormatter cellFormatter = formatterResolver.getFormatter(formatIndex);
            return cellFormatter.format(poiCell, locale);
            
        } else if(formatterResolver.canResolve(formatPattern)) {
            final CellFormatter cellFormatter = formatterResolver.getFormatter(formatPattern);
            return cellFormatter.format(poiCell, locale);
            
        } else {
            // キャッシュに存在しない場合
            final CellFormatter cellFormatter = formatterResolver.createFormatter(formatPattern) ;
            if(isCache()) {
                formatterResolver.registerFormatter(formatPattern, cellFormatter);
            }
            return cellFormatter.format(poiCell, locale);
            
        }
    }
    
    /**
     * {@link FormatterResolver}を取得する。
     * @return
     */
    public FormatterResolver getFormatterResolver() {
        return formatterResolver;
    }
    
    /**
     * {@link FormatterResolver}を設定する。
     * 独自のものに入れ替える際に利用します。
     * @param formatterResolver
     */
    public void setFormatterResolver(FormatterResolver formatterResolver) {
        this.formatterResolver = formatterResolver;
    }
    
    /**
     * パースしたフォーマッタをキャッシュするかどうか。
     * 初期値はtrueです。
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
    
    /**
     * エラーセルの値を空文字として取得するかどうか。
     * 初期値はfalseです。
     * @since 0.4
     * @return
     */
    public boolean isErrorCellAsEmpty() {
        return errorCellAsEmpty;
    }
    
    /**
     * エラーセルの値を空文字として取得するかどうか設定する。
     * @since 0.4
     * @param errorCellAsEmpty true:空文字として取得する。
     */
    public void setErrorCellAsEmpty(boolean errorCellAsEmpty) {
        this.errorCellAsEmpty = errorCellAsEmpty;
    }
    
}
