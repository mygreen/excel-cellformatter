package com.github.mygreen.cellformatter;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.mygreen.cellformatter.lang.MessageResolver;


/**
 * セルのフォーマッタを解決するクラス。
 * <p>解析したフォーマットをキャッシュし、性能を向上する。
 * @author T.TSUCHIE
 *
 */
public class FormatterResolver {
    
    private static final MessageResolver messageResolver = new MessageResolver("com.github.mygreen.cellformatter.format");
    
    /**
     * カスタム書式のインスタンスを作成する。
     */
    private CustomFormatterFactory customFormatterFactory = new CustomFormatterFactory();
    
    /**
     * 書式のインデックスとフォーマッターのマップ
     */
    private Map<Short, CellFormatter> indexFormatterMap = new ConcurrentHashMap<>();
    
    /**
     * 和暦用の日本語のロケール
     */
    private static final Locale LOCALE_JAPANESE = new Locale("ja", "JP", "JP");
    
    /**
     * 日本語に関するロケール
     */
    private static final Locale[] JAPANESE_LOCALES = new Locale[]{Locale.JAPANESE, Locale.JAPAN, LOCALE_JAPANESE};
    
    /**
     * 書式のパターンとフォーマッターのマップ
     */
    private Map<String, CellFormatter> patternFormatterMap = new ConcurrentHashMap<>();
    
    /**
     * インスタンスを作成する。
     * <p>ビルドインフォーマットなどのキャッシュ情報を予め登録する。
     */
    public FormatterResolver() {
        clearFormat();
        
        registerDefaultFormat();
    }
    
    /**
     * キャッシュを初期化する。
     */
    public synchronized void clearFormat() {
        
        // インデックス番号指定のフォーマッタの初期化
        indexFormatterMap.clear();
        
        // パターン指定の指定のフォーマッタの初期化
        patternFormatterMap.clear();
        
    }
    
    /**
     * キャッシュに初期値データを登録する。
     * ・ロケールによって切り替わるフォーマットや、間違った組み込みフォーマットの場合を登録しておく。
     */
    public synchronized void registerDefaultFormat() {
        
        
        final Locale[] availableLocales = new Locale[]{Locale.JAPANESE};
        
        
        // 組み込み書式の登録
        for(int i=0; i <= 58; i++) {
            
            final CellFormatter formatter = createDefaultFormatter(String.valueOf(i), availableLocales);
            if(formatter != null) {
                registerFormatter((short) i, formatter);
                
            }
            
        }
        
        // 特別な書式
        final String[] names = new String[]{"F800", "F400"};
        for(String name : names) {
            
            final String key = String.format("format.%s", name);
            
            final String defaultFormat = messageResolver.getMessage(key);
            if(defaultFormat == null) {
                continue;
            }
            
            final CellFormatter formatter = createDefaultFormatter(name, availableLocales);
            if(formatter != null) {
                registerFormatter(defaultFormat, formatter);
            }
        }
        
    }
    
    /**
     * 指定したインデックスでプロパティに定義されているフォーマットを作成する。
     * @param name 書式の名前。({@link format.<書式の名前>=})
     * @param locales 検索するロケール。
     * @return 存在しないインデックス番号の時は、nullを返す。
     */
    protected CellFormatter createDefaultFormatter(final String name, final Locale... locales) {
        
        final String key = String.format("format.%s", name);
        
        final String defaultFormat = messageResolver.getMessage(key);
        if(defaultFormat == null) {
            return null;
        }
        
        CellFormatter formatter = createFormatter(defaultFormat);
        
        // ロケールのフォーマットの取得
        for(Locale locale : locales) {
            
            final String localeFormat = messageResolver.getMessage(locale, key, null);
            if(localeFormat == null) {
                continue;
            }
            
            final LocaleSwitchFormatter switchFormatter;
            if(formatter instanceof LocaleSwitchFormatter) {
                switchFormatter = (LocaleSwitchFormatter) formatter;
                
            } else {
                // LocaleSwitchFormatterに入れ替える。
                switchFormatter = new LocaleSwitchFormatter(formatter);
                formatter = switchFormatter;
            }
            
            // ロケールごとのフォーマットの登録
            if(locale.equals(Locale.JAPANESE)) {
                switchFormatter.register(createFormatter(localeFormat), JAPANESE_LOCALES);
                
            } else {
                switchFormatter.register(createFormatter(localeFormat), locale);
            }
            
        }
        
        return formatter;
        
    }
    
