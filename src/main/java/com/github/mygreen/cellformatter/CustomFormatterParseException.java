package com.github.mygreen.cellformatter;


/**
 * パース時に書式が不正と判断する場合にスローされる例外。
 * 
 * @version 0.2
 * @since 0.2
 * @author T.TSUCHIE
 *
 */
public class CustomFormatterParseException extends RuntimeException {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    /** 書式 */
    private final String pattern;
    
    /**
     * メッセージを指定してインスタンスを作成する。
     * @param pattern 問題となった書式。
     * @param message エラーメッセージ。
     */
    public CustomFormatterParseException(final String pattern, final String message) {
        super(message);
        this.pattern = pattern;
    }
    
    /**
     * 書式を取得する。
     * @return
     */
    public String getPattern() {
        return pattern;
    }
    
}
