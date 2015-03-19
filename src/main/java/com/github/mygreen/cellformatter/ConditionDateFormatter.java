package com.github.mygreen.cellformatter;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.mygreen.cellformatter.callback.Callback;
import com.github.mygreen.cellformatter.lang.ArgUtils;
import com.github.mygreen.cellformatter.lang.Utils;
import com.github.mygreen.cellformatter.term.Term;


/**
 * ユーザ定義型の日時を解釈するフォーマッタ
 * @author T.TSUCHIE
 *
 */
public class ConditionDateFormatter extends ConditionFormatter<Calendar> {
    
    /**
     * Excelでの基準日である「1900年1月0日」の値。
     */
    private static final long ZERO_TIME = Utils.getExcelZeroDate();
    
    /**
     * 日時の各項
     */
    private List<Term<Calendar>> terms = new CopyOnWriteArrayList<>();
    
    public ConditionDateFormatter(final String pattern) {
        super(pattern);
    }
    
    @Override
    public FormatterType getType() {
        return FormatterType.Date;
    }
    
    /**
     * 値が条件に一致するかどうか。
     * <p>Excelの1900年1月1日を基準に、ミリ秒に直して判定する。
     * @param date
     * @return
     */
    @Override
    public boolean isMatch(final Calendar cal) {
        final long value = cal.getTime().getTime() - ZERO_TIME;
        return getOperator().isMatch(value);
    }
    
    @Override
    public String format(final Calendar cal, final Locale runtimeLocale) {
        ArgUtils.notNull(cal, "cal");
        
        // 各項の処理
        StringBuilder sb = new StringBuilder();
        for(Term<Calendar> term : terms) {
            sb.append(applyFormatCallback(cal, term.format(cal, getLocale(), runtimeLocale)));
        }
        
        String value = sb.toString();
        
        return value;
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    private String applyFormatCallback(final Calendar cal, final String str) {
        
        String value = str;
        
        for(Callback callback : getCallbacks()) {
            value = callback.call(cal, value);
        }
        
        return value;
        
    }
    
    /**
     * フォーマットの項を追加する。
     * @param term
     */
    public void addTerm(final Term<Calendar> term) {
        this.terms.add(term);
    }
    
    /**
     * フォーマットの複数の項を追加する。
     * @param term
     */
    public void addAllTerms(List<Term<Calendar>> terms) {
        this.terms.addAll(terms);
    }
    
    /**
     * フォーマットの項を全て取得する。
     * @return
     */
    public List<Term<Calendar>> getTerms() {
        return terms;
    }
}
