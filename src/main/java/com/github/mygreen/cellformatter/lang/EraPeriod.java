package com.github.mygreen.cellformatter.lang;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;


/**
 * 平成などの時代を表すクラス。
 * @author T.TSUCHIE
 */
public class EraPeriod {
    
    /**
     * 不明な場合
     */
    public static final EraPeriod UNKNOWN = new EraPeriod() {
        {
            setAbbrevRomanName("U");
            setAbbrevName("不");
            setName("不明");
        }
    };
    
    /** 明治の定義 */
    public static final EraPeriod MEIJI = new EraPeriod() {
        {
            setAbbrevRomanName("M");
            setAbbrevName("明");
            setName("明治");
            setStartDate(Timestamp.valueOf("1868-01-25 00:00:00.000"));
            setEndDate(Timestamp.valueOf("1912-07-29 23:59:59.999"));
            
        }
    };
    
    /** 大正の定義 */
    public static final EraPeriod TAISYO = new EraPeriod() {
        {
            setAbbrevRomanName("T");
            setAbbrevName("大");
            setName("大正");
            setStartDate(Timestamp.valueOf("1912-07-30 00:00:00.000"));
            setEndDate(Timestamp.valueOf("1926-12-24 23:59:59.999"));
            
        }
    };
    
    /** 昭和の定義 */
    public static final EraPeriod SYOWA = new EraPeriod() {
        {
            setAbbrevRomanName("S");
            setAbbrevName("昭");
            setName("昭和");
            setStartDate(Timestamp.valueOf("1926-12-25 00:00:00.000"));
            setEndDate(Timestamp.valueOf("1989-01-07 23:59:59.999"));
            
        }
    };
    
    /** 平成の定義 */
    public static final EraPeriod HEISEI = new EraPeriod() {
        {
            setAbbrevRomanName("H");
            setAbbrevName("平");
            setName("平成");
            setStartDate(Timestamp.valueOf("1989-01-08 00:00:00.000"));
            
            // 終了日がないため、無期限とする
            setEndDate(null);
        }
    };
            
    /** 元号の定義 */
    public static final List<EraPeriod> PERIODS = Collections.unmodifiableList(Arrays.asList(
            MEIJI, TAISYO, SYOWA, HEISEI));
    
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
     * @param date
     * @return true:この時代に含まれている。
     * @throws IllegalArgumentException date is null.
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
     * @param cal
     * @return
     * @throws IllegalArgumentException date is null.
     */
    public int getEraYear(final Calendar cal) {
        ArgUtils.notNull(cal, "cal");
        
        final Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate);
        
        final int diff = cal.get(Calendar.YEAR) - startCal.get(Calendar.YEAR) + 1;
        return diff;
        
    }
    
    @Override
    public String toString() {
        
        StringBuilder sb = new StringBuilder();
        sb.append(EraPeriod.class.getSimpleName()).append("[").append(getName()).append("]");
        if(startDate != null) {
            sb.append("[start=").append(Utils.formatDate(startDate)).append("]");
        }
        
        if(endDate != null) {
            sb.append("[end=").append(Utils.formatDate(endDate)).append("]");
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
     * 元号の日本語名の省略名を取得する。
     * @return
     */
    public String getAbbrevName() {
        return abbrevName;
    }
    
    /**
     * 元号の日本語名の省略名を設定する。
     * @return
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
    
    void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    
    void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    
}
