package com.github.mygreen.cellformatter.callback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 数字の表現を漢数字に変換する。
 * <p>数字が連続しないと、桁数を考慮した変換はできない。
 * <p>途中に区切り文字などがあると、そこで途切れる。
 * <p>大字の定義：<a href="http://www.benricho.org/kanji/kansuji.html">漢数字と大字〔だいじ〕の書き方</a>
 * 
 * @author T.TSUCHIE
 *
 */
public class KansujiCallback implements Callback<Object>{
    
    /**
     * インスタンスを取得する
     * @return
     */
    public static KansujiCallback create() {
        return new KansujiCallback();
    }
    
    /**
     * 整数部分の切り出し用正規表現
     */
    private static final Pattern PATTERN_NUM = Pattern.compile("([\\D]*)([\\d]+)([\\.]{0,1}[.\\s\\w]*)");
    
    @Override
    public String call(final Object data, final String value) {
        
        final Matcher matcher = PATTERN_NUM.matcher(value);
        if(!matcher.matches()) {
            // 単純に数値の変換
            return replaceSimple(value);
        }
        
        final String before = matcher.group(1);
        final String num = matcher.group(2);
        final String after = matcher.group(3);
        
        return replaceSimple(before) + replaceDisits(num) + replaceSimple(after);
    }
    
    /**
     * 数字のマップ
     */
    private static final String[][] NUM_MAP = {
        {"0", "〇"},
        {"1", "一"},
        {"2", "二"},
        {"3", "三"},
        {"4", "四"},
        {"5", "五"},
        {"6", "六"},
        {"7", "七"},
        {"8", "八"},
        {"9", "九"},
    };
    
    /**
     * 数字を単純に変換する。
     * @param value
     * @return
     */
    private String replaceSimple(final String value) {
        String str = value;
        for(String[] item : NUM_MAP) {
            str = str.replaceAll(item[0], item[1]);
        }
        
        return str;
    }
    
    /**
     * 10^4ごとの桁数のマップ
     * <p><a href="http://www2s.biglobe.ne.jp/~hotori/kazu.html">桁数の名前</a>
     */
    private static final String[] DIGITS_10_MAP = {
        "",      // 10^0
        "万",    // 10^4
        "億",    // 10^8
        "兆",    // 10^12
        "京",    // 10^16
    };
    
    /**
     * 数字を桁数に合わせて変換する。
     * @param value
     * @return
     */
    private String replaceDisits(final String value) {
        
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
            item = replace4Digits(item) + DIGITS_10_MAP[i];
            
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
     * 4桁の桁数のマップ
     */
    private static final String[] DIGITS_4_MAP = {
        "",
        "十",
        "百",
        "千",
    };
    
    /**
     * 4桁以下の数字を、漢数字に変換する。
     * @param value
     * @return
     */
    private String replace4Digits(final String value) {
        
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
                item = DIGITS_4_MAP[i];
            } else {
                item = replaceSimple(String.valueOf(c)) + DIGITS_4_MAP[i];
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
}
