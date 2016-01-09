package com.github.mygreen.cellformatter.lang;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * メッセージソースを管理するクラス。
 * <p>複数のロケールのメッセージを管理する。
 * 
 * @since 0.5
 * @author T.TSUCHIE
 *
 */
public class MessageResource {
    
    /**
     * このメッセージが処理可能なロケール
     * <p>標準の場合、値は空を設定。
     */
    private final MSLocale locale;
    
    /**
     * メッセージのキャッシュされたデータセット
     * <p>key = キー、value=メッセージ
     * <p>読み込む度にキャッシュしていく。
     */
    private Map<String, String> messages;
    
    public MessageResource(final MSLocale locale) {
        this.locale = locale;
        this.messages = new ConcurrentHashMap<>();
    }
    
    /**
     * ロケ-ルを取得する。
     * @return
     */
    public MSLocale getLocale() {
        return locale;
    }
    
    /**
     * キーを指定してメッセージを取得する。
     * @param key
     * @return
     */
    public String getMessage(final String key) {
        return messages.get(key);
    }
    
    /**
     * キーとメッセージを指定して登録する。
     * @param key
     * @param message
     */
    public void addMessage(final String key, final String message) {
        messages.put(key, message);
    }
    
}
