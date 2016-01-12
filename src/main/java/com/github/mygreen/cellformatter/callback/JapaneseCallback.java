package com.github.mygreen.cellformatter.callback;

import java.util.Locale;

/**
 * 日本語のロケール情報を許可するCallback。
 *
 * @since 0.5
 * @author T.TSUCHIE
 *
 * @param <T>  変換元の値
 */
public abstract class JapaneseCallback<T> implements Callback<T> {
    
    @Override
    public boolean isApplicable(final Locale locale) {
        if(locale == null) {
            return false;
        }
        
        if(locale.getLanguage().equalsIgnoreCase("ja")) {
            return true;
        }
        
        return false;
    }
    
}
