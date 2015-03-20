package com.github.mygreen.cellformatter.term;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mygreen.cellformatter.lang.EraPeriod;
import com.github.mygreen.cellformatter.lang.MSLocale;
import com.github.mygreen.cellformatter.lang.Utils;


/**
 * 日時の書式の項
 * @author T.TSUCHIE
 *
 */
public abstract class DateTerm implements Term<Calendar> {
    
    protected static final Logger logger = LoggerFactory.getLogger(DateTerm.class);
    
    /**
     * 指定した桁分、ゼロサプライ（ゼロ埋め）する。
     * <p>既に指定したサイズを超える桁数の場合は、何もしない。
     * <p>nullや空文字の場合は、空文字を返す。
     * @param str 対象の文字
     * @param size 桁数
     * @return ゼロサプライした文字列
     */
    private static String supplyZero(final String str, final int size) {
        
        if(str == null) {
            return "";
        }
        final int length = str.length();
        if(length > size) {
            return str;
        }
        
        StringBuilder sb = new StringBuilder();
        final int appendSize = size - length;
        for(int i=0; i < appendSize; i++) {
            sb.append("0");
        }
        sb.append(str);
        
        return sb.toString();
        
    }
    
    /**
     * [h] - 24時を超える経過時間の処理
     */
    public static DateTerm elapsedHour(final String format) {
        return new ElapsedHourTerm(format);
    }
    
    
    /**
     * [m] - 60分を超える経過時間の処理
     */
    public static DateTerm elapsedMinute(final String format) {
        return new ElapsedMinuteTerm(format);
    }
    
    /**
     * [s] - 60秒を超える経過時間の処理
     */
    public static DateTerm elapsedSecond(final String format) {
        return new ElapsedSecondTerm(format);
    }
    
    /**
     * y - 年の場合の処理
     */
    public static DateTerm year(final String format) {
        return new YearTerm(format);
    }
    
    /**
     * g - 元号の名称の場合の処理
     */
    public static DateTerm eraName(final String format) {
        return new EraNameTerm(format);
    }
    
    /**
     * e - 元号の年の場合の処理
     */
    public static DateTerm eraYear(final String format) {
        return new EraYearTerm(format);
    }
    
    /**
     * r - 元号の名称と年の場合の処理
     */
    public static DateTerm eraNameYear(final String format) {
        return new EraNameYearTerm(format);
    }
    
    /**
     * m - 月の場合の処理
     */
    public static DateTerm month(final String format) {
        return new MonthTerm(format);
    }
    
    /**
     * d - 日の場合の処理
     */
    public static DateTerm day(final String format) {
        return new DayTerm(format);
    }
    
    /**
     * aまたはn - 曜日の名称場合の処理
     */
    public static DateTerm weekName(final String format) {
        return new WeekName(format);
    }
    
    /**
     * ww - 年の週番号
     */
    public static DateTerm weekNumber(final String format) {
        return new WeekNumberTerm(format);
    }
    
    /**
     * h - 時間の場合の処理
     * @param format
     * @param half 12時間表示かどうか
     */
    public static DateTerm hour(final String format, final boolean half) {
        return new HourTerm(format, half);
    }
    
    /**
     * m - 分の場合の処理
     */
    public static DateTerm minute(final String format) {
        return new MinuteTerm(format);
    }
    
    /**
     * s - 秒の場合の処理
     */
    public static DateTerm second(final String format) {
        return new SecondTerm(format);
    }
    
    /**
     * q - 四半期の場合の処理
     */
    public static DateTerm quater(final String format) {
        return new QuaterTerm(format);
    }
    
    /**
     * AM/PM - 午前／午後の場合の処理
     */
    public static DateTerm amPm(final String format) {
        return new AmPmTerm(format);
    }
    
    /**
     * [h] - 24時を超える経過時間の処理
     */
    public static class ElapsedHourTerm extends DateTerm {
        
        /** ミリ秒を時間に直すための基底 */
        private static final long BASE = 1000*60*60;
        
        private final String format;
        
        public ElapsedHourTerm(final String format) {
            this.format = format;
        }
        
