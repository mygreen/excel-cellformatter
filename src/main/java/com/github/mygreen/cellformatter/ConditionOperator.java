package com.github.mygreen.cellformatter;


/**
 * ユーザ定義の条件式を表現するクラス。
 * <pre><、>、>=、<=、<></pre>を表現する。
 *
 * @author T.TSUCHIE
 *
 */
public abstract class ConditionOperator {
    
    /**
     * 全ての値に一致する条件。常にtrueを返す。
     */
    public static ConditionOperator ALL = new ConditionOperator() {
        
        @Override
        public boolean isMatch(final double value) {
            return true;
        }
        
    };
    
    /**
     * 正の値に一致する条件。(>0.0)
     */
    public static ConditionOperator POSITIVE = new GreaterThan(0.0);
    
    /**
     * 負の値に一致する条件（<0.0）
     */
    public static ConditionOperator NEGATIVE = new LessThan(0.0);
    
    /**
     * ゼロの値に一致する条件（=0.0）
     */
    public static ConditionOperator ZERO = new Equal(0.0);
    
    /**
     * ゼロ以上の値に一致する条件（非負の数）（>= 0.0）;
     */
    public static ConditionOperator NON_NEGATIVE = new GreaterEqual(0.0);
    
    /**
     * 指定した値が条件に一致するかどうか。
     * @param value
     * @return
     */
    public abstract boolean isMatch(double value);
    
    /**
     * 等しい(=条件値)かどうか。
     */
    public static class Equal extends ConditionOperator{
        
        private final double condition;
        
        public Equal(final double condition) {
            this.condition = condition;
        }
        
        @Override
        public boolean isMatch(final double value) {
            return value == condition;
        }
        
        public double getConditionValue() {
            return condition;
        }
        
    }
    
    /**
     * 等しいくない(<>条件値)かどうか。
     */
    public static class NotEqual extends ConditionOperator{
        
        private final double condition;
        
        public NotEqual(final double condition) {
            this.condition = condition;
        }
        
        @Override
        public boolean isMatch(final double value) {
            return value != condition;
        }
        
        public double getConditionValue() {
            return condition;
        }
        
    }
    
    /**
     * より大きい（>条件値）
     */
    public static class GreaterThan extends ConditionOperator{
        
        private final double condition;
        
        public GreaterThan(final double condition) {
            this.condition = condition;
        }
        
        @Override
        public boolean isMatch(final double value) {
            return value > condition;
        }
        
        public double getConditionValue() {
            return condition;
        }
        
    }
    
    /**
     * より小さい（<条件値）
     */
    public static class LessThan extends ConditionOperator{
        
        private final double condition;
        
        public LessThan(final double condition) {
            this.condition = condition;
        }
        
        @Override
        public boolean isMatch(final double value) {
            return value < condition;
        }
        
        public double getConditionValue() {
            return condition;
        }
        
    }
    
    /**
     * 以上（>=条件値）
     */
    public static class GreaterEqual extends ConditionOperator{
        
        private final double condition;
        
        public GreaterEqual(final double condition) {
            this.condition = condition;
        }
        
        @Override
        public boolean isMatch(final double value) {
            return value >= condition;
        }
        
        public double getConditionValue() {
            return condition;
        }
        
    }
    
    /**
     * 以下（<=条件値）
     */
    public static class LessEqual extends ConditionOperator{
        
        private final double condition;
        
        public LessEqual(final double condition) {
            this.condition = condition;
        }
        
        @Override
        public boolean isMatch(final double value) {
            return value <= condition;
        }
        
        public double getConditionValue() {
            return condition;
        }
        
    }
    
}
