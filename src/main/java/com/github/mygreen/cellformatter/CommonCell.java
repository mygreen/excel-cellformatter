package com.github.mygreen.cellformatter;

import java.util.Date;


/**
 * 共通のセルのインタフェース。
 * POI、JExcelAPIなどのライブラリ間の違いを吸収するためのもの。
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
     * 非数値型の文字列、Boolean型の場合にtrueを返す。
     * @return 
     */
    boolean isText();
    
    /**
     * 文字列型のセルとして値を取得する。
     * @return
     */
    String getTextCellValue();
    
    /**
     * 数値型のセルとして値を取得する。
     * @return
     */
    double getNumberCellValue();
    
    /**
     * 日時型のセルとして値を取得する。
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
