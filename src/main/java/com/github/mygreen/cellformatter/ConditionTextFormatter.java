package com.github.mygreen.cellformatter;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.mygreen.cellformatter.term.Term;


/**
 * Excelのテキスト表示のフォーマットと処理を行うクラス。
 * <p>書式中に'@'を含むもの。
 * @version 0.3
 * @author T.TSUCHIE
 *
 */
public class ConditionTextFormatter extends ConditionFormatter {
    
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
     * セルのタイプが文字列の場合に一致。
     */
    @Override
    public boolean isMatch(final CommonCell cell) {
        return cell.isText() || cell.isBoolean();
    }
    
    @Override
    public CellFormatResult format(final CommonCell cell, final Locale runtimeLocale) {
        
        final String value;
        if(cell.isBoolean()) {
            value = String.valueOf(cell.getBooleanCellValue()).toUpperCase();
        } else {
            value = cell.getTextCellValue();
        }
        
        final StringBuilder sb = new StringBuilder();
        
        for(Term<String> term : terms) {
            sb.append(term.format(value, getLocale(), runtimeLocale));
        }
        
        String text = sb.toString();
        
        final CellFormatResult result = new CellFormatResult();
        if(cell.isBoolean()) {
            result.setValue(cell.getBooleanCellValue());
            result.setCellType(FormatCellType.Boolean);
        } else {
            result.setValue(value);
            result.setCellType(FormatCellType.Text);
        }
        
        result.setText(text);
        result.setTextColor(getColor());
        result.setSectionPattern(getPattern());
        
        return result;
        
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
