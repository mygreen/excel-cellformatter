package com.github.mygreen.cellformatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

import com.github.mygreen.cellformatter.callback.Callback;
import com.github.mygreen.cellformatter.lang.MSColor;
import com.github.mygreen.cellformatter.lang.MSLocale;


/**
 * Excelのユーザ定義の各条件式を表現するための抽象クラス。
 * <p>区切り文字';'で区切られたもの。
 * 
 * @version 0.3
 * @param <T> 扱う処理のタイプ
 * @author T.TSUCHIE
 *
 */
public abstract class ConditionFormatter {
    
    /** 元の書式のパターン */
    protected final String pattern;
    
    /** 一致する条件   */
    protected ConditionOperator operator;
    
    /** ロケール  */
    protected MSLocale locale;
    
    /** 条件の色 */
    protected MSColor color;
    
    /** 全ての条件式 */
    protected List<String> conditions = new CopyOnWriteArrayList<>();
    
    /** 処理後のコールバック */
    protected List<Callback<?>> callbacks = new CopyOnWriteArrayList<>();
    
    public ConditionFormatter(final String pattern) {
        this.pattern = pattern;
    }
    
    /**
     * フォーマッタの種類を取得する。
     * @return
     */
    public abstract FormatterType getType();
    
    /**
     * 値が条件に一致するかどうか。
     * <p>{@link ConditionOperator}に一致するかどうか。
     * @param cell 判定対象のセル。
     * @return true:一致する。
     */
    public abstract boolean isMatch(CommonCell cell);
    
    /**
     * 値をフォーマットする。
     * @param cell フォーマット対象のセル。
     * @return true:一致する。
     */
    public CellFormatResult format(CommonCell cell) {
        return format(cell, Locale.getDefault());
    }
    
    /**
     * ロケールを指定して値フォーマットする。
     * @param cell フォーマット対象のセル。
     * @param runtimeLocale 実行時のロケール。nullの場合は、実行環境のロケールが指定される。
     * @return フォーマット結果。
     */
    public abstract CellFormatResult format(CommonCell cell, Locale runtimeLocale);
    
    /**
     * 種類が'日時'のフォーマッタかどうか。
     * @return
     */
    public boolean isDateFormatter() {
        return getType() == FormatterType.Date;
    }
    
    /**
     * 種類が'数値'のフォーマッタかどうか。
     * @return
     */
    public boolean isNumberFormatter() {
        return getType() == FormatterType.Number;
    }
    
    /**
     * 種類が'テキスト'のフォーマッタかどうか。
     * @return
     */
    public boolean isTextFormatter() {
        return getType() == FormatterType.Text;
    }
    
    /**
     * 書式のパターンを取得する。
     * @return
     */
    public String getPattern() {
        return pattern;
    }
    
    /**
     * 条件式を追加する。
     * @param condition
     */
    public void addCondition(final String condition) {
        this.conditions.add(condition);
    }
    
    /**
     * 複数の条件式を追加する。
     * @param conditions
     */
    public void addAllCondition(final List<String> conditions) {
        this.conditions.addAll(conditions);
    }
    
    /**
     * 全ての条件式を取得する
     * @return
     */
    public List<String> getConditions() {
        return conditions;
    }
    
    /**
     * 色を取得する。
     * @return 値を持たない場合は、nullを返す。
     */
    public MSColor getColor() {
        return color;
    }
    
    /**
     * 色を設定する。
     */
    public void setColor(MSColor color) {
        this.color = color;
    }
    
    /**
     * 一致条件を取得する。
     * @return
     */
    public ConditionOperator getOperator() {
        return operator;
    }
    
    /**
     * 一致条件を設定する。
     * @return
     */
    public void setOperator(ConditionOperator operator) {
        this.operator = operator;
    }
    
    /**
     * ロケールを取得する。
     * @return 値を持たない場合は、nullを返す。
     */
    public MSLocale getLocale() {
        return locale;
    }
    
    /**
     * ロケールを設定する。
     * @param locale
     */
    public void setLocale(MSLocale locale) {
        this.locale = locale;
    }
    
    /**
     * コールバック処理を追加する。
     * @param callback
     */
    public void addCallback(final Callback<?> callback) {
        this.callbacks.add(callback);
    }
    
    /**
     * コールバック処理を先頭に追加する。（優先度を高くする。）
     */
    public void addFirstCallcack(final Callback<?> callback) {
        // 一旦他のリストにコピーする。
        List<Callback<?>> list = new ArrayList<Callback<?>>();
        list.add(callback);
        list.addAll(callbacks);
        
        // 削除して、追加し直す。
        callbacks.clear();
        callbacks.addAll(list);
    }
    
    /**
     * コールバック処理を取得する
     * @param callback
     */
    public List<Callback<?>> getCallbacks() {
        return callbacks;
    }
}
