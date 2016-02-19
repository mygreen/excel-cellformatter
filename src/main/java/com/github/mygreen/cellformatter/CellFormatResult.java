package com.github.mygreen.cellformatter;

import java.util.Date;
import java.util.TimeZone;

import com.github.mygreen.cellformatter.lang.MSColor;


/**
 * フォーマット結果を保持するクラス。
 * <p>条件に色が付与されている場合などの情報を保持する。
 * 
 * @version 0.6
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
     * フォーマットした際のセルの種類
     */
    private FormatCellType cellType;
    
    /**
     * フォーマット対象の値を取得する。
     * <p>具体的に値を取得する際には、キャストして利用する。
     * @return フォーマット対象の値。
     */
    public Object getValue() {
        return value;
    }
    
    /**
     * フォーマット対象の値をdouble型として取得する。
     * @return 数値の値を返す。{@link #isNumber()}の値が{@literal true}の時に値が取得可能。
     * @throws 書式が一致しない場合は、{@link ClassCastException}をスローする。
     */
    public double getValueAsDoulbe() {
        return (double) value;
    }
    
    /**
     * フォーマット対象の値を日時型として取得する。
     * @return タイムゾーンが考慮された日時です。タイムゾーンはデフォルトです。
     *         {@link #isDate()}の値が{@literal true}の時に値が取得可能。
     * @throws 書式が一致しない場合は、{@link ClassCastException}をスローする。
     */
    public Date getValueAsDate() {
        return getValueAsDate(null);
    }
    
    /**
     * タイムゾーンを指定して、フォーマット対象の値を日時型として取得する。
     * @since 0.6
     * @param tz タイムゾーン。値がnullの場合は、デフォルトを使用します。
     * @return タイムゾーンを考慮した日時オブジェクト。
     *          {@link #isDate()}の値が{@literal true}の時に値が取得可能。
     * @throws 書式が一致しない場合は、{@link ClassCastException}をスローする。
     */
    public Date getValueAsDate(final TimeZone tz) {
        long time = ((Date) value).getTime();
        long offset;
        if(tz == null) {
            offset = TimeZone.getDefault().getRawOffset();
        } else {
            offset = tz.getRawOffset();
        }
        
        return new Date(time - offset);
    }
    
    /**
     * フォーマット対象の値を文字列型として取得する。
     * @throws 書式が一致しない場合は、{@link ClassCastException}をスローする。
     * @return 文字列の値。{@link #isText()}の値が{@literal true}の時に値が取得可能。
     */
    public String getValueAsString() {
        return (String) value;
    }
    
    /**
     * フォーマット対象の値をブール型として取得する。
     * @throws 書式が一致しない場合は、{@link ClassCastException}をスローする。
     * @return {@link #isBoolean()}の値が{@literal true}の時に値が取得可能。
     */
    public boolean getValueAsBoolean() {
        return (boolean) value;
    }
    
    /**
     * 値がブランクかどうか。
     * @return true:空。
     */
    public boolean isBlank() {
        return getCellType() == FormatCellType.Blank;
    }
    
    /**
     * 値が日付型かどうか。
     * @return true:日時型の場合。
     */
    public boolean isDate() {
        return getCellType() == FormatCellType.Date;
    }
    
    /**
     * 値が数値型かどうか。
     * @return true:数値型の場合。
     */
    public boolean isNumber() {
        return getCellType() == FormatCellType.Number;
    }
    
    /**
     * 値が文字列型かどうか。
     * @return true:文字列型の場合。
     */
    public boolean isText() {
        return getCellType() == FormatCellType.Text;
    }
    
    /**
     * 値がエラー型かどうか。
     * @return true:エラーの場合。
     */
    public boolean isError() {
        return getCellType() == FormatCellType.Error;
    }
    
    /**
     * フォーマット対象の値を設定する。
     * @param value 値。
     */
    public void setValue(Object value) {
        this.value = value;
    }
    
    /**
     * 文字色を取得する。
     * @return 書式に色が適用されていない場合は、nullを返す。
     */
    public MSColor getTextColor() {
        return textColor;
    }
    
    /**
     * 書式の文字色を設定する。
     * @param color 文字色。
     */
    public void setTextColor(MSColor textColor) {
        this.textColor = textColor;
    }
    
    /**
     * フォーマットした文字列を取得する
     * @return フォーマットした文字列。
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
     * フォーマットの際、一致した書式内のセクションの書式を返す。
     * @return 一致したセクションの書式。
     */
    public String getSectionPattern() {
        return sectionPattern;
    }
    
    /**
     * フォーマットの際、一致した書式内のセクションの書式を設定する
     * @param sectionPattern 一致したセクションの書式。
     */
    public void setSectionPattern(String sectionPattern) {
        this.sectionPattern = sectionPattern;
    }
    
    
    /**
     * セルの種類を取得する
     * <p>各メソッド{@link #isText()}などのisXXX()メソッドの判定に利用する。
     * @return フォーマットした結果のセルの種類。
     */
    public FormatCellType getCellType() {
        return cellType;
    }
    
    /**
     * セルの種類を設定する。
     * @param cellType
     */
    public void setCellType(FormatCellType cellType) {
        this.cellType = cellType;
    }
}
