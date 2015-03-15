package com.github.mygreen.cellformatter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.mygreen.cellformatter.term.Term;


/**
 * Excelのテキスト表示のフォーマットと処理を行うクラス。
 * <p>書式中に'@'を含むもの。
 * @author T.TSUCHIE
 *
 */
public class TextFormatter {
    
    /**
     * 書式のパターン
     */
    private final String pattern;
    
    /**
     * テキストの書式の項
     */
    private List<Term<String>> terms = new CopyOnWriteArrayList<>();
    
    public TextFormatter(final String pattern) {
        this.pattern = pattern;
    }
    
    public String format(final String value) {
        
        final StringBuilder sb = new StringBuilder();
        
        for(Term<String> term : terms) {
            sb.append(term.format(value));
        }
        
        return sb.toString();
        
    }
    
    /**
     * 書式の項を全て取得する。
     * @return
     */
    public List<Term<String>> getTerms() {
        return terms;
    }
    
    /**
     * 書式の項を追加する。
     * @param term
     */
    public void addTerm(Term<String> term) {
        this.terms.add(term);
    }
    
    /**
     * 書式のパターンを取得する。
     * @return
     */
    public String getPattern() {
        return pattern;
    }
    
}
