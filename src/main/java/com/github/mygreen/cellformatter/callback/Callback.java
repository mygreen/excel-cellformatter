package com.github.mygreen.cellformatter.callback;


/**
 * フォーマット処理後に呼ばれるcallbakのインタフェース。
 * <p>Javaの書式変換に対応していない場合などに行う。
 * 
 * @param <T> 変換元の値
 * @author T.TSUCHIE
 *
 */
public interface Callback<T> {
    
    /**
     * フォーマットの後に実行する処理。
     * @param data 変換元のデータ
     * @param value フォーマットされた値。
     * @return 処理後の値
     */
    String call(T data, String value);
    
}
