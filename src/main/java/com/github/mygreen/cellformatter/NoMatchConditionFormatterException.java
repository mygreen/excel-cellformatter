package com.github.mygreen.cellformatter;


/**
 * 適切な条件に一致するフォーマットが見つからない場合にスローする例外
 * @author T.TSUCHIE
 *
 */
public class NoMatchConditionFormatterException extends RuntimeException {
    
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    
    private final CommonCell cell;
    
    public NoMatchConditionFormatterException(final CommonCell cell, final String message) {
        super(message);
        this.cell = cell;
    }
    
    /**
     * フォーマットに失敗したセルを取得する。
     * @return
     */
    public CommonCell getCell() {
        return cell;
    }
    
}