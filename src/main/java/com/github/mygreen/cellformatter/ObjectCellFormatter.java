package com.github.mygreen.cellformatter;

import java.util.Date;
import java.util.Locale;

/**
 * Javaのオブジェクト型を直接フォーマットするクラス。
 * <p>Excelの基本型である「文字列型」「ブール型」「数値型」「日付型」の4つをサポートします。</p>
 * <p>数値型については、Javaの{@link Number}を継承している標準クラスに対応しています。</p>
 * <ul>
 *  <li>プリミティブ型：byte/shrot/int/long/float/double</li>
 *  <li>ラッパークラス：Byte/Short/Integer/Long/Float/Double</li>
 *  <li>その他：AtomicInteger/AtomicLong/BigDecimal/BigInteger</li>
 * </ul>
 * 
 * <pre class="highlight"><code class="java">
 * // 基本的な使い方。
 * ObjectCellFormatter cellFormatter = new ObjectCellFormatter();
 * Date date = Timestamp.valueOf("2012-02-01 12:10:00.000");
 * 
 * // 各型に対応したインタフェースを利用します。
 * String text = cellFormatter.formatAsString("yyyy\"年\"m\"月\"d\"日\";@", date);
 * 
 * // 細かく指定したい場合。
 * // 仮想的なセルのクラス「ObejctCell」の、型に合った具象クラスを利用します。
 * ObejctCell cell = new DateCell(date, "yyyy\"年\"m\"月\"d\"日\";@", false)
 * CellFormatResult result = cellFormatter.format(cell);
 * String text = result.getText(); // フォーマットした文字列の取得
 * MSColor color = result.getTextColor(); // 文字色が設定されている場合、その色の取得。
 * </code></pre>
 * 
 * 
 * @since 0.6
 * @author T.TSUCHIE
 *
 */
public class ObjectCellFormatter {
    
    private FormatterResolver formatterResolver = new FormatterResolver();
    
    /**
     * パースしたフォーマッタをキャッシングするかどうか。
     */
    private boolean cache = true;
    
    /**
     * 文字列型をフォーマットし、結果を直接文字列として取得する。
     * @param formatPattern フォーマットの書式。
     * @param value フォーマット対象の値。
     * @return フォーマットした結果の文字列。
     */
    public String formatAsString(final String formatPattern, final String value) {
        return format(formatPattern, value).getText();
    }
    
    /**
     * 文字列型をフォーマットする。
     * @param formatPattern フォーマットの書式。
     * @param value フォーマット対象の値。
     * @return フォーマットした結果。
     */
    public CellFormatResult format(final String formatPattern, final String value) {
        return format(formatPattern, value, Locale.getDefault());
    }
    
    /**
     * ロケールを指定して、文字列型をフォーマットし、結果を直接文字列として取得する。
     * @param formatPattern フォーマットの書式。
     * @param value フォーマット対象の値。
     * @param locale ロケール。書式にロケール条件の記述（例. {@code [$-403]}）が含まれている場合は、書式のロケールが優先されます。
     * @return フォーマットした結果の文字列。
     */
    public String formatAsString(final String formatPattern, final String value, final Locale locale) {
        return format(formatPattern, value, locale).getText();
    }
    
    /**
     * ロケールを指定して、文字列型をフォーマットする。
     * @param formatPattern フォーマットの書式。
     * @param value フォーマット対象の値。
     * @param locale ロケール。書式にロケール条件の記述（例. {@code [$-403]}）が含まれている場合は、書式のロケールが優先されます。
     * @return フォーマットした結果。
     */
    public CellFormatResult format(final String formatPattern, final String value, final Locale locale) {
        return format(new TextCell(value, formatPattern), locale);
    }
    
    /**
     * ブール型をフォーマットし、結果を直接文字列として取得する。
     * @param formatPattern フォーマットの書式。
     * @param value フォーマット対象の値。
     * @return フォーマットした結果の文字列。
     */    
    public String formatAsString(final String formatPattern, final boolean value, final Locale locale) {
        return format(formatPattern, value, locale).getText();
    }
    
