package com.github.mygreen.cellformatter;

import java.util.Locale;

/**
 * Javaのオブジェクト型を直接フォーマットする。
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
     * セルの値を文字列として取得する
     * @param cell 取得対象のセル
     * @return フォーマットしたセルの値。 cellがnullの場合、空文字を返す。
     */
    public String formatAsString(final ObjectCell<?> cell) {
        return formatAsString(cell, Locale.getDefault());
    }
    
    /**
     * ロケールを指定してセルの値を文字列として取得する
     * @param cell フォーマット対象のセル
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