        @Override
        public String format(final Calendar cal, final MSLocale formatLocale, final Locale runtimeLocale) {
            
            final long zeroTime = Utils.getExcelZeroDate(cal.getTime());
            if(logger.isInfoEnabled()) {
                logger.info("ElapsedHour:calendar={}, zeroTime={}.", Utils.formatDate(cal.getTime()), Utils.formatDate(new Date(zeroTime)));
            }
            
            final int formatLength = format.length();
            final long time = (long) ((cal.getTime().getTime() - zeroTime) / BASE);
            return supplyZero(String.valueOf(time), formatLength);
        }
        
        public String getFormat() {
            return format;
        }
        
    }
    
    /**
     * [m] - 60分を超える経過時間の処理
     */
    public static class ElapsedMinuteTerm extends DateTerm {
        
        /** ミリ秒を分に直すための基底 */
        private static final long BASE = 1000*60;
        
        private final String format;
        
        public ElapsedMinuteTerm(final String format) {
            this.format = format;
        } 
        
        @Override
        public String format(final Calendar cal, final MSLocale formatLocale, final Locale runtimeLocale) {
            
            final long zeroTime = Utils.getExcelZeroDate(cal.getTime());
            if(logger.isInfoEnabled()) {
                logger.info("ElapsedMinute:calendar={}, zeroTime={}.", Utils.formatDate(cal.getTime()), Utils.formatDate(new Date(zeroTime)));
            }
            
            final int formatLength = format.length();
            final long time = (long) ((cal.getTime().getTime() - zeroTime) / BASE);
            return supplyZero(String.valueOf(time), formatLength);
        }
        
        public String getFormat() {
            return format;
        }
        
    }
    
    /**
     * [s] - 60秒を超える経過時間の処理
     */
    public static class ElapsedSecondTerm extends DateTerm {
        
        /** ミリ秒を秒に直すための基底 */
        private static final long BASE = 1000;
        
        private final String format;
        
        public ElapsedSecondTerm(final String format) {
            this.format = format;
        } 
        
        @Override
        public String format(final Calendar cal, final MSLocale formatLocale, final Locale runtimeLocale) {
            
            final long zeroTime = Utils.getExcelZeroDate(cal.getTime());
            if(logger.isInfoEnabled()) {
                logger.info("ElapsedSecond:calendar={}, zeroTime={}.", Utils.formatDate(cal.getTime()), Utils.formatDate(new Date(zeroTime)));
            }
            
            final int formatLength = format.length();
            final long time = (long) ((cal.getTime().getTime() - zeroTime) / BASE);
            return supplyZero(String.valueOf(time), formatLength);
        }
        
        public String getFormat() {
            return format;
        }
        
    }
    
    /**
     * y - 年の場合
     */
    public static class YearTerm extends DateTerm {
        
        private final String format;
        
        public YearTerm(final String format) {
            this.format = format;
        } 
        
        @Override
        public String format(final Calendar cal, final MSLocale formatLocale, final Locale runtimeLocale) {
            
            final String value = String.valueOf(cal.get(Calendar.YEAR));
            final int formatLength = format.length();
            
            // 2桁、4桁補正する
            if(formatLength <= 2) {
                return supplyZero(value, 2).substring(2);
            } else {
                return supplyZero(value, 4);
            }
        }
        
        public String getFormat() {
            return format;
        }
        
    }
    
    /**
     * g - 元号の名称の場合
     */
    public static class EraNameTerm extends DateTerm {
        
        private final String format;
        
        public EraNameTerm(final String format) {
            this.format = format;
        } 
        
        @Override
        public String format(final Calendar cal, final MSLocale formatLocale, final Locale runtimeLocale) {
            
            final Date date = cal.getTime();
            
            EraPeriod period = null;
            for(EraPeriod p : EraPeriod.PERIODS) {
                if(p.contains(date)) {
                    period = p;
                    break;
                }
            }
            
            if(period == null) {
                return "";
            }
            
            final int formatLength = format.length();
            if(formatLength == 1) {
                return period.getAbbrevRomanName();
                
            } else if(formatLength == 2) {
                return period.getAbbrevName();
                
            } else {
                return period.getName();
            }
        }
        
