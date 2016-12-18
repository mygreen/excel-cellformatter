package com.github.mygreen.cellformatter.lang;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Locale;

import org.junit.Test;

/**
 * {@link MessageResolver}のテスタ
 *
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class MessageResolverTest {
    
    
    @Test
    public void testGetMessage() {
        
        MessageResolver messageResolver = new MessageResolver("com.github.mygreen.cellformatter.label");
        
        assertThat(messageResolver.loadResource(Locale.JAPANESE).isNullObject(), is(false));
        
        // default
        assertThat(messageResolver.getMessage("week.0.name"), is("Sunday"));
        
        // ロケール
        assertThat(messageResolver.getMessage(MSLocale.JAPANESE, "week.0.name"), is("日曜日"));
        
        // 存在しないロケール
        assertThat(messageResolver.getMessage(MSLocale.UK, "week.0.name"), is("Sunday"));
        
        // 存在しないプロパティ
        assertThat(messageResolver.getMessage("aaaa"), is(nullValue()));
    }
    
    @Test
    public void testNullObject() {
        
        MessageResolver messageResolver = new MessageResolver("com.sample", true);
        
        // default
        assertThat(messageResolver.loadResource(Locale.JAPANESE).isNullObject(), is(true));
        
    }
    
}
