package com.github.mygreen.cellformatter.number;



/**
 * 書式を表現するための数値。
 *
 * @version 0.10
 * @author T.TSUCHIE
 *
 */
public abstract class FormattedNumber {

    /**
     * 元の数値。
     */
    protected final double value;

    /**
     * 桁の区切を利用するかどうか。
     */
    protected boolean useSeparator;

    public FormattedNumber(final double value) {
        this.value = value;
    }

    /**
     * 元の数値の値を取得する。
     * @return
     */
    public double getValue() {
        return value;
    }

    /**
     * 値がゼロかどうか。
     * @return
     */
    public boolean isZero() {
        return value == 0.0d;
    }

    /**
     * 正の数かどうか。
     * @return
     */
    public boolean isPositive() {
        return value > 0;
    }

    /**
     * 負の数かどうか。
     * @return
     */
    public boolean isNegative() {
        return value < 0;
    }

    public NativeNumber asNative() {
        return (NativeNumber) this;
    }

    public DecimalNumber asDecimal() {
        return (DecimalNumber) this;
    }

    public PercentNumber asPercent() {
        return (PercentNumber) this;
    }

    public ExponentNumber asExponent() {
        return (ExponentNumber) this;
    }

    public FractionNumber asFraction() {
        return (FractionNumber) this;
    }

    /**
     * 桁の区切を利用するかどうか。
     * @return trueのとき区切り文字がある。
     */
    public boolean isUseSeparator() {
        return useSeparator;
    }

    /**
     * 桁の区切を利用するかどうか設定する。
     * @param useSeparator trueのとき区切り文字がある。
     */
    public void setUseSeparator(boolean useSeparator) {
        this.useSeparator = useSeparator;
    }
}
