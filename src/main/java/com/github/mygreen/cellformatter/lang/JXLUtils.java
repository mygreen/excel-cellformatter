package com.github.mygreen.cellformatter.lang;

import java.lang.reflect.Field;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.SheetImpl;
import jxl.read.biff.WorkbookParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * JExcelAPIのユーティリティクラス。
 * @author T.TSUCHIE
 *
 */
public class JXLUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(JXLUtils.class);
    
    /**
     * ファイルが1904年始まりの設定かどうか。
     * @param workbook 判定対象のワークブック。
     * @return true:1904年始まり
     * @throws IllegalArgumentException {@literal workbook == null.}
     */
    public static boolean isDateStart1904(final Workbook workbook) {
        ArgUtils.notNull(workbook, "workbook");
        
        if(workbook instanceof WorkbookParser) {
            try {
                Field field = WorkbookParser.class.getDeclaredField("nineteenFour");
                field.setAccessible(true);
                
                boolean value = field.getBoolean(workbook);
                return value;
                
            } catch (NoSuchFieldException | SecurityException e) {
                logger.warn("fail access field WrokbookParser#nineteenFour", e);
                return false;
                
            } catch (IllegalArgumentException | IllegalAccessException e) {
                logger.warn("fail invoke field WrokbookParser#nineteenFour", e);
                return false;
            }
            
        }
        
        return false;
    }
    
    /**
     * ファイルが1904年始まりの設定かどうか。
     * @param sheet 判定対象のシート。
     * @return true:1904年始まり
     * @throws IllegalArgumentException  {@literal sheet == null.}
     */
    public static boolean isDateStart1904(final Sheet sheet) {
        ArgUtils.notNull(sheet, "sheet");
        
        if(sheet instanceof SheetImpl) {
            try {
                Field field = SheetImpl.class.getDeclaredField("nineteenFour");
                field.setAccessible(true);
                
                boolean value = field.getBoolean(sheet);
                return value;
                
            } catch (NoSuchFieldException | SecurityException e) {
                logger.warn("fail access field SheetImpl#nineteenFour", e);
                return false;
                
            } catch (IllegalArgumentException | IllegalAccessException e) {
                logger.warn("fail invoke field SheetImpl#nineteenFour", e);
                return false;
            }
            
        }
        
        return false;
    }
    
}
