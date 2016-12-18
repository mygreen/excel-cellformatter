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
 * <p>ロケールを指定した場合、そのロケールで存在しないキーがあるときに、標準の値を返す。
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
    
    /**
     * デフォルトのメッセージがない場合を許可するかどうか。
     */
    private final boolean allowedNoDefault;
    
    /**
     * クラスパスのルートにあるユーザ定義のメッセージソースも読み込むかどうか指定します。
     */
    private final boolean appendUserResource;
    
    /**
     * デフォルトのメッセージ情報
     */
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
        this(resourceName, false, false);
        
    }
    
    /**
     * リソース名を指定してインスタンスを生成する。
     * @param resourceName リソース名。形式は、{@link ResourceBundle}の名称。
     * @param allowedNoDefault デフォルトのメッセージがない場合を許可するかどうか。
     * @param appendUserResouce クラスパスのルートにあるユーザ定義のメッセージソースも読み込むかどうか指定します。
     *      引数resourceNameの値が {@literal sample.SampleMessages}のとき、クラスパスのルート上にある「SampleMessages」を読み込みます。
     */
    public MessageResolver(final String resourceName, final boolean allowedNoDefault, final boolean appendUserResouce) {
        this.resourceName = resourceName;
        this.allowedNoDefault = allowedNoDefault;
        this.appendUserResource = appendUserResouce;
        this.defaultResource = loadDefaultResource(allowedNoDefault, appendUserResouce);
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
        
        final List<String> list = new ArrayList<>();
        
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
     * @param allowedNoDefault デフォルトのメッセージがない場合を許可するかどうか。
     * @param appendUserResource クラスパスのルートにあるユーザ定義のメッセージソースも読み込むかどうか指定します。
     * @return
     */
    private MessageResource loadDefaultResource(final boolean allowedNoDefault, final boolean appendUserResource) {
        
        final String path = getPropertyPath();
        Properties props = new Properties();
        try {
            props.load(MessageResolver.class.getResourceAsStream(path));
        } catch (NullPointerException | IOException e) {
            if(allowedNoDefault) {
                return MessageResource.NULL_OBJECT;
            } else {
                throw new RuntimeException("fail default properties. :" + path, e);
            }
        }
        
        final MessageResource resource = new MessageResource();
        
        final Enumeration<?> keys = props.propertyNames();
        while(keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            String value = props.getProperty(key);
            resource.addMessage(key, value);
        }
        
        // ユーザのリソースの読み込み
        if(appendUserResource) {
            resource.addMessage(loadUserResource(path));
        }
        
        return resource;
        
    }
    
    /**
     * 指定したロケールのリソースを取得する。
     * <p>該当するロケールのリソースが存在しない場合は、デフォルトのリソースを返す。
     * @param locale ロケールがnullの場合は、デフォルトのリソースを返す。
     * @return
     */
    MessageResource loadResource(final Locale locale) {
        
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
                    final Properties props = new Properties();
                    props.load(MessageResolver.class.getResourceAsStream(path));
                    
                    localeResource = new MessageResource();
                    
                    final Enumeration<?> keys = props.propertyNames();
                    while(keys.hasMoreElements()) {
                        String key = (String) keys.nextElement();
                        String value = props.getProperty(key);
                        localeResource.addMessage(key, value);
                    }
                    
                    // ユーザのリソースの読み込み
                    if(appendUserResource) {
                        localeResource.addMessage(loadUserResource(path));
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
     * 
     * @param basePath 基準となるプロパティファイルパス。「/」区切りで、拡張子、ロケール名が付いている。
     * @return 読み込んだメッセージソース。存在しない場合は、{@link MessageResource#NULL_OBJECT}を返す。
     */
    private MessageResource loadUserResource(final String basePath) {
        
        // ファイル名の切り出し
        final int index = basePath.lastIndexOf("/");
        if(index <= 0) {
            return MessageResource.NULL_OBJECT;
        }
        
        final String userPropertyPath = "/" + basePath.substring(index+1);
        try {
            Properties props = new Properties();
            props.load(MessageResolver.class.getResourceAsStream(userPropertyPath));
            
            final MessageResource resource = new MessageResource();
            final Enumeration<?> keys = props.propertyNames();
            while(keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                String value = props.getProperty(key);
                resource.addMessage(key, value);
            }
            
            return resource;
            
        } catch(NullPointerException | IOException e) {
            return MessageResource.NULL_OBJECT;
        }
        
    }
    
    /**
     * リソース名の取得。
     * @return
     */
    public String getResourceName() {
        return resourceName;
    }
    
    /**
     * デフォルトのメッセージ情報がない場合を許可するかどうか。
     * @return
     */
    public boolean isAllowedNoDefault() {
        return allowedNoDefault;
    }
    
    /**
     * キーを指定してメッセージを取得する。
     * @param key メッセージキー
     * @return 存在しないキーの場合、nullを返す。
     */
    public String getMessage(final String key) {
        return defaultResource.getMessage(key);
    }
    
    /**
     * ロケールとキーを指定してメッセージを取得する。
     * <p>ロケールに該当する値を取得する。
     * @param locale ロケール
     * @param key メッセージキー
     * @return 該当するロケールのメッセージが見つからない場合は、デフォルトのメッセージを返す。
     */
    public String getMessage(final MSLocale locale, final String key) {
        if(locale == null) {
            return loadResource(null).getMessage(key);
            
        } else {
            return loadResource(locale.getLocale()).getMessage(key);
        }
    }
    
    /**
     * ロケールとキーを指定してメッセージを取得する。
     * @param locale ロケール
     * @param key メッセージキー
     * @param defaultValue
     * @return 該当するロケールのメッセージが見つからない場合は、引数で指定した'defaultValue'の値を返す。
     */
    public String getMessage(final MSLocale locale, final String key, final String defaultValue) {
        String message = getMessage(locale, key);
        return message == null ? defaultValue : message;
    }
    
    /**
     * ロケールとキーを指定してメッセージを取得する。
     * <p>ロケールに該当する値を取得する。
     * @param locale ロケール
     * @param key メッセージキー
     * @return 該当するロケールのメッセージが見つからない場合は、デフォルトのメッセージを返す。
     */
    public String getMessage(final Locale locale, final String key) {
        return loadResource(locale).getMessage(key);
    }
    
    /**
     * 値がnullの場合、defaultValueの値を返す。
     * @param locale ロケール
     * @param key メッセージキー
     * @param defaultValue
     * @return 該当するロケールのメッセージが見つからない場合は、引数で指定した'defaultValue'の値を返す。
     */
    public String getMessage(final Locale locale, final String key, final String defaultValue) {
        String message = getMessage(locale, key);
        return message == null ? defaultValue : message;
    }
}
