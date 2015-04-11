package com.github.mygreen.cellformatter;

import java.util.Date;

import com.github.mygreen.cellformatter.lang.MSColor;


/**
 * フォーマット結果を保持するクラス。
 * <p>条件に色が付与されている場合などの情報を保持する。
 * 
 * @since 0.3
 * @author T.TSUCHIE
 *
 */
public class CellFormatResult {
    
    /**
     * フォーマットする際に使用した値
     */
    private Object value;
    
    /**
     * フォーマット後の値
     */
    private String text;
    
    /**
     * フォーマット後の色。nullの場合がある。
     */
    private MSColor textColor;
    
    /**
     * 一致したExcelのセクションの書式。
     */
    private String sectionPattern;
    
    /**
     * フォーマットの種類
     */
    private FormatterType formatterType;
    
    /**
     * 書式を持たない結果の場合。
     * <p>単純に文字列だけを設定する。
     * @param text
     * @return
     */
    public static CellFormatResult noFormatResult(final String text) {
        CellFormatResult result = new CellFormatResult();
        result.setText(text);
        result.setFormatterType(FormatterType.Unknown);
        return result;
    }
    
    /**
     * フォーマット対象の値を取得する
     * @return
     */
    public Object getValue() {
        return value;
    }
    
    /**
     * フォーマット対象の値をdouble型として取得する。
     * @return
     * @throws 書式が一致しない場合は、{@link ClassCastException}をスローする。
     */
    public double getValueAsDoulbe() {
        return (double) value;
    }
    
    /**
     * フォーマット対象の値を日時型として取得する。
     * @return
     * @throws 書式が一致しない場合は、{@link ClassCastException}をスローする。
     */
    public Date getValueAsDate() {
        return (Date) value;
    }
    
    /**
     * フォーマット対象の値を文字列型として取得する。
     * @throws 書式が一致しない場合は、{@link ClassCastException}をスローする。
     * @return
     */
    public String getValueAsString() {
        return (String) value;
    }
    
    /**
     * フォーマット対象の値を設定する。
     * @param value
     */
    public void setValue(Object value) {
        this.value = value;
    }
    
    /**
     * 文字色
     * @return
     */
    public MSColor getTextColor() {
        return textColor;
    }
    
    /**
     * 文字色を設定する。
     * @param color
     */
    public void setTextColor(MSColor textColor) {
        this.textColor = textColor;
    }
    
    /**
     * フォーマットした文字列を取得する
     * @return
     */
    public String getText() {
        return text;
    }
    
    /**
     * フォーマットした文字列を設定する
     * @param value
     */
    public void setText(String text) {
        this.text = text;
    }
    
    /**
     * フォーマットの際の一致した書式内のセクションの書式を返す。
     * @return
     */
    public String getSectionPattern() {
        return sectionPattern;
    }
    
    /**
     * フォーマットの際の一致した書式内のセクションの書式を設定する
     * @param sectionPattern
     */
    public void setSectionPattern(String sectionPattern) {
        this.sectionPattern = sectionPattern;
    }
    
    /**
     * フォーマットの種類を取得する
     * @return
     */
    public FormatterType getFormatterType() {
        return formatterType;
    }
    
    /**
     * フォーマットの種類を設定する。
     * @param formatterType
     */
    public void setFormatterType(FormatterType formatterType) {
        this.formatterType = formatterType;
    }
}
