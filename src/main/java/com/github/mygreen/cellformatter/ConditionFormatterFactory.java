package com.github.mygreen.cellformatter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mygreen.cellformatter.callback.Callback;
import com.github.mygreen.cellformatter.callback.DaijiCallback;
import com.github.mygreen.cellformatter.callback.KansujiCallback;
import com.github.mygreen.cellformatter.callback.ZenkakuNumberCallback;
import com.github.mygreen.cellformatter.lang.MSColor;
import com.github.mygreen.cellformatter.lang.MSLocale;
import com.github.mygreen.cellformatter.tokenizer.Token;
import com.github.mygreen.cellformatter.tokenizer.TokenStore;


/**
 * 条件付きの書式の組み立てるための抽象クラス。
 * <p>主にテンプレートメソッドの実装を行う。
 * 
 * @version 0.8
 * @author T.TSUCHIE
 * @param <F> 組み立てるフォーマッタクラス
 */
public abstract class ConditionFormatterFactory<F> {
    
    private static Logger logger = LoggerFactory.getLogger(ConditionFormatterFactory.class);
    
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
     * 記号付きロケールの条件式のパターン
     */
    private static final Pattern PATTERN_CONDITION_LOCALE_SYMBOL = Pattern.compile("\\[\\$(.+)\\-([0-9a-zA-Z]+)\\]");
    
    /**
     * 特殊な処理の条件式のパターン
     */
    private static final Pattern PATTERN_CONDITION_DBNUM = Pattern.compile("\\[DBNum([0-9]+)\\]");
    
    /**
     * インデックス形式の色の条件式のパターン
     */
    private static final Pattern PATTERN_CONDITION_INDEX_COLOR = Pattern.compile("\\[(色|Color)([0-9]+)\\]");
    
    /**
     * '[<=1000]'などの演算子の条件式かどうか。
     * @param token 判定対象のトークン。
     * @return true: 演算子の条件式。
     *
     */
    protected boolean isConditionOperator(final Token.Condition token) {
        return PATTERN_CONDITION_OPERATOR.matcher(token.getValue()).matches();
    }
    
    /**
     * '[$-403]'などのロケールの条件式かどうか。
     * @param token 判定対象のトークン。
     * @return true: ロケールの条件式。
     *
     */
    protected boolean isConditionLocale(final Token.Condition token) {
        return PATTERN_CONDITION_LOCALE.matcher(token.getValue()).matches();
    }
    
    /**
     *'[$€-403]'などの記号付きロケールの条件式かどうか。
     *@since 0.8
     * @param token 判定対象のトークン。
     * @return true: 記号付きロケールの条件式。     */
    protected boolean isConditionLocaleSymbol(final Token.Condition token) {
        return PATTERN_CONDITION_LOCALE_SYMBOL.matcher(token.getValue()).matches();
    }
    
    /**
     * '[DBNum1]'などの組み込み処理の条件式かどうか。
     * @param token 判定対象のトークン。
     * @return true: 特殊な処理の条件式。
     *
     */
    protected boolean isConditionDbNum(final Token.Condition token) {
        return PATTERN_CONDITION_DBNUM.matcher(token.getValue()).matches();
    }
    
    /**
     * '[Red]'などの色の条件式の書式かどうか
     * @param token 判定対象のトークン。
     * @return true: 色の条件式。
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
     * @param formatter 現在の組み立て中のフォーマッタのインスタンス。
     * @param token 条件式のトークン。
     * @return 演算子の条件式。
     * @throws IllegalArgumentException 処理対象の条件として一致しない場合
     */
    protected ConditionOperator setupConditionOperator(final ConditionFormatter formatter, final Token.Condition token) {
        
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
     * '[$-403]'などのロケールの条件を組み立てる
     * @param formatter 現在の組み立て中のフォーマッタのインスタンス。
     * @param token 条件式のトークン。
     * @return ロケールの条件式。
     * @throws IllegalArgumentException 処理対象の条件として一致しない場合
     */
    protected MSLocale setupConditionLocale(final ConditionFormatter formatter, final Token.Condition token) {
        
        final Matcher matcher = PATTERN_CONDITION_LOCALE.matcher(token.getValue());
        if(!matcher.matches()) {
            throw new IllegalArgumentException("not match condition:" + token.getValue());
        }
        
        final String number = matcher.group(1);
        
        // 16進数=>10進数に直す
        final int value = Integer.valueOf(number, 16);
        MSLocale locale = MSLocale.createKnownLocale(value);
        if(locale == null) {
            locale = new MSLocale(value);
        }
        
        formatter.setLocale(locale);
        
        return locale;
        
    }
    
    /**
     * '[$€-403]'などの記号付きロケールの条件を組み立てる
     * @since 0.8
     * @param formatter 現在の組み立て中のフォーマッタのインスタンス。
     * @param token 条件式のトークン。
     * @return 記号付きロケールの条件式。
     * @throws IllegalArgumentException 処理対象の条件として一致しない場合
     */
    protected LocaleSymbol setupConditionLocaleSymbol(final ConditionFormatter formatter, final Token.Condition token) {
        
        final Matcher matcher = PATTERN_CONDITION_LOCALE_SYMBOL.matcher(token.getValue());
        if(!matcher.matches()) {
            throw new IllegalArgumentException("not match condition:" + token.getValue());
        }
        
        final String symbol = matcher.group(1);
        final String number = matcher.group(2);
        
        // 16進数=>10進数に直す
        final int value = Integer.valueOf(number, 16);
        MSLocale locale = MSLocale.createKnownLocale(value);
        if(locale == null) {
            locale = new MSLocale(value);
        }
        
        formatter.setLocale(locale);
        
        return new LocaleSymbol(locale, symbol);
        
    }
    
    /**
     * '[DBNum1]'などの組み込み処理の条件を組み立てる。
     * @param formatter 現在の組み立て中のフォーマッタのインスタンス。
     * @param token 条件式のトークン。
     * @return 組み込みの条件式。
     * @throws IllegalArgumentException 処理対象の条件として一致しない場合
     */
    protected Callback<?> setupConditionDbNum(final ConditionFormatter formatter, final Token.Condition token) {
        
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
     * '[Red]'などの色の条件の組み立てる。
     * @param formatter 現在の組み立て中のフォーマッタのインスタンス。
     * @param token 条件式のトークン。
     * @return 色の条件式。
     * @throws IllegalArgumentException 処理対象の条件として一致しない場合
     */
    protected MSColor setupConditionColor(final ConditionFormatter formatter, final Token.Condition token) {
        
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
        
//        final String prefix = matcher.group(1);
        final String number = matcher.group(2);
        final short index = Short.valueOf(number);
        color = MSColor.valueOfIndexColor(index);
        
        formatter.setColor(color);
        
        return color;
    }
    
}
