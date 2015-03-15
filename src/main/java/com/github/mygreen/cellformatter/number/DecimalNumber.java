package com.github.mygreen.cellformatter.number;

import java.math.RoundingMode;
import java.text.DecimalFormat;


/**
 * 小数表現を含む数値を表現するクラス。
 * <p>{@link ConditionNumberFormatter}でフォーマットする際に利用する。
 * 
 * @version 0.4
 * @since 0.4
 * @author T.TSUCHIE
 *
 */
public class DecimalNumber extends FormattedNumber {
    
    /**
     * 精度（小数の精度）
     * ・小数を持たない場合は、ゼロを設定する。
     */
    protected final int scale;
    
    /**
     * 整数部分。
     * ・0以下の場合は、空文字。
     * ・符号はつかない。
     */
    protected String integerPart;
    
    /**
     * 小数部分。
     * ・精度が0以上場合は、空文字。
     */
    protected String decimalPart;
    
    public DecimalNumber(final double value, final int scale) {
        super(value);
        this.scale = scale > 0 ? scale : 0;
        
        init();
    }
    
    /**
     * 値を精度に従い整数部と小数部に分解する。
     */
    protected void init() {
        
        if(isZero()) {
            this.integerPart = "";
            this.decimalPart = "";
            return;
        }
        
        final StringBuilder sb = new StringBuilder();
        sb.append("#");
        if(scale > 0) {
            sb.append(".");
        }
        
        // 小数の精度分、書式を追加する。
        for(int i=0; i < scale; i++) {
            sb.append("#");
        }
        
        final DecimalFormat format = new DecimalFormat(sb.toString());
        format.setRoundingMode(RoundingMode.HALF_UP);
        String str = format.format(Math.abs(getValue()));
        
        // 数値を小数部と整数部に分割する。
        setupIntegerAndDecimalPart(str);
        
    }
    
    /**
     * 文字列形式の数値を整数部と小数部に分割し、各フィールドに設定する
     * @param str
     */
    protected void setupIntegerAndDecimalPart(final String str) {
        
        final int dotIdx = str.indexOf(".");
        if(dotIdx < 0) {
            // 整数部のみの場合
            this.integerPart = str;
            this.decimalPart = "";
        } else {
            this.integerPart = str.substring(0, dotIdx);
            if(this.integerPart.equals("0")) {
                this.integerPart = "";
            }
            
            this.decimalPart = str.substring(dotIdx+1);
            
        }
    }
    
    /**
     * 小数の精度を取得する。
     * ・小数を持たない場合は、0を返す。
     * @return
     */
    public int getScale() {
        return scale;
    }
    
    /**
     * 整数部のフォーマットした値を取得する。
     * @return
     */
    public String getIntegerPart() {
        return integerPart;
    }
    
    /**
     * 整数部の指定した桁の値を取得する。
     * @param digit 1から始まる。
     * @return その桁の値がない場合は空文字を返す。
     */
    public String getIntegerPart(final int digit) {
        
        final int length = integerPart.length();
        if(length < digit || digit <= 0) {
            return "";
        }
        
        return String.valueOf(integerPart.charAt(length - digit));
        
    }
    
    /**
     * 整数部の指定した桁以降の値を取得する。
     * @param digit 1から始まる。
     * @return その桁の値がない場合は空文字を返す。
     */
    public String getIntegerPartAfter(final int digit) {
        
        final int length = integerPart.length();
        if(length < digit || digit <= 0) {
            return "";
        }
        
        return integerPart.substring(0, (length - digit + 1));
        
    }
    
    /**
     * 小数部のフォーマットした値を取得する。
     * @return
     */
    public String getDecimalPart() {
        return decimalPart;
    }
    
    /**
     * 小数部の指定した桁の値を取得する。
     * @param digit 1から始まる。
     * @return その桁の値がない場合は空文字を返す。
     */
    public String getDecimalPart(final int digit) {
        
        final int length = decimalPart.length();
        if(length < digit || digit <= 0) {
            return "";
        }
        
        return String.valueOf(decimalPart.charAt(digit-1));
        
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        if(isNegative()) {
            sb.append("-");
        }
        
        if(getIntegerPart().isEmpty()) {
            sb.append("0");
        }
        
        if(!getDecimalPart().isEmpty()) {
            sb.append(".").append(getDecimalPart());
        }
        
        return sb.toString();
    }
    
}
