package com.github.mygreen.cellformatter.lang;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


/**
 * 平成などの時代の期間を表すクラス。
 * 
 * @version 0.5
 * @author T.TSUCHIE
 */
public class EraPeriod {
    
    /**
     * 不明な時代の期間を示すクラス。
     * <p>存在しない期間を示すにも使用する。
     * @since 0.5
     */
    public static final EraPeriod UNKNOWN_PERIOD = new EraPeriod() {
        
        @Override
        public boolean isUnknown() {
            return true;
        }
        
    };
            
    
    /** 省略時のローマ字名 （例：H）*/
    private String abbrevRomanName;
    
    /** 省略時の名称（例：平） */
    private String abbrevName;
    
    /** 正式名称（例：平成） */
    private String name;
    
    /** 開始日時（期限がない場合は、nullを設定する） */
    private Date startDate;
    
    /** 終了日時（期限がない場合は、nullを設定する） */
    private Date endDate;
    
    /**
     * 指定した日時が含まれているかどうか。
     * @param date チェック対象の日時。タイムゾーンは、{@literal GMT-00:00}である必要がある。
     * @return true:この時代に含まれている。
     * @throws IllegalArgumentException {@literal date == null.}
     */
    public boolean contains(final Date date) {
        
        ArgUtils.notNull(date, "date");
        
        if(startDate == null) {
            return endDate.compareTo(date) >= 0;
            
        } else if(endDate == null) {
            return startDate.compareTo(date) <= 0;
            
        } else {
            return (endDate.compareTo(date) >= 0) && (startDate.compareTo(date) <= 0);
        }
        
    }
    
    /**
     * 指定した日時が、開始日時から経過した年を取得する。
     * @param cal チェック対象の日時。タイムゾーンは、{@literal GMT-00:00}である必要がある。
     * @return
     * @throws IllegalArgumentException {@literal cal == null.}
     */
    public int getEraYear(final Calendar cal) {
        ArgUtils.notNull(cal, "cal");
        
        final Calendar startCal = Calendar.getInstance(TimeZone.getTimeZone("GMT-00:00"));
        startCal.setTime(startDate);
        
        final int diff = cal.get(Calendar.YEAR) - startCal.get(Calendar.YEAR) + 1;
        return diff;
        
    }
    
    @Override
    public String toString() {
        
        StringBuilder sb = new StringBuilder();
        sb.append(EraPeriod.class.getSimpleName()).append("[").append(getName()).append("]");
        if(startDate != null) {
            sb.append("[start=").append(ExcelDateUtils.formatDate(startDate)).append("]");
        }
        
        if(endDate != null) {
            sb.append("[end=").append(ExcelDateUtils.formatDate(endDate)).append("]");
        }
        
        return sb.toString();
        
    }
    
    /**
     * 元号のローマ字の省略名を取得する。
     * @return
     */
    public String getAbbrevRomanName() {
        return abbrevRomanName;
    }
    
    /**
     * 元号のローマ字の省略名を設定する。
     * @param abbrevRomanName
     */
    void setAbbrevRomanName(String abbrevRomanName) {
        this.abbrevRomanName = abbrevRomanName;
    }
    
    /**
     * 元号のロケールに対する省略名を取得する。
     * 
     * @return 日本語の場合、「平成」だと「平」の値。
     */
    public String getAbbrevName() {
        return abbrevName;
    }
    
    /**
     * 元号のロケールに対する省略名を設定する。
     * @param abbrevName ロケールに対する省略名
     */
    void setAbbrevName(String abbrevName) {
        this.abbrevName = abbrevName;
    }
    
    /**
     * 元号の名称を取得する
     * @return
     */
    public String getName() {
        return name;
    }
    
    /**
     * 元号の名称を設定する
     * @return
     */
    void setName(String name) {
        this.name = name;
    }
    
    /**
     * 開始日を取得する
     * @return
     */
    public Date getStartDate() {
        return startDate;
    }
    
    /**
     * 開始日を設定する
     * @param startDate
     */
    void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    
    /**
     * 終了日を設定する
     * @param endDate
     */
    void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    
    /**
     * 終了日を取得する
     * @return 終了日がない現行の元号の場合は{@literal null}を返す。
     */
    public Date getEndDate() {
        return endDate;
    }
    
    /**
     * 不明な期間かどうか。
     * @since 0.5
     * @return {@link #UNKNOWN_PERIOD}のインスタンスのとき、trueを返す。
     */
    public boolean isUnknown() {
        return false;
    }
    
}