        public String getFormat() {
            return format;
        }
        
    }
    
    /**
     * e - 元号の年の場合
     */
    public static class EraYearTerm extends DateTerm {
        
        private final String format;
        
        public EraYearTerm(final String format) {
            this.format = format;
        } 
        
        @Override
        public String format(final Calendar cal, final MSLocale formatLocale, final Locale runtimeLocale) {
            
            final Date date = cal.getTime();
            final int formatLength = format.length();
            
            EraPeriod period = null;
            for(EraPeriod p : EraPeriod.PERIODS) {
                if(p.contains(date)) {
                    period = p;
                    break;
                }
            }
            
            // 不明な場合
            if(period == null) {
                String value = String.valueOf(cal.get(Calendar.YEAR));
                return supplyZero(value, formatLength);
                
            }
            
            final String value = String.valueOf(period.getEraYear(cal));
            return supplyZero(value, formatLength);
            
        }
        
        public String getFormat() {
            return format;
        }
        
    }
    
    /**
     * r - 元号の名称と年の場合
     */
    public static class EraNameYearTerm extends DateTerm {
        
        private final String format;
        
        public EraNameYearTerm(final String format) {
            this.format = format;
        } 
        
        @Override
        public String format(final Calendar cal, final MSLocale formatLocale, final Locale runtimeLocale) {
            
            final Date date = cal.getTime();
            final int formatLength = format.length();
            
            EraPeriod period = null;
            for(EraPeriod p : EraPeriod.PERIODS) {
                if(p.contains(date)) {
                    period = p;
                    break;
                }
            }
            
            if(period == null) {
                String value = String.valueOf(cal.get(Calendar.YEAR));
                return supplyZero(value, formatLength);
                
            }
            
            StringBuilder sb = new StringBuilder();
            
            // 元号の組み立て（2桁以上の時に元号を追加）
            if(formatLength >= 2) {
                sb.append(period.getName());
            }
            
            // 年の組み立て
            final String strYear = String.valueOf(period.getEraYear(cal));
            sb.append(supplyZero(strYear, 2));
            
            return sb.toString();
        }
        
        public String getFormat() {
            return format;
        }
        
    }
    
    /**
     * m - 月の場合
     */
    public static class MonthTerm extends DateTerm {
        
        private final String format;
        
        static final String[][] MONTH_NAMES = {
            {"January", "Jan", "J"},
            {"February", "Feb", "F"},
            {"March", "Mar", "M"},
            {"April", "Apr", "A"},
            {"May", "May", "M"},
            {"June", "Jun", "J"},
            {"July", "Jul", "J"},
            {"August", "Aug", "A"},
            {"September", "Sep", "S"},
            {"October", "Oct", "O"},
            {"November", "Nov", "N"},
            {"December", "Dec", "D"},
            
        };
        
        public MonthTerm(final String format) {
            this.format = format;
        } 
        
        @Override
        public String format(final Calendar cal, final MSLocale formatLocale, final Locale runtimeLocale) {
            
            final int value = cal.get(Calendar.MONTH);
            final int formatLength = format.length();
            
            if(formatLength == 1) {
                return String.valueOf(value + 1);
                
            } else if(formatLength == 2) {
                return supplyZero(String.valueOf(value + 1), 2);
                
            } else if(formatLength == 3) {
                // 月名の先頭3文字
                return MONTH_NAMES[value][1];
                
            } else if(formatLength == 4) {
                // 月名
                return MONTH_NAMES[value][0];
                
            } else if(formatLength == 5) {
                // 月名の先頭1文字
                return MONTH_NAMES[value][2];
                
            } else {
                return supplyZero(String.valueOf(value + 1), 2);
            }
        }
        
        public String getFormat() {
            return format;
        }
        
    }
    
    /**
     * 曜日のインデックスを取得する。
     * ・日曜始まりで、0から始まる。
     * @param cal
     * @return
     */
    private static int getWeekIndex(final Calendar cal) {
        final int val = cal.get(Calendar.DAY_OF_WEEK);
        switch(val) {
            case Calendar.SUNDAY:
                return 0;
            case Calendar.MONDAY:
                return 1;
            case Calendar.TUESDAY:
                return 2;
            case Calendar.WEDNESDAY:
                return 3;
            case Calendar.THURSDAY:
                return 4;
            case Calendar.FRIDAY:
                return 5;
            case Calendar.SATURDAY:
                return 6;
        }
        
        return 0;
    }
    
