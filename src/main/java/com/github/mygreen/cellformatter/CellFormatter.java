package com.github.mygreen.cellformatter;

import java.util.Locale;


/**
 * セルのフォーマッタの共通インタフェース
 *
 * @version 0.10
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

    /**
     * 書式を取得する
     * @since 0.10
     * @return フォーマッタの書式
     */
    public String getPattern() {
        return getPattern(Locale.getDefault());
    }

    /**
     * ロケールを指定して書式を取得する
     * @since 0.10
     * @param locale ロケール。指定しない場合は、実行環境のロケールが設定される。
     * @return フォーマッタの書式
     */
    public abstract String getPattern(final Locale locale);


}
