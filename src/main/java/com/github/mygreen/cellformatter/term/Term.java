package com.github.mygreen.cellformatter.term;


/**
 * フォーマッタ中の項を表現するインタフェース。
 * @author T.TSUCHIE
 * @param <T> フォーマットするオブジェクトのタイプ。
 */
public interface Term<T> {
    
    /**
     * 値をフォーマットする。
     * @param value
     * @return 必ずnull以外の文字列を返す。
     */
    String format(T value);
    
}
