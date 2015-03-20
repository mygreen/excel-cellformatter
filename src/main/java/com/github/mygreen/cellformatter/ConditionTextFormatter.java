package com.github.mygreen.cellformatter;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.mygreen.cellformatter.term.Term;


/**
 * Excelのテキスト表示のフォーマットと処理を行うクラス。
 * <p>書式中に'@'を含むもの。
 * @author T.TSUCHIE
 *
 */
public class ConditionTextFormatter extends ConditionFormatter<String> {
    
    /**
     * テキストの書式の項
     */
    private List<Term<String>> terms = new CopyOnWriteArrayList<>();
    
    public ConditionTextFormatter(final String pattern) {
        super(pattern);
    }
    
    @Override
    public FormatterType getType() {
        return FormatterType.Text;
    }
    
    /**
     * 常にtrueを返す。
     */
    @Override
    public boolean isMatch(String value) {
        return true;
    }
    
    @Override
    public String format(final String value, final Locale runtimeLocale) {
        
        final StringBuilder sb = new StringBuilder();
        
        for(Term<String> term : terms) {
            sb.append(term.format(value, getLocale(), runtimeLocale));
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
    
}
