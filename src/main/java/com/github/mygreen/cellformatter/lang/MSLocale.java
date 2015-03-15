package com.github.mygreen.cellformatter.lang;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * Microsoftで使用しているロケール。
 * <p>インスタンスを作成する際には、{@link MSLocaleBuilder}を使用する。
 * <p><a href="https://msdn.microsoft.com/ja-jp/library/cc392381.aspx">ロケール ID (LCID) の一覧</a>
 * <p><a href="https://msdn.microsoft.com/ja-jp/goglobal/bb964664.aspx">Locale IDs Assigned by Microsoft</a>
 * 
 * @author T.TSUCHIE
 *
 */
public class MSLocale {
    
    /** 日本語 */
    public static final MSLocale JAPANESE = MSLocaleBuilder.create().code("ja").value(1041).language("Japanese")
            .country("Japan").name("日本語").locale(Locale.JAPANESE).build();
    
    /** 英語 (米国) */
    public static final MSLocale US = MSLocaleBuilder.create().code("en-us").value(1033).language("English")
            .country("United States").name("英語 (米国)").locale(Locale.US).build();
    
    /** 英語 (英国) */
    public static final MSLocale UK = MSLocaleBuilder.create().code("en-gb").value(2057).language("English")
            .country("United Kingdom").name("英語 (英国)").locale(Locale.UK).build();
    
    /**
     * 定義されている有効なロケール
     */
    public static List<MSLocale> KNOWN_LOCALES;
    static {
        KNOWN_LOCALES = Collections.unmodifiableList(new CopyOnWriteArrayList<MSLocale>(
                Arrays.asList(JAPANESE, US, UK)));
    }
    
    /**
     * 10 進数のコードを指定して既知のロケールを取得する。
     * @param value 
     * @return 変換できない場合は、nullを返す。
     */
    public static MSLocale valueOfKnwonValue(final int value) {
        for(MSLocale locale : KNOWN_LOCALES) {
            if(locale.getValue() == value) {
                return locale;
            }
        }
        
        return null;
    }
    
    /** 言語コード(2桁) */
    private String code;
    
    /** 10進値 */
    private int value;
    
    /** 言語名 */
    private String language;
    
    /** 国 / 地域 */
    private String country;
    
    /** 名称 */
    private String name;
    
    /** Javaのロケール */
    private Locale locale;
    
    /** 16進値(10進数を元に設定する) */
    private String hexValue;
    
    public String getCode() {
        return code;
    }
    
    void setCode(String code) {
        this.code = code;
    }
    
    public int getValue() {
        return value;
    }
    
    void setValue(int value) {
        this.value = value;
        
        // 16進数の設定を行う
        this.hexValue = Integer.toHexString(value).toUpperCase();
    }
    
    /**
     * システムの日付用のロケールの場合。
     * <p>16進数の値が'F800'のとき。
     * @return
     */
    public boolean isSystemDate() {
        return getHexValue().equalsIgnoreCase("F800");
    }
    
    /**
     * システムの時刻用のロケールの場合。
     * <p>16進数の値が'F400'のとき。
     * @return
     */
    public boolean isSystemTime() {
        return getHexValue().equalsIgnoreCase("F400");
    }
    
    public String getHexValue() {
        return this.hexValue;
    }
    
    public String getLanguage() {
        return language;
    }
    
    void setLanguage(String language) {
        this.language = language;
    }
    
    public String getCountry() {
        return country;
    }
    
    void setCountry(String country) {
        this.country = country;
    }
    
    public String getName() {
        return name;
    }
    
    void setName(String name) {
        this.name = name;
    }
    
    public Locale getLocale() {
        return locale;
    }
    
    void setLocale(Locale locale) {
        this.locale = locale;
    }
    
}
