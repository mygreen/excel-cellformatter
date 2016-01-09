package com.github.mygreen.cellformatter.lang;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

/**
 * メッセージソースを管理するクラス。
 * <p>ロケールを指定した場合、そのロケールで存在しないキーがあるときに、
 *    標準の値を返す。
 * 
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class MessageResolver {
    
    /**
     * 解決するリソース名。
     * <p>リソースバンドル名の形式。
     */
    private final String resourceName;
    
    private MessageResource defaultResource;
    
    /**
     * ロケールごとのメッセージリソースの取得
     */
    private Map<Locale, MessageResource> resources;
    
    /**
     * リソース名を指定してインスタンスを生成する。
     * @param resourceName リソース名。形式は、{@link ResourceBundle}の名称。
     */
    public MessageResolver(final String resourceName) {
        this.resourceName = resourceName;
        this.defaultResource = loadDefaultResource();
        this.resources = new ConcurrentHashMap<>();
        
    }
    
    private String getPropertyPath() {
        
        final String baseName = getResourceName().replaceAll("\\.", "/");
        String path = new StringBuilder()
                .append("/")
                .append(baseName)
                .append(".properties")
                .toString();
        
        return path;
    }
    
    private String[] getPropertyPath(final Locale locale) {
        
        List<String> list = new ArrayList<>();
        
        final String baseName = getResourceName().replaceAll("\\.", "/");
        if(Utils.isNotEmpty(locale.getLanguage())) {
            String path = new StringBuilder()
                    .append("/")
                    .append(baseName)
                    .append("_").append(locale.getLanguage())
                    .append(".properties")
                    .toString();
            list.add(path);
        }
        
        if(Utils.isNotEmpty(locale.getLanguage()) && Utils.isNotEmpty(locale.getCountry())) {
            String path = new StringBuilder()
                    .append("/")
                    .append(baseName)
                    .append("_").append(locale.getLanguage())
                    .append("_").append(locale.getCountry())
                    .append(".properties")
                    .toString();
            list.add(path);
        }
        
        Collections.reverse(list);
        
        return list.toArray(new String[list.size()]);
    }
    
    /**
     * プロパティファイルから取得する。
     * <p>プロパティ名を補完する。
     * @param path プロパティファイルのパス名
     * @return
     */
    private MessageResource loadDefaultResource() {
        
        final String path = getPropertyPath();
        Properties props = new Properties();
        try {
            props.load(MessageResolver.class.getResourceAsStream(path));
        } catch (IOException e) {
            throw new RuntimeException("fail default properties. :" + path, e);
        }
        
        final MessageResource resource = new MessageResource(null);
        
        Enumeration<?> keys = props.propertyNames();
        while(keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            String value = props.getProperty(key);
            resource.addMessage(key, value);
        }
        
        return resource;
        
    }
    
    /**
     * 指定したロケールのリソースを取得する。
     * <p>該当するロケールのリソースが存在しない場合は、デフォルトのリソースを返す。
     * @param locale ロケールがnullの場合は、デフォルトのリソースを返す。
     * @return
     */
    private MessageResource loadResource(final Locale locale) {
        
        if(locale == null) {
            return defaultResource;
        }
        
        if(resources.containsKey(locale)) {
            return resources.get(locale);
        }
        
        MessageResource localeResource = null;
        synchronized (resources) {
            for(String path : getPropertyPath(locale)) {
                
                try {
                    Properties props = new Properties();
                    props.load(MessageResolver.class.getResourceAsStream(path));
                    
                    localeResource = new MessageResource(null);
                    
                    Enumeration<?> keys = props.propertyNames();
                    while(keys.hasMoreElements()) {
                        String key = (String) keys.nextElement();
                        String value = props.getProperty(key);
                        localeResource.addMessage(key, value);
                    }
                    
                    break;
                    
                } catch(NullPointerException | IOException e) {
                    continue;
                }
                
            }
            
            if(localeResource == null) {
                // 該当するリソースが見つからない場合は、デフォルトの値を返す。
                localeResource = defaultResource;
            }
            
            resources.put(locale, localeResource);
        }
        
        return localeResource;
        
    }
    
    /**
     * リソース名の取得。
     * @return
     */
    public String getResourceName() {
        return resourceName;
    }

    /**
     * キーを指定してメッセージを取得する。
     * @param key メッセージキー
     * @return
     */
    public String getMessage(final String key) {
        return defaultResource.getMessage(key);
    }
    
    /**
     * ロケールとキーを指定してメッセージを取得する。
     * <p>ロケールに該当する値を取得する。
     * <p>該当するロケールのメッセージが見つからない場合は、デフォルトのメッセージを返す。
     * @param locale ロケール
     * @param key メッセージキー
     * @return
     */
    public String getMessage(final MSLocale locale, final String key) {
        if(locale == null) {
            return loadResource(null).getMessage(key);
            
        } else {
            return loadResource(locale.getLocale()).getMessage(key);
        }
    }
    
    /**
     * 値がnullの場合、defaultValueの値を返す。
     * @param locale
     * @param key
     * @param defaultValue
     * @return
     */
    public String getMessage(final MSLocale locale, final String key, final String defaultValue) {
        String message = getMessage(locale, key);
        return message == null ? defaultValue : message;
    }
    
    /**
     * ロケールとキーを指定してメッセージを取得する。
     * <p>ロケールに該当する値を取得する。
     * <p>該当するロケールのメッセージが見つからない場合は、デフォルトのメッセージを返す。
     * @param locale ロケール
     * @param key メッセージキー
     * @return
     */
    public String getMessage(final Locale locale, final String key) {
        return loadResource(locale).getMessage(key);
    }
    
    /**
     * 値がnullの場合、defaultValueの値を返す。
     * @param locale
     * @param key
     * @param defaultValue
     * @return
     */
    public String getMessage(final Locale locale, final String key, final String defaultValue) {
        String message = getMessage(locale, key);
        return message == null ? defaultValue : message;
    }
}
