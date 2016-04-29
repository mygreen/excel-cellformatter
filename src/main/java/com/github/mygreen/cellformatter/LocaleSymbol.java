package com.github.mygreen.cellformatter;

import com.github.mygreen.cellformatter.lang.MSLocale;

/**
 * 記号付きロケールを保持するクラス。
 * 
 * @since 0.8
 * @author T.TSUCHIE
 *
 */
public class LocaleSymbol {
    
    private final MSLocale locale;
    
    private final String symbol;
    
    public LocaleSymbol(final MSLocale locale, final String symbol) {
        this.locale = locale;
        this.symbol = symbol;
    }
    
    /**
     * ロケールを取得する。
     * @return
     */
    public MSLocale getLocale() {
        return locale;
    }
    
    /**
     * 記号を取得する。
     * @return
     */
    public String getSymbol() {
        return symbol;
    }
    
}