    /**
     * d - 日の場合
     * ・dが3～4桁の場合は、英字の曜日。
     */
    public static class DayTerm extends DateTerm {
        
        private final String format;
        
        static final String[][] WEEK_NAMES = {
            {"Sunday", "Sun"},
            {"Monday", "Mon"},
            {"Tuesday", "Tue"},
            {"Wednesday", "Wed"},
            {"Thursday", "Thu"},
            {"Friday", "Fri"},
            {"Saturday", "Sat"},
        };
        
        public DayTerm(final String format) {
            this.format = format;
        } 
        
        @Override
        public String format(final Calendar cal, final MSLocale formatLocale, final Locale runtimeLocale) {
            
            final int value = cal.get(Calendar.DAY_OF_MONTH);
            final int formatLength = format.length();
            
            if(formatLength == 1) {
                return String.valueOf(value);
                
            } else if(formatLength == 2) {
                return supplyZero(String.valueOf(value), 2); 
                
            } else if(formatLength == 3) {
                // 曜日の省略名
                final int index = getWeekIndex(cal);
                if(formatLocale == MSLocale.JAPANESE) {
                    return WeekName.WEEK_NAMES[index][1];
                } else {
                    return WEEK_NAMES[index][1];
                }
                
            } else if(formatLength >= 4) {
                // 曜日の正式名
                final int index = getWeekIndex(cal);
                if(formatLocale == MSLocale.JAPANESE) {
                    return WeekName.WEEK_NAMES[index][0];
                } else {
                    return WEEK_NAMES[index][0];
                }
                
            } else {
               return supplyZero(String.valueOf(value), 2); 
            }
            
        }
        
        public String getFormat() {
            return format;
        }
        
    }
    
    /**
     * aまたはn - 日本語名称の曜日の場合
     * 
     */
    public static class WeekName extends DateTerm {
        
        private final String format;
        
        static final String[][] WEEK_NAMES = {
            {"日曜日", "日"},
            {"月曜日", "月"},
            {"火曜日", "火"},
            {"水曜日", "水"},
            {"木曜日", "木"},
            {"金曜日", "金"},
            {"土曜日", "土"},
        };
        
        public WeekName(final String format) {
            this.format = format;
        } 
        
        @Override
        public String format(final Calendar cal, final MSLocale formatLocale, final Locale runtimeLocale) {
            
            final int index = getWeekIndex(cal);
            final int formatLength = format.length();
            
            if(Utils.startsWithIgnoreCase(format, "n")) {
                // Libreの時
                if(formatLength == 1) {
                    return WEEK_NAMES[index][1];
                } else {
                    return WEEK_NAMES[index][0];
                }
            } else {
                if(formatLength <= 3) {
                    return WEEK_NAMES[index][1];
                } else {
                    return WEEK_NAMES[index][0];
                }
            }
        }
        
        public String getFormat() {
            return format;
        }
        
    }
    
    /**
     * ww - 年の週番号を取得する。
     * 
     */
    public static class WeekNumberTerm extends DateTerm {
        
        private final String format;
        
        public WeekNumberTerm(final String format) {
            this.format = format;
        } 
        
        @Override
        public String format(final Calendar cal, final MSLocale formatLocale, final Locale runtimeLocale) {
            
            
            if(format.equalsIgnoreCase("ww")) {
                final int val = cal.get(Calendar.WEEK_OF_YEAR);;
                return String.valueOf(val);
                
            } else {
                return format;
            }
           
        }
        
        public String getFormat() {
            return format;
        }
        
    }
    
    /**
     * h - 時間の場合
     */
    public static class HourTerm extends DateTerm {
        
        private final String format;
        
        /**
         * 12時間表示かどうか。
         */
        private final boolean half;
        
        public HourTerm(final String format, final boolean half) {
            this.format = format;
            this.half = half;
        } 
        