    /**
     * ブール型をフォーマットする。
     * @param formatPattern フォーマットの書式。
     * @param value フォーマット対象の値。
     * @return フォーマットした結果。
     */
    public CellFormatResult format(final String formatPattern, final boolean value, final Locale locale) {
        return format(new BooleanCell(value, formatPattern), locale);
    }
    
    /**
     * ロケールを指定して、ブール型をフォーマットし、結果を直接文字列として取得する。
     * @param formatPattern フォーマットの書式。
     * @param value フォーマット対象の値。
     * @param locale ロケール。書式にロケール条件の記述（例. {@code [$-403]}）が含まれている場合は、書式のロケールが優先されます。
     * @return フォーマットした結果の文字列。
     */    
    public String formatAsString(final String formatPattern, final boolean value) {
        return format(formatPattern, value).getText();
    }
    
    /**
     * ロケールを指定して、ブール型をフォーマットする。
     * @param formatPattern フォーマットの書式。
     * @param value フォーマット対象の値。
     * @param locale ロケール。書式にロケール条件の記述（例. {@code [$-403]}）が含まれている場合は、書式のロケールが優先されます。
     * @return フォーマットした結果。
     */
    public CellFormatResult format(final String formatPattern, final boolean value) {
        return format(formatPattern, value, Locale.getDefault());
    }
    
    /**
     * 数値型をフォーマットし、結果を直接文字列として取得する。
     * @param formatPattern フォーマットの書式。
     * @param value フォーマット対象の値。
     * @return フォーマットした結果の文字列。
     */
    public <N extends Number> String formatAsString(final String formatPattern, final N value, final Locale locale) {
        return format(formatPattern, value, locale).getText();
    }
    
    /**
     * 数値列型をフォーマットする。
     * @param formatPattern フォーマットの書式。
     * @param value フォーマット対象の値。
     * @return フォーマットした結果。
     */
    public <N extends Number> CellFormatResult format(final String formatPattern, final N value, final Locale locale) {
        return format(new NumberCell<N>(value, formatPattern), locale);
    }
    
    /**
     * ロケールを指定して、数値型をフォーマットし、結果を直接文字列として取得する。
     * @param formatPattern フォーマットの書式。
     * @param value フォーマット対象の値。
     * @param locale ロケール。書式にロケール条件の記述（例. {@code [$-403]}）が含まれている場合は、書式のロケールが優先されます。
     * @return フォーマットした結果の文字列。
     */
    public <N extends Number>String formatAsString(final String formatPattern, final N value) {
        return format(formatPattern, value).getText();
    }
    
    /**
     * ロケールを指定して、数値型をフォーマットする。
     * @param formatPattern フォーマットの書式。
     * @param value フォーマット対象の値。
     * @param locale ロケール。書式にロケール条件の記述（例. {@code [$-403]}）が含まれている場合は、書式のロケールが優先されます。
     * @return フォーマットした結果。
     */
    public <N extends Number> CellFormatResult format(final String formatPattern, final N value) {
        return format(formatPattern, value, Locale.getDefault());
    }
    
    /**
     * 日付型をフォーマットし、結果を直接文字列として取得する。
     * @param formatPattern フォーマットの書式。
     * @param value フォーマット対象の値。タイムゾーンを含んだ値を指定します。
     * @return フォーマットした結果の文字列。
     */
    public String formatAsString(final String formatPattern, final Date value, final Locale locale) {
        return format(formatPattern, value, locale).getText();
    }
    
    /**
     * 日付型をフォーマットする。
     * @param formatPattern フォーマットの書式。
     * @param value フォーマット対象の値。タイムゾーンを含んだ値を指定します。
     * @return フォーマットした結果。
     */
    public CellFormatResult format(final String formatPattern, final Date value, final Locale locale) {
        return format(new DateCell(value, formatPattern), locale);
    }
    
