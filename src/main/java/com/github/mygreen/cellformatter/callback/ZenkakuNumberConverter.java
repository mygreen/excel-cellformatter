package com.github.mygreen.cellformatter.callback;

/**
 * 半角数字を全角数字に変換する
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class ZenkakuNumberConverter {

    /**
     * 変換する数字のマップ。
     * 配列のインデックスの値に対する値が変換する値と一致する。
     */
    private String[] numMap = {
            "０",
            "１",
            "２",
            "３",
            "４",
            "５",
            "６",
            "７",
            "８",
            "９",
    };

    /**
     * 文字列を変換する
     * @param value 変換対象の文字列
     * @return 変換後の文字列
     */
    public String convert(final String value) {

        if(!value.matches(".*[0-9].*")) {
            return value;
        }

        String str = value;
        for(int i=0; i < numMap.length; i++) {
            str = str.replaceAll(String.valueOf(i), numMap[i]);
        }

        return str;

    }

    /**
     * 数字の変換マップを設定する。
     * @param numMap 配列のインデックスの値に対する値が変換する値と一致する。
     */
    public void setNumMap(String[] numMap) {
        this.numMap = numMap;
    }

}
