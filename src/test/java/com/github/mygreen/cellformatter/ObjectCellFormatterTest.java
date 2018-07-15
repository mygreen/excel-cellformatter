package com.github.mygreen.cellformatter;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static com.github.mygreen.cellformatter.lang.TestUtils.*;

import java.sql.Timestamp;
import java.util.Date;

import org.junit.Test;

import com.github.mygreen.cellformatter.lang.MSColor;

/**
 * {@link ObjectCellFormatter}のテスタ
 *
 * @since 0.6
 * @author T.TSUCHIE
 *
 */
public class ObjectCellFormatterTest {
    
    @Test
    public void test_format_blank() {
        
        ObjectCellFormatter cellFormatter = new ObjectCellFormatter();
        
        CellFormatResult result = cellFormatter.format(null);
        
        assertThat(result.getCellType(), is(FormatCellType.Blank));
        assertThat(result.getText(), is(""));
        assertThat(result.getTextColor(), is(nullValue()));
        assertThat(result.getSectionPattern(), is(nullValue()));
    }
    
    @Test
    public void test_format_numeric_double() {
        
        ObjectCellFormatter cellFormatter = new ObjectCellFormatter();
        
        ObjectCell<?> cell = new NumberCell<Double>(-1234.5d, "#,##0.0_);[Red]\\(#,##0.0\\)");
        
        CellFormatResult result = cellFormatter.format(cell);
        
        assertThat(result.getCellType(), is(FormatCellType.Number));
        assertThat(result.getText(), is("(1,234.5)"));
        assertThat(result.getTextColor(), is(MSColor.RED));
        assertThat(result.getSectionPattern(), is("[Red]\\(#,##0.0\\)"));
        assertThat(result.getValueAsDoulbe(), is(-1234.5));
    }
    
    @Test
    public void test_format_numeric_int() {
        
        ObjectCellFormatter cellFormatter = new ObjectCellFormatter();
        
        ObjectCell<?> cell = new NumberCell<Integer>(-12345, "#,##0.0_);[Red]\\(#,##0.0\\)");
        
        CellFormatResult result = cellFormatter.format(cell);
        
        assertThat(result.getCellType(), is(FormatCellType.Number));
        assertThat(result.getText(), is("(12,345.0)"));
        assertThat(result.getTextColor(), is(MSColor.RED));
        assertThat(result.getSectionPattern(), is("[Red]\\(#,##0.0\\)"));
        assertThat(result.getValueAsDoulbe(), is(-12345.0));
    }
    
    @Test
    public void test_format_numeric_escapeFormat() {
        
        ObjectCellFormatter cellFormatter = new ObjectCellFormatter();
        
        ObjectCell<?> cell = new NumberCell<Integer>(-1234, "\"\\\"#,##0_);[Red]\\(\"\\\"#,##0\\)");
        
        CellFormatResult result = cellFormatter.format(cell);
        
        assertThat(result.getCellType(), is(FormatCellType.Number));
        assertThat(result.getText(), is("(\\1,234)"));
        assertThat(result.getTextColor(), is(MSColor.RED));
        assertThat(result.getSectionPattern(), is("[Red]\\(\"\\\"#,##0\\)"));
        assertThat(result.getValueAsDoulbe(), is(-1234.0));
        
    }
    
    // Java8の場合、丸め誤差により結果が 1.234E-05 となるため注意が必要
    @Test
    public void test_format_num() {
        
        ObjectCellFormatter cellFormatter = new ObjectCellFormatter();
        
        ObjectCell<?> cell = new NumberCell<Double>(0.000012345d, "0.000E+00");
        
        CellFormatResult result = cellFormatter.format(cell);
        
        assertThat(result.getCellType(), is(FormatCellType.Number));
        
        if(IS_JAVA_1_8) {
            assertThat(result.getText(), is("1.234E-05"));
        } else {
            assertThat(result.getText(), is("1.235E-05"));
            
        }
        
    }
    
    @Test
    public void test_format_date() {
        
        ObjectCellFormatter cellFormatter = new ObjectCellFormatter();
        
        ObjectCell<?> cell = new DateCell(Timestamp.valueOf("2012-02-29 13:01:40"), "[$-411]ggge\"年\"m\"月\"d\"日\"\\ h:mm\\ AM/PM;@");
        
        CellFormatResult result = cellFormatter.format(cell);
        
        assertThat(result.getCellType(), is(FormatCellType.Date));
        assertThat(result.getText(), is("平成24年2月29日 1:01 午後"));
        assertThat(result.getTextColor(), is(nullValue()));
        assertThat(result.getSectionPattern(), is("[$-411]ggge\"年\"m\"月\"d\"日\"\\ h:mm\\ AM/PM"));
        assertThat(result.getValueAsDate(), is(new Date(Timestamp.valueOf("2012-02-29 13:01:40.000").getTime())));
        
    }
    
    @Test
    public void test_format_boolean() {
        
        ObjectCellFormatter cellFormatter = new ObjectCellFormatter();
        
        ObjectCell<?> cell = new BooleanCell(true, "接頭語： @");
        
        CellFormatResult result = cellFormatter.format(cell);
        
        assertThat(result.getCellType(), is(FormatCellType.Boolean));
        assertThat(result.getText(), is("接頭語： TRUE"));
        assertThat(result.getTextColor(), is(nullValue()));
        assertThat(result.getSectionPattern(), is("接頭語： @"));
        assertThat(result.getValueAsBoolean(), is(true));
        
    }
    
    @Test
    public void test_format_text() {
        
        ObjectCellFormatter cellFormatter = new ObjectCellFormatter();
        
        ObjectCell<?> cell = new TextCell("今日", "接頭語： @");
        
        CellFormatResult result = cellFormatter.format(cell);
        
        assertThat(result.getCellType(), is(FormatCellType.Text));
        assertThat(result.getText(), is("接頭語： 今日"));
        assertThat(result.getTextColor(), is(nullValue()));
        assertThat(result.getSectionPattern(), is("接頭語： @"));
        assertThat(result.getValueAsString(), is("今日"));
        
    }
}