    /**
     * ロケールを指定して、日付型をフォーマットし、結果を直接文字列として取得する。
     * @param formatPattern フォーマットの書式。
     * @param value フォーマット対象の値。タイムゾーンを含んだ値を指定します。
     * @param locale ロケール。書式にロケール条件の記述（例. {@code [$-403]}）が含まれている場合は、書式のロケールが優先されます。
     * @return フォーマットした結果の文字列。
     */
    public String formatAsString(final String formatPattern, final Date value) {
        return format(formatPattern, value).getText();
    }
    
    /**
     * ロケールを指定して、日付型をフォーマットする。
     * @param formatPattern フォーマットの書式。
     * @param value フォーマット対象の値。タイムゾーンを含んだ値を指定します。
     * @param locale ロケール。書式にロケール条件の記述（例. {@code [$-403]}）が含まれている場合は、書式のロケールが優先されます。
     * @return フォーマットした結果。
     */
    public CellFormatResult format(final String formatPattern, final Date value) {
        return format(formatPattern, value, Locale.getDefault());
    }
    
    /**
     * セルの値を文字列として取得する。
     * @param cell Javaの仮想的なオブジェクトを表現するセル。
     * @return フォーマットしたセルの値。 cellがnullの場合、空文字を返す。
     */
    public String formatAsString(final ObjectCell<?> cell) {
        return formatAsString(cell, Locale.getDefault());
    }
    
    /**
     * ロケールを指定してセルの値を文字列として取得する
     * @param cell Javaの仮想的なオブジェクトを表現するセル。
     * @param locale locale フォーマットしたロケール。nullでも可能。
     *        ロケールに依存する場合、指定したロケールにより自動的に切り替わります。
     * @return フォーマットした文字列。cellがnullの場合、空文字を返す。
     */
    public String formatAsString(final ObjectCell<?> cell, final Locale locale) {
        return format(cell, locale).getText();
    }
    
    /**
     * 値をフォーマットする。
     * @param cell Javaの仮想的なオブジェクトを表現するセル。
     * @return フォーマット結果。cellがnullの場合、空セルとして値を返す。
     */
    public CellFormatResult format(final ObjectCell<?> cell) {
        return format(cell, Locale.getDefault());
    }
    
    /**
     * 値をフォーマットする。
     * @param cell Javaの仮想的なオブジェクトを表現するセル。
     * @param locale フォーマットしたロケール。nullでも可能。
     *        ロケールに依存する場合、指定したロケールにより自動的に切り替わります。
     * @return フォーマット結果。cellがnullの場合、空セルとして値を返す。
     */
    public CellFormatResult format(final ObjectCell<?> cell, final Locale locale) {
        
        if(cell == null) {
            final CellFormatResult result = new CellFormatResult();
            result.setCellType(FormatCellType.Blank);
            result.setText("");
            return result;
        }
        
        final Locale runtimeLocale = locale != null ? locale : Locale.getDefault();
        
        final short formatIndex = cell.getFormatIndex();
        final String formatPattern = cell.getFormatPattern();
        
        if(formatterResolver.canResolve(formatIndex)) {
            final CellFormatter cellFormatter = formatterResolver.getFormatter(formatIndex);
            return cellFormatter.format(cell, runtimeLocale);
            
        } else if(formatterResolver.canResolve(formatPattern)) {
            final CellFormatter cellFormatter = formatterResolver.getFormatter(formatPattern);
            return cellFormatter.format(cell, runtimeLocale);
            
        } else {
            // キャッシュに存在しない場合
            final CellFormatter cellFormatter = formatterResolver.createFormatter(formatPattern) ;
            if(isCache()) {
                formatterResolver.registerFormatter(formatPattern, cellFormatter);
            }
            return cellFormatter.format(cell, runtimeLocale);
            
        }
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
    
}
