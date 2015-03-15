package com.github.mygreen.cellformatter.term;

import com.github.mygreen.cellformatter.lang.ArgUtils;
import com.github.mygreen.cellformatter.tokenizer.Token;


/**
 *
 * @author T.TSUCHIE
 *
 */
public class OtherTerm<T> implements Term<T> {
    
    private final Token token;
    
    public OtherTerm(final Token token) {
        ArgUtils.notNull(token, "token");
        this.token = token;
    }
    
    @Override
    public String format(final T value) {
        return token.getValue();
    }
    
    public Token getToken() {
        return token;
    }
}
