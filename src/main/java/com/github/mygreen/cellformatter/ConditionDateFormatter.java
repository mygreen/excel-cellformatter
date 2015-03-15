package com.github.mygreen.cellformatter;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.mygreen.cellformatter.callback.Callback;
import com.github.mygreen.cellformatter.term.Term;


/**
 * ユーザ定義型の日時を解釈するフォーマッタ
 * @author T.TSUCHIE
 *
 */
public class ConditionDateFormatter extends ConditionFormatter<Date> {
    
    /**
     * Excelでの基準日である「1900年1月1日」の値。
     */
    private static final long ZERO_TIME = Timestamp.valueOf("1900-01-01 00:00:00.000").getTime();
    
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
    public boolean isMatch(final Date date) {
        final long value = date.getTime() - ZERO_TIME;
        return getOperator().isMatch(value);
    }
    
    @Override
    public String format(final Date date, final Locale locale) {
        
        final Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        
        //TODO: ロケール、タイムゾーンの設定
        
        // 各項の処理
        StringBuilder sb = new StringBuilder();
        for(Term<Calendar> term : terms) {
            sb.append(applyFormatCallback(date, term.format(cal)));
        }
        
        String value = sb.toString();
        
        return value;
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    private String applyFormatCallback(final Date date, final String str) {
        
        String value = str;
        
        for(Callback callback : getCallbacks()) {
            value = callback.call(date, value);
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
