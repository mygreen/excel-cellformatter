package com.github.mygreen.cellformatter.term;

import com.github.mygreen.cellformatter.lang.ArgUtils;
import com.github.mygreen.cellformatter.tokenizer.Token;


/**
 * 引用符'"'で囲まれた文字列の項
 * @author T.TSUCHIE
 *
 */
public class WordTerm<T> implements Term<T> {
    
    private final Token.Word token;
    
    public WordTerm(Token.Word token) {
        ArgUtils.notNull(token, "token");
        this.token = token;
    }
    
    @Override
    public String format(final T value) {
        return token.getWord();
    }
    
    public Token.Word getToken() {
        return token;
    }
}
