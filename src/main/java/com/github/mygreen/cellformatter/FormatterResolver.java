package com.github.mygreen.cellformatter;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * セルのフォーマッタを解決するクラス。
 * @author T.TSUCHIE
 *
 */
public class FormatterResolver {
    
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
     * 書式のパターンとフォーマッターのマップ
     */
    private Map<String, CellFormatter> patternFormatterMap = new ConcurrentHashMap<>();
    
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
        
        // 間違ったフォーマット
        registerFormatter((short)5, createFormatter("¥#,##0;¥-#,##0"));
        registerFormatter((short)6, createFormatter("¥#,##0;[Red]¥-#,##0"));
        registerFormatter((short)7, createFormatter("¥#,##0.00;¥-#,##0.00"));
        registerFormatter((short)8, createFormatter("¥#,##0.00;[Red]¥-#,##0.00"));
        
        // ロケールによって変わるフォーマット
        registerFormatter((short)14, new LocaleSwitchFormatter(new CustomFormatter("m/d/yy"))
                .register(createFormatter("yyyy/m/d"), Locale.JAPAN, Locale.JAPANESE, LOCALE_JAPANESE));
        
//        registerFormatter((short)20, createFormatter("h:mm"));
//        registerFormatter((short)21, createFormatter("h:mm:ss"));
        registerFormatter((short)22, createFormatter("yyyy/m/d h:mm"));
        
        registerFormatter((short)30, createFormatter("m/d/yy"));
        registerFormatter((short)31, createFormatter("yyyy\"年\"m\"月\"d\"日\""));
        registerFormatter((short)32, createFormatter("h\"時\"mm\"分\""));
        registerFormatter((short)33, createFormatter("h\"時\"mm\"分\"ss\"秒\""));
        
        // 間違ったフォーマットの訂正
        registerFormatter((short)37, createFormatter("#,##0;-#,##0"));
        registerFormatter((short)38, createFormatter("#,##0;[Red]-#,##0"));
        registerFormatter((short)39, createFormatter("#,##0.00;-#,##0.00"));
        registerFormatter((short)40, createFormatter("#,##0.00;[Red]-#,##0.00"));
        
        registerFormatter((short)41, createFormatter("_ * #,##0_ ;_ * \\-#,##0_ ;_ * \"-\"_ ;_ @_"));
        registerFormatter((short)42, createFormatter("_ \"¥\"* #,##0_ ;_ \"¥\"* \\-#,##0_ ;_ \"¥\"* \"-\"_ ;_ @_ "));
        registerFormatter((short)43, createFormatter("_ * #,##0.00_ ;_ * (#,##0.00);_ * \"-\"??_ ;_ @_ "));
        registerFormatter((short)44, createFormatter("_ \"¥\"* #,##0.00_ ;_ \"¥\"* -#,##0.00_ ;_ \"¥\"* \"-\"??_ ;_ @_ "));
        
        // インデックス番号のみあり、フォーマットがない場合
        registerFormatter((short)55, createFormatter("yyyy\"年\"m\"月\""));
        registerFormatter((short)56, createFormatter("m\"月\"d\"日\""));
        registerFormatter((short)57, createFormatter("[$-411]ge\\.m\\.d;@"));
        registerFormatter((short)58, createFormatter("[$-411]ggge\"年\"m\"月\"d\"日\";@"));
        
        // ロケールによって変わるフォーマット
        registerFormatter("[$-F800]dddd\\,\\ mmmm\\ dd\\,\\ yyyy", new LocaleSwitchFormatter(new CustomFormatter("[$-F800]dddd\\,\\ mmmm\\ dd\\,\\ yyyy"))
            .register(createFormatter("yyyy\"年\"m\"月\"d\"日\""), Locale.JAPAN, Locale.JAPANESE, LOCALE_JAPANESE));
        
        registerFormatter("[$-F400]h:mm:ss\\ AM/PM", new LocaleSwitchFormatter(new CustomFormatter("[$-F400]h:mm:ss\\ AM/PM"))
            .register(createFormatter("h:mm:ss"), Locale.JAPAN, Locale.JAPANESE, LOCALE_JAPANESE));
    }
    
    /**
     * インデックス形式の書式指定が解決可能かどうか。
     * @param formatIndex
     * @return
     */
    public boolean canResolve(final short formatIndex) {
        return indexFormatterMap.containsKey(formatIndex);
    }
    
    /**
     * パターン形式の書式指定が解決可能かどうか。
     * @param formatPattern
     * @return
     */
    public boolean canResolve(final String formatPattern) {
        final String key = (formatPattern == null ? "" : formatPattern);
        return patternFormatterMap.containsKey(key);
    }
    
    /**
     * インデックスを指定してフォーマッタを取得する。
     * @param formatIndex
     * @return 登録されていないインデックスの場合は、nullを返す。
     */
    public CellFormatter getFormatter(final short formatIndex) {
        
        return indexFormatterMap.get(formatIndex);
    }
    
    /**
     * 書式のパターンを指定してフォーマッタを取得する。
     * @param pattern
     * @return 登録されていないインデックスの場合は、nullを返す。
     */
    public CellFormatter getFormatter(final String formatPattern) {
        
        final String key = (formatPattern == null ? "" : formatPattern);
        return patternFormatterMap.get(key);
    }
    
    /**
     * パターンを指定して新たに書式を作成する。
     * @param formatPattern
     * @return
     */
    public CellFormatter createFormatter(final String formatPattern) {
        
        final CellFormatter formatter = customFormatterFactory.create(formatPattern);
        return formatter;
        
    }
    
    /**
     * インデックス番号を指定してフォーマッタを登録する。
     * @param formatIndex
     * @param cellFormatter
     * @return
     */
    public synchronized CellFormatter registerFormatter(final short formatIndex, final CellFormatter cellFormatter) {
        return indexFormatterMap.put(formatIndex, cellFormatter);
    }
    
    /**
     * パターンを指定してフォーマッタを登録する。
     * @param formatPattern
     * @param cellFormatter
     * @return
     */
    public synchronized CellFormatter registerFormatter(final String formatPattern, final CellFormatter cellFormatter) {
        return patternFormatterMap.put(formatPattern, cellFormatter);
    }
    
    public CustomFormatterFactory getCustomFormatterFactory() {
        return customFormatterFactory;
    }
    
    public void setCustomFormatterFactory(CustomFormatterFactory customFormatterFactory) {
        this.customFormatterFactory = customFormatterFactory;
    }
}
