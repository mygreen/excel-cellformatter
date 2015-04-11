package com.github.mygreen.cellformatter;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.mygreen.cellformatter.lang.ArgUtils;


/**
 * ロケールによって、フォーマッタを切り替えて処理する。
 * @author T.TSUCHIE
 *
 */
public class LocaleSwitchFormatter extends CellFormatter {
    
    private final CellFormatter defaultFormatter;
    
    private final Map<Locale, CellFormatter> formatterMap = new ConcurrentHashMap<>();
    
    public LocaleSwitchFormatter(final CellFormatter defaultFormatter) {
        ArgUtils.notNull(defaultFormatter, "defaultFormatter");
        this.defaultFormatter = defaultFormatter;
    }
    
    @Override
    public CellFormatResult format(final CommonCell cell, final Locale locale) {
        
        if(locale == null) {
            return defaultFormatter.format(cell, locale);
            
        } else if(formatterMap.containsKey(locale)) {
            return formatterMap.get(locale).format(cell, locale);
        }
        
        return defaultFormatter.format(cell, locale);
    }
    
    /**
     * ローケールとフォーマッタを登録する。
     * @param cellFormatter
     * @param locales
     * @return
     * @throws IllegalArgumentException cellFormatter is null.
     * @throws IllegalArgumentException locales size is zero.
     */
    public LocaleSwitchFormatter register(final CellFormatter cellFormatter, final Locale... locales) {
        ArgUtils.notNull(cellFormatter, "cellFormatter");
        ArgUtils.notEmpty(locales, "locales");
        
        for(Locale locale : locales) {
            formatterMap.put(locale, cellFormatter);
        }
        
        return this;
    }
    
}
