package com.github.mygreen.cellformatter;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.github.mygreen.cellformatter.lang.MSColor;

/**
 * {@link CustomFormatterFactory}のテスタ
 * @since 0.3
 * @author T.TSUCHIE
 *
 */
public class CustomFormatterFactoryTest {

    // エスケープ文字のテスト
    @Test
    public void testEscapedChar() {

        CommonCell testCell = new TestNumberCell(23.1, (short)0, "*a!a!a##.00");
        CustomFormatterFactory factory = new CustomFormatterFactory();
        CustomFormatter formatter = factory.create(testCell.getFormatPattern());

        CellFormatResult actual = formatter.format(testCell);
        assertThat(actual.getText(), is("aa23.10"));
    }

    // エスケープ文字のテスト
    @Test
    public void testEscapedChar2() {

        // "\"#,##0_);[Red]\("\"#,##0\)
        NumberCell<Integer> cell = new NumberCell<Integer>(-1234, "\"\\\"#,##0_);[Red]\\(\"\\\"#,##0\\)");

        CustomFormatterFactory factory = new CustomFormatterFactory();
        CustomFormatter formatter = factory.create(cell.getFormatPattern());

        CellFormatResult actual = formatter.format(cell);
        assertThat(actual.getText(), is("(\\1,234)"));
        assertThat(actual.getTextColor(), is(MSColor.RED));
    }

    // セクションの個数のテスト
    @Test
    public void testSectionNumber() {

        CustomFormatterFactory factory = new CustomFormatterFactory();

        try {
            // 4つの場合
            factory.create("###1;###2;###3;4@");
            factory.create("!¥ 0;- !¥ 0;!¥ 0;''");
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        }

        try {
            // 5つの場合
            factory.create("###1;###2;###3;###4;5@");
            fail();
        } catch(Exception e) {

            assertThat(e, instanceOf(CustomFormatterParseException.class));
        }

    }

    // フォーマット結果のテスト
    @Test
    public void testCellFormatResult() {

        CommonCell testCell = new TestNumberCell(100.1, (short)0, "[赤][>100]\">\"##.0#;[青][=100]\"=\"##.0#;[紫]\"その他\"##.0#");

        CustomFormatterFactory factory = new CustomFormatterFactory();
        CustomFormatter formatter = factory.create(testCell.getFormatPattern());

        CellFormatResult actual = formatter.format(testCell);

        assertThat(actual.getValueAsDoulbe(), is(100.1));
        assertThat(actual.getText(), is(">100.1"));
        assertThat(actual.getTextColor(), is(MSColor.RED));
        assertThat(actual.getSectionPattern(), is("[赤][>100]\">\"##.0#"));
        assertThat(actual.getCellType(), is(FormatCellType.Number));

    }
}
