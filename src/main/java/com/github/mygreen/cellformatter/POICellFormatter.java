package com.github.mygreen.cellformatter;

import java.util.Locale;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FormulaError;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;


/**
 * Apache POIのセルの値を文字列として取得するためのクラス。
 * 
 * 
 * <h3 class="description">基本的な使い方</h3> 
 * <p>{@link POICellFormatter}のインスタンスを生成して利用します。</p>
 * <ul>
 *   <li>結果を単純に文字列で取得したい場合は、{@link #formatAsString(Cell)}を利用します。</li>
 *   <li>フォーマット対象のセルの値や書式に適用された文字色などを取得したい場合は、
 *       {@link #format(Cell)}の結果である{@link CellFormatResult}から取得します。</li>
 *   <li>書式「{@literal m/d/yy}」など、実行環境の言語設定によって切り替わるような場合は、
 *       {@link #formatAsString(Cell, Locale)}でロケールを直接指定します。</li>
 * </ul>
 * 
 * <pre class="highlight"><code class="java">
 * POICellFormatter  cellFormatter = new POICellFormatter ();
 * 
 * Cell cell = // セルの取得
 * String text1 = cellForrmatter.formatAsString(cell);
 * 
 * // ロケールに依存する書式の場合
 * String text2 = cellForrmatter.formatAsString(cell, Locale.US);
 *
 * // 文字色の条件が設定されている場合
 * CellFormatResult result = cellForrmatter.format(cell);
 * String text3 = result.getText(); // フォーマット結果の文字列
 * MSColor textColor = result.getTextColor(); // 書式の文字色
 * </code></pre>
 * 
 * <h3 class="description">注意事項</h3>
 * <ul>
 *   <li>Cellのインスタンスがnullの場合、空（Blank）セルとして扱います。
 *       <br>POIの場合、データの入力がない領域のセルは、nullとなるためです。</li>
 *   <li>結合されたセルの場合、結合領域を走査し、非空セルがそのセルの値を評価します。
 *       <br>POIの場合、結合されたセルの領域は、基本的に左上のセルに値が設定され、それ以外のセルは空セルとなるためです。</li>
 *   <li>数式や関数が設定されたセルの場合、それらを評価した結果を返します。
 *       <br>POIが対応していない数式や関数の場合、Excel上では正しく表示されていても、エラーセルの扱いとなります。
 *       <br>使用するPOIのバージョンによって対応する関数も異なります。</li>
 * </ul>
 * 
 * @see <a href="http://www.ne.jp/asahi/hishidama/home/tech/apache/poi/cell.html" target="_blank">ひしだま's 技術メモページ - Apache POI Cell : Cellの値の取得</a>
 * @see <a href="http://shin-kawara.seesaa.net/article/159663314.html" target="_blank">POIでセルの値をとるのは大変　日付編</a>
 * 
 * @version 0.8.3
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
     * 式を評価する際に失敗したときに、例外をスローするかどうか。
     */
    private boolean throwFailEvaluateFormula = false;
    
    /**
     * 結合セルを考慮するかどうか。
     */
    private boolean considerMergedCell = true;
    
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
     * @return フォーマット結果。cellがnullの場合、空セルとして値を返す。
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
     * @return フォーマット結果。cellがnullの場合、空セルとして値を返す。
     */
    public CellFormatResult format(final Cell cell, final Locale locale) {
        
        if(cell == null) {
            return createBlankCellResult();
        }
        
        final Locale runtimeLocale = locale != null ? locale : Locale.getDefault();
        
        switch(cell.getCellType()) {
            case Cell.CELL_TYPE_BLANK:
                if(isConsiderMergedCell()) {
                    // 結合しているセルの場合、左上のセル以外に値が設定されている場合がある。
                    return getMergedCellValue(cell, runtimeLocale);
                } else {
                    return createBlankCellResult();
                }
                
            case Cell.CELL_TYPE_BOOLEAN:
                return getCellValue(cell, runtimeLocale);
                
            case Cell.CELL_TYPE_STRING:
                return getCellValue(cell, runtimeLocale);
                
            case Cell.CELL_TYPE_NUMERIC:
                return getCellValue(cell, runtimeLocale);
                
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
     * ブランクセルの結果を作成する。
     * @since 0.7
     * @return
     */
    private CellFormatResult createBlankCellResult() {
        CellFormatResult result = new CellFormatResult();
        result.setCellType(FormatCellType.Blank);
        result.setText("");
        return result;
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
            final CellValue value = evaluator.evaluate(cell);
            final POIEvaluatedCell evaluatedCell = new POIEvaluatedCell(cell, value);
            
            switch(value.getCellType()) {
                
                case Cell.CELL_TYPE_BOOLEAN:
                    return getCellValue(evaluatedCell, locale);
                    
                case Cell.CELL_TYPE_STRING:
                    return getCellValue(evaluatedCell, locale);
                    
                case Cell.CELL_TYPE_NUMERIC:
                    return getCellValue(evaluatedCell, locale);
                    
                case Cell.CELL_TYPE_ERROR:
                    return getErrorCellValue(value.getErrorValue(), locale);
                    
                default:
                    final CellFormatResult result = new CellFormatResult();
                    result.setCellType(FormatCellType.Unknown);
                    result.setText("");
                    return result;
            }
            
        } catch(Exception e) {
            if(isThrowFailEvaluateFormula()) {
                throw new FormulaEvaluateException(cell, e);
            } else {
                return getErrorCellValue(cell, locale);
            }
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
        
        return getErrorCellValue(cell.getErrorCellValue(), locale);
    }
    
    /**
     * エラーセルの値を評価する。
     * @since 0.8.3
     * @param errorValue エラーセルの値。
     * @param locale
     * @return
     */
    private CellFormatResult getErrorCellValue(final byte errorValue, final Locale locale) {
        
       final FormulaError error = FormulaError.forInt(errorValue);
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
            for(int rowIdx=range.getFirstRow(); rowIdx <= range.getLastRow(); rowIdx++) {
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
        
        return createBlankCellResult();
    }
    
    /**
     * セルの値をフォーマットする。
     * @param cell フォーマット対象のセル
     * @param locale ロケール
     * @return フォーマットした結果
     */
    private CellFormatResult getCellValue(final Cell cell, final Locale locale) {
        return getCellValue(new POICell(cell), locale);
    }
    
    /**
     * セルの値をフォーマットする。
     * @param poiCell フォーマット対象のセル
     * @param locale ロケール
     * @return フォーマットした結果
     */
    private CellFormatResult getCellValue(final POICell poiCell, final Locale locale) {
        
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
    
    /**
     * 式を評価する際に失敗したときに、例外{@link FormulaEvaluateException}をスローするかどうか。
     * <p>初期値はfalseで、式の評価に失敗したときは、エラーセルとして扱われます。
     * @since 0.7
     * @return true: 例外をスローする。
     */
    public boolean isThrowFailEvaluateFormula() {
        return throwFailEvaluateFormula;
    }
    
    /**
     * 式を評価する際に失敗したときに、例外{@link FormulaEvaluateException}をスローするかどうか設定する。
     * @since 0.7
     * @param throwFailEvaluateFormula true: 例外をスローする。
     */
    public void setThrowFailEvaluateFormula(boolean throwFailEvaluateFormula) {
        this.throwFailEvaluateFormula = throwFailEvaluateFormula;
    }
    
    /**
     * 結合されたセルを考慮するかどうか。
     * <p>POIの場合、結合されている領域は、左上のセル以外はブランクセルとなるため、値が設定してあるセルを操作する必要がある。
     * <p>初期値はtrueで、ブランクセルを結合セルと見なして処理を行います。
     * @since 0.7
     * @return true: 結合セルを考慮する。
     */
    public boolean isConsiderMergedCell() {
        return considerMergedCell;
    }
    
    /**
     * 結合されたセルを考慮するかどうか。
     * <p>POIの場合、結合されている領域は、左上のセル以外はブランクセルとなるため、値が設定してあるセルを走査する必要がある。
     * @since 0.7
     * @param considerMergedCell true:結合セルを考慮して処理を行う。
     */
    public void setConsiderMergedCell(boolean considerMergedCell) {
        this.considerMergedCell = considerMergedCell;
    }
    
}
