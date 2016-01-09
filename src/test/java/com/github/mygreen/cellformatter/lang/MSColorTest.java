package com.github.mygreen.cellformatter.lang;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Locale;

import org.junit.Test;

/**
 * {@link MSColor}のテスタ
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class MSColorTest {
    
    @Test
    public void testGetIndex() {
        assertThat(MSColor.BLACK.getIndex(), is((short)1));
        assertThat(MSColor.WHITE.getIndex(), is((short)2));
        assertThat(MSColor.RED.getIndex(), is((short)3));
        assertThat(MSColor.GREEN.getIndex(), is((short)4));
        assertThat(MSColor.BLUE.getIndex(), is((short)5));
        assertThat(MSColor.YELLOW.getIndex(), is((short)6));
        assertThat(MSColor.MAGENTA.getIndex(), is((short)7));
        assertThat(MSColor.CYAN.getIndex(), is((short)8));
    }
    
    @Test
    public void testGetName() {
        assertThat(MSColor.BLACK.getName(), is("Black"));
        assertThat(MSColor.WHITE.getName(), is("White"));
        assertThat(MSColor.RED.getName(), is("Red"));
        assertThat(MSColor.GREEN.getName(), is("Green"));
        assertThat(MSColor.BLUE.getName(), is("Blue"));
        assertThat(MSColor.YELLOW.getName(), is("Yellow"));
        assertThat(MSColor.MAGENTA.getName(), is("Magenta"));
        assertThat(MSColor.CYAN.getName(), is("Cyan"));
    }
    
    @Test
    public void testGetLocaleNameLocale() {
        assertThat(MSColor.BLACK.getLocaleName(Locale.JAPANESE), is("黒"));
        assertThat(MSColor.WHITE.getLocaleName(Locale.JAPANESE), is("白"));
        assertThat(MSColor.RED.getLocaleName(Locale.JAPANESE), is("赤"));
        assertThat(MSColor.GREEN.getLocaleName(Locale.JAPANESE), is("緑"));
        assertThat(MSColor.BLUE.getLocaleName(Locale.JAPANESE), is("青"));
        assertThat(MSColor.YELLOW.getLocaleName(Locale.JAPANESE), is("黄"));
        assertThat(MSColor.MAGENTA.getLocaleName(Locale.JAPANESE), is("紫"));
        assertThat(MSColor.CYAN.getLocaleName(Locale.JAPANESE), is("水"));
        
        // 定義されていないロケール
        assertThat(MSColor.BLACK.getLocaleName(Locale.CHINESE), is("Black"));
    }
    
    @Test
    public void testGetHtmlColor() {
        assertThat(MSColor.BLACK.getHtmlColor(), is("#000000"));
        assertThat(MSColor.WHITE.getHtmlColor(), is("#FFFFFF"));
        assertThat(MSColor.RED.getHtmlColor(), is("#FF0000"));
        assertThat(MSColor.GREEN.getHtmlColor(), is("#00FF00"));
        assertThat(MSColor.BLUE.getHtmlColor(), is("#0000FF"));
        assertThat(MSColor.YELLOW.getHtmlColor(), is("#FFFF00"));
        assertThat(MSColor.MAGENTA.getHtmlColor(), is("#FF00FF"));
        assertThat(MSColor.CYAN.getHtmlColor(), is("#00FFFF"));
    }
    
    @Test
    public void testGetRgbColor() {
        assertThat(MSColor.BLACK.getRgbColor(), is(new short[]{0,0,0}));
        assertThat(MSColor.WHITE.getRgbColor(), is(new short[]{255,255,255}));
        assertThat(MSColor.RED.getRgbColor(), is(new short[]{255, 0, 0}));
        assertThat(MSColor.GREEN.getRgbColor(), is(new short[]{0, 255, 0}));
        assertThat(MSColor.BLUE.getRgbColor(), is(new short[]{0, 0, 255}));
        assertThat(MSColor.YELLOW.getRgbColor(), is(new short[]{255, 255, 0}));
        assertThat(MSColor.MAGENTA.getRgbColor(), is(new short[]{255, 0, 255}));
        assertThat(MSColor.CYAN.getRgbColor(), is(new short[]{0, 255, 255}));
    }
}
