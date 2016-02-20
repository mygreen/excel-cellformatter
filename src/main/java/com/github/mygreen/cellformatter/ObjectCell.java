package com.github.mygreen.cellformatter;

import java.util.Date;

import com.github.mygreen.cellformatter.lang.ArgUtils;


/**
 * Javaのオブジェクトを直接フォーマットするための抽象クラス。
 *
 * @since 0.6
 * @author T.TSUCHIE
 *
 */
public abstract class ObjectCell<T> implements CommonCell {
    
    /**
     * 値
     */
    protected final T value;
    
    /**
     * フォーマットのインデックス
     */
    private final short formatIndex;
    
    /**
     * フォーマットの書式
     */
    protected final String formatPattern;
    
    /**
     * 値と書式のインデックス番号を指定するコンストラクタ。
     * <p>フォーマットの書式は、{@literal null}になります。
     * @param value フォーマット対象の値。
     * @param formatIndex 書式のインデックス番号。
     * @throws IllegalArgumentException {@literal value == null}
     * @throws IllegalArgumentException {@literal formatIndex < 0}
     * 
     */
    public ObjectCell(final T value, final short formatIndex) {
        ArgUtils.notNull(value, "value");
        ArgUtils.notMin(formatIndex, (short)0, "formatIndex");
        
        this.value = value;
        this.formatIndex = formatIndex;
        this.formatPattern = null;
    }
    
    /**
     * 値とその書式を指定するコンストラクタ。
     * <p>フォーマットのインデックス番号は、{@literal 0}となります。
     * @param value フォーマット対象の値。
     * @param formatPattern Excelの書式。
     * @throws IllegalArgumentException {@literal value == null}
     * @throws IllegalArgumentException {@literal formatPattern == null || formatPatter.length() == 0}.
     */
    public ObjectCell(final T value, final String formatPattern) {
        ArgUtils.notNull(value, "value");
        ArgUtils.notEmpty(formatPattern, "formatPattern");
        this.value = value;
        this.formatIndex = (short)0;
        this.formatPattern = formatPattern;
    }
    
    /**
     * 値と、書式のインデックス番号、書式を指定するコンストラクタ。
     * @param value フォーマット対象の値。
     * @param formatIndex フォーマットのインデックス番号。
     * @param formatPattern Excelの書式。
     * @throws IllegalArgumentException {@literal value == null}
     * @throws IllegalArgumentException {@literal formatIndex < 0}
     * @throws IllegalArgumentException {@literal formatPattern == null || formatPatter.length() == 0}.
     * 
     */
    public ObjectCell(final T value, final short formatIndex, final String formatPattern) {
        ArgUtils.notNull(value, "value");
        ArgUtils.notMin(formatIndex, (short)0, "formatIndex");
        ArgUtils.notEmpty(formatPattern, "formatPattern");
        
        this.value = value;
        this.formatIndex = formatIndex;
        this.formatPattern = formatPattern;
    }
    
    /**
     * 設定した値を取得します。
     * @return コンストラクタで渡された値。
     */
    public T getValue() {
        return value;
    }
    
    @Override
    public short getFormatIndex() {
        return formatIndex;
    }
    
    /**
     * {@inheritDoc}
     * <p>コンストラクタで指定したフォーマットを返します。
     */
    @Override
    public String getFormatPattern() {
        return formatPattern;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isText() {
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBoolean() {
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isNumber() {
        return false;
    }
    
    /**
     * 設定した値の型と一致しない場合、このメソッドを呼ぶと{@link IllegalStateException}をスローします。
     */
    @Override
    public String getTextCellValue() {
        throw new IllegalStateException("not match value type.");
    }
    
    /**
     * 設定した値の型と一致しない場合、このメソッドを呼ぶと{@link IllegalStateException}をスローします。
     */
    @Override
    public boolean getBooleanCellValue() {
        throw new IllegalStateException("not match value type.");
    }
    
    /**
     * 設定した値の型と一致しない場合、このメソッドを呼ぶと{@link IllegalStateException}をスローします。
     */
    @Override
    public double getNumberCellValue() {
        throw new IllegalStateException("not match value type.");
    }
    
    /**
     * 設定した値の型と一致しない場合、このメソッドを呼ぶと{@link IllegalStateException}をスローします。
     */
    @Override
    public Date getDateCellValue() {
        throw new IllegalStateException("not match value type.");
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDateStart1904() {
        return false;
    }
    
    /**
     * {@inheritDoc}
     * <p>常に、'A1'を返します。
     */
    @Override
    public String getCellAddress() {
        return "A1";
    }
}
