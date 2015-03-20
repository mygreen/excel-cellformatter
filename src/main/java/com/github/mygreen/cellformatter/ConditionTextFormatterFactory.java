package com.github.mygreen.cellformatter;

import java.util.ArrayList;
import java.util.List;

import com.github.mygreen.cellformatter.lang.ArgUtils;
import com.github.mygreen.cellformatter.term.AsteriskTerm;
import com.github.mygreen.cellformatter.term.EscapedCharTerm;
import com.github.mygreen.cellformatter.term.OtherTerm;
import com.github.mygreen.cellformatter.term.TextTerm;
import com.github.mygreen.cellformatter.term.UnderscoreTerm;
import com.github.mygreen.cellformatter.term.WordTerm;
import com.github.mygreen.cellformatter.tokenizer.Token;
import com.github.mygreen.cellformatter.tokenizer.TokenStore;


/**
 * 書式を解析して{@link ConditionTextFormatter}のインスタンスを作成するクラス。
 * @author T.TSUCHIE
 *
 */
public class ConditionTextFormatterFactory extends ConditionFormatterFactory<ConditionTextFormatter> {
    
    /**
     * テキストの書式かどうか判定する。
     * @param store
     * @return
     */
    public boolean isTextPattern(final TokenStore store) {
        return store.containsInFactor("@");
    }
    
    /**
     * {@link ConditionTextFormatter}のインスタンスを作成する。
     * @param store
     * @return
     * @throws IllegalArgumentException store is null.
     */
    public ConditionTextFormatter create(final TokenStore store) {
        ArgUtils.notNull(store, "store");
        
        final ConditionTextFormatter formatter = new ConditionTextFormatter(store.getConcatenatedToken());
        
        for(Token token : store.getTokens()) {
            
            if(token instanceof Token.Condition) {
                // 条件の場合
                final Token.Condition conditionToken = token.asCondition();
                final String condition = conditionToken.getCondition();
                formatter.addCondition(condition);
                
                if(isConditionOperator(conditionToken)) {
                    setupConditionOperator(formatter, conditionToken);
                    
                } else if(isConditionLocale(conditionToken)) {
                    setupConditionLocale(formatter, conditionToken);
                    
                } else if(isConditionDbNum(conditionToken)) {
                    setupConditionDbNum(formatter, conditionToken);
                    
                } else if(isConditionColor(conditionToken)) {
                    setupConditionColor(formatter, conditionToken);
                    
                }
                
            } else if(token instanceof Token.Word) {
                formatter.addTerm(new WordTerm<String>(token.asWord()));
                
            } else if(token instanceof Token.EscapedChar) {
                formatter.addTerm(new EscapedCharTerm<String>(token.asEscapedChar()));
                
            } else if(token instanceof Token.Underscore) {
                formatter.addTerm(new UnderscoreTerm<String>(token.asUnderscore()));
                
            } else if(token instanceof Token.Asterisk) {
                formatter.addTerm(new AsteriskTerm<String>(token.asAsterisk()));
                
            } else if(token instanceof Token.Factor) {
                // 因子を記号'@'で分割する
                List<Token> list = convertFactor(token.asFactor());
                
                for(Token item : list) {
                    if(item.equals(Token.Symbol.SYMBOL_AT_MARK)) {
                        formatter.addTerm(TextTerm.atMark(item.asSymbol()));
                    } else {
                        formatter.addTerm(new OtherTerm<String>(item));
                    }
                }
                
            }
            
        }
        
        return formatter;
        
    }
    
    /**
     * 書式の因子を記号'@'とそれ以外に変換する。
     * @param str
     * @return
     */
    private List<Token> convertFactor(final Token.Factor factor) {
        
        final String item = factor.getValue();
        final int itemLength = item.length();
        final List<Token> list = new ArrayList<>();
        
        StringBuilder sb = new StringBuilder();
        for(int i=0; i < itemLength; i++) {
            char c = item.charAt(i);
            if(c == '@') {
                // バッファから取り出し、新たにバッファを作成する
                list.add(Token.factor(sb.toString()));
                sb = new StringBuilder();
                
                list.add(Token.SYMBOL_AT_MARK);
                
            } else {
                sb.append(c);
            }
        }
        
        if(sb.length() > 0) {
            list.add(Token.factor(sb.toString()));
            sb = null;
        }
        
        return list;
    }
}
