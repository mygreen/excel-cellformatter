package com.github.mygreen.cellformatter.lang;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 時代の情報を解決するクラス。
 * <p>各ロケールごとに情報を保持する。
 * <p>一度読み込んだ情報はキャッシュする。
 * 
 * @version 0.5
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class EraResolver {
    
    /**
     * 時代の定義用のプロパティファイル
     */
    private static final MessageResolver messageResolver = new MessageResolver("com.github.mygreen.cellformatter.era", true);
    
    /**
     * ロケールごとの時代
     */
    private Map<Locale, Era> eras;
    
    public EraResolver() {
        this.eras = new ConcurrentHashMap<>();
    }
    
    /**
     * ロケールに該当する時代情報を取得する。
     * @param locale ロケール。null指定可能。
     * @return 見つからない場合は、nullを返す。
     */
    public Era getEra(final MSLocale locale) {
        return locale == null ? Era.UNKNOWN_ERA : getEra(locale.getLocale());
    }
    
    /**
     * ロケールに該当する時代情報を取得する。
     * @param locale ロケール。null指定可能。
     * @return 見つからない場合は、不明な時代情報を返す{@link Era.UnknownEra}クラスのインスタンスを返す。
     */
    public Era getEra(final Locale locale) {
        
        if(locale == null) {
            return Era.UNKNOWN_ERA;
        }
        
        if(eras.containsKey(locale)) {
            return eras.get(locale);
        }
        
        Era era = null;
        synchronized (eras) {
            MessageResource resource = messageResolver.loadResource(locale);
            
            // 該当する時代情報の定義のメッセージプロパティが存在するか。
            if(resource.isNullObject()) {
                era = Era.UNKNOWN_ERA;
            } else {
                era = createEra(resource);
            }
            
            eras.put(locale, era);
        }
        
        return era;
        
    }
    
    private Era createEra(final MessageResource resource) {
        
        // メッセージから時代情報キー名を取得する。
        Pattern keyPattern = Pattern.compile("^era\\.(.+)\\.name$");
        Set<String> keyNameSet = new HashSet<>();
        for(String key : resource.getKeys()) {
            
            Matcher matcher = keyPattern.matcher(key);
            if(!matcher.matches()) {
                continue;
            }
            
            String keyName = matcher.group(1);
            keyNameSet.add(keyName);
            
        }
        
        // 時代情報の組み立て
        List<EraPeriod> periodList = new ArrayList<>();
        for(String keyName : keyNameSet) {
            
            EraPeriod period = new EraPeriod();
            String name = resource.getMessage(String.format("era.%s.name", keyName));
            period.setName(name);
            
            String abbrevName = resource.getMessage(String.format("era.%s.abbrevName", keyName));
            period.setAbbrevName(abbrevName);
            
            String abbrevRomanName = resource.getMessage(String.format("era.%s.abbrevRomanName", keyName));
            period.setAbbrevRomanName(abbrevRomanName);
            
            String start = resource.getMessage(String.format("era.%s.start", keyName));
            if(Utils.isNotEmpty(start)) {
                period.setStartDate(Timestamp.valueOf(start));
            }
            
            String end = resource.getMessage(String.format("era.%s.end", keyName));
            if(Utils.isNotEmpty(end)) {
                period.setEndDate(Timestamp.valueOf(end));
            }
            
            periodList.add(period);
        }
        
        if(periodList.isEmpty()) {
            return Era.UNKNOWN_ERA;
        }
        
        final Era era = new Era(periodList);
        return era;
        
    }
    
}
