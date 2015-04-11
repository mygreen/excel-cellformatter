package com.github.mygreen.cellformatter;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.github.mygreen.cellformatter.lang.MSColor;

/**
 * {@link CustomFormatterFactory}のテスタ
 * @since 0.3
 * @author T.TSUCHIE
 *
 */
public class CustomFormatterFactoryTest {
    
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }
    
    @Before
    public void setUp() throws Exception {
    }
    
    @After
    public void tearDown() throws Exception {
    }
    
    // エスケープ文字のテスト
    @Test
    public void testEscapedChar() {
        
        CommonCell testCell = new TestNumberCell(23.1, (short)0, "*a!a!a##.00");
        CustomFormatterFactory factory = new CustomFormatterFactory();
        CustomFormatter formatter = factory.create(testCell.getFormatPattern());
        
        CellFormatResult actual = formatter.format(testCell);
        assertThat(actual.getText(), is("aa23.10"));
    }
    
    // セクションの個数のテスト
    @Test
    public void testSectionNumber() {
        
        CustomFormatterFactory factory = new CustomFormatterFactory();
        
        try {
            // 4つの場合
            factory.create("###1;###2;###3;4@");
            
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
        assertThat(actual.getFormatterType(), is(FormatterType.Number));
        
    }
}
