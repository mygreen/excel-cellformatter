package com.github.mygreen.cellformatter.callback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.mygreen.cellformatter.term.Term;

/**
 * DBNum1を処理する。
 * <p>漢数字に変換する</p>
 *
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class DBNum1 extends DBNumBase {

    /**
     * インスタンスを取得する
     * @return {@link DBNum1}のインスタンス
     */
    public static DBNum1 create() {
        return new DBNum1();
    }

    /**
     * 言語ごとの変換処理のマップ
     */
    private Map<String, KansujiConverter> converters = new ConcurrentHashMap<>();

    public DBNum1() {
        super("ja", "zh", "ko");

        this.converters.put("ja", new KansujiConverter());
        this.converters.put("zh", new ChineseConverter());
        this.converters.put("ko", new KoreanConverter());
    }

    @Override
    public String call(Object data, String value, Locale locale, Term<?> term) {

        final String language = locale.getLanguage().toLowerCase();

        return converters.get(language).convert(value, is4YearTerm(term));
    }

    /**
     * 中国語の場合の変換処理
     *
     * @since 2.0
     * @author T.TSUCHIE
     *
     */
    public static class ChineseConverter extends KansujiConverter {

        public ChineseConverter() {

            setNumMap(new String[] {
                    "○",    // 819B。815A(〇)とは異なる。
                    "一",
                    "二",
                    "三",
                    "四",
                    "五",
                    "六",
                    "七",
                    "八",
                    "九"
            });

            // 10^4ごとの桁数
            setDigits10Map(new String[] {
                    "",      // 10^0
                    "万",    // 10^4
                    "亿",    // 10^8     // 億の桁名が異なる
                    "兆",    // 10^12
                    "京"    // 10^16
            });

        }

        @Override
        protected String replace4Digits(final String value) {

            // 桁ごとに変換を行う。
            final int length = value.length();
            List<String> digits = new ArrayList<>();

            for(int i=0; i < length; i++) {
                // 下の桁から処理する
                final char c = value.charAt(length-i-1);
                if(c == '0' && i == 0) {
                    /*
                     * 1桁目のゼロは省略する。
                     * ただし、変換後、下の桁がゼロで終わる場合は後から取り除く。
                     */
                    continue;

                }

                // 1の省略は行わない。
                String item;
                if(c == '0') {
                    // 0のときは桁の単位を省略する
                    item = replaceSimple(String.valueOf(c));
                } else {
                    item = replaceSimple(String.valueOf(c)) + digits4Map[i];
                }

                digits.add(item);

            }

            // 下の桁がゼロから始まる場合は除去する
            digits = removeZero(digits);

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
         * ゼロ以外の値が現れるまでのゼロを取り除く。
         * @param digits 取り除く対象のリスト。
         * @return 取り除いた後の
         */
        private List<String> removeZero(List<String> digits) {

            // ゼロ以外の数字が見つかったか
            boolean foundNonZero = false;

            // 1つ前の数字がゼロかどうか(連続するゼロを省略する)
            boolean beforeZero = false;

            List<String> list = new ArrayList<>();
            for(String num : digits) {

                if(num.equals(numMap[0]) && !foundNonZero) {
                    // ゼロ以外の数字が見つからないときのゼロは省略する
                    continue;
                }

                if(!num.equals(numMap[0])) {
                    // ゼロ以外の数字が見つかった場合
                    foundNonZero = true;
                }

                if(num.equals(numMap[0])) {
                    // 今回がゼロで、前回がゼロ出ない場合は追加する。
                    if(!beforeZero) {
                        list.add(num);
                    }

                } else {
                    list.add(num);
                }

                beforeZero = num.equals(numMap[0]);

            }

            return list;

        }

    }

    /**
     * 韓国語の変換処理
     *
     * @since 2.0
     * @author T.TSUCHIE
     *
     */
    public static class KoreanConverter extends KansujiConverter {

        public KoreanConverter() {

            // 10^4ごとの桁数
            setNumMap(new String[] {
                    "０",    // ゼロが全角のゼロ
                    "一",
                    "二",
                    "三",
                    "四",
                    "五",
                    "六",
                    "七",
                    "八",
                    "九"
            });

        }

        @Override
        protected String replace4Digits(final String value) {

            // 桁ごとに変換を行う。
            final int length = value.length();
            List<String> digits = new ArrayList<>();

            for(int i=0; i < length; i++) {
                // 下の桁から処理する
                final char c = value.charAt(length-i-1);
                if(c == '0') {
                    continue;

                }

                // 1の省略は行わない。
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

}
