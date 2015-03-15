package com.github.mygreen.cellformatter.tokenizer;

import java.util.ArrayList;
import java.util.List;

import com.github.mygreen.cellformatter.lang.ArgUtils;


/**
 * {@link Token}を保持するクラス。
 * <p>検索機能などを提供する。
 * @author T.TSUCHIE
 *
 */
public class TokenStore {
    
    private List<Token> tokens = new ArrayList<>();
    
    public TokenStore() {
        
    }
    
    /**
     * トークンを追加する。
     * @param token
     */
    public void add(final Token token) {
        getTokens().add(token);
    }
    
    /**
     * トークンを全て追加する。
     * @param tokens
     */
    public void addAll(final List<Token> tokens) {
        getTokens().addAll(tokens);
    }
    
    /**
     * トークンを取得する。
     * @return
     */
    public List<Token> getTokens() {
        return tokens;
    }
    
    /**
     * 記号でトークンを分割する。
     * @param symbol 分割する記号
     * @return
     * @throws IllegalArgumentException symbol == null.
     */
    public List<TokenStore> split(final Token.Symbol symbol) {
        
        ArgUtils.notNull(symbol, "symbol");
        
        final List<TokenStore> list = new ArrayList<>();
        
        TokenStore store = new TokenStore();
        list.add(store);
        
        for(Token token : tokens) {
            
            if(token.equals(Token.SYMBOL_SEMI_COLON)) {
                store = new TokenStore();
                list.add(store);
                
            } else {
                store.add(token);
            }
            
        }
        
        return list;
    }
    
    /**
     * トークンを全て結合した値を取得する。
     * @return
     */
    public String getConcatenatedToken() {
        
        StringBuilder sb = new StringBuilder();
        
        for(Token token : tokens) {
            sb.append(token.getValue());
        }
        
        return sb.toString();
        
    }
    
    /**
     * {@link Token.Factor}中に指定した文字列を含むかどうか。
     * @param search
     * @return
     */
    public boolean containsInFactor(final String search) {
        
        for(Token token : tokens) {
            if(!(token instanceof Token.Factor)) {
                continue;
            }
            
            final Token.Factor factor = token.asFactor();
            if(factor.getValue().contains(search)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * {@link Token.Factor}中に指定した文字列の何れかを含むかどうか。
     * @param searchChars
     * @return
     */
    public boolean containsAnyInFactor(final String[] searchChars) {
        
        for(Token token : tokens) {
            if(!(token instanceof Token.Factor)) {
                continue;
            }
            
            final Token.Factor factor = token.asFactor();
            for(String search : searchChars) {
                if(factor.getValue().contains(search)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    @Override
    public String toString() {
        
        StringBuilder sb = new StringBuilder();
        
        final int size = tokens.size();
        for(int i=0; i < size; i++) {
            final Token token = tokens.get(i);
            sb.append(String.format("[i]%s(%s)",
                    i,
                    token.getClass().getSimpleName(),
                    token.getValue()));
            
            if(i < size-1) {
                sb.append(", ");
            }
            
        }
        
        return sb.toString();
        
    }
    
}
