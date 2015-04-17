package com.github.mygreen.cellformatter;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.mygreen.cellformatter.number.NumberFactory;
import com.github.mygreen.cellformatter.term.NumberTerm;
import com.github.mygreen.cellformatter.term.TextTerm;
import com.github.mygreen.cellformatter.tokenizer.Token;



/**
 * ユーザ定義のフォーマッタ。
 * 
 * @version 0.4
 * @author T.TSUCHIE
 *
 */
public class CustomFormatter extends CellFormatter {
    
    /**
     * 書式がない、標準フォーマッター
     */
    public static final CustomFormatter DEFAULT_FORMATTER;
    static {
        // 数値の場合
        final ConditionNumberFormatter numberFormatter = new ConditionNumberFormatter("General");
        numberFormatter.addTerm(NumberTerm.general());
        numberFormatter.setOperator(ConditionOperator.ALL);
        numberFormatter.setNumberFactory(NumberFactory.decimalNumber(0, false, 0));
        
        // 文字列の場合
        final ConditionTextFormatter textFormatter = new ConditionTextFormatter("General");
        textFormatter.addTerm(TextTerm.atMark(Token.SYMBOL_AT_MARK));
        textFormatter.setOperator(ConditionOperator.ALL);
        
        final CustomFormatter formatter = new CustomFormatter("");
        formatter.addConditionFormatter(numberFormatter);
        formatter.addConditionFormatter(textFormatter);
        
        DEFAULT_FORMATTER = formatter;
    }
    
    /**
     * 書式のパターン
     */
    private final String pattern;
    
    /**
     * 条件付きのフォーマッタ
     */
    private List<ConditionFormatter> conditionFormatters = new CopyOnWriteArrayList<>();
    
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
    public CellFormatResult format(final CommonCell cell, final Locale runtimeLocale) {
        
        for(ConditionFormatter formatter : conditionFormatters) {
            if(formatter.isMatch(cell)) {
                return formatter.format(cell, runtimeLocale);
            }
        }
        
        /*
         * 一致するものがない場合は、デフォルトのフォーマッタで処理する。
         * ・セクションとセルの属性が一致していない場合に発生する。
         * ・数値が設定されているのに、文字列用のセクションしかない場合。
         */
        if(cell.isText()) {
            return DEFAULT_FORMATTER.format(cell, runtimeLocale);
            
        } else if(cell.isNumber()) {
            return DEFAULT_FORMATTER.format(cell, runtimeLocale);
        }
        
        throw new NoMatchConditionFormatterException(cell, String.format(
                "not match format for cell : '%s'", cell.getCellAddress()));
        
    }
    
    /**
     * 書式のパターンを取得する。
     * @return
     */
    public String getPattern() {
        return pattern;
    }
    
    /**
     * 文字列の書式を持つかどうか。
     * @return
     */
    public boolean hasTextFormatter() {
        for(ConditionFormatter formatter : conditionFormatters) {
            if(formatter.isTextFormatter()) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 日時のフォーマッタを持つかどうか。
     * <p>ただし、';'で区切り数値と日時の書式を同時に持つ可能性がある。
     * @return
     */
    public boolean hasDateFormatter() {
        for(ConditionFormatter formatter : conditionFormatters) {
            if(formatter.isDateFormatter()) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 数値のフォーマッタを持つかどうか。
     * <p>ただし、';'で区切り数値と日時の書式を同時に持つ可能性がある。
     * @return
     */
    public boolean hasNumberFormatter() {
        for(ConditionFormatter formatter : conditionFormatters) {
            if(formatter.isNumberFormatter()) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 条件付きのフォーマッタを追加する。
     * @param formatter
     */
    public void addConditionFormatter(ConditionFormatter formatter) {
        this.conditionFormatters.add(formatter);
    }
    
    /**
     * 条件付きのフォーマッタを取得する。
     * @param formatter
     */
    public List<ConditionFormatter> getConditionFormatters() {
        return conditionFormatters;
    }
    
}
