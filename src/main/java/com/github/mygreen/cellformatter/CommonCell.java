package com.github.mygreen.cellformatter;

import java.util.Date;


/**
 * 共通のセルのインタフェース。
 * POI、JExcelAPIなどのライブラリ間の違いを吸収するためのもの。
 * 
 * @version 0.6
 * @since 0.4
 * @author T.TSUCHIE
 *
 */
public interface CommonCell {
    
    /**
     * 書式のインデックス番号を取得する。
     * <p>インデックス番号がない場合は0を返す。
     * @return
     */
    short getFormatIndex();
    
    /**
     * 書式のパターンを取得する。
     * <p>パターンがない場合は、空文字を返す。
     * @return
     */
    String getFormatPattern();
    
    /**
     * 文字列型のセルかどうか。
     * @return 
     */
    boolean isText();
    
    /**
     * ブール型のセルかどうか。
     * @since 0.4
     * @return true:セルの形式がブール型の場合
     */
    boolean isBoolean();
    
    /**
     * 数値型のセルかどうか。
     * @return true:セルの形式が数値型、日付型の場合。
     */
    boolean isNumber();
    
    /**
     * 文字列型のセルとして値を取得する。
     * @return true:セルの形式が文字型の場合
     */
    String getTextCellValue();
    
    /**
     * Boolean型のセルとして値を取得する。
     * @since 0.4
     * @return
     */
    boolean getBooleanCellValue();
    
    /**
     * 数値型のセルとして値を取得する。
     * <p>日時型の場合でもExcelの内部表現である数値に変換して値を取得する。
     * @return 
     */
    double getNumberCellValue();
    
    /**
     * 日時型のセルとして値を取得する。
     * <p>ただじ、タイムゾーンは標準の{@literal GMT-00:00}とする。
     * <p>数値型の場合でも日時型に変換して値を取得する。
     */
    Date getDateCellValue();
    
    /**
     * 日時の開始日が1904年かどうか。
     * 通常は、1900年始まり。
     * @return
     */
    boolean isDateStart1904();
    
    /**
     * セルのアドレスを取得する。
     * @since 0.2
     * @return 'A12'の形式。
     */
    String getCellAddress();
    
}
