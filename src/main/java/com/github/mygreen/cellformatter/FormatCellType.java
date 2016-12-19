package com.github.mygreen.cellformatter;

/**
 * フォーマットする際に判断したセルの種類。
 * @since 0.4
 * @author T.TSUCHIE
 *
 */
public enum FormatCellType {
    
    /** Blank */
    Blank,
    
    /** Boolean */
    Boolean,
    
    /** Text - String */
    Text,
    
    /** Number - double */
    Number,
    
    /** Date - date and time */
    Date,
    
    /** Error cell */
    Error,
    
    /** Unknown */
    Unknown,
    ;
    
}
