package com.github.mygreen.cellformatter.number;

import java.math.RoundingMode;
import java.text.DecimalFormat;


/**
 * 指数表現の数値。
 * @since 0.4
 * @version 0.4
 * @author T.TSUCHIE
 *
 */
public class ExponentNumber extends DecimalNumber {
    
    /**
     * 指数部分。
     * ・0以下の場合は、空文字。
     * ・符号はつかない。
     */
    private String exponentPart;
    
    /**
     * 指数部分の符号。
     * ・0の時にはプラス。
     */
    private int exponentSign;
    
    /**
     * 
     * @param value
     * @param decimalScale
     */
    public ExponentNumber(final double value, final int decimalScale) {
        super(value, decimalScale);
    }
    
    @Override
    protected void init() {
        
        if(isZero()) {
            this.integerPart = "";
            this.decimalPart = "";
            this.exponentPart = "0";
            this.exponentSign = 1;
            return;
        }
        
        final StringBuilder sb = new StringBuilder();
        sb.append("#");
        
        // 指数の場合、小数部分の桁がなくても"."を追加する。
        sb.append(".");
        
        // 小数の精度分、書式を追加する。
        for(int i=0; i < getScale(); i++) {
            sb.append("#");
        }
        
        // 指数の精度分、書式を追加する。
        sb.append("E0");
        
        final DecimalFormat format = new DecimalFormat(sb.toString());
        format.setRoundingMode(RoundingMode.HALF_UP);
        String str = format.format(Math.abs(getValue()));
        
        // 指数部とその他に分割する
        final int exponentIdx = str.indexOf("E");
        final String numberPart = str.substring(0, exponentIdx);
        final String exponentPart = str.substring(exponentIdx+1);
        
        setupIntegerAndDecimalPart(numberPart);
        
        // 指数部の解析
        if(exponentPart.startsWith("-")) {
            // 負の符号から始まる場合
            this.exponentSign = -1;
            this.exponentPart = exponentPart.substring(1);
        } else {
            this.exponentSign = 1;
            this.exponentPart = exponentPart;
        }
        
    }
    
    /**
     * 指数の符号が正かどうか。
     * ・指数が0の場合も正と判断する。
     * @return
     */
    public boolean isExponentPositive() {
        return exponentSign >= 0;
    }
    
    /**
     * 指数の符号が負かどうか。
     * ・指数が0の場合も正と判断する。
     * @return
     */
    public boolean isExponentNegative() {
        return exponentSign < 0;
    }
    
    /**
     * 指数部のフォーマットした値を取得する。
     * @return
     */
    public String getExponentPart() {
        return exponentPart;
    }
    
    /**
     * 指数部の指定した桁の値を取得する。
     * @param digit 1から始まる。
     * @return その桁の値がない場合は空文字を返す。
     */
    public String getExponentPart(final int digit) {
        
        final int length = exponentPart.length();
        if(length < digit || digit <= 0) {
            return "";
        }
        
        return String.valueOf(exponentPart.charAt(length - digit));
        
    }
    
    /**
     * 指数部の指定した桁以降の値を取得する。
     * @param digit 1から始まる。
     * @return その桁の値がない場合は空文字を返す。
     */
    public String getExponentPartAfter(final int digit) {
        
        final int length = exponentPart.length();
        if(length < digit || digit <= 0) {
            return "";
        }
        
        return exponentPart.substring(0, length - digit + 1);
        
    }
    
    @Override
    public String toString() {
        
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        
        sb.append("E");
        if(isExponentPositive()) {
            sb.append("+");
        } else {
            sb.append("-");
        }
        
        sb.append(getExponentPart());
        
        return sb.toString();
    }
    
}
