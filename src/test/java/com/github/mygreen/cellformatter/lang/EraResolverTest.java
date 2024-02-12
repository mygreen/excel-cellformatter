package com.github.mygreen.cellformatter.lang;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.Date;

import org.junit.Test;

/**
 * {@link EraResolver}のテスタ
 *
 * @version 0.11
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

        {
            // 存在する期間
            Date date = ExcelDateUtils.parseDate("2015-01-01 00:00:00.000");
            assertThat(era.contains(date), is(true));

            EraPeriod period = era.getTargetPeriod(date);
            assertThat(period.isUnknown(), is(false));
        }

        {
            // 存在しない期間
            Date date = ExcelDateUtils.parseDate("1850-01-01 00:00:00.000");
            assertThat(era.contains(date), is(false));

            EraPeriod period = era.getTargetPeriod(date);
            assertThat(period.isUnknown(), is(true));
        }

    }

    /**
     * ユーザのクラスパスのルートに配置している定義を読み込む
     * <p>2099-12-31で令和が終わる定義</p>
     */
    @Test
    public void testUserSetting() {

        EraResolver resolver = new EraResolver();

        Era era = resolver.getEra(MSLocale.JAPANESE);

        {
            // 令和の終わり
            Date date = ExcelDateUtils.parseDate("2089-12-31 23:59:59.999");
            EraPeriod period = era.getTargetPeriod(date);

            assertThat(period.getName(), is("令和"));
            assertThat(period.getEndDate(), is(not(nullValue())));

        }

        {
            // 令和の次の仮号の場合
            Date date = ExcelDateUtils.parseDate("2090-01-01 00:00:00.000");
            EraPeriod period = era.getTargetPeriod(date);

            assertThat(period.getName(), is("仮号"));
            assertThat(period.getEndDate(), is(nullValue()));
        }

    }
}
