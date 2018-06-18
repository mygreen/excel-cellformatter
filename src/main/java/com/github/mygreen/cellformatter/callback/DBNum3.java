package com.github.mygreen.cellformatter.callback;

import java.util.Locale;

import com.github.mygreen.cellformatter.term.Term;

/**
 * DBNum3を処理する。
 * <p>半角数字を全角数字に置換する。</p>
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class DBNum3 extends DBNumBase {

    /**
     * インスタンスを取得する
     * @return {@link DBNum3}のインスタンス
     */
    public static DBNum3 create() {
        return new DBNum3();
    }

    private ZenkakuNumberConverter converter = new ZenkakuNumberConverter();

    public DBNum3() {
        super("ja");
    }

    @Override
    public String call(final Object data, final String value, final Locale locale, Term<?> term) {
        return converter.convert(value);
    }
}
