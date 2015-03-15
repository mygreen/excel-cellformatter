package com.github.mygreen.cellformatter.number;


/**
 * 分数を表現するクラス。
 * <p>POIのSimpleFraction、Commons-MathのFractionを参照。
 * @version 0.4
 * @since 0.4
 * @author T.TSUCHIE
 *
 */
public class FractionNumber extends FormattedNumber {
    
    /**
     * 分母の部分
     */
    private String denominatorPart;
    
    /**
     * 分子の部分
     */
    private String numeratorPart;
    
    /**
     * 帯分数の整数部分
     */
    private String wholeNumberPart;
    
    /**
     * 帯分数の形式かどうか。
     */
    private boolean wholeType;
    
    private FractionNumber(final double value) {
        super(value);
        
    }
    
    /**
     * 分母の値を指定した分数を作成する。
     * @param value
     * @param exactDenom 分母の値
     * @return
     */
    public static FractionNumber createExactDenominator(final double value, int exactDenom) {
        return createExactDenominator(value, exactDenom, false);
    }
    
    /**
     * 分母の値を指定した分数を作成する。
     * @param value
     * @param exactDenom 分母の値
     * @param wholeType
     * @return
     */
    public static FractionNumber createExactDenominator(final double value, int exactDenom, boolean wholeType) {
        final FractionNumber fractionNumber = new FractionNumber(value);
        fractionNumber.wholeType = wholeType;
        
        final SimpleFraction fraction = SimpleFraction.createFractionExactDenominator(Math.abs(value), exactDenom);
        setupFractionPart(fraction, fractionNumber);
        
        return fractionNumber;
    }
    
    /**
     * 分母の最大値を指定した分数を作成する。
     * @param value
     * @param maxDenom
     * @return
     */
    public static FractionNumber createMaxDenominator(final double value, int maxDenom) {
        return createMaxDenominator(value, maxDenom, false);
    }
    
    /**
     * 分母の最大値を指定した分数を作成する。
     * @param value
     * @param maxDenom
     * @param wholeType
     * @return
     */
    public static FractionNumber createMaxDenominator(final double value, int maxDenom, boolean wholeType) {
        final FractionNumber fractionNumber = new FractionNumber(value);
        fractionNumber.wholeType = wholeType;
        
        final SimpleFraction fraction = SimpleFraction.createFractionMaxDenominator(Math.abs(value), maxDenom);
        setupFractionPart(fraction, fractionNumber);
        
        return fractionNumber;
    }
    
    /**
     * 分数の各部品を設定する
     * @param fraction
     * @param fractionNumber
     */
    private static void setupFractionPart(final SimpleFraction fraction, final FractionNumber fractionNumber) {
        
        fractionNumber.denominatorPart = String.valueOf(fraction.getDenominator());
        
        if(fractionNumber.isWholeType()) {
            int wholeNumber = fraction.getNumerator() / fraction.getDenominator();
            fractionNumber.wholeNumberPart = wholeNumber == 0 ? "" : String.valueOf(wholeNumber);
            
            int numerator = fraction.getNumerator() % fraction.getDenominator();
            fractionNumber.numeratorPart = String.valueOf(numerator);
            
        } else {
            fractionNumber.wholeNumberPart = "";
            fractionNumber.numeratorPart = String.valueOf(fraction.getNumerator());
            
        }
    }
    
    /**
     * 帯分数の形式かどうか。
     * @return
     */
    public boolean isWholeType() {
        return wholeType;
    }
    
    /**
     * 分母の部分を取得する。
     * @return
     */
    public String getDenominatorPart() {
        return denominatorPart;
    }
    
    /**
     * 分母の指定した桁の値を取得する。
     * @param digit 1から始まる
     * @return 存在しない桁の場合は空文字を返す。
     */
    public String getDenominatorPart(final int digit) {
        
        final int length = denominatorPart.length();
        if(length < digit || digit <= 0) {
            return "";
        }
        
        return String.valueOf(denominatorPart.charAt(length - digit));
    }
    
    /**
     * 分母の指定した桁以降の値を取得する。
     * @param digit 1から始まる
     * @return 存在しない桁の場合は空文字を返す。
     */
    public String getDenominatorPartAfter(final int digit) {
        
        final int length = denominatorPart.length();
        if(length < digit || digit <= 0) {
            return "";
        }
        
        return denominatorPart.substring(0, (length - digit + 1));
    }
    
    /**
     * 分子の部分を取得する
     * @return
     */
    public String getNumeratorPart() {
        return numeratorPart;
    }
    
    /**
     * 分子の指定した桁の値を取得する。
     * @param digit 1から始まる
     * @return 存在しない桁の場合は空文字を返す。
     */
    public String getNumeratorPart(final int digit) {
        
        final int length = numeratorPart.length();
        if(length < digit || digit <= 0) {
            return "";
        }
        
        return String.valueOf(numeratorPart.charAt(length - digit));
    }
    
    /**
     * 分子の指定した桁以降の値を取得する。
     * @param digit 1から始まる
     * @return 存在しない桁の場合は空文字を返す。
     */
    public String getNumeratorPartAfter(final int digit) {
        
        final int length = numeratorPart.length();
        if(length < digit || digit <= 0) {
            return "";
        }
        
        return numeratorPart.substring(0, (length - digit + 1));
    }
    
    /**
     * 帯分数の整数部分を取得する。
     * <p>整数部分が0または帯分数出ない場合は、空文字を返す。
     * @return
     */
    public String getWholeNumberPart() {
        return wholeNumberPart;
    }
    
    /**
     * 帯分数の整数部分の指定した桁の値を取得する。
     * @param digit 1から始まる
     * @return 存在しない桁の場合は空文字を返す。
     */
    public String getWholeNumberPart(final int digit) {
        
        final int length = wholeNumberPart.length();
        if(length < digit || digit <= 0) {
            return "";
        }
        
        return String.valueOf(wholeNumberPart.charAt(length - digit));
    }
    
    /**
     * 帯分数の整数部分の指定した桁以降の値を取得する。
     * @param digit 1から始まる
     * @return 存在しない桁の場合は空文字を返す。
     */
    public String getWholeNumberPartAfter(final int digit) {
        
        final int length = wholeNumberPart.length();
        if(length < digit || digit <= 0) {
            return "";
        }
        
        return wholeNumberPart.substring(0, (length - digit + 1));
    }
    
    @Override
    public String toString() {
        
        if(isZero()) {
            return "0";
        }
        
        StringBuilder sb = new StringBuilder();
        
        if(isNegative()) {
            sb.append("-");
        }
        
        if(isWholeType() && !getWholeNumberPart().isEmpty()) {
            sb.append(getWholeNumberPart()).append(" ");
        }
        
        sb.append(getNumeratorPart()).append("/").append(getDenominatorPart());
        
        return sb.toString();
        
    }
    
}
