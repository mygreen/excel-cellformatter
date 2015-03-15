package com.github.mygreen.cellformatter.callback;

/**
 * 半角数字を全角数字に変換する。
 * 
 * @author T.TSUCHIE
 *
 */
public class ZenkakuNumberCallback implements Callback<Object> {
    
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
    public String call(final Object data, final String value) {
        
        String str = value;
        for(String[] item : MAP) {
            str = str.replaceAll(item[0], item[1]);
        }
        return str;
    }
}
