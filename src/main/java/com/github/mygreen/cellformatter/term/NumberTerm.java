package com.github.mygreen.cellformatter.term;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import com.github.mygreen.cellformatter.number.FormattedNumber;
import com.github.mygreen.cellformatter.number.PartType;
import com.github.mygreen.cellformatter.tokenizer.Token;


/**
 * 数値の書式の項
 * @author T.TSUCHIE
 *
 */
public abstract class NumberTerm implements Term<FormattedNumber> {
    
    public static GeneralTerm general() {
        return new GeneralTerm();
    }
    
    public static ZeroTerm zero() {
        return new ZeroTerm();
    }
    
    public static SharpTerm sharp() {
        return new SharpTerm();
    }
    
    public static QuestionTerm question() {
        return new QuestionTerm();
    }
    
    public static ExponentTerm exponnet(final Token token) {
        return new ExponentTerm(token);
    }
    
    public static SymbolTerm symbol(final Token.Symbol token) {
        return new SymbolTerm(token);
    }
    
    public static DigitsTerm digits(final Token.Digits token) {
        return new DigitsTerm(token);
    }
    
    /**
     * フォーマットの書式"General"を表現する項
     *
     */
    public static class GeneralTerm extends NumberTerm {
        
        private final ThreadLocal<DecimalFormat> formatter = new ThreadLocal<DecimalFormat>() {
            
            @Override
            protected DecimalFormat initialValue() {
                final DecimalFormat format = new DecimalFormat("0.##########");
                format.setRoundingMode(RoundingMode.HALF_UP);
                return format;
            }
            
        };
        
        @Override
        public String format(final FormattedNumber number) {
            return formatter.get().format(Math.abs(number.getValue()));
        }
        
    }
    
    /**
     * 数値のフォーマット部分の項を表す抽象クラス。
     *
     */
    public static abstract class FormattedTerm extends NumberTerm {
        
        /** 桁のインデックス */
        protected int index;
        
        /** 書式の部分 */
        protected PartType partType;
        
        /** 書式の部分の最後かどうか */
        protected boolean lastPart;
        
        //TODO: 数値の区切り",,"の場合、自動的に区切るようにする。
        
        public FormattedTerm index(final int index) {
            this.index = index;
            return this;
        }
        
        public FormattedTerm partType(final PartType partType) {
            this.partType = partType;
            return this;
        }
        
        public FormattedTerm lastPart(final boolean lastPart) {
            this.lastPart = lastPart;
            return this;
        }
        
        /**
         * 数値の部分に対する桁の値を取得する。
         * @param number
         * @return
         */
        protected String getNumber(final FormattedNumber number) {
            
            switch(partType) {
                case Integer:
                    if(isLastPart()) {
                        return number.asDecimal().getIntegerPartAfter(getIndex());
                    } else {
                        return number.asDecimal().getIntegerPart(getIndex());
                    }
                    
                case Decimal:
                    return number.asDecimal().getDecimalPart(getIndex());
                    
                case Exponent:
                    if(isLastPart()) {
                        return number.asExponent().getExponentPartAfter(getIndex());
                    } else {
                        return number.asExponent().getExponentPart(getIndex());
                    }
                case Denominator:
                    if(isLastPart()) {
                        return number.asFraction().getDenominatorPartAfter(getIndex());
                    } else {
                        return number.asFraction().getDenominatorPart(getIndex());
                    }
                    
                case Numerator:
                    
                    if(isLastPart()) {
                        return number.asFraction().getNumeratorPartAfter(getIndex());
                    } else {
                        return number.asFraction().getNumeratorPart(getIndex());
                    }
                    
                case WholeNumber:
                    if(isLastPart()) {
                        return number.asFraction().getWholeNumberPartAfter(getIndex());
                    } else {
                        return number.asFraction().getWholeNumberPart(getIndex());
                    }
                    
                default:
                    return "";
            }
            
            
        }
        
        public int getIndex() {
            return index;
        }
        
        public void setIndex(int index) {
            this.index = index;
        }
        
        public PartType getPartType() {
            return partType;
        }
        
        public void setPart(PartType partType) {
            this.partType = partType;
        }
        
        public boolean isLastPart() {
            return lastPart;
        }
        
        public void setLastPart(boolean lastPart) {
            this.lastPart = lastPart;
        }
        
    }
    
    /**
     * フォーマットの書式"0"の記号。
     * ・出力する項がない場合は、0を出力する。
     */
    public static class ZeroTerm extends FormattedTerm {
        
        private static final String ZEOR = "0";
        
        @Override
        public String format(final FormattedNumber number) {
            
            String num = getNumber(number);
            if(num.isEmpty()) {
                return ZEOR;
            }
            
            return num;
        }
        
    }
    
    /**
     * フォーマットの書式"#"の記号。
     * ・出力する項がない場合は、何も出力しない。
     *
     */
    public static class SharpTerm extends FormattedTerm {
        
        @Override
        public String format(final FormattedNumber number) {
            String num = getNumber(number);
            return num;
        }
        
    }
    
    /**
     * フォーマットの書式"?"の記号。
     * ・出力する項がない場合は、半角スペースを出力する。
     *
     */
    public static class QuestionTerm extends FormattedTerm {
        
        private static final String SPACE = " ";
        
        @Override
        public String format(final FormattedNumber number) {
            String num = getNumber(number);
            if(num.isEmpty()) {
                return SPACE;
            }
            
            return num;
        }
        
    }
    
    /**
     * フォーマットの書式の指数"E"を表現する項。
     * ・指数部の符号も出力する。
     *
     */
    public static class ExponentTerm extends NumberTerm {
        
        /**
         * 符号
         * ・ただし、符号がない場合がある。
         * ・出力するときには、設定された符号は無視する。
         */
        private final Token token;
        
        public ExponentTerm(final Token token) {
            this.token = token;
        }
        
        @Override
        public String format(final FormattedNumber number) {
            
            if(number.asExponent().isExponentPositive()) {
                return "E+";
            } else {
                return "E-";
            }
        }
        
        public Token getToken() {
            return token;
        }
        
    }
    
    /**
     * 記号の処理
     *
     * @author T.TSUCHIE
     *
     */
    public static class SymbolTerm extends NumberTerm {
        
        private final Token.Symbol token;
        
        public SymbolTerm(final Token.Symbol token) {
            this.token = token;
        }
        
        @Override
        public String format(final FormattedNumber value) {
            return token.getValue();
        }
        
        public Token.Symbol getToken() {
            return token;
        }
        
    }
    
    public static class DigitsTerm extends NumberTerm {
        
        private final Token.Digits token;
        
        public DigitsTerm(Token.Digits token) {
            this.token = token;
        }
        
        @Override
        public String format(final FormattedNumber value) {
            return token.getValue();
        }
        
        public Token.Digits getToken() {
            return token;
        }
        
    }
    
}
