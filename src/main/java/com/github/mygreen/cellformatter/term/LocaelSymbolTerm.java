package com.github.mygreen.cellformatter.term;

import java.util.Locale;

import com.github.mygreen.cellformatter.LocaleSymbol;
import com.github.mygreen.cellformatter.lang.ArgUtils;
import com.github.mygreen.cellformatter.lang.MSLocale;

/**
 * 記号付きロケールの項
 * @since 0.8
 * @author T.TSUCHIE
 *
 */
public class LocaelSymbolTerm<T> implements Term<T> {
    
    private final LocaleSymbol localeSymbol;
    
    public LocaelSymbolTerm(final LocaleSymbol localeSymbol) {
        ArgUtils.notNull(localeSymbol, "localeSymbol");
        this.localeSymbol = localeSymbol;
    }
    
    @Override
    public String format(final T value, final MSLocale formatLocale, final Locale runtimeLocale) {
        return localeSymbol.getSymbol();
    }
    
    
}
