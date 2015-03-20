package com.github.mygreen.cellformatter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mygreen.cellformatter.callback.DaijiCallback;
import com.github.mygreen.cellformatter.callback.KansujiCallback;
import com.github.mygreen.cellformatter.callback.Callback;
import com.github.mygreen.cellformatter.callback.ZenkakuNumberCallback;
import com.github.mygreen.cellformatter.lang.MSColor;
import com.github.mygreen.cellformatter.lang.MSLocale;
import com.github.mygreen.cellformatter.lang.MSLocaleBuilder;
import com.github.mygreen.cellformatter.tokenizer.Token;
import com.github.mygreen.cellformatter.tokenizer.TokenStore;


/**
 * 条件付きの書式の組み立てるための抽象クラス。
 * <p>主にテンプレートメソッドの実装を行う。
 * 
 * @author T.TSUCHIE
 * @param <F> 組み立てるフォーマッタクラス
 */
public abstract class ConditionFormatterFactory<F> {
    
    protected static Logger logger = LoggerFactory.getLogger(ConditionFormatterFactory.class);
    
    /**
     * 条件付き書式を組み立てる
     * @param store
     * @return
     */
    public abstract F create(TokenStore store);
    
    /**
     * 演算子を使用した条件式のパターン
     */
    private static final Pattern PATTERN_CONDITION_OPERATOR = Pattern.compile("\\[([><=]+)([+-]*[0-9\\.]+)\\]");
    
    /**
     * ロケールの条件式のパターン
     */
    private static final Pattern PATTERN_CONDITION_LOCALE = Pattern.compile("\\[\\$\\-([0-9a-zA-Z]+)\\]");
    
    /**
     * 特殊な処理の条件式のパターン
     */
    private static final Pattern PATTERN_CONDITION_DBNUM = Pattern.compile("\\[DBNum([0-9]+)\\]");
    
    /**
     * インデックス形式の色の条件式のパターン
     */
    private static final Pattern PATTERN_CONDITION_INDEX_COLOR = Pattern.compile("\\[(色|Color)([0-9]+)\\]");
    
    /**
     * 演算子の条件式かどうか。
     * @param token
     *
     */
    protected boolean isConditionOperator(final Token.Condition token) {
        return PATTERN_CONDITION_OPERATOR.matcher(token.getValue()).matches();
    }
    
    /**
     * ロケールの条件式かどうか。
     * @param token
     *
     */
    protected boolean isConditionLocale(final Token.Condition token) {
        return PATTERN_CONDITION_LOCALE.matcher(token.getValue()).matches();
    }
    
    /**
     * 特殊な処理の条件式かどうか。
     * @param token
     *
     */
    protected boolean isConditionDbNum(final Token.Condition token) {
        return PATTERN_CONDITION_DBNUM.matcher(token.getValue()).matches();
    }
    
    /**
     * 色の条件式の書式かどうか
     * @param token
     *
     */
    protected boolean isConditionColor(final Token.Condition token) {
        if(MSColor.valueOfKnownColor(token.getCondition()) != null) {
            return true;
        }
        return PATTERN_CONDITION_INDEX_COLOR.matcher(token.getValue()).matches();
    }
    
    /**
     * '[<=1000]'などの数値の条件を組み立てる
     * @param formatter
     * @param token
     * @return
     * @throws IllegalArgumentException 処理対象の条件として一致しない場合
     */
    protected ConditionOperator setupConditionOperator(final ConditionFormatter<?> formatter, final Token.Condition token) {
        
        final Matcher matcher = PATTERN_CONDITION_OPERATOR.matcher(token.getValue());
        if(!matcher.matches()) {
            throw new IllegalArgumentException("not match condition:" + token.getValue());
        }
        
        final String operator = matcher.group(1);
        final String number = matcher.group(2);
        final double condition = Double.valueOf(number);
        
        final ConditionOperator conditionOperator;
        switch(operator) {
            case "=":
                conditionOperator = new ConditionOperator.Equal(condition);
                break;
            case "<>":
                conditionOperator = new ConditionOperator.NotEqual(condition);
                break;
            case ">":
                conditionOperator = new ConditionOperator.GreaterThan(condition);
                break;
            case "<":
                conditionOperator = new ConditionOperator.LessThan(condition);
                break;
            case ">=":
                conditionOperator = new ConditionOperator.GreaterEqual(condition);
                break;
            case "<=":
                conditionOperator = new ConditionOperator.LessEqual(condition);
                break;
            default:
                logger.warn("unknown operator : {}", operator);
                conditionOperator = ConditionOperator.ALL;
                break;
            
        }
        
        formatter.setOperator(conditionOperator);
        return conditionOperator;
    }
    
    /**
     * ロケールの条件を組み立てる
     * @param formatter
     * @param str
     * @return
     * @throws IllegalArgumentException 処理対象の条件として一致しない場合
     */
    protected MSLocale setupConditionLocale(final ConditionFormatter<?> formatter, final Token.Condition token) {
        
        final Matcher matcher = PATTERN_CONDITION_LOCALE.matcher(token.getValue());
        if(!matcher.matches()) {
            throw new IllegalArgumentException("not match condition:" + token.getValue());
        }
        
        final String number = matcher.group(1);
        
        // 16進数=>10進数に直す
        final int value = Integer.valueOf(number, 16);
        MSLocale locale = MSLocale.valueOfKnwonValue(value);
        if(locale == null) {
            locale = MSLocaleBuilder.create().value(value).build();
        }
        
        formatter.setLocale(locale);
        
        return locale;
        
    }
    
    /**
     * '[DBNum1]'などの組み込み処理の条件を組み立てる。
     * @param formatter
     * @param str
     * @return
     * @throws IllegalArgumentException 処理対象の条件として一致しない場合
     */
    protected Callback<?> setupConditionDbNum(final ConditionFormatter<?> formatter, final Token.Condition token) {
        
        final Matcher matcher = PATTERN_CONDITION_DBNUM.matcher(token.getValue());
        if(!matcher.matches()) {
            throw new IllegalArgumentException("not match condition:" + token.getValue());
        }
        
        Callback<?> callback = null;
        final String number = matcher.group(1);
        if(number.startsWith("1")) {
            callback = KansujiCallback.create();
            
        } else if(number.startsWith("2")) {
            callback = DaijiCallback.create();
            
        } else if(number.startsWith("3")) {
            callback = ZenkakuNumberCallback.create();
        }
        
        if(callback != null) {
            formatter.addCallback(callback);
        }
        
        return callback;
    }
    
    
    
    /**
     * 色の条件の組み立てる。
     * @param formatter
     * @param str
     * @return
     * @throws IllegalArgumentException 処理対象の条件として一致しない場合
     */
    protected MSColor setupConditionColor(final ConditionFormatter<?> formatter, final Token.Condition token) {
        
        // 名前指定の場合
        MSColor color = MSColor.valueOfKnownColor(token.getCondition());
        if(color != null) {
            formatter.setColor(color);
            return color;
        }
        
        // インデックス指定の場合
        final Matcher matcher = PATTERN_CONDITION_INDEX_COLOR.matcher(token.getValue());
        if(!matcher.matches()) {
            throw new IllegalArgumentException("not match condition:" + token.getValue());
        }
        
        final String prefix = matcher.group(1);
        final String number = matcher.group(2);
        final short index = Short.valueOf(number);
        color = MSColor.valueOfIndexColor(index);
        
        formatter.setColor(color);
        
        return color;
    }
    
}
