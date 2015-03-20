package com.github.mygreen.cellformatter.number;

import java.math.RoundingMode;
import java.text.DecimalFormat;


/**
 * 百分率の数値を表現するクラス。
 * 
 * @version 0.4
 * @since 0.4
 * @author T.TSUCHIE
 *
 */
public class PercentNumber extends DecimalNumber {
    
    public PercentNumber(final double value, final int decimalScale, final int permilles) {
        super(value, decimalScale, permilles);
    }
    
    public PercentNumber(final double value, final int decimalScale) {
        this(value, decimalScale, 0);
    }
    
    @Override
    protected void init() {
        
        if(isZero()) {
            this.integerPart = "";
            this.decimalPart = "";
            return;
        }
        
        final StringBuilder sb = new StringBuilder();
        sb.append("#");
        if(getScale() > 0) {
            sb.append(".");
        }
        
        // 小数の精度分、書式を追加する。
        for(int i=0; i < getScale(); i++) {
            sb.append("#");
        }
        
        // パーセントの追加
        sb.append("%");
        
        final DecimalFormat format = new DecimalFormat(sb.toString());
        format.setRoundingMode(RoundingMode.HALF_UP);
        
        double num = Math.abs(getValue());
        if(getPermilles() > 0) {
            num /= Math.pow(1000, getPermilles());
        }
        
        String str = format.format(num);
        
        // パーセントの除去
        str = str.substring(0, str.length()-1);
        
        // 数値を小数部と整数部に分割する。
        setupIntegerAndDecimalPart(str);
        
    }
    
    @Override
    public String toString() {
        return super.toString() + "%";
    }
    
    
}
