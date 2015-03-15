package com.github.mygreen.cellformatter.lang;

import java.util.Locale;


/**
 * {@link MSLocale}のビルダクラス。
 * @author T.TSUCHIE
 *
 */
public class MSLocaleBuilder {
    
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
    
    private MSLocaleBuilder() {
        
    }
    
    /**
     * ビルダのインスタンスの取得
     * @return
     */
    public static MSLocaleBuilder create() {
        return new MSLocaleBuilder();
    }
    
    /**
     * {@link MSLocale}のインスタンスを組み立てる。
     * @return
     */
    public MSLocale build() {
        
        final MSLocale msLocale = new MSLocale();
        
        msLocale.setCode(code);
        msLocale.setValue(value);
        msLocale.setLanguage(language);
        msLocale.setCountry(country);
        msLocale.setName(name);
        msLocale.setLocale(locale);
        
        return msLocale;
    }
    
    public MSLocaleBuilder code(final String code) {
        this.code = code;
        return this;
    }
    
    public MSLocaleBuilder value(final int value) {
        this.value = value;
        return this;
    }
    
    public MSLocaleBuilder language(final String language) {
        this.language = language;
        return this;
    }
    
    public MSLocaleBuilder country(final String country) {
        this.country = country;
        return this;
    }
    
    public MSLocaleBuilder name(final String name) {
        this.name = name;
        return this;
    }
    
    public MSLocaleBuilder locale(final Locale locale) {
        this.locale = locale;
        return this;
    }
    
}
