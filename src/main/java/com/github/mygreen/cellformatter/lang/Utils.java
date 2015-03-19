package com.github.mygreen.cellformatter.lang;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 *
 * @author T.TSUCHIE
 *
 */
public class Utils {
    
    /**
     * 値がnullまたは空文字か判定する。
     * @param str
     * @return true: 引数がnullまたは空文字。
     */
    public static boolean isEmpty(final String str) {
        if(str == null || str.isEmpty()) {
            return true;
        }
        
        return false;
    }
    
    /**
     * 値がnullかつ空文字でないか判定する。
     * @param str
     * @return true: 引数がnullかつ空文字でない場合
     */
    public static boolean isNotEmpty(final String str) {
        return !isEmpty(str);
    }
    
    /**
     * 辞書の逆順で並び替える
     * @param array 並び替える配列
     * @return
     * @throws IllegalArgumentException array is null.
     */
    public static List<String> reverse(final String[] array) {
        
        ArgUtils.notNull(array, "array");
        if(array.length == 0) {
            return new ArrayList<String>();
        }
        
        final List<String> list = Arrays.asList(array);
        Collections.sort(list, new Comparator<String>() {
            
            @Override
            public int compare(final String str1, final String str2) {
                
                int len1 = str1.length();
                int len2 = str2.length();
                
                if(len1 != len2) {
                    // 逆順
                    return Integer.compare(len1, len2) * -1;
                } else {
                    // 逆順
                    return str1.compareTo(str2) * 1;
                }
            }
        });
        
        return list;
    }
    
    /**
     * 大文字・小文字を無視して指定した文字から始まるかチェックする。
     * ・Commons-Langの「StringUtils.startsWithIgnoreCase」を参照。
     * <pre>
     * Utils.startsWithIgnoreCase(null, null)      = true
     * Utils.startsWithIgnoreCase(null, "abc")     = false
     * Utils.startsWithIgnoreCase("abcdef", null)  = false
     * Utils.startsWithIgnoreCase("abcdef", "abc") = true
     * Utils.startsWithIgnoreCase("ABCDEF", "abc") = true
     * </pre>
     * @param str
     * @param prefix
     * @return
     */
    public static boolean startsWithIgnoreCase(final String str, final String prefix) {
        return startsWith(str, prefix, true);
    }
    
    /**
     * 開始位置を指定し、
     * @param str
     * @param prefix
     * @param offset 比較する文字の開始位置。文字数よりも大きい場合は、falseを返す。
     * @return
     */
    public static boolean startsWithIgnoreCase(final String str, final String prefix, final int offset) {
        
        if (str == null || prefix == null) {
            return (str == null && prefix == null);
        }
        
        if(str.length() < offset) {
            return false;
        }
        
        final String subStr = str.substring(offset);
        return startsWithIgnoreCase(subStr, prefix);
        
    }
    
    private static boolean startsWith(final String str, final String prefix, boolean ignoreCase) {
        if (str == null || prefix == null) {
            return (str == null && prefix == null);
        }
        if (prefix.length() > str.length()) {
            return false;
        }
        return str.regionMatches(ignoreCase, 0, prefix, 0, prefix.length());
    }
    
    /**
     * 大文字・小文字を無視して指定した文字を含むかチェックする。
     * ・Commons-Langの「StringUtils.containsIgnoreCase」を参照。
     * @param str
     * @param searchStr
     * @return
     */
    public static boolean containsIgnoreCase(final String str, final String searchStr) {
        
        if (str == null || searchStr == null) {
            return false;
        }
        int len = searchStr.length();
        int max = str.length() - len;
        for (int i = 0; i <= max; i++) {
            if (str.regionMatches(true, i, searchStr, 0, len)) {
                return true;
            }
        }
        return false;
        
    }
    
    /**
     * 指定した文字の何れかを含むかどうか。
     * @param str
     * @param list
     * @param ignoreCase 大文字・小文字を無視するかどうか。
     * @return
     */
    public static boolean containsAny(final String str, final String[] list, final boolean ignoreCase) {
        
        if(str == null) {
            return false;
        }
        
        if(list == null || list.length == 0) {
            return false;
        }
        
        for(String item : list) {
            if(ignoreCase) {
                if(containsIgnoreCase(str, item)) {
                    return true;
                }
            } else {
                if(str.indexOf(item) >= 0) {
                    return true;
                }
                
            }
        }
        
        return false;
        
    }
    
    /**
     * 何れかの文字と等しいかどうか
     * @param str
     * @param searchChars
     * @return
     */
    public static boolean equalsAny(final String str, String[] searchChars) {
        
        for(String search : searchChars) {
            if(str.equals(search)) {
                return true;
            }
        }
        
        return false;
        
    }
    
    /**
     * 大文字・小文字を無視して、何れかの文字と等しいか。
     * <pre>
     *  Utils.equalsAnyIgnoreCase("abc", null) = false
     *  Utils.equalsAnyIgnoreCase("abc", new String[]{}) = false
     *  Utils.equalsAnyIgnoreCase(null, new String[]{"abc"}) = false
     *  Utils.equalsAnyIgnoreCase("", new String[]{""}) = true
     * </pre>
     * 
     * @param str
     * @param list
     * @return
     */
    public static boolean equalsAnyIgnoreCase(final String str, final String[] list) {
        
        if(str == null) {
            return false;
        }
        
        if(list == null || list.length == 0) {
            return false;
        }
        
        for(String item : list) {
            if(str.equalsIgnoreCase(item)) {
                return true;
            }
        }
        
        return false;
        
    }
    
    /**
     * Excelの日付の開始日。
     * ・数値が0の時、「1900/1/0 0:00:00」 。この値は、1900年1月1日から、2日（48時間）を引いた値。
     *  
     * @return
     */
    public static long getExcelZeroDate() {
        
        long time = Timestamp.valueOf("1900-01-01 00:00:00.000").getTime();
        time -= 2*(1000*60*60*24);
        
        return time;
        
    }
}
