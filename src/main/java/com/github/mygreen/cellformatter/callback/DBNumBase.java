package com.github.mygreen.cellformatter.callback;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import com.github.mygreen.cellformatter.lang.ArgUtils;
import com.github.mygreen.cellformatter.term.DateTerm;
import com.github.mygreen.cellformatter.term.Term;

/**
 * 数値変換の基底クラス。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public abstract class DBNumBase implements Callback<Object> {

    /**
     * 許可する言語。
     */
    private Set<String> allowedLanguages = new HashSet<>();

    /**
     * コンストラクタ
     * @param allowedLanguages 許可する言語の指定。
     * @throws IllegalArgumentException {@literal allowedLanguages is null or empty.}
     */
    protected DBNumBase(final String... allowedLanguages) {

        ArgUtils.notEmpty(allowedLanguages, "allowedLanguages");

        for(String lang : allowedLanguages) {
            this.allowedLanguages.add(lang.toLowerCase());
        }

    }

    /**
     * {@inheritDoc}
     * <p>コンストラクタで指定したロケール（言語）で許可するかどうか判定する。
     */
    @Override
    public boolean isApplicable(final Locale locale) {
        if(locale == null) {
            return false;
        }

        final String language = locale.getLanguage().toLowerCase();
        return allowedLanguages.contains(language);
    }

    /**
     * 項が日付の4桁の年かどうか
     * @param term
     * @return
     */
    protected boolean is4YearTerm(Term<?> term) {

        if(!(term instanceof DateTerm.YearTerm)) {
            return false;
        }

        DateTerm.YearTerm yearTerm = (DateTerm.YearTerm)term;
        if(yearTerm.getFormat().length() == 4) {
            return true;
        }

        return false;

    }


}
