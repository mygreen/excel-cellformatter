package com.github.mygreen.cellformatter.lang;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.sql.Timestamp;
import java.util.Date;

import org.junit.Test;

/**
 * {@link EraResolver}のテスタ
 *
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class EraResolverTest {
    
    @Test
    public void testIsUnkown() {
        
        EraResolver resolver = new EraResolver();
        
        assertThat(resolver.getEra(MSLocale.US).isUnkndown(), is(true));
        
        assertThat(resolver.getEra(MSLocale.JAPANESE).isUnkndown(), is(false));
        
    }
    
    @Test
    public void testPeriod() {
        
        EraResolver resolver = new EraResolver();
        
        Era era = resolver.getEra(MSLocale.JAPANESE);
        
        Date date = null;
        EraPeriod period = null;
        
        // 存在する期間
        date = Timestamp.valueOf("2015-01-01 00:00:00.000");
        assertThat(era.contains(date), is(true));
        
        period = era.getTargetPeriod(date);
        assertThat(period.isUnknown(), is(false));
        
        // 存在しない期間
        date = Timestamp.valueOf("1850-01-01 00:00:00.000");
        assertThat(era.contains(date), is(false));
        
        period = era.getTargetPeriod(date);
        assertThat(period.isUnknown(), is(true));
        
    }
}
