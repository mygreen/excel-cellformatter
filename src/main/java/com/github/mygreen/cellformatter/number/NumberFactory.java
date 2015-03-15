package com.github.mygreen.cellformatter.number;


/**
 * 数値オブジェクトのファクトリクラス。
 * @author T.TSUCHIE
 *
 */
public abstract class NumberFactory {
    
    /**
     * 小数のファクトリクラスの取得
     * @param scale
     * @return
     */
    public static DecimalNumberFactory decimalNumber(int scale) {
        return new DecimalNumberFactory(scale);
    }
    
    /**
     * 百分率のファクトリクラスの取得
     * @param scale
     * @return
     */
    public static PercentNumberFactory percentNumber(int scale) {
        return new PercentNumberFactory(scale);
    }
    
    /**
     * 指数のファクトリクラスの取得
     * @param scale
     * @return
     */
    public static ExponentNumberFactory exponentNumber(int scale) {
        return new ExponentNumberFactory(scale);
    }
    
    /**
     * 分数のファクトリクラスの取得
     * @param denominator
     * @param exactDenom
     * @param wholeType
     * @return
     */
    public static FractionNumberFactory fractionNumber(final int denominator, final boolean exactDenom, final boolean wholeType) {
        return new FractionNumberFactory(denominator, exactDenom, wholeType);
    }
    
    /**
     * 数値オブジェクトのインスタンスを取得する。
     * @param value
     * @return
     */
    public abstract FormattedNumber create(double value);
    
    public static class DecimalNumberFactory extends NumberFactory {
        
        private int scale;
        
        public DecimalNumberFactory(final int scale) {
            this.scale = scale;
        }
        
        @Override
        public FormattedNumber create(double value) {
            return new DecimalNumber(value, scale);
        }
        
    }
    
    public static class ExponentNumberFactory extends NumberFactory {
        
        private int scale;
        
        public ExponentNumberFactory(final int scale) {
            this.scale = scale;
        }
        
        @Override
        public FormattedNumber create(double value) {
            return new ExponentNumber(value, scale);
        }
        
    }
    
    public static class PercentNumberFactory extends NumberFactory {
        
        private int scale;
        
        public PercentNumberFactory(final int scale) {
            this.scale = scale;
        }
        
        @Override
        public FormattedNumber create(double value) {
            return new PercentNumber(value, scale);
        }
        
    }
    
    public static class FractionNumberFactory extends NumberFactory {
        
        /** 分母の値 */
        private int denominator;
        
        /** 分母を直接指定かどうか */
        private boolean exactDenom;
        
        /** 帯分数形式かどうか */
        private boolean wholeType;
        
        public FractionNumberFactory(final int denominator, final boolean exactDenom, final boolean wholeType) {
            this.denominator = denominator;
            this.exactDenom = exactDenom;
            this.wholeType = wholeType;
        }
        
        @Override
        public FormattedNumber create(double value) {
            
            FractionNumber number;
            if(exactDenom) {
                number = FractionNumber.createExactDenominator(value, denominator, wholeType);
            } else {
                number = FractionNumber.createMaxDenominator(value, denominator, wholeType);
            }
            
            return number;
        }
        
        
        
    }

}
