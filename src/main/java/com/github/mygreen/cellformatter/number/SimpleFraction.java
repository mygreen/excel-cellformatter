package com.github.mygreen.cellformatter.number;

/**
 * 簡易的な分数を表現するクラス。
 * <p>古いPOIだと提供されていないため、同じものを実装。
 *
 * @author T.TSUCHIE
 *
 */
public class SimpleFraction {
    
    /** 分母 */
    private final int denominator;
    
    /** 分子 */
    private final int numerator;
    
    /**
     * 分子と分母を指定してインスタンスを作成する。
     * @param numerator 分子。
     * @param denominator 分母。
     */
    public SimpleFraction(final int numerator, final int denominator) {
        if(denominator == 0) {
            throw new IllegalArgumentException("denominator should not be zero.");
        }
        
        this.numerator = numerator;
        this.denominator = denominator;
    }
    
    /**
     * 分母の値を指定してインスタンスを作成する。
     * 
     * @param val 分数として表現するもとの数値の値。
     * @param exactDenom 分母の値。
     * @return 
     */
    public static SimpleFraction createFractionExactDenominator(final double val, final int exactDenom){
        int num =  (int)Math.round(val*(double)exactDenom);
        return new SimpleFraction(num,exactDenom);
    }
    
    /**
     * 分母の最大値を指定してインスタンスを作成する。
     * <p>指定した分母の値以下に近似した分数を取得する。
     * 
     * @param value 分数として表現するもとの数値の値。
     * @param maxDenominator 分母の最大値。
     * 
     * @throws RuntimeException if the continued fraction failed to converge.
     * @throws IllegalArgumentException if value > Integer.MAX_VALUE
     */
    public static SimpleFraction createFractionMaxDenominator(final double value, final int maxDenominator){
        return buildFractionMaxDenominator(value, 0, maxDenominator, 100);
    }
    /**
     * Create a fraction given the double value and either the maximum error
     * allowed or the maximum number of denominator digits.
     * <p>
     * References:
     * <ul>
     * <li><a href="http://mathworld.wolfram.com/ContinuedFraction.html">
     * Continued Fraction</a> equations (11) and (22)-(26)</li>
     * </ul>
     * </p>
     *
     *  Based on org.apache.commons.math.fraction.Fraction from Apache Commons-Math.
     *  YK: The only reason of having this class is to avoid dependency on the Commons-Math jar.
     *
     * @param value the double value to convert to a fraction.
     * @param epsilon maximum error allowed.  The resulting fraction is within
     *        <code>epsilon</code> of <code>value</code>, in absolute terms.
     * @param maxDenominator maximum denominator value allowed.
     * @param maxIterations maximum number of convergents
     * @throws RuntimeException if the continued fraction failed to
     *         converge.
     * @throws IllegalArgumentException if value > Integer.MAX_VALUE
     */
    private static SimpleFraction buildFractionMaxDenominator(final double value, final double epsilon,
            final int maxDenominator, final int maxIterations) {
        
        final long overflow = Integer.MAX_VALUE;
        double r0 = value;
        long a0 = (long)Math.floor(r0);
        if (a0 > overflow) {
            throw new IllegalArgumentException("Overflow trying to convert "+value+" to fraction ("+a0+"/"+1l+")");
        }
        
        // check for (almost) integer arguments, which should not go
        // to iterations.
        if (Math.abs(a0 - value) < epsilon) {
            return new SimpleFraction((int)a0, 1);
        }

        long p0 = 1;
        long q0 = 0;
        long p1 = a0;
        long q1 = 1;

        long p2;
        long q2;

        int n = 0;
        boolean stop = false;
        do {
            ++n;
            double r1 = 1.0 / (r0 - a0);
            long a1 = (long)Math.floor(r1);
            p2 = (a1 * p1) + p0;
            q2 = (a1 * q1) + q0;
            //MATH-996/POI-55419
            if (epsilon == 0.0f && maxDenominator > 0 && Math.abs(q2) > maxDenominator &&
                    Math.abs(q1) < maxDenominator){

                return new SimpleFraction((int)p1, (int)q1);
            }
            if ((p2 > overflow) || (q2 > overflow)) {
                throw new RuntimeException("Overflow trying to convert "+value+" to fraction ("+p2+"/"+q2+")");
            }

            double convergent = (double)p2 / (double)q2;
            if (n < maxIterations && Math.abs(convergent - value) > epsilon && q2 < maxDenominator) {
                p0 = p1;
                p1 = p2;
                q0 = q1;
                q1 = q2;
                a0 = a1;
                r0 = r1;
            } else {
                stop = true;
            }
        } while (!stop);

        if (n >= maxIterations) {
            throw new RuntimeException("Unable to convert "+value+" to fraction after "+maxIterations+" iterations");
        }

        if (q2 < maxDenominator) {
            return new SimpleFraction((int) p2, (int)q2);
        } else {
            return new SimpleFraction((int)p1, (int)q1);
        }

    }
    
    /**
     * 分母の取得
     * 
     * @return the denominator.
     */
    public int getDenominator() {
        return denominator;
    }
    
    /**
     * 分子の取得
     * 
     * @return the numerator.
     */
    public int getNumerator() {
        return numerator;
    }
}
