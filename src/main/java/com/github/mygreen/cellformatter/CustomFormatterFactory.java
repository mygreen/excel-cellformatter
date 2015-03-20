package com.github.mygreen.cellformatter;

import java.util.List;

import com.github.mygreen.cellformatter.tokenizer.CustomFormatTokenizer;
import com.github.mygreen.cellformatter.tokenizer.Token;
import com.github.mygreen.cellformatter.tokenizer.TokenStore;


/**
 * {@link CustomFormatter}のインスタンスを作成するクラス。
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
     * @return
     */
    public CustomFormatter create(final String pattern) {
        
        final CustomFormatTokenizer tokenizer = new CustomFormatTokenizer();
        final TokenStore allStore = tokenizer.parse(pattern);
        if(allStore.getTokens().isEmpty()) {
            // 標準のフォーマッタ
            return CustomFormatter.DEFAULT_FORMATTER;
        }
        
        final CustomFormatter formatter = new CustomFormatter(pattern);
        
        // ユーザ定義に分割し、処理していく。
        final List<TokenStore> stores = allStore.split(Token.SYMBOL_SEMI_COLON);
        final int storeSize = stores.size();
        for(int i=0; i < storeSize; i++) {
            
            final TokenStore storeItem = stores.get(i);
            
            if(textFormatterFactory.isTextPattern(storeItem)) {
                formatter.setTextFormatter(textFormatterFactory.create(storeItem));
                continue; 
            }
            
            /*
             * フォーマットのインデックス番号の判定する。
             * 番号によって、数値の条件が付与されていない書式は、自動的に正、負、ゼロと判定する。
             */
            int formatIndex = i;
            if(storeSize <= 1) {
                // // 区切り文字がない場合、インデックス番号に意味がない場合は-1をする。
                formatIndex = -1;
            }
            
            /*
             * 1番目の書式がデフォルトの条件の場合
             */
            boolean hasConditionFirst = false;
            if(i >= 1) {
                hasConditionFirst = !formatter.getConditionFormatters().get(0).getOperator().equals(ConditionOperator.POSITIVE);
            }
            
            ConditionFormatter<?> conditionFormatter;
            if(dateFormatterFactory.isDatePattern(storeItem)) {
                conditionFormatter = dateFormatterFactory.create(storeItem);
                
            } else if(numberFormatterFactory.isNumberPattern(storeItem)) {
                conditionFormatter = numberFormatterFactory.create(storeItem);
                
            } else {
                conditionFormatter = numberFormatterFactory.create(storeItem);
                
            }
            
            formatter.addConditionFormatter(conditionFormatter);
            
            // 条件がない場合の指定
            if(conditionFormatter.getOperator() == null) {
                if(formatIndex == 0) {
                    conditionFormatter.setOperator(ConditionOperator.POSITIVE);
                } else if(formatIndex == 1) {
                    if(hasConditionFirst) {
                        conditionFormatter.setOperator(ConditionOperator.ALL);
                    } else {
                        conditionFormatter.setOperator(ConditionOperator.NEGATIVE);
                    }
                } else if(formatIndex == 2) {
                    // 3番目はゼロの場合ではなく、その他の場合であることに注意
                    conditionFormatter.setOperator(ConditionOperator.ALL);
                } else {
                    conditionFormatter.setOperator(ConditionOperator.ALL);
                }
            }
            
        }
        
        // フォーマッタが1つしか内場合は、補正する。
        if(formatter.getConditionFormatters().size() == 1) {
           formatter.getConditionFormatters().get(0).setOperator(ConditionOperator.ALL);
        }
        
        return formatter;
    }
    
}
