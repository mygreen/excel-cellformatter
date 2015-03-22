package com.github.mygreen.cellformatter.lang;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 色のパレットの定義クラス。
 * <p>色はパレットが指定可能。
 * <p>Excelの色パレット一覧：<a href="http://dmcritchie.mvps.org/excel/colors.htm">Color Palette and the 56 Excel ColorIndex Colors</a>
 * 
 * @author T.TSUCHIE
 *
 */
public class MSColor {
    
    /** 名前付きの色 - 黒 */
    public static final MSColor BLACK = new MSColor("Black", "黒", (short)1);
    
    /** 名前付きの色 - 白 */
    public static final MSColor WHITE = new MSColor("White", "白", (short)2);
    
    /** 名前付きの色 - 赤 */
    public static final MSColor RED = new MSColor("Red", "赤", (short)3);
    
    /** 名前付きの色 - 緑 */
    public static final MSColor GREEN = new MSColor("Green", "緑", (short)4);
    
    /** 名前付きの色 - 青 */
    public static final MSColor BLUE = new MSColor("Blue", "青", (short)5);
    
    /** 名前付きの色 - 黄 */
    public static final MSColor YELLOW = new MSColor("Yellow", "黄", (short)6);
    
    /** 名前付きの色 - 紫 */
    public static final MSColor MAGENTA = new MSColor("Magenta", "紫", (short)7);
    
    /** 名前付きの色 - 水 */
    public static final MSColor CYAN = new MSColor("Cyan", "水", (short)8);
    
    /**
     * 名前付きの色のリスト
     */
    public static final List<MSColor> KNOWN_COLORS;
    static{
        KNOWN_COLORS = Collections.unmodifiableList(new CopyOnWriteArrayList<MSColor>(
                Arrays.asList(BLACK, WHITE, RED, GREEN, BLUE, YELLOW, MAGENTA, CYAN)));
    }
    
    /** 色の名称 */
    private final String name;
    
    /** 色の別名（日本語など） */
    private final String aliasName;
    
    /** 色のインデックス */
    private final short index;
    
    /**
     * 色の名称、インデックスを指定してインスタンスを作成する。
     * @param name 色の名称(ex. white)
     * @param aliasName 色の別名（日本語名など） 白
     * @param index 色のインデックス番号
     */
    public MSColor(final String name, final String aliasName, final short index) {
        this.name = name;
        this.aliasName = aliasName;
        this.index = index;
    }
    
    /**
     * インデックスを指定してインスタンスを作成する。
     * @param index 色のインデックス番号
     */
    public MSColor(final short index) {
        this(null, null, index);
    }
    
    /**
     * 色の名称の取得。
     * @return 名前がない場合は、nullを返す。
     */
    public String getName() {
        return name;
    }
    
    /**
     * 色の別名の取得。日本語名称などを返す。
     * @return 別名がない場合は、nullを返す。
     */
    public String getAliasName() {
        return aliasName;
    }
    
    /**
     * 色のインデックス番号を取得する。
     * @return
     */
    public short getIndex() {
        return index;
    }
    
    /**
     * 既知の名前付きの色かどうか。
     * @param name
     * @return 不明な場合は、nullを返す。
     */
    public static MSColor valueOfKnownColor(final String name) {
        
        for(MSColor color : KNOWN_COLORS) {
            if(color.name.equalsIgnoreCase(name)
                    || color.aliasName.equalsIgnoreCase(name)) {
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
    
    
}
