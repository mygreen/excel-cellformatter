package com.github.mygreen.cellformatter;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Timestamp;

import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * 組み込み書式のシートを作成する。
 * @since 0.6
 * @author T.TSUCHIE
 *
 */
public class BuiltInFormatGenerator {
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        
        BuiltInFormatGenerator tool = new BuiltInFormatGenerator();
        
        // 新規に作成
//        File templateFile = new File("src/test/data/cell_format_2000_builtinformat_template.xls");
//        File outFile = new File("src/test/data/cell_format_2000_builtinformat.xls");
//        tool.generate(templateFile, outFile);
        
        // 更新する
        File templateFile = new File("src/test/data/cell_format_2000_builtinformat_en_template.xls");
        File outFile = new File("src/test/data/cell_format_2000_builtinformat_en.xls");
        tool.update(templateFile, outFile);
        
        
        
    }
    
    /**
     * テンプレートファイルを元に新規に作成する。
     * @param templateFile
     * @param outFile
     */
    private void generate(final File templateFile, final File outFile) throws Exception {
        
        Workbook workbook = WorkbookFactory.create(templateFile);
        
        int fromSheetIndex = workbook.getSheetIndex("書式（テンプレート)");
        Sheet cloneSheet = workbook.cloneSheet(fromSheetIndex);
        int cloneSheetIndex = workbook.getSheetIndex(cloneSheet);
        
        // シートのフォーカス
        workbook.setActiveSheet(cloneSheetIndex);
        workbook.getSheetAt(fromSheetIndex).setSelected(false);
        
        workbook.setSheetName(cloneSheetIndex, "書式（組み込み書式)");
        
        execute(cloneSheet);
        
        workbook.write(new FileOutputStream(outFile));
        
        System.out.printf("write file '%s'\n", outFile.getName());
        
    }
    
    /**
     * 既に作成済みのファイルに対してもう一度上書きする。
     * @param templateFile
     * @param outFile
     * @throws Exception
     */
    private void update(final File templateFile, final File outFile) throws Exception {
        
        Workbook workbook = WorkbookFactory.create(templateFile);
        
        Sheet sheet = workbook.getSheet("書式（組み込み書式)");
        
        execute(sheet);
        
        workbook.write(new FileOutputStream(outFile));
        
        System.out.printf("write file '%s'\n", outFile.getName());
        
    }
    
    private void execute(final Sheet sheet) {
        
        DataFormat format = sheet.getWorkbook().createDataFormat();
        
        for(int i=0; i <= 49; i++) {
            
            final Row row = sheet.getRow(3 + i);
            
            Cell descriptionCell = row.getCell(1);
            Cell testCaseCell = row.getCell(2);
            Cell testResultCell = row.getCell(3);
            Cell indexCell = row.getCell(4);
            Cell formatCell = row.getCell(5);
            
            descriptionCell.setCellValue(String.format("[10進数]=%d, [16進数]=%s", i, Integer.toHexString(i)));
            
            // テストケースのスタイルの設定
            CellStyle style = sheet.getWorkbook().createCellStyle();
            style.setDataFormat((short)i);
            style.setBorderBottom(CellStyle.BORDER_THIN);
            
            testCaseCell.setCellStyle(style);
            
            if(i == 0) {
                testCaseCell.setCellValue("テキスト");
                
            } else if(range(i, 1, 8)) {
                // 数値
                testCaseCell.setCellValue(-123.456);
                
            } else if(range(i, 9, 11)) {
                // パーセント、小数
                testCaseCell.setCellValue(0.0123);
                
            } else if(range(i, 12, 13)) {
                // 分数
                testCaseCell.setCellValue(12.345);
                
            } else if(range(i, 14, 36)) {
                // 日時
                testCaseCell.setCellValue(Timestamp.valueOf("2000-02-29 10:19:23.123"));
                
            } else if(range(i, 37, 44)) {
                // 会計
                testCaseCell.setCellValue(-123.456);
                
            } else if(range(i, 45, 48)) {
                // 時間、経過時間
                testCaseCell.setCellValue(Timestamp.valueOf("1900-01-01 00:01:01.000"));
                
            } else if(range(i, 9, 11)) {
                // 指数
                testCaseCell.setCellValue(123.456789);
                
            } else if(i == 49) {
                // テキスト
                testCaseCell.setCellValue("テキスト");
            }
            
            // インデックス
            indexCell.setCellValue(i);
            
            // フォーマット
            formatCell.setCellValue(BuiltinFormats.getBuiltinFormat(i));
            
        }
        
    }
    
    private boolean range(int value, int min, int max) {
        
        if(min <= value && value <= max) {
            return true;
        }
        
        return false;
        
    }
    
    
}
