package com.github.mygreen.cellformatter.term;

import java.util.Locale;

import com.github.mygreen.cellformatter.lang.ArgUtils;
import com.github.mygreen.cellformatter.lang.MSLocale;
import com.github.mygreen.cellformatter.tokenizer.Token;


/**
 * テキスト用の書式用の特別な項
 * @author T.TSUCHIE
 *
 */
public abstract class TextTerm implements Term<String> {
    
    public static AtMarkTerm atMark(final Token.Symbol token) {
        return new AtMarkTerm(token);
    } 
    
    /**
     * 記号'@'の項
     */
    public static class AtMarkTerm extends TextTerm {
        
        private final Token.Symbol token;
        
        public AtMarkTerm(final Token.Symbol token) {
            ArgUtils.notNull(token, "token");
            this.token = token;
        }
        
        @Override
        public String format(final String value, final MSLocale formatLocale, final Locale runtimeLocale) {
            return value == null ? "" : value;
        }
        
        public Token.Symbol getToken() {
            return token;
        }
        
    }
}
