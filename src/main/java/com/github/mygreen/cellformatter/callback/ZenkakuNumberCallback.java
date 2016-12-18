package com.github.mygreen.cellformatter.callback;

import java.util.Locale;

/**
 * 半角数字を全角数字に変換する。
 * 
 * @version 0.5
 * @author T.TSUCHIE
 *
 */
public class ZenkakuNumberCallback extends JapaneseCallback<Object> {
    
    private static final String[][] MAP = {
        {"0", "０"},
        {"1", "１"},
        {"2", "２"},
        {"3", "３"},
        {"4", "４"},
        {"5", "５"},
        {"6", "６"},
        {"7", "７"},
        {"8", "８"},
        {"9", "９"},
    };
    
    public static ZenkakuNumberCallback create() {
        return new ZenkakuNumberCallback();
    }
    
    @Override
    public String call(final Object data, final String value, final Locale locale) {
        
        String str = value;
        for(String[] item : MAP) {
            str = str.replaceAll(item[0], item[1]);
        }
        return str;
    }
}
