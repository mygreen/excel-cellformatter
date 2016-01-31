package com.github.mygreen.cellformatter.lang;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 色のパレットの定義クラス。
 * <p>色はパレットが指定可能。
 * <p>Excelの色パレット一覧：<a href="http://dmcritchie.mvps.org/excel/colors.htm" target="_blank">Color Palette and the 56 Excel ColorIndex Colors</a>
 * 
 * @author T.TSUCHIE
 *
 */
public class MSColor {
    
    private static final MessageResolver messageResolver = new MessageResolver("com.github.mygreen.cellformatter.color");
    
    /** 名前付きの色 - 黒 */
    public static final MSColor BLACK = new MSColor((short)1);
    
    /** 名前付きの色 - 白 */
    public static final MSColor WHITE = new MSColor((short)2);
    
    /** 名前付きの色 - 赤 */
    public static final MSColor RED = new MSColor((short)3);
    
    /** 名前付きの色 - 緑 */
    public static final MSColor GREEN = new MSColor((short)4);
    
    /** 名前付きの色 - 青 */
    public static final MSColor BLUE = new MSColor((short)5);
    
    /** 名前付きの色 - 黄 */
    public static final MSColor YELLOW = new MSColor((short)6);
    
    /** 名前付きの色 - 紫 */
    public static final MSColor MAGENTA = new MSColor((short)7);
    
    /** 名前付きの色 - 水 */
    public static final MSColor CYAN = new MSColor((short)8);
    
    /**
     * 名前付きの色のリスト
     */
    public static final List<MSColor> KNOWN_COLORS;
    static{
        KNOWN_COLORS = Collections.unmodifiableList(new CopyOnWriteArrayList<MSColor>(
                Arrays.asList(BLACK, WHITE, RED, GREEN, BLUE, YELLOW, MAGENTA, CYAN)));
    }
    
    /** 色のインデックス */
    private final short index;
    
    /** 色の名称 */
    private final String name;
    
    /**
     * インデックスを指定してインスタンスを作成する。
     * @param index 色のインデックス番号
     */
    public MSColor(final short index) {
        this.index = index;
        
        // 色名の取得
        this.name = messageResolver.getMessage(String.format("color.%d.name", index));
    }
    
    /**
     * 色のインデックス番号を取得する。
     * @return
     */
    public short getIndex() {
        return index;
    }
    
    /**
     * 色の名称の取得。
     * @return 名前がない場合は、nullを返す。
     */
    public String getName() {
        return name;
    }
    
    /**
     * ロケール固有の色の名称の取得。
     * <p>実行環境のロケールを元に取得する。
     * @return 別名がない場合は、nullを返す。
     */
    public String getLocaleName() {
        return messageResolver.getMessage(Locale.getDefault(), String.format("color.%d.name", index));
    }
    
    /**
     * ロケールを指定し固有の色の名称の取得。
     * @param locale ロケール
     * @return 別名がない場合は、nullを返す。
     */
    public String getLocaleName(final Locale locale) {
        return messageResolver.getMessage(locale, String.format("color.%d.name", index));
    }
    
    /**
     * HTMLの形式の色を取得する。
     * <p>例:{@code #000000}
     * @since 0.5
     * @return
     */
    public String getHtmlColor() {
        return messageResolver.getMessage(String.format("color.%d.html", index));
    }
    
    /**
     * RGB値を取得する。
     * @since 0.5
     * @return 配列の0番目から準備、Red、Green、Blubeの値。
     */
    public short[] getRgbColor() {
        String value = messageResolver.getMessage(String.format("color.%d.rgb", index));
        String[] split = value.split(",");
        
        short[] rgb = new short[3];
        rgb[0] = Short.valueOf(split[0]);
        rgb[1] = Short.valueOf(split[1]);
        rgb[2] = Short.valueOf(split[2]);
        
        return rgb;
        
    }
    
    /**
     * 既知の名前付きの色かどうか。
     * @param name
     * @return 不明な場合は、nullを返す。
     */
    public static MSColor valueOfKnownColor(final String name) {
        
        for(MSColor color : KNOWN_COLORS) {
            if(color.name.equalsIgnoreCase(name)) {
                return color;
                
            } else if(color.getLocaleName() != null && color.getLocaleName().equalsIgnoreCase(name)) {
                return color;
                
            }
            
        }
        
        return null;
        
    }
    
    /**
     * インデックス番号を指定して色を作成する。
     * <p>名前付きの既知の色の場合は、そのインスタンスを返す。
     * @param index
     * @return
     */
    public static MSColor valueOfIndexColor(final short index) {
        for(MSColor color : KNOWN_COLORS) {
            if(color.index == index){
                return color;
            }
            
        }
        
        return new MSColor(index);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + index;
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(obj == null) {
            return false;
        }
        if(!(obj instanceof MSColor)) {
            return false;
        }
        MSColor other = (MSColor) obj;
        if(index != other.index) {
            return false;
        }
        return true;
    }
    
    
}
