package com.github.mygreen.cellformatter.term;

import com.github.mygreen.cellformatter.tokenizer.Token;


/**
 * フォーマットの書式'_'の記号を表現する項。
 * ・'_'の次に続く文字を空白で置き換える。
 * @author T.TSUCHIE
 *
 */
public class UnderscoreTerm<T> implements Term<T> {
    
    private static final String SPACE = " ";
    
    private final Token.Underscore token;
    
    public UnderscoreTerm(final Token.Underscore token) {
        this.token = token;
    }
    
    @Override
    public String format(final T number) {
        return SPACE;
    }
    
    public Token.Underscore getToken() {
        return token;
    }
}
