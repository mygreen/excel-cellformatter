package com.github.mygreen.cellformatter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.github.mygreen.cellformatter.lang.ExcelDateUtils;

/**
 * 数値型の値を直接扱うための仮想的なセル。
 * 
 * @since 0.6
 * @param <T> 
 * @author T.TSUCHIE
 *
 */
public class NumberCell<T extends Number> extends ObjectCell<T> {
    
    /** 日付の始まりが1904年開始かどうか */
    private final boolean dateStart1904;
    
    /**
     * コンストラクタで渡した値の数値表現。
     */
    private double number;
    
    /**
     * 値と書式のインデックス番号を指定するコンストラクタ。
     * <p>フォーマットの書式は、{@literal null}になります。
     * @param value フォーマット対象の値。
     * @param formatIndex 書式のインデックス番号。
     * @throws IllegalArgumentException {@literal value == null}.
     * @throws IllegalArgumentException {@literal formatIndex < 0}
     * 
     */
    public NumberCell(final T value, final short formatIndex) {
        this(value, formatIndex, false);
    }
    
    /**
     * 値とその書式を指定するコンストラクタ。
     * <p>フォーマットのインデックス番号は、{@literal 0}となります。
     * @param value フォーマット対象の値。
     * @param formatPattern Excelの書式。
     * @throws IllegalArgumentException {@literal value == null}
     * @throws IllegalArgumentException fommatPattern is empty.
     */
    public NumberCell(final T value, final String formatPattern) {
        this(value, formatPattern, false);
    }
    
    /**
     * 値と、書式のインデックス番号、書式を指定するコンストラクタ。
     * @param value フォーマット対象の値。
     * @param formatIndex フォーマットのインデックス番号。
     * @param formatPattern Excelの書式。
     * @throws IllegalArgumentException {@literal value == null}
     * @throws IllegalArgumentException {@literal formatIndex < 0}
     * @throws IllegalArgumentException fommatPattern is empty.
     * 
     */
    public NumberCell(final T value, final short formatIndex, final String formatPattern) {
        this(value, formatIndex, formatPattern, false);
    }
    
    /**
     * 値と書式のインデックス番号を指定するコンストラクタ。
     * <p>フォーマットの書式は、{@literal null}になります。
     * @param value フォーマット対象の値。
     * @param formatIndex 書式のインデックス番号。
     * @param dateStart1904 1904年始まりかどうか。
     * @throws IllegalArgumentException {@literal value == null}.
     * @throws IllegalArgumentException {@literal formatIndex < 0}
     * 
     */
    public NumberCell(final T value, final short formatIndex, final boolean dateStart1904) {
        super(value, formatIndex);
        this.number = toDouble(value);
        this.dateStart1904 = dateStart1904;
    }
    
    /**
     * 値とその書式を指定するコンストラクタ。
     * <p>フォーマットのインデックス番号は、{@literal 0}となります。
     * @param value フォーマット対象の値。
     * @param formatPattern Excelの書式。
     * @param dateStart1904 1904年始まりかどうか。
     * @param dateStart1904 1904年始まりかどうか。
     * @throws IllegalArgumentException {@literal value == null}
     * @throws IllegalArgumentException fommatPattern is empty.
     */
    public NumberCell(final T value, final String formatPattern, final boolean dateStart1904) {
        super(value, formatPattern);
        this.number = toDouble(value);
        this.dateStart1904 = dateStart1904;
    }
    
    /**
     * 値と、書式のインデックス番号、書式を指定するコンストラクタ。
     * @param value フォーマット対象の値。
     * @param formatIndex フォーマットのインデックス番号。
     * @param formatPattern Excelの書式。
     * @throws IllegalArgumentException {@literal value == null}
     * @throws IllegalArgumentException {@literal formatIndex < 0}
     * @throws IllegalArgumentException fommatPattern is empty.
     * 
     */
    public NumberCell(final T value, final short formatIndex, final String formatPattern, final boolean dateStart1904) {
        super(value, formatIndex, formatPattern);
        this.number = toDouble(value);
        this.dateStart1904 = dateStart1904;
    }
    
    private double toDouble(final T value) {
        
        final Class<?> clazz = value.getClass();
        if(clazz.isPrimitive()) {
            if(clazz.equals(Byte.TYPE)) {
                return new BigDecimal((byte) value).doubleValue();
            } else if(clazz.equals(Short.TYPE)) {
                return new BigDecimal((short) value).doubleValue();
            } else if(clazz.equals(Integer.TYPE)) {
                return new BigDecimal((int) value).doubleValue();
            } else if(clazz.equals(Long.TYPE)) {
                new BigDecimal((long) value).doubleValue();
            } else if(clazz.equals(Float.TYPE)) {
                new BigDecimal((float) value).doubleValue();
            } else if(clazz.equals(Double.TYPE)) {
                new BigDecimal((double) value).doubleValue();
            } 
        }
        
        if(value instanceof Byte) {
            return new BigDecimal((byte) value).doubleValue();
        } else if(value instanceof Short) {
            return new BigDecimal((short) value).doubleValue();
        } else if(value instanceof Integer) {
            return new BigDecimal((int) value).doubleValue();
        } else if(value instanceof Long) {
            return new BigDecimal((long) value).doubleValue();
        } else if(value instanceof Float) {
            return new BigDecimal((float) value).doubleValue();
        } else if(value instanceof Double) {
            return new BigDecimal((double) value).doubleValue();
        }
        
        if(value instanceof AtomicInteger) {
            return new BigDecimal(((AtomicInteger) value).get()).doubleValue();
        } else if(value instanceof AtomicLong) {
            return new BigDecimal(((AtomicLong) value).get()).doubleValue();
        }
        
        if(value instanceof BigDecimal) {
            return ((BigDecimal) value).doubleValue();
        } else if(value instanceof BigInteger) {
            return ((BigInteger) value).doubleValue();
        }
        
        throw new IllegalArgumentException("not support type class : " + clazz.getName());
    }
    
    /**
     * {@inheritDoc}
     * <p>常に{@literal true}を返します。
     */
    @Override
    public boolean isNumber() {
        return true;
    }
    
    @Override
    public double getNumberCellValue() {
        return number;
    }
    
    @Override
    public Date getDateCellValue() {
        return ExcelDateUtils.convertJavaDate(number, isDateStart1904());
    }
    
    @Override
    public boolean isDateStart1904() {
        return dateStart1904;
    }
}
