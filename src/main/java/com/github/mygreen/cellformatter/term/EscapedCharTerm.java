package com.github.mygreen.cellformatter.term;

import java.util.Locale;

import com.github.mygreen.cellformatter.lang.ArgUtils;
import com.github.mygreen.cellformatter.lang.MSLocale;
import com.github.mygreen.cellformatter.tokenizer.Token;


/**
 * エスケープされた文字の項
 * @author T.TSUCHIE
 *
 */
public class EscapedCharTerm<T> implements Term<T> {
    
    private final Token.EscapedChar token;
    
    public EscapedCharTerm(final Token.EscapedChar token) {
        ArgUtils.notNull(token, "token");
        this.token = token;
    }
    
    @Override
    public String format(final T value, final MSLocale formatLocale, final Locale runtimeLocale) {
        return token.getChar();
    }
    
    public Token.EscapedChar getToken() {
        return token;
    }
}
