package com.github.mygreen.cellformatter.callback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.mygreen.cellformatter.lang.ArgUtils;

/**
 * 数字の表現を漢数字に変換する。
 * <p>数字が連続しないと、桁数を考慮した変換はできない。</p>
 * <p>途中に区切り文字などがあると、そこで途切れる。</p>
 * <p>大字の定義：<a href="http://www.benricho.org/kanji/kansuji.html">漢数字と大字〔だいじ〕の書き方</a></p>
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class KansujiConverter {

    /**
     * 整数部分の切り出し用正規表現
     */
    protected static final Pattern PATTERN_NUM = Pattern.compile("([\\D]*)([\\d]+)([\\.]{0,1}[.\\s\\w]*)");

    /**
     * 0～9の数字のマップ
     */
    protected String[] numMap = {
        "〇",
        "一",
        "二",
        "三",
        "四",
        "五",
        "六",
        "七",
        "八",
        "九"
    };

    /**
     * 10^4ごとの桁単位のマップ
     * <p><a href="http://www2s.biglobe.ne.jp/~hotori/kazu.html">桁数の名前</a>
     */
    protected String[] digits10Map = {
        "",      // 10^0
        "万",    // 10^4
        "億",    // 10^8
        "兆",    // 10^12
        "京",    // 10^16
    };

    /**
     * 4桁の桁単位のマップ
     */
    protected String[] digits4Map = {
        "",
        "十",
        "百",
        "千",
    };

    /**
     * 文字列を変換する
     * @param value 変換対象の値
     * @param is4YearTerm 4桁の年指定の項の場合
     * @return 変換後の値
     */
    public String convert(final String value, final boolean is4YearTerm) {

        final Matcher matcher = PATTERN_NUM.matcher(value);
        if(!matcher.matches()) {
            // 一致しない場合は、単純に数値の変換
            return replaceSimple(value);
        }

        final String before = matcher.group(1);
        final String num = matcher.group(2);
        final String after = matcher.group(3);

        if(is4YearTerm && num.length() == 4) {
            // 年の桁は、単純に数値変換する
            return replaceSimple(before) + replaceSimple(num) + replaceSimple(after);

        } else {
            return replaceSimple(before) + replaceDisits(num) + replaceSimple(after);

        }

    }

    /**
     * 数字を単純に変換する。
     * @param value 変換対象の文字列
     * @return
     */
    protected String replaceSimple(final String value) {
        String str = value;
        for(int i=0; i < numMap.length; i++) {
            str = str.replaceAll(String.valueOf(i), numMap[i]);
        }

        return str;
    }

    /**
     * 数字を桁数に合わせて変換する。
     * @param value
     * @return
     */
    protected String replaceDisits(final String value) {

        if(value.equals("0")) {
            return numMap[0];
        }

        // 4桁ごとに、分割する。
        final int length = value.length();
        final List<String> split4 = new ArrayList<>();
        for(int i=0; i < length; i=i+4) {

            // 下の桁から切り出す
            int end = length -i;
            int start;
            if(i + 4 < length) {
                start = end - 4;
            } else {
                start = 0;
            }
            String item = value.substring(start, end);
            split4.add(item);
        }

        // 4桁ごとに変換を行う。
        final List<String> digits = new ArrayList<>();
        for(int i=0; i < split4.size(); i++) {
            String item = split4.get(i);
            item = replace4Digits(item) + digits10Map[i];

            digits.add(item);
        }

        /*
         * 文字列に直す。
         * ・桁数が逆順になっているので、戻し結合する。
         */
        Collections.reverse(digits);
        StringBuilder sb = new StringBuilder();
        for(String item : digits) {
            sb.append(item);
        }

        return sb.toString();
    }

    /**
     * 4桁以下の数字を、漢数字に変換する。
     * @param value
     * @return
     */
    protected String replace4Digits(final String value) {

        // 桁ごとに変換を行う。
        final int length = value.length();
        final List<String> digits = new ArrayList<>();

        for(int i=0; i < length; i++) {
            // 下の桁から処理する
            final char c = value.charAt(length-i-1);
            if(c == '0') {
                continue;

            }

            String item;
            if(c == '1' && i > 0) {
                // 10の位以上で、かつ1の場合、数字部分は省略し、桁数のみにする。
                item = digits4Map[i];
            } else {
                item = replaceSimple(String.valueOf(c)) + digits4Map[i];
            }

            digits.add(item);

        }

        /*
         * 文字列に直す。
         * ・桁数が逆順になっているので、戻し結合する。
         */
        Collections.reverse(digits);
        StringBuilder sb = new StringBuilder();
        for(String item : digits) {
            sb.append(item);
        }

        return sb.toString();

    }

    /**
     * 0～9の数字のマップを設定する
     * @param numMap 配列のインデックスに対応する数字のマップ
     * @throws IllegalArgumentException {@literal numMap size != 10}
     */
    public void setNumMap(String[] numMap) {
        ArgUtils.notEmpty(numMap, "numMap");

        if(numMap.length != 10) {
            throw new IllegalArgumentException("numMap length should be 10.");
        }

        this.numMap = numMap;
    }

    /**
     * 10^4ごとの桁単位のマップ
     * @param digits10Map 万、億などの桁のマップ。
     * @throws IllegalArgumentException {@literal digits10Map size != 5}
     */
    public void setDigits10Map(String[] digits10Map) {
        ArgUtils.notEmpty(digits10Map, "digits10Map");

        if(digits10Map.length != 5) {
            throw new IllegalArgumentException("digits10Map length should be 5.");
        }

        this.digits10Map = digits10Map;
    }

    /**
     * 4桁の桁単位のマップ
     * @param digits4Map 十、百などの桁のマップ
     * @throws IllegalArgumentException {@literal digits4Map size != 4}
     */
    public void setDigits4Map(String[] digits4Map) {

        ArgUtils.notEmpty(digits4Map, "digits4Map");

        if(digits4Map.length != 4) {
            throw new IllegalArgumentException("digits4Map length should be 4.");
        }

        this.digits4Map = digits4Map;
    }

}
