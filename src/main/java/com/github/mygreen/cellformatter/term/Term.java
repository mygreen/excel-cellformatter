package com.github.mygreen.cellformatter.term;

import java.util.Locale;

import com.github.mygreen.cellformatter.lang.MSLocale;


/**
 * フォーマッタ中の項を表現するインタフェース。
 * @author T.TSUCHIE
 * @param <T> フォーマットするオブジェクトのタイプ。
 */
public interface Term<T> {
    
    /**
     * 値をフォーマットする。
     * @param value
     * @param formatLocale 書式上のロケール。nullである場合がある。
     * @param runtimeLocale 実行環境のロケール。
     * @return 必ずnull以外の文字列を返す。
     */
    String format(T value, MSLocale formatLocale, Locale runtimeLocale);
    
}
