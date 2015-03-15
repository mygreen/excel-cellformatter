package com.github.mygreen.cellformatter.number;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;


/**
 * {@link FormattedNumber}の具象クラスのテスタ。
 * 
 */
public class FormattedNumberTest {
    
    /**
     * {@link DecimalNumber}のテスタ
     */
    @Test
    public void testDecimalNumber() {
        
        DecimalNumber num0 = new DecimalNumber(0.0, 0);
        assertEquals("", num0.getIntegerPart());
        assertEquals("", num0.getDecimalPart());
        
        DecimalNumber num1 = new DecimalNumber(10.0, 0);
        assertEquals("10", num1.getIntegerPart());
        assertEquals("", num1.getDecimalPart());
        
        DecimalNumber num2 = new DecimalNumber(10.0, 2);
        assertEquals("10", num2.getIntegerPart());
        assertEquals("", num2.getDecimalPart());
        
        DecimalNumber num3 = new DecimalNumber(10.123, 2);
        assertEquals("10", num3.getIntegerPart());
        assertEquals("12", num3.getDecimalPart());
        
        // 丸めの確認1
        DecimalNumber num4 = new DecimalNumber(10.124, 2);
        assertEquals("10", num4.getIntegerPart());
        assertEquals("12", num4.getDecimalPart());
        
        // 丸めの確認2
        DecimalNumber num5 = new DecimalNumber(10.125, 2);
        assertEquals("10", num5.getIntegerPart());
        assertEquals("13", num5.getDecimalPart());
    }
    
    /**
     * {@link PercentNumber}のテスタ
     */
    @Test
    public void testPercentNumber() {
        
        PercentNumber num0 = new PercentNumber(0.0, 0);
        assertEquals("", num0.getIntegerPart());
        assertEquals("", num0.getDecimalPart());
        
        PercentNumber num1 = new PercentNumber(0.10, 0);
        assertEquals("10", num1.getIntegerPart());
        assertEquals("", num1.getDecimalPart());
        
        PercentNumber num2 = new PercentNumber(0.10, 2);
        assertEquals("10", num2.getIntegerPart());
        assertEquals("", num2.getDecimalPart());
        
        PercentNumber num3 = new PercentNumber(0.10123, 2);
        assertEquals("10", num3.getIntegerPart());
        assertEquals("12", num3.getDecimalPart());
        
        // 丸めの確認1
        PercentNumber num4 = new PercentNumber(0.10124, 2);
        assertEquals("10", num4.getIntegerPart());
        assertEquals("12", num4.getDecimalPart());
        
        // 丸めの確認2
        PercentNumber num5 = new PercentNumber(0.10125, 2);
        assertEquals("10", num5.getIntegerPart());
        assertEquals("13", num5.getDecimalPart());
        
        PercentNumber num6 = new PercentNumber(789.10125, 2);
        assertEquals("78910", num6.getIntegerPart());
        assertEquals("13", num6.getDecimalPart());
    }
    
    @Test
    public void testExponentNumbr() {
        
        ExponentNumber num0 = new ExponentNumber(0, 1);
        assertEquals("", num0.getIntegerPart());
        assertEquals("", num0.getDecimalPart());
        assertEquals("0", num0.getExponentPart());
        assertEquals(true, num0.isExponentPositive());
        
        ExponentNumber num1 = new ExponentNumber(0.1234, 2);
        assertEquals("1", num1.getIntegerPart());
        assertEquals("23", num1.getDecimalPart());
        assertEquals("1", num1.getExponentPart());
        assertEquals(false, num1.isExponentPositive());
        
        ExponentNumber num2 = new ExponentNumber(12345.6789, 4);
        assertEquals("1", num2.getIntegerPart());
        assertEquals("2346", num2.getDecimalPart());
        assertEquals("4", num2.getExponentPart());
        assertEquals(true, num2.isExponentPositive());
    }
    
    @Test
    public void testFractionNumber() {
        
        FractionNumber num0 = FractionNumber.createExactDenominator(0, 10, true);
        assertEquals("", num0.getWholeNumberPart());
        assertEquals("0", num0.getNumeratorPart());
        assertEquals("10", num0.getDenominatorPart());
        
        FractionNumber num1 = FractionNumber.createExactDenominator(0.25, 2, true);
        assertEquals("", num1.getWholeNumberPart());
        assertEquals("1", num1.getNumeratorPart());
        assertEquals("2", num1.getDenominatorPart());
        
        FractionNumber num2 = FractionNumber.createExactDenominator(3.25, 2, true);
        assertEquals("3", num2.getWholeNumberPart());
        assertEquals("1", num2.getNumeratorPart());
        assertEquals("2", num2.getDenominatorPart());
        
        FractionNumber num3 = FractionNumber.createExactDenominator(3.25, 2, false);
        assertEquals("", num3.getWholeNumberPart());
        assertEquals("7", num3.getNumeratorPart());
        assertEquals("2", num3.getDenominatorPart());
        
    }
    
}
