package com.github.mygreen.cellformatter;

import org.apache.poi.ss.usermodel.Cell;

/**
 * セル中の式の評価に失敗したときにスローされる例外
 *
 * @since 0.7
 * @author T.TSUCHIE
 *
 */
public class FormulaEvaluateException extends RuntimeException {

    /** serialVersionUID */
    private static final long serialVersionUID = -6090565710624572832L;
    
    private final Cell cell;
    
    /**
     * 
     * @param cell 評価に失敗したセル
     * @param error 発生したエラー
     */
    public FormulaEvaluateException(final Cell cell, final Exception error) {
        super(error);
        this.cell = cell;
    }
    
    /**
     * 式の評価に失敗したセルを取得する。
     * @return 評価に失敗したセル
     */
    public Cell getCell() {
        return cell;
    }
    
}
