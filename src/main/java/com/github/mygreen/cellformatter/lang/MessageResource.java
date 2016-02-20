package com.github.mygreen.cellformatter.lang;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
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
     * 存在しないメッセージソースを示すクラス。
     * <p>メッセージの追加はできない、読み込み専用のメッセージ。
     */
    public static final MessageResource NULL_OBJECT = new MessageResource() {
        
        {
            this.messages = Collections.unmodifiableMap(new ConcurrentHashMap<String, String>());
            
        }
        
        @Override
        public boolean isNullObject() {
            return true;
        }
    };
    
    /**
     * メッセージのキャッシュされたデータセット
     * <p>key = キー、value=メッセージ
     * <p>読み込む度にキャッシュしていく。
     */
    protected Map<String, String> messages;
    
    public MessageResource() {
        this.messages = new ConcurrentHashMap<>();
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
     * メッセージ定義中に含まれるキーを全て返す。
     * @return
     */
    public Set<String> getKeys() {
        return messages.keySet();
    }
    
    /**
     * キーとメッセージを指定して登録する。
     * @param key
     * @param message
     */
    public void addMessage(final String key, final String message) {
        messages.put(key, message);
    }
    
    /**
     * 存在しないメッセージソースを示すかどうか。
     * @return {@link #NULL_OBJECT}のインスタンスの場合、trueを返す。
     */
    public boolean isNullObject() {
        return false;
    }
    
}