    /**
     * インデックス形式の書式指定がキャッシュに登録され、解決可能かどうか。
     * @param formatIndex 検証対象の書式のインデックス番号。
     * @return true: 事前に登録されているものやキャッシュされているインデックス番号の場合。
     */
    public boolean canResolve(final short formatIndex) {
        return indexFormatterMap.containsKey(formatIndex);
    }
    
    /**
     * パターン形式の書式指定がキャッシュに登録され、解決可能かどうか。
     * @param formatPattern 検証対象の書式パターン。
     * @return true:  事前に登録されているものやキャッシュされている書式パターンの場合。
     */
    public boolean canResolve(final String formatPattern) {
        final String key = (formatPattern == null ? "" : formatPattern);
        return patternFormatterMap.containsKey(key);
    }
    
    /**
     * インデックス番号を指定して、キャッシュに登録されているフォーマッタを取得する。
     * @param formatIndex 検証対象の書式のインデックス番号。
     * @return 登録されていないインデックスの場合は、nullを返す。
     */
    public CellFormatter getFormatter(final short formatIndex) {
        
        return indexFormatterMap.get(formatIndex);
    }
    
    /**
     * 書式のパターンを指定して、キャッシュに登録されているフォーマッタを取得する。
     * @param pattern
     * @return 登録されていないインデックスの場合は、nullを返す。
     */
    public CellFormatter getFormatter(final String formatPattern) {
        
        final String key = (formatPattern == null ? "" : formatPattern);
        return patternFormatterMap.get(key);
    }
    
    /**
     * パターンを指定して新たに書式を作成する。
     * @param formatPattern 書式パターン。
     * @return パースしたフォーマッタ。
     */
    public CellFormatter createFormatter(final String formatPattern) {
        
        final CellFormatter formatter = customFormatterFactory.create(formatPattern);
        return formatter;
        
    }
    
    /**
     * 書式インデックス番号を指定してフォーマッタをキャッシュに登録する。
     * @param formatIndex 書式のインデックス番号。
     * @param cellFormatter 登録対象のフォーマッタ。
     * @return 以前に登録されたフォーマッタのインスタンス。登録されたフォーマッタがなければ、nullを返す。
     */
    public synchronized CellFormatter registerFormatter(final short formatIndex, final CellFormatter cellFormatter) {
        return indexFormatterMap.put(formatIndex, cellFormatter);
    }
    
    /**
     * 書式パターンを指定してフォーマッタをキャッシュに登録する。
     * @param formatPattern 書式パターン。
     * @param cellFormatter 登録対象のフォーマッタ。
     * @return 以前に登録されたフォーマッタのインスタンス。登録されたフォーマッタがなければ、nullを返す。
     */
    public synchronized CellFormatter registerFormatter(final String formatPattern, final CellFormatter cellFormatter) {
        return patternFormatterMap.put(formatPattern, cellFormatter);
    }
    
    /**
     * 書式パターンを解析して、{@link CellFormatter}のインスタンスを作成するクラスの取得。
     * @return デフォルトは標準のクラスが設定されています。
     */
    public CustomFormatterFactory getCustomFormatterFactory() {
        return customFormatterFactory;
    }
    
    /**
     * 書式パターンを解析して、{@link CellFormatter}のインスタンスを作成するクラスの設定。
     * <p>独自のカスタマイズしたものが設定可能。
     * @param customFormatterFactory
     */
    public void setCustomFormatterFactory(CustomFormatterFactory customFormatterFactory) {
        this.customFormatterFactory = customFormatterFactory;
    }
}
