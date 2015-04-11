package com.github.mygreen.cellformatter.tokenizer;


/**
 * Excelのセルの書式の基本的な構成要素を表現するトークン。
 * 
 * @author T.TSUCHIE
 *
 */
public abstract class Token {
    
    /**
     * 書式の区切り文字';'用の記号。
     */
    public static final Symbol SYMBOL_SEMI_COLON = new Symbol(";");
    
    /**
     * 数値の桁数の区切り文字','の記号
     */
    public static final Symbol SYMBOL_COLON = new Symbol(",");
    
    /**
     * 数値の百分率'%'の記号
     */
    public static final Symbol SYMBOL_PERCENT = new Symbol("%");
    
    /**
     * 数値の分数'/'の記号
     */
    public static final Symbol SYMBOL_SLASH = new Symbol("/");
    
    /**
     * 小数の区切り'.'の記号
     */
    public static final Symbol SYMBOL_DOT = new Symbol(".");
    
    /**
     * テキストのマーカー'@'用の記号。
     */
    public static final Symbol SYMBOL_AT_MARK = new Symbol("@");
    
    /**
     * エスケープ文字（バックスペース\\）
     */
    public static final String STR_ESCAPE_BACKSPACE = "\\";
    
    /**
     * エスケープ文字（クエスチョン!）
     */
    public static final String STR_ESCAPE_QUESTION = "!";
    
    /**
     * エスケープ文字の配列
     */
    public static final String[] STR_ESCAPES = {STR_ESCAPE_BACKSPACE, STR_ESCAPE_QUESTION};
    
    /**
     * トークンの文字列。
     */
    private final String value;
    
    public Token(final String value) {
        this.value = value;
    }
    
    public static Word word(final String token) {
        return new Word(token);
    }
    
    public static EscapedChar escapedChar(final String token) {
        return new EscapedChar(token);
    }
    
    public static Condition condition(final String token) {
        return new Condition(token);
    }
    
    public static Underscore underscore(final String token) {
        return new Underscore(token);
    }
    
    public static Asterisk asterisk(final String token) {
        return new Asterisk(token);
    }
    
    public static Factor factor(final String token) {
        return new Factor(token);
    }
    
    public static Formatter formatter(final String token) {
        return new Formatter(token);
    }
    
    public static Digits digits(final String token) {
        return new Digits(token);
    }
    
    public Word asWord() {
        return (Word) this;
    }
    
    public Symbol asSymbol() {
        return (Symbol) this;
    }
    
    public EscapedChar asEscapedChar() {
        return (EscapedChar) this;
    }
    
    public Condition asCondition() {
        return (Condition) this;
    }
    
    public Underscore asUnderscore() {
        return (Underscore) this;
    }
    
    public Asterisk asAsterisk() {
        return (Asterisk) this;
    }
    
    public Factor asFactor() {
        return (Factor) this;
    }
    
    public Formatter asFormatter() {
        return (Formatter) this;
    }
    
    public Digits asDigits() {
        return (Digits) this;
    }
    
    @Override
    public String toString() {
        return getValue();
    }
    
    /**
     * トークンの値を取得する。
     * @return
     */
    public String getValue() {
        return value;
    }
    
    /**
     * ダブルクウォート'"'で囲まれた文字列。
     *
     */
    public static class Word extends Token {
        
        public Word(final String value) {
            super(value);
        }
        
        /**
         * ダブルクウォート'"'を除いたテキストの値を取得する。
         * @return
         */
        public String getWord() {
            int length = getValue().length();
            return getValue().substring(1, length-1);
            
        }
        
    }
    
    /**
     * 書式用の記号。
     *
     */
    public static class Symbol extends Token {
        
        public Symbol(final String value) {
            super(value);
        }
    }
    
    /**
     * エスケープされた文字。
     *
     */
    public static class EscapedChar extends Token {
        
        public EscapedChar(final String value) {
            super(value);
        }
        
        /**
         * エスケープ文字'\'を除いた文字を取得する。
         * @return
         */
        public String getChar() {
            return getValue().substring(1);
        }
    }
    
    /**
     * 括弧で囲まれた条件の書式'[condition]'。
     *
     */
    public static class Condition extends Token {
        
        public Condition(final String value) {
            super(value);
        }
        
        /**
         * 括弧を除いた条件の値の取得。
         * @return
         */
        public String getCondition() {
            int length = getValue().length();
            return getValue().substring(1, length-1);
        }
        
    }
    
    /**
     * アンダースコア'_'とそれに続く次の文字。
     */
    public static class Underscore extends Token {
        
        public Underscore(final String value) {
            super(value);
        }
        
        /**
         * アンダースコアに付随する次の文字の取得
         * @return
         */
        public String getAttachedValue() {
            return getValue().substring(1);
        }
        
    }
    
    /**
     * アスタリスク'*'とそれに続く次の文字。
     */
    public static class Asterisk extends Token {
        
        public Asterisk(final String value) {
            super(value);
        }
        
        /**
         * アスタリスクに付随する次の文字の取得
         * @return
         */
        public String getAttachedValue() {
            return getValue().substring(1);
        }
        
    }
    
    /**
     * 書式の因子となるものを構成するもの。
     * ・日付などの'yyyy'や数値の'##'など。
     */
    public static class Factor extends Token {
        
        public Factor(String value) {
            super(value);
        }
        
    }
    
    /**
     * 書式のフォーマットの最小単位。
     * ・各書式ごとに解析して、{@link Factor}をさらに分解したもの。
     *
     */
    public static class Formatter extends Token {
        
        public Formatter(String value) {
            super(value);
        }
        
    }
    
    /**
     * 整数の数値
     * ・各書式ごとに解析して、{@link Factor}をさらに分解したもの。
     *
     */
    public static class Digits extends Token {
        
        public Digits(String value) {
            super(value);
            
        }
        
        /**
         * 整数に変換した値
         * @return
         */
        public int intValue() {
            return Integer.valueOf(getValue());
        }
        
    }
}
