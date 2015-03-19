package com.github.mygreen.cellformatter;

import java.util.Calendar;


/**
 * 共通のセルのインタフェース
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
     * 文字列型のセルからどうか。
     * @return
     */
    boolean isString();
    
    /**
     * 文字列型のセルとして値を取得する。
     * @return
     */
    String getStringCellValue();
    
    /**
     * 数値型のセルとして値を取得する。
     * @return
     */
    double getNumberCellValue();
    
    /**
     * 日時型のセルとして値を取得する。
     * @return タイムゾーンを考慮し{@link Calendar}のオブジェクトを取得する。
     */
    Calendar getDateCellValue();
    
}
