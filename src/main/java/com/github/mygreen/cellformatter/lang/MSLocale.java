package com.github.mygreen.cellformatter.lang;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Microsoftで使用しているロケールを表現するクラス。
 * <ul>
 *  <li><a href="https://msdn.microsoft.com/ja-jp/library/cc392381.aspx" target="_blank">ロケール ID (LCID) の一覧</a></li>
 *  <li><a href="https://msdn.microsoft.com/ja-jp/goglobal/bb964664.aspx" target="_blank">Locale IDs Assigned by Microsoft</a></li>
 * </ul>
 * 
 * @version 0.5
 * @author T.TSUCHIE
 *
 */
public class MSLocale {
    
    private static final MessageResolver messageResolver = new MessageResolver("com.github.mygreen.cellformatter.locale");
    
    /**
     * 既知（プロパティファイル）に定義したのロケールはキャッシュする。
     * ・キー：id（10進数）
     * ・値：既知のロケール。
     */
    private static Map<Integer, MSLocale> KNOWN_LOCALES = new ConcurrentHashMap<>();
    
    /** 日本語 */
    public static final MSLocale JAPANESE = MSLocale.createKnownLocale(0x0411);
    
    /** 英語 (米国) */
    public static final MSLocale US = MSLocale.createKnownLocale(0x0409);
    
    /** 英語 (英国) */
    public static final MSLocale UK = MSLocale.createKnownLocale(0x0809);
    
    /** 英語 (カナダ) */
    public static final MSLocale CANADA = MSLocale.createKnownLocale(0x2809);
    
    /** ドイツ語 */
    public static final MSLocale GERMAN = MSLocale.createKnownLocale(0x0407);
    
    /** フランス語 */
    public static final MSLocale FRENCE = MSLocale.createKnownLocale(0x040C);
    
    /** フランス語（カナダ） */
    public static final MSLocale CANADA_FRENCH = MSLocale.createKnownLocale(0x0C0C);
    
    /** イタリア語 */
    public static final MSLocale ITALY = MSLocale.createKnownLocale(0x0410);
    
    /** 韓国語 */
    public static final MSLocale KOREA = MSLocale.createKnownLocale(0x0412);
    
    /** 中国語（中華人民共和国） */
    public static final MSLocale PRC = MSLocale.createKnownLocale(0x0804);
    
    /** 中国語（台湾） */
    public static final MSLocale TAIWAN = MSLocale.createKnownLocale(0x0404);
    
    /** ID - 10進値 */
    private final int id;
    
    /** ID - 16進値(10進数を元に設定する) */
    private final String hexId;
    
    /** 言語コード */
    private String code;
    
    /** 言語名 */
    private String language;
    
    /** 国 / 地域 */
    private String country;
    
    /** 名称 */
    private String name;
    
    /** Javaのロケール */
    private Locale locale;
    
    /**
     * 既知のIDかどうか。
     * <p>プロパティファイルに定義されているかで確認する。
     * @since 0.5
     * @param id ロケールID(10進数)。
     */
    public static boolean isKnownById(int id) {
        final String hexId = Utils.supplyZero(Integer.toHexString(id).toUpperCase(), 4);
        
        String code = messageResolver.getMessage(String.format("locale.%s.code", hexId));
        return Utils.isNotEmpty(code);
    }
    
    /**
     * IDを指定してインスタンスを作成する。
     * @param id ロケールID(10進数)
     */
    public MSLocale(final int id) {
        this.id = id;
        
        // IDを16進数に変換する
        final String hexId = Utils.supplyZero(Integer.toHexString(id).toUpperCase(), 4);
        this.hexId = hexId;
        
    }
    
    /**
     * 既知の言語を組み立てる。
     * <p>プロパティファイルに定義されている情報を元に作成する。
     * @since 0.5
     * @param id 10進数の言語ID
     * @return 不明なIDの場合は、nullを返す。
     */
    public static MSLocale createKnownLocale(final int id) {
        
        if(KNOWN_LOCALES.containsKey(id)) {
            return KNOWN_LOCALES.get(id);
        }
        
        final MSLocale locale = new MSLocale(id);
        
        synchronized (KNOWN_LOCALES) {
            
            // IDを16進数に変換する
            final String hexId = Utils.supplyZero(Integer.toHexString(id).toUpperCase(), 4);
            
            String code = messageResolver.getMessage(String.format("locale.%s.code", hexId));
            if(Utils.isEmpty(code)) {
                return null;
            }
            
            locale.code = code;
            locale.language = messageResolver.getMessage(String.format("locale.%s.language", hexId));
            locale.country = messageResolver.getMessage(String.format("locale.%s.country", hexId));
            locale.name = messageResolver.getMessage(String.format("locale.%s.name", hexId));
            
            // Javaのロケールの設定
            String jid = messageResolver.getMessage(String.format("locale.%s.jid", hexId));
            if(Utils.isNotEmpty(jid)) {
                locale.locale = parseLocale(jid);
            }
            
            // キャッシュに登録する。
            KNOWN_LOCALES.put(id, locale);
        }
        
        return locale;
        
    }
    
    private static Locale parseLocale(final String jid) {
        
        String[] split = jid.split("_");
        if(split.length == 1) {
            return new Locale(split[0]);
            
        } if(split.length == 2) {
            return new Locale(split[0], split[1]);
            
        } else if(split.length == 3) {
            return new Locale(split[0], split[2]);
            
        }
        
        return null;
        
    }
    
    /**
     * システムの日付用のロケールの場合。
     * <p>16進数の値が'F800'のとき。
     * @return
     */
    public boolean isSystemDate() {
        return getHexId().equalsIgnoreCase("F800");
    }
    
    /**
     * システムの時刻用のロケールの場合。
     * <p>16進数の値が'F400'のとき。
     * @return
     */
    public boolean isSystemTime() {
        return getHexId().equalsIgnoreCase("F400");
    }
    
    /**
     * IDを取得する。10進数の数値
     * @return
     */
    public int getId() {
        return id;
    }
    
    /**
     * 16進数のIDを取得する。
     * ・大文字、4桁に整形されている。
     * @return
     */
    public String getHexId() {
        return hexId;
    }
    
    /**
     * コードの取得
     * @return
     */
    public String getCode() {
        return code;
    }
    
    /**
     * 言語コードを取得する
     * @return
     */
    public String getLanguage() {
        return language;
    }
    
    /**
     * 国コードを取得する
     * @return
     */
    public String getCountry() {
        return country;
    }
    
    /**
     * 名称を取得する。
     * @return
     */
    public String getName() {
        return name;
    }
    
    /**
     * 指定したロケールの名称を取得する。
     * @param locale
     * @return
     */
    public String getName(final Locale locale) {
        return messageResolver.getMessage(locale, String.format("locale.%s.name", hexId));
    }
    
    /**
     * 対応するJavaのロケールを取得する。
     * <p>存在しない場合は、nullを返す。
     * @return
     */
    public Locale getLocale() {
        return locale;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        MSLocale other = (MSLocale) obj;
        if(id != other.id)
            return false;
        return true;
    }
    
}
