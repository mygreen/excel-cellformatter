package com.github.mygreen.cellformatter.tokenizer;

import java.util.ArrayList;
import java.util.List;

import com.github.mygreen.cellformatter.lang.ArgUtils;
import com.github.mygreen.cellformatter.lang.Utils;


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
            
            if(token.equals(symbol)) {
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
        return containsInFactor(search, false);
    }
    
    /**
     * 大文字・小文字を無視して{@link Token.Factor}中に指定した文字列を含むかどうか。
     * @param search
     * @return
     */
    public boolean containsInFactorIgnoreCase(final String search) {
        return containsInFactor(search, true);
    }
    
    public boolean containsInFactor(final String search, final boolean ignoreCase) {
        
        for(Token token : tokens) {
            if(!(token instanceof Token.Factor)) {
                continue;
            }
            
            final Token.Factor factor = token.asFactor();
            if(ignoreCase) {
                if(Utils.containsIgnoreCase(factor.getValue(), search)) {
                    return true;
                }
            } else {
                if(factor.getValue().contains(search)) {
                    return true;
                }
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
        return containsAnyInFactor(searchChars, false);
    }
    
    /**
     * 大文字・小文字を無視して{@link Token.Factor}中に指定した文字列の何れかを含むかどうか。
     * @param searchChars
     * @return
     */
    public boolean containsAnyInFactorIgnoreCase(final String[] searchChars) {
        return containsAnyInFactor(searchChars, true);
    }
    
    /**
     * 大文字・小文字を無視して{@link Token.Factor}中に指定した文字列の何れかを含むかどうか。
     * @param searchChars
     * @return
     */
    private boolean containsAnyInFactor(final String[] searchChars, final boolean ignoreCase) {
        
        for(Token token : tokens) {
            if(!(token instanceof Token.Factor)) {
                continue;
            }
            
            final Token.Factor factor = token.asFactor();
            if(Utils.containsAny(factor.getValue(), searchChars, ignoreCase)) {
                return true;
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
            sb.append(String.format("[%d]%s(%s)",
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
