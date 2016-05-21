package com.github.mygreen.cellformatter.tokenizer;

import java.util.LinkedList;

import com.github.mygreen.cellformatter.lang.Utils;


/**
 * カスタム定義の書式をトークンに分割する。
 * @author T.TSUCHIE
 *
 */
public class CustomFormatTokenizer {
    
    /**
     * 書式をトークンに分割する。
     * @param pattern Excelの書式
     * @return
     */
    public TokenStore parse(final String pattern) {
        
        final TokenStore store = new TokenStore();
        if(Utils.isEmpty(pattern)) {
            return store;
        }
        
        splitToken(store, pattern);
        return store;
    }
    
    private void splitToken(final TokenStore store, final String pattern) {
        
        // 解析時の途中の文字を一時的に補完しておく。
        final LinkedList<String> stack = new LinkedList<String>();
        
        final int length = pattern.length();
        for(int i=0; i < length; i++) {
            final char c = pattern.charAt(i);
            
            if(StackUtils.equalsAnyTopElement(stack, Token.STR_ESCAPES)) {
                
                // スタックの一番上がエスケープの文字だが、文字列の囲み文字(")の終了の場合は、文字列として追加する
                if(c == '"' && StackUtils.equalsBottomElement(stack, "\"")) {
                    store.add(Token.word(StackUtils.popupAndConcat(stack) + c));
                    continue;
                }
                // スタックの一番上がエスケープ文字の場合でかつ、括弧や文字列などの囲み文字の中の場合は、通常の文字として扱う。
                if(StackUtils.equalsAnyBottomElement(stack, new String[]{"[", "\""})) {
                    stack.push(String.valueOf(c));
                    
                } else {
                    // エスケープ文字として分割する。
                    final String escapedChar = StackUtils.popup(stack);
                    final String concatStr = StackUtils.popupAndConcat(stack);
                    if(concatStr.length() >= 1) {
                        // エスケープ文字以前の文字を追加する。
                        store.add(Token.factor(concatStr));
                    }
                    
                    store.add(Token.escapedChar(escapedChar + c));
                    
                }
                
                continue;
            }
            
            if(c == '"') {
                
                if(StackUtils.equalsBottomElement(stack, "\"")) {
                    // 文字列の囲み文字'"'の終わりの場合、文字列として追加する。
                    store.add(Token.word(StackUtils.popupAndConcat(stack) + c));
                    
                } else if(!stack.isEmpty()) {
                    // 文字列の引用符で始まらず、既に文字が入っている場合は、既存のものを取り出し分割する。
                    // 既存のものはFactorとする。
                    store.add(Token.factor(StackUtils.popupAndConcat(stack)));
                    stack.push(String.valueOf(c));
                    
                } else {
                    // 文字列の開始の場合
                    stack.push(String.valueOf(c));
                    
                }
                
            } else if(c == '[') {
                if(!stack.isEmpty() && !StackUtils.equalsBottomElement(stack, "\"")) {
                    // 文字列の引用符の中ではなく、既に文字が入っている場合は、既存のものを取り出し分割する。
                    // 既存のものはFactorとする。
                    store.add(Token.factor(StackUtils.popupAndConcat(stack)));
                }
                
                stack.push(String.valueOf(c));
                
            } else if(c == ']') {
                
                if(StackUtils.equalsBottomElement(stack, "[")) {
                    // 条件の終わりの場合
                    store.add(Token.condition(StackUtils.popupAndConcat(stack) + c));
                    
                } else {
                    stack.push(String.valueOf(c));
                }
                
            } else if(c == ';') {
                
                if(!stack.isEmpty()) {
                    // 既に文字が入っている場合は、既存のものを取り出し分割する。
                    store.add(Token.factor(StackUtils.popupAndConcat(stack)));
                }
                
                store.add(Token.SYMBOL_SEMI_COLON);
                
            } else if(c == '_') {
                if(StackUtils.equalsBottomElement(stack, "\"")) {
                    // 文字列の中の場合の場合は、文字列として処理する。
                    stack.push(String.valueOf(c));
                    
                    continue;
                }
                
                // スタックに文字がある場合、既存のものを取り出し分割する。
                if(!stack.isEmpty()) {
                    store.add(Token.factor(StackUtils.popupAndConcat(stack)));
                    
                }
                
                // 次に続く１文字を取得する
                StringBuilder next = new StringBuilder();
                if(i + 1 < length) {
                    i++;
                    final char c2 = pattern.charAt(i);
                    next.append(c2);
                    
                    if(c2 == '\\') {
                        if(i + 1 < length) {
                            i++;
                            i++;
                            final char c3 = pattern.charAt(i);
                            next.append(c3);
                        }
                    }
                    
                    store.add(Token.underscore(c + next.toString()));
                    
                } else {
                    store.add(Token.factor(String.valueOf(c)));
                }
                
                
            } else if(c == '*') {
                
                if(StackUtils.equalsBottomElement(stack, "\"")) {
                    // 文字列の中の場合の場合は、文字列として処理する。
                    stack.push(String.valueOf(c));
                    
                    continue;
                }
                
                // スタックに文字がある場合、既存のものを取り出し分割する。
                if(!stack.isEmpty()) {
                    store.add(Token.factor(StackUtils.popupAndConcat(stack)));
                    
                }
                
                // 次に続く１文字を取得する
                StringBuilder next = new StringBuilder();
                if(i + 1 < length) {
                    i++;
                    final char c2 = pattern.charAt(i);
                    next.append(c2);
                    
                    if(c2 == '\\') {
                        if(i + 1 < length) {
                            i++;
                            i++;
                            final char c3 = pattern.charAt(i);
                            next.append(c3);
                        }
                    }
                    
                    store.add(Token.asterisk(c + next.toString()));
                    
                } else {
                    store.add(Token.factor(String.valueOf(c)));
                }
                
            } else {
                
                stack.push(String.valueOf(c));
            }
            
            
        }
        
        if(!stack.isEmpty()) {
            store.add(Token.factor(StackUtils.popupAndConcat(stack)));
        }
        
    }
    
}
