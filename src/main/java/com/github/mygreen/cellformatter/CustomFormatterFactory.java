package com.github.mygreen.cellformatter;

import java.util.List;

import com.github.mygreen.cellformatter.tokenizer.CustomFormatTokenizer;
import com.github.mygreen.cellformatter.tokenizer.Token;
import com.github.mygreen.cellformatter.tokenizer.TokenStore;


/**
 * ユーザ定義の書式を解析して、{@link CustomFormatter}のインスタンスを作成するクラス。
 * @author T.TSUCHIE
 *
 */
public class CustomFormatterFactory {
    
    private ConditionTextFormatterFactory textFormatterFactory = new ConditionTextFormatterFactory();
    
    private ConditionDateFormatterFactory dateFormatterFactory = new ConditionDateFormatterFactory();
    
    private ConditionNumberFormatterFactory numberFormatterFactory = new ConditionNumberFormatterFactory();
    
    /**
     * 書式を元に、{@link CustomFormatter}のインスタンスを作成する。
     * @param pattern ユーザ定義の書式
     * @return 指定したユーザ定義に対するフォーマッタ。
     * @throws CustomFormatterParseException 書式が不正な場合にスローされる。
     */
    public CustomFormatter create(final String pattern) {
        
        final CustomFormatTokenizer tokenizer = new CustomFormatTokenizer();
        final TokenStore allStore = tokenizer.parse(pattern);
        if(allStore.getTokens().isEmpty()) {
            // 標準のフォーマッタ
            return CustomFormatter.DEFAULT_FORMATTER;
            
        } else if(pattern.equalsIgnoreCase("General")) {
            return CustomFormatter.DEFAULT_FORMATTER;
        }
        
        // セクション単位に分割し、処理していく。
        final List<TokenStore> sections = allStore.split(Token.SYMBOL_SEMI_COLON);
        if(sections.size() > 4) {
            throw new CustomFormatterParseException(pattern,
                    String.format("section size over 4. but '%s' number of %d secitions.", pattern, sections.size()));
        }
        
        final CustomFormatter formatter = new CustomFormatter(pattern);
        boolean containsTextFormatter = false;
        for(TokenStore section : sections) {
            
            final ConditionFormatter conditionFormatter;
            if(textFormatterFactory.isTextPattern(section)) {
                conditionFormatter = textFormatterFactory.create(section);
                containsTextFormatter = true;
                
            } else if(dateFormatterFactory.isDatePattern(section)) {
                conditionFormatter = dateFormatterFactory.create(section);
                
            } else if(numberFormatterFactory.isNumberPattern(section)) {
                conditionFormatter = numberFormatterFactory.create(section);
                
            } else {
                conditionFormatter = numberFormatterFactory.create(section);
                
            }
            
            formatter.addConditionFormatter(conditionFormatter);
            
        }
        
        // 条件式の設定
        int sectionSize = sections.size();
        if(containsTextFormatter) {
            // 文字列の書式を含む場合は、個数から除外する。
            sectionSize--;
        }
        
        // 1番目の書式がデフォルトの条件の場合
        boolean hasConditionFirst = false;
        
        for(int i=0; i < sectionSize; i++) {
            
            final ConditionFormatter conditionFormatter = formatter.getConditionFormatters().get(i);
            if(conditionFormatter.getOperator() != null) {
                if(i == 0) {
                    hasConditionFirst = true;
                }
                continue;
            }
            
            if(sectionSize <= 1) {
                // 書式が1つしかない場合
                conditionFormatter.setOperator(ConditionOperator.ALL);
                
            } else if(sectionSize == 2) {
                if(i==0) {
                    // ゼロ以上の数の書式
                    conditionFormatter.setOperator(ConditionOperator.NON_NEGATIVE);
                    
                } else if(i==1) {
                    // その他の書式
                    if(hasConditionFirst) {
                        conditionFormatter.setOperator(ConditionOperator.ALL);
                    } else {
                        conditionFormatter.setOperator(ConditionOperator.NEGATIVE);
                    }
                }
                
            } else if(sectionSize == 3) {
                if(i==0) {
                    // 正の書式
                    conditionFormatter.setOperator(ConditionOperator.POSITIVE);
                    
                } else if(i==1) {
                    // 負の数の書式
                    conditionFormatter.setOperator(ConditionOperator.NEGATIVE);
                    
                } else {
                    // その他の書式
                    conditionFormatter.setOperator(ConditionOperator.ALL);
                    
                }
                
            } else {
                throw new CustomFormatterParseException(pattern,
                        String.format("section size over 4. but '%s' number of %d secitions.", pattern, sections.size())); 
            }
            
        }
        
        return formatter;
    }
    
}
