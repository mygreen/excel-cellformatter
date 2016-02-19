package com.github.mygreen.cellformatter;

import java.util.Locale;

import com.github.mygreen.cellformatter.lang.ArgUtils;
import com.github.mygreen.cellformatter.lang.JXLUtils;

import jxl.Cell;
import jxl.CellType;
import jxl.ErrorCell;


/**
 * JExcel APIのセルのフォーマッタ。
 * 
 * <pre class="highlight"><code class="java">
 * // シートの読み込み
 * final WorkbookSettings settings = new WorkbookSettings();
 * settings.setSuppressWarnings(true);
 * settings.setGCDisabled(true);
 * 
 * // 文字コードを「ISO8859_1」にしないと、会計の記号が文字化けする
 * settings.setEncoding("ISO8859_1");
 * 
 * final Workbook workbook = Workbook.getWorkbook(in, settings);
 * 
 * // 基本的な使い方。
 * JXLCellFormatter   cellFormatter = new JXLCellFormatter  ();
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
 * 
 * // 1904年始まりのシートの場合、JXLUtils.isDateStart1904(...) を使って判定を行います。
 * String text4 = cellForrmatter.formatAsString(cell, JXLUtils.isDateStart1904(sheet));
 * </code></pre>
 * 
 * @version 0.6
 * @since 0.4
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
     * エラーセルの値を空文字として取得するかどうか。
     */
    private boolean errorCellAsEmpty = false;
    
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
        
        final Locale runtimeLocale = locale != null ? locale : Locale.getDefault();
        final CellType cellType = cell.getType();
        
        if(cellType == CellType.EMPTY) {
            final CellFormatResult result = new CellFormatResult();
            result.setCellType(FormatCellType.Blank);
            result.setText("");
            return result;
            
        } else if(cellType == CellType.LABEL || cellType == CellType.STRING_FORMULA) {
            return getOtherCellValue(cell, runtimeLocale, isStartDate1904);
            
        } else if(cellType == CellType.BOOLEAN || cellType == CellType.BOOLEAN_FORMULA) {
            return getOtherCellValue(cell, runtimeLocale, isStartDate1904);
        
        } else if(cellType == CellType.ERROR || cellType == CellType.FORMULA_ERROR) {
            return getErrorCellValue(cell, runtimeLocale, isStartDate1904);
            
        } else if(cellType == CellType.DATE || cellType == CellType.DATE_FORMULA) {
            return getNumericCellValue(cell, runtimeLocale, isStartDate1904);
            
        } else if(cellType == CellType.NUMBER || cellType == CellType.NUMBER_FORMULA) {
            return getNumericCellValue(cell, runtimeLocale, isStartDate1904);
            
        } else {
            final CellFormatResult result = new CellFormatResult();
            result.setCellType(FormatCellType.Unknown);
            result.setText("");
            return result;
        }
        
    }
    
    
    /**
     * エラー型のセルの値を取得する。
     * @since 0.4
     * @param cell
     * @param locale
     * @param isStartDate1904
     * @return
     */
    private CellFormatResult getErrorCellValue(final Cell cell, final Locale locale, final boolean isStartDate1904) {
        
        final CellFormatResult result = new CellFormatResult();
        result.setCellType(FormatCellType.Error);
        
        final ErrorCell errorCell = (ErrorCell) cell;
        final int errorCode = errorCell.getErrorCode();
        result.setValue(errorCode);
        
        if(isErrorCellAsEmpty()) {
            result.setText("");
            
        } else {
            /*
             * エラーコードについては、POIクラスを参照。
             * ・org.apache.poi.ss.usermodel.FormulaError
             * ・org.apache.poi.ss.usermodel.ErrorConstants
             */
            switch(errorCode) {
                case 7:
                    // 0除算
                    result.setText("#DIV/0!");
                    break;
                    
                case 42:
                    // 関数や数式に使用できる値がない
                    result.setText("#N/A");
                    break;
                    
                case 29:
                    // 数式が参照している名称がない
                    result.setText("#NAME?");
                    break;
                    
                case 0:
                    // 正しくない参照演算子または正しくないセル参照を使っている
                    result.setText("#NULL!");
                    break;
                    
                case 36:
                    // 数式または関数の数値が不適切
                    result.setText("#NUM!");
                    break;
                    
                case 23:
                    // 数式が参照しているセルがない
                    result.setText("#REF!");
                    break;
                    
                case 15:
                    // 文字列が正しいデータ型に変換されない
                    result.setText("#VALUE!");
                    break;
                    
                default:
                    result.setText("");
                    break;
            }
            
        }
        
        return result;
        
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
            
        } else {
            // キャッシュに登録する。
            final CellFormatter cellFormatter = formatterResolver.createFormatter(formatPattern) ;
            if(isCache()) {
                formatterResolver.registerFormatter(formatPattern, cellFormatter);
            }
            return cellFormatter.format(jxlCell, locale);
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
            if(isCache()) {
                formatterResolver.registerFormatter(formatPattern, cellFormatter);
            }
            return cellFormatter.format(jxlCell, locale);
            
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
