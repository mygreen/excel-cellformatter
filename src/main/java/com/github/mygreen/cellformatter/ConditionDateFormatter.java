package com.github.mygreen.cellformatter;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mygreen.cellformatter.callback.Callback;
import com.github.mygreen.cellformatter.lang.ArgUtils;
import com.github.mygreen.cellformatter.lang.Utils;
import com.github.mygreen.cellformatter.term.DateTerm;
import com.github.mygreen.cellformatter.term.Term;


/**
 * ユーザ定義型の日時を解釈するフォーマッタ
 * 
 * @version 0.3
 * @author T.TSUCHIE
 *
 */
public class ConditionDateFormatter extends ConditionFormatter {
    
    private static final Logger logger = LoggerFactory.getLogger(ConditionDateFormatter.class);
    
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
     * @param cell
     * @return
     */
    @Override
    public boolean isMatch(final CommonCell cell) {
        if(cell.isText()) {
            return false;
        }
        
        final long zeroTime = Utils.getExcelZeroDateTime(cell.isDateStart1904());
        final Date date = cell.getDateCellValue();
        final long value = date.getTime() - zeroTime;
        
        if(logger.isDebugEnabled()) {
            logger.debug("isMatch::date={}, zeroTime={}, diff={}",
                    Utils.formatDate(date), Utils.formatDate(new Date(zeroTime)), value);
        }
        
        return getOperator().isMatch(value);
    }
    
    @Override
    public CellFormatResult format(final CommonCell cell, final Locale runtimeLocale) {
        ArgUtils.notNull(cell, "date");
        
        final Date date = cell.getDateCellValue();
        final Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.setTime(date);
        
        // 各項の処理
        StringBuilder sb = new StringBuilder();
        for(Term<Calendar> term : terms) {
            final String formatValue;
            if(term instanceof DateTerm) {
                formatValue = ((DateTerm) term).format(cal, getLocale(), runtimeLocale, cell.isDateStart1904());
            } else {
                formatValue = term.format(cal, getLocale(), runtimeLocale);
            }
            sb.append(applyFormatCallback(cal, formatValue));
        }
        
        String value = sb.toString();
        
        final CellFormatResult result = new CellFormatResult();
        result.setValue(date);
        result.setText(value);
        result.setTextColor(getColor());
        result.setSectionPattern(getPattern());
        result.setFormatterType(getType());
        
        return result;
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
