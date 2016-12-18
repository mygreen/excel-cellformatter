package com.github.mygreen.cellformatter;


/**
 * 文字列型の値を直接扱うための仮想的なセル。
 * 
 * @since 0.6
 * @author T.TSUCHIE
 *
 */
public class TextCell extends ObjectCell<String> {
    
    /**
     * 値と書式のインデックス番号を指定するコンストラクタ。
     * <p>フォーマットの書式は、{@literal null}になります。
     * @param value フォーマット対象の値。
     * @param formatIndex 書式のインデックス番号。
     * @throws IllegalArgumentException {@literal formatIndex < 0}
     * 
     */
    public TextCell(final String value, short formatIndex) {
        super(value, formatIndex);
    }
    
    /**
     * 値とその書式を指定するコンストラクタ。
     * <p>フォーマットのインデックス番号は、{@literal 0}となります。
     * @param value フォーマット対象の値。
     * @param formatPattern Excelの書式。
     * @throws IllegalArgumentException {@literal formatPattern == null || formatPatter.length() == 0}.
     */
    public TextCell(final String value, final String formatPattern) {
        super(value, formatPattern);
    }
    
    /**
     * 値と、書式のインデックス番号、書式を指定するコンストラクタ。
     * @param value フォーマット対象の値。
     * @param formatIndex フォーマットのインデックス番号。
     * @param formatPattern Excelの書式。
     * @throws IllegalArgumentException {@literal formatIndex < 0}
     * @throws IllegalArgumentException {@literal formatPattern == null || formatPatter.length() == 0}.
     * 
     */
    public TextCell(final String value, final short formatIndex, final String formatPattern) {
        super(value, formatIndex, formatPattern);
    }
    
    @Override
    public boolean isText() {
        return true;
    }
    
    @Override
    public String getTextCellValue() {
        return getValue();
    }
    
}
