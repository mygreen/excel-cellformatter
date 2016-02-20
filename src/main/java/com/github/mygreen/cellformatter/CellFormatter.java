package com.github.mygreen.cellformatter;

import java.util.Locale;


/**
 * セルのフォーマッタの共通インタフェース
 * 
 * @version 0.3
 * @author T.TSUCHIE
 *
 */
public abstract class CellFormatter {
    
    /**
     * セルの値をフォーマットする
     * @param cell フォーマット対象のセル。
     * @return フォーマットした結果。
     * @throws IllegalArgumentException {@literal cell == null.}
     */
    public CellFormatResult format(final CommonCell cell) {
        return format(cell, Locale.getDefault());
    }
    
    /**
     * セルの値をロケールを指定してフォーマットする
     * @param cell フォーマット対象のセル。
     * @param locale ロケール。指定しない場合は、実行環境のロケールが設定される。
     * @return フォーマットした結果。
     * @throws IllegalArgumentException {@literal cell == null.}
     */
    public abstract CellFormatResult format(final CommonCell cell, final Locale locale);
    
}
