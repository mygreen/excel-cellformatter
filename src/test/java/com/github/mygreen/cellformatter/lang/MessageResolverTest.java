package com.github.mygreen.cellformatter.lang;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.Locale;

import org.junit.Test;

/**
 * {@link MessageResolver}のテスタ
 *
 * @version 0.9
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
        
        MessageResolver messageResolver = new MessageResolver("com.sample", true, true);
        
        // default
        assertThat(messageResolver.loadResource(Locale.JAPANESE).isNullObject(), is(true));
        
    }
    
    /**
     * クラスパスのルートにあるファイルの読み込み - 既存の年号の場合
     * @since 0.9
     */
    @Test
    public void testUserResource_era() {
        
        MessageResolver messageResolver = new MessageResolver("com.github.mygreen.cellformatter.era", true, true);
        
        assertThat(messageResolver.getMessage(Locale.JAPANESE, "era.karigou.name"), is("仮号"));
        
    }
    
}
