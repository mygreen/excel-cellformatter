package com.github.mygreen.cellformatter;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.mygreen.cellformatter.number.NumberFactory;
import com.github.mygreen.cellformatter.term.NumberTerm;



/**
 * ユーザ定義のフォーマッタ。
 *
 * @author T.TSUCHIE
 *
 */
public class CustomFormatter extends CellFormatter {
    
    /**
     * 書式がない、標準フォーマッター
     */
    public static final CustomFormatter DEFAULT_FORMATTER;
    static {
        final ConditionNumberFormatter conditionFormatter = new ConditionNumberFormatter("General");
        conditionFormatter.addTerm(NumberTerm.general());
        conditionFormatter.setOperator(ConditionOperator.ALL);
        conditionFormatter.setNumberFactory(NumberFactory.decimalNumber(0, false, 0));
        
        final CustomFormatter formatter = new CustomFormatter("");
        formatter.addConditionFormatter(conditionFormatter);
        
        DEFAULT_FORMATTER = formatter;
    }
    
    /**
     * 書式のパターン
     */
    private final String pattern;
    
    /**
     * テキスト用のフォーマッタ
     */
    private ConditionTextFormatter textFormatter;
    
    /**
     * 条件付きのフォーマッタ
     */
    private List<ConditionFormatter<?>> conditionFormatters = new CopyOnWriteArrayList<>();
    
    /**
     * 書式を指定してインスタンスを作成する。
     * @param pattern
     * @throws IllegalArgumentException patternが空文字の場合。
     */
    public CustomFormatter(final String pattern) {
        this.pattern = pattern;
    }
    
    /**
     * ロケールを指定してフォーマットする
     * @param cell
     * @param locale
     * @return
     */
    @Override
    public String format(final CommonCell cell, final Locale locale) {
        
        if(cell.isString()) {
            if(textFormatter != null) {
                return textFormatter.format(cell.getStringCellValue());
            }  else {
                return cell.getStringCellValue();
            }
            
        } else {
            
            for(ConditionFormatter formatter : conditionFormatters) {
                
                if(formatter.isNumberFormatter() && formatter.isMatch(cell.getNumberCellValue())) {
                    return formatter.asNumberFormatter().format(cell.getNumberCellValue());
                    
                } else if(formatter.isDateFormatter() && formatter.isMatch(cell.getDateCellValue())) {
                    return formatter.asDateFormatter().format(cell.getDateCellValue());
                    
                }
                
            }
            
        }
        
        return "";
        
    }
    
    /**
     * 書式のパターンを取得する。
     * @return
     */
    public String getPattern() {
        return pattern;
    }
    
    /**
     * 日時のフォーマッタかどうか。
     * <p>ただし、';'で区切り数値と日時の書式を同時に持つ可能性がある。
     * @return
     */
    public boolean isDateFormatter() {
        for(ConditionFormatter<?> formatter : conditionFormatters) {
            if(formatter.isDateFormatter()) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 数値のフォーマッタかどうか。
     * <p>ただし、';'で区切り数値と日時の書式を同時に持つ可能性がある。
     * @return
     */
    public boolean isNumberFormatter() {
        for(ConditionFormatter<?> formatter : conditionFormatters) {
            if(formatter.isNumberFormatter()) {
                return true;
            }
        }
        
        return false;
    }
    
    public ConditionTextFormatter getTextFormatter() {
        return textFormatter;
    }
    
    public void setTextFormatter(ConditionTextFormatter textFormatter) {
        this.textFormatter = textFormatter;
    }
    
    public void addConditionFormatter(ConditionFormatter<?> formatter) {
        this.conditionFormatters.add(formatter);
    }
    
    public List<ConditionFormatter<?>> getConditionFormatters() {
        return conditionFormatters;
    }
    
}
