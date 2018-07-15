package com.github.mygreen.cellformatter.lang;


/**
 * テストのユーティリティ
 * @since 2.2
 * @author T.TSUCHIE
 *
 */
public class TestUtils {
    
    /**
     * Javaのバージョン
     */
    public static String JAVA_SPECIFICATION_VERSION = System.getProperty("java.specification.version");
    
    public static final boolean IS_JAVA_1_7 = getJavaVersionMatches("1.7");
    
    public static final boolean IS_JAVA_1_8 = getJavaVersionMatches("1.8");
    
    public static final boolean IS_JAVA_9 = getJavaVersionMatches("9");
    
    public static final boolean IS_JAVA_10 = getJavaVersionMatches("10");
    
    /**
     * 
     * @param versionPrefix
     * @return
     */
    private static boolean getJavaVersionMatches(final String versionPrefix) {
        return isJavaVertionMatch(JAVA_SPECIFICATION_VERSION, versionPrefix);
    }
    
    /**
     * Javaのバージョンが一致するか確認する。
     * @param version
     * @param versionPrefix
     * @return
     */
    private static boolean isJavaVertionMatch(final String version, final String versionPrefix) {
        
        if(version == null) {
            return false;
        }
        
        return version.startsWith(versionPrefix);
        
    }
    
}
