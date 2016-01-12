package com.github.mygreen.cellformatter.callback;

import java.util.Locale;

/**
 * フォーマット処理後に呼ばれるcallbakのインタフェース。
 * <p>Javaの書式変換に対応していない場合などに行う。
 * 
 * @version 0.5
 * @param <T> 変換元の値
 * @author T.TSUCHIE
 *
 */
public interface Callback<T> {
    
    /**
     * 適用可能なロケールかどうか。
     * @since 0.5
     * @param locale ロケール情報。
     * @return
     */
    boolean isApplicable(Locale locale);
    
    /**
     * フォーマットの後に実行する処理。
     * @param data 変換元のデータ
     * @param value フォーマットされた値。
     * @param locale ロケール
     * @return 処理後の値
     */
    String call(T data, String value, Locale locale);
    
}
