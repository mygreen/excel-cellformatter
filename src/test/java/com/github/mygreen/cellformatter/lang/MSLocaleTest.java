package com.github.mygreen.cellformatter.lang;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.Locale;

import org.junit.Test;

/**
 * {@link MSLocale}のテスタ
 * 
 * @version 0.5
 * @author T.TSUCHIE
 *
 */
public class MSLocaleTest {
    
    @Test
    public void testKnownLocale() {
        
        assertThat(MSLocale.JAPANESE.getHexId(), is("0411"));
        assertThat(MSLocale.JAPANESE.getCode(), is("ja"));
        assertThat(MSLocale.JAPANESE.getLanguage(), is("Japanese"));
        assertThat(MSLocale.JAPANESE.getCountry(), is(""));
        assertThat(MSLocale.JAPANESE.getName(), is("Japanese"));
        assertThat(MSLocale.JAPANESE.getName(Locale.JAPAN), is("日本語"));
        assertThat(MSLocale.JAPANESE.getName(Locale.JAPANESE), is("日本語"));
        
        assertThat(MSLocale.JAPANESE.getLocale().getLanguage(), is("ja"));
        
    }
}
