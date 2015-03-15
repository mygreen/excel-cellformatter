package com.github.mygreen.cellformatter.lang;

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
     * @return
     */
    public static boolean isEmpty(final String str) {
        if(str == null || str.isEmpty()) {
            return true;
        }
        
        return false;
    }
    
    /**
     * 辞書の逆順で並び替える
     * @param array
     * @return
     */
    public static List<String> reverse(final String[] array) {
        
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
}
