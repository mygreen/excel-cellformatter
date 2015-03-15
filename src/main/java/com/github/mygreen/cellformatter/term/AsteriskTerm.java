package com.github.mygreen.cellformatter.term;

import com.github.mygreen.cellformatter.tokenizer.Token;


/**
 *  フォーマットの書式'*'の記号を表現する項。
 * ・'*'の次に続く文字をセルの幅によって埋める。
 * ・通常は何もしない。
 * 
 * @author T.TSUCHIE
 *
 */
public class AsteriskTerm<T> implements Term<T> {
    
    private static final String EMPTY = "";
    
    private final Token.Asterisk token;
    
    public AsteriskTerm(final Token.Asterisk token) {
        this.token = token;
    }
    
    @Override
    public String format(final T number) {
        return EMPTY;
    }
    
    public Token.Asterisk getToken() {
        return token;
    }
}
