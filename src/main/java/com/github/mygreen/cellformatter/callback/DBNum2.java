package com.github.mygreen.cellformatter.callback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.mygreen.cellformatter.term.Term;

/**
 * DBNum2を処理する。
 * <p>大字(だいじ)に変換する</p>
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class DBNum2 extends DBNumBase {

   /**
    * インスタンスを取得する
    * @return {@link DBNum2}のインスタンス
    */
   public static DBNum2 create() {
       return new DBNum2();
   }

   /**
    * 言語ごとの変換処理のマップ
    */
   private Map<String, KansujiConverter> converters = new ConcurrentHashMap<>();

   public DBNum2() {
       super("ja", "zh", "ko");

       this.converters.put("ja", new JapaneseConverter());
       this.converters.put("zh", new ChineseConverter());
       this.converters.put("ko", new KoreanConverter());
   }

   @Override
   public String call(Object data, String value, Locale locale, Term<?> term) {

       final String language = locale.getLanguage().toLowerCase();

       return converters.get(language).convert(value, is4YearTerm(term));
   }

   /**
    * 日本語の場合の変換処理
    *
    * @since 2.0
    * @author T.TSUCHIE
    *
    */
   public static class JapaneseConverter extends KansujiConverter {

       public JapaneseConverter() {

           // 数字のマップ
           setNumMap(new String[] {
                   "〇",
                   "壱",
                   "弐",
                   "参",
                   "四",
                   "伍",
                   "六",
                   "七",
                   "八",
                   "九"
           });

           // 10^4桁ごと単位のマップ
           setDigits10Map(new String[] {
                   "",      // 10^0
                   "萬",    // 10^4
                   "億",    // 10^8
                   "兆",    // 10^12
                   "京"     // 10^16
           });

           // 4桁の単位のマップ
           setDigits4Map(new String[] {
                   "",
                   "拾",
                   "百",
                   "阡"
           });

       }

       @Override
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

               // 12の場合も、「壱拾弐」と1を省略さない。
               String item = replaceSimple(String.valueOf(c)) + digits4Map[i];

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

   /**
    * 中国語の場合の変換処理
    *
    * @since 2.0
    * @author T.TSUCHIE
    *
    */
   public static class ChineseConverter extends DBNum1.ChineseConverter {

       public ChineseConverter() {

           // 数字のマップ
           setNumMap(new String[] {
                   "零",
                   "壹",
                   "贰",
                   "叁",
                   "肆",
                   "伍",
                   "陆",
                   "柒",
                   "捌",
                   "玖"
           });

           // 10^4桁ごと単位のマップ
           setDigits10Map(new String[] {
                   "",      // 10^0
                   "万",    // 10^4  日本語の万(0x969c)とは異なる、万(0x8DC0)
                   "亿",    // 10^8
                   "兆",    // 10^12
                   "京"     // 10^16
           });

           // 4桁の単位のマップ
           setDigits4Map(new String[] {
                   "",
                   "拾",
                   "佰",
                   "仟"
           });

       }
   }

   /**
    * 韓国国の場合の変換処理
    *
    * @since 2.0
    * @author T.TSUCHIE
    *
    */
   public static class KoreanConverter extends DBNum1.KoreanConverter {

       public KoreanConverter() {

           // 数字のマップ
           setNumMap(new String[] {
                   "零",
                   "壹",
                   "貳",
                   "參",
                   "四",
                   "伍",
                   "六",
                   "七",
                   "八",
                   "九"
           });

           // 10^4桁ごと単位のマップ
           setDigits10Map(new String[] {
                   "",      // 10^0
                   "萬",    // 10^4
                   "億",    // 10^8
                   "兆",    // 10^12
                   "京"     // 10^16
           });

           // 4桁の単位のマップ
           setDigits4Map(new String[] {
                   "",
                   "拾",
                   "百",
                   "阡"
           });

       }

   }


}
