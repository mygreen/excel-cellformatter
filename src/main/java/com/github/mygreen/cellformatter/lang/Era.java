package com.github.mygreen.cellformatter.lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 時代情報の定義用のクラス。
 * <p>複数の期間{@link EraPeriod}を持つ。
 * 
 * @version 0.5
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class Era {
    
    /**
     * 不明な時代情報の場合
     */
    public static final Era UNKNOWN_ERA = new UnknownEra();
    
    private final List<EraPeriod> periods;
    
    public Era(final List<EraPeriod> periods) {
        ArgUtils.notNull(periods, "periods");
        
        this.periods = Collections.unmodifiableList(periods);
    }
    
    /**
     * 指定した日時が時代情報に含まれているかどうか。
     * @param date
     * @return
     */
    public boolean contains(final Date date) {
        ArgUtils.notNull(date, "date");
        
        for(EraPeriod period : periods) {
            if(period.contains(date)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 指定した日時に該当する期間情報を取得する。
     * @param date
     * @return 見つからない場合は、存在しない期間を示すクラス{@link EraPeriod.UnknownPeriod}のインスタンスを返す。
     */
    public EraPeriod getTargetPeriod(final Date date) {
        ArgUtils.notNull(date, "date");
        
        for(EraPeriod period : periods) {
            if(period.contains(date)) {
                return period;
            }
        }
        
        return EraPeriod.UNKNOWN_PERIOD;
    }
    
    /**
     * 不明な時代情報かどうか。
     * @return
     */
    public boolean isUnkndown() {
        return false;
    }
    
    /**
     * 不明な時代を表すクラス。
     * <p>ロケールに対して時代情報が存在しない場合に使用する。
     *
     */
    public static class UnknownEra extends Era {
        
        public UnknownEra() {
            super(new ArrayList<EraPeriod>());
        }
        
        @Override
        public boolean isUnkndown() {
            return true;
        }
        
    }
    
}
