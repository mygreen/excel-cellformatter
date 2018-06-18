package com.github.mygreen.cellformatter.number;


/**
 * 数値オブジェクトのファクトリクラス。
 *
 * @version 0.10
 * @author T.TSUCHIE
 *
 */
public abstract class NumberFactory {

    /**
     * 何も加工をしない数値のファクトリクラスの取得。
     * @return
     */
    public static NumberFactory nativeNumber() {
        return new NumberFactory() {

            @Override
            public FormattedNumber create(double value) {
                return new NativeNumber(value);
            }
        };
    }

    /**
     * 小数のファクトリクラスの取得
     * @param scale 小数の精度（桁数）。
     * @param useSeparator 区切り文字があるかどうか。
     * @param permilles 千分率の次数。ゼロと指定した場合は、{@code 1000^0}の意味。
     * @return {@link DecimalNumberFactory}のインスタンス。
     */
    public static DecimalNumberFactory decimalNumber(int scale, boolean useSeparator, final int permilles) {
        return new DecimalNumberFactory(scale, useSeparator, permilles);
    }

    /**
     * パーセントのファクトリクラスの取得
     * @param scale 小数の精度（桁数）。
     * @param useSeparator 区切り文字があるかどうか。
     * @param permilles 千分率の次数。ゼロと指定した場合は、{@code 1000^0}の意味。
     * @return {@link PercentNumberFactory}のインスタンス。
     */
    public static PercentNumberFactory percentNumber(int scale, boolean useSeparator, final int permilles) {
        return new PercentNumberFactory(scale, useSeparator, permilles);
    }

    /**
     * 指数のファクトリクラスの取得
     * @param scale 小数の精度（桁数）。
     * @param useSeparator 区切り文字があるかどうか。
     * @return {@link ExponentNumberFactory}のインスタンス。
     */
    public static ExponentNumberFactory exponentNumber(int scale, boolean useSeparator) {
        return new ExponentNumberFactory(scale, useSeparator);
    }

    /**
     * 分数のファクトリクラスの取得
     * @param denominator 分母の値
     * @param exactDenom 分母を直接指定かどうか
     * @param wholeType 帯分数形式かどうか
     * @return {@link FractionNumberFactory}のインスタンス。
     */
    public static FractionNumberFactory fractionNumber(final int denominator,
            final boolean exactDenom, final boolean wholeType) {
        return new FractionNumberFactory(denominator, exactDenom, wholeType);
    }

    /**
     * 数値オブジェクトのインスタンスを取得する。
     * @param value 変換元数値。
     * @return 組み立てた数値オブジェクト。
     */
    public abstract FormattedNumber create(double value);

    /**
     * 整数、小数の数値として作成するクラス。
     *
     */
    public static class DecimalNumberFactory extends NumberFactory {

        private int scale;

        private boolean useSeparator;

        private int permilles;

        public DecimalNumberFactory(final int scale, final boolean useSeparator, final int permilles) {
            this.scale = scale;
            this.useSeparator = useSeparator;
            this.permilles = permilles;
        }

        @Override
        public FormattedNumber create(double value) {
            FormattedNumber number = new DecimalNumber(value, scale, permilles);
            number.setUseSeparator(useSeparator);
            return number;
        }

    }

    /**
     * 指数として数値を作成するクラス。
     *
     */
    public static class ExponentNumberFactory extends NumberFactory {

        private int scale;

        private boolean useSeparator;

        public ExponentNumberFactory(final int scale, final boolean useSeparator) {
            this.scale = scale;
            this.useSeparator = useSeparator;
        }

        @Override
        public FormattedNumber create(double value) {
            FormattedNumber number = new ExponentNumber(value, scale);
            number.setUseSeparator(useSeparator);
            return number;
        }

    }

    /**
     * パーセントとして数値を作成するクラス。
     *
     */
    public static class PercentNumberFactory extends NumberFactory {

        private int scale;

        private boolean useSeparator;

        private int permilles;

        public PercentNumberFactory(final int scale, final boolean useSeparator, final int permilles) {
            this.scale = scale;
            this.useSeparator = useSeparator;
            this.permilles = permilles;
        }

        @Override
        public FormattedNumber create(double value) {
            FormattedNumber number = new PercentNumber(value, scale, permilles);
            number.setUseSeparator(useSeparator);
            return number;
        }

    }

    /**
     * 分数として数値を作成するクラス
     *
     */
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