        @Override
        public String format(final Calendar cal, final MSLocale formatLocale, final Locale runtimeLocale) {
            
            final String value;
            if(isHalf()) {
                value = String.valueOf(cal.get(Calendar.HOUR));
            } else {
                value = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
            }
            
            final int formatLength = format.length();
            return supplyZero(value, formatLength);
            
        }
        
        public boolean isHalf() {
            return half;
        }
        
        public String getFormat() {
            return format;
        }
        
    }
    
    /**
     * m - 分の場合
     */
    public static class MinuteTerm extends DateTerm {
        
        private final String format;
        
        public MinuteTerm(final String format) {
            this.format = format;
        } 
        
        @Override
        public String format(final Calendar cal, final MSLocale formatLocale, final Locale runtimeLocale) {
            
            final String value = String.valueOf(cal.get(Calendar.MINUTE));
            final int formatLength = format.length();
            return supplyZero(value, formatLength);
            
        }
        
        public String getFormat() {
            return format;
        }
        
    }
    
    /**
     * s - 秒の場合
     */
    public static class SecondTerm extends DateTerm {
        
        private final String format;
        
        public SecondTerm(final String format) {
            this.format = format;
        } 
        
        @Override
        public String format(final Calendar cal, final MSLocale formatLocale, final Locale runtimeLocale) {
            
            final String value = String.valueOf(cal.get(Calendar.SECOND));
            final int formatLength = format.length();
            return supplyZero(value, formatLength);
            
        }
        
        public String getFormat() {
            return format;
        }
        
    }
    
    /**
     * AM/PM - 午前/午後の場合
     */
    public static class AmPmTerm extends DateTerm {
        
        private final String format;
        
        private final String am;
        
        private final String pm;
        
        public AmPmTerm(final String format) {
            this.format = format;
            
            String[] split = format.split("/");
            if(split.length >= 2) {
                this.am = split[0];
                this.pm = split[1];
            } else {
                this.am = "AM";
                this.pm = "PM";
            }
        } 
        
        @Override
        public String format(final Calendar cal, final MSLocale formatLocale, final Locale runtimeLocale) {
            
            final int val = cal.get(Calendar.AM_PM);
            if(formatLocale == MSLocale.JAPANESE) {
                return val == Calendar.AM ? "午前" : "午後";
                
            } else {
                return val == Calendar.AM ? am : pm;
                
            }
            
        }
        
        public String getFormat() {
            return format;
        }
        
        public String getAm() {
            return am;
        }
        
        public String getPm() {
            return pm;
        }
        
    }
    
    /**
     * q - 四半期の場合
     */
    public static class QuaterTerm extends DateTerm {
        
        private final String format;
        
        static final String QUATER_NAMES[][] = {
            {"1st quater", "Q1"}, 
            {"2nd quater", "Q2"}, 
            {"3rd quater", "Q3"}, 
            {"4th quater", "Q4"}, 
        };
        
        static final String QUATER_NAMES_JA[] = {
            "第１四半期",
            "第２四半期",
            "第３四半期",
            "第４四半期",
        };
        
        public QuaterTerm(final String format) {
            this.format = format;
        } 
        
        @Override
        public String format(final Calendar cal, final MSLocale formatLocale, final Locale runtimeLocale) {
            
            final int index = cal.get(Calendar.MONTH) / 3;
            
            if(format.length() == 1) {
                return QUATER_NAMES[index][1];
                
            } else if(isJapaneseLocale(formatLocale, runtimeLocale)) {
                return QUATER_NAMES_JA[index];
                
            } else {
                return QUATER_NAMES[index][0];
            }
            
        }
        
        /**
         * 日本語のロケールかチェックする
         * @param formatLocale 書式上のロケール
         * @param runtimeLocale 実行時のロケール
         * @return
         */
        private boolean isJapaneseLocale(final MSLocale formatLocale, final Locale runtimeLocale) {
            
            if(formatLocale == MSLocale.JAPANESE) {
                return true;
                
            } else if(runtimeLocale != null && runtimeLocale.getLanguage().equals("ja")) {
                return true;
            }
            
            return false;
        }
        
        public String getFormat() {
            return format;
        }
        
    }
}
