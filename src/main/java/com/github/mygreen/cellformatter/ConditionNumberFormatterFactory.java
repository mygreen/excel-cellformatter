package com.github.mygreen.cellformatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mygreen.cellformatter.lang.ArgUtils;
import com.github.mygreen.cellformatter.lang.Utils;
import com.github.mygreen.cellformatter.number.FormattedNumber;
import com.github.mygreen.cellformatter.number.NumberFactory;
import com.github.mygreen.cellformatter.number.PartType;
import com.github.mygreen.cellformatter.term.AsteriskTerm;
import com.github.mygreen.cellformatter.term.EscapedCharTerm;
import com.github.mygreen.cellformatter.term.NumberTerm;
import com.github.mygreen.cellformatter.term.OtherTerm;
import com.github.mygreen.cellformatter.term.Term;
import com.github.mygreen.cellformatter.term.UnderscoreTerm;
import com.github.mygreen.cellformatter.term.WordTerm;
import com.github.mygreen.cellformatter.tokenizer.Token;
import com.github.mygreen.cellformatter.tokenizer.TokenStore;


/**
 * {@link ConditionNumberFormatter}のインスタンスを作成するクラス。
 * @author T.TSUCHIE
 *
 */
public class ConditionNumberFormatterFactory extends ConditionFormatterFactory<ConditionNumberFormatter> {
    
    private static final Logger logger = LoggerFactory.getLogger(ConditionNumberFormatterFactory.class);
    
    private static final String[] FORMAT_CHARS = {
        "#", "0", "?",
    };
    
    public static final String[] SYMBOL_CHARS = {
        ".", ",",
        "%", "/",
    };
    
    public static final String[] OTHER_CHARS = {
        "E", "E+", "E-",
        "General",
    };
    
    public static final String[] DIGITS_START_CHARS = {
        "1", "2", "3", "4", "5", "6", "7", "8", "9",
    };
    
    public static final String[] DIGITS_CHARS = {
        "1", "2", "3", "4", "5", "6", "7", "8", "9", "0",
    };
    
    /**
     * 数値の書式かどうかを決定するためのキーワード。
     */
    private static final String[] NUMBER_DECISTION_CHARS = toArray(
            FORMAT_CHARS,
            SYMBOL_CHARS
    );
    
    /**
     * 数値の項としてのキーワード
     */
    private static final String[] NUMBER_TERM_CHARS = toArray(
            FORMAT_CHARS,
            SYMBOL_CHARS,
            OTHER_CHARS,
            DIGITS_START_CHARS
    );
    
    /**
     * {@link #NUMBER_TERM_CHARS}を、検索用に並び替えたもの。
     * ・フォーマットのキーワードを①文字列の長い順、辞書順に並び変え、比較していく。
     */
    private static final List<String> SORTED_NUMBER_TERM_CHARS = Utils.reverse(NUMBER_TERM_CHARS);
    
    /**
     * 数値の書式かどうか判定する。
     * @param store
     * @return
     */
    public boolean isNumberPattern(final TokenStore store) {
        return store.containsAnyInFactor(NUMBER_DECISTION_CHARS);
    }
    
    @Override
    public ConditionNumberFormatter create(final TokenStore store) {
        ArgUtils.notNull(store, "store");
        
        final ConditionNumberFormatter formatter = new ConditionNumberFormatter(store.getConcatenatedToken());
        
        for(Token token : store.getTokens()) {
            
            if(token instanceof Token.Condition) {
                // 条件の場合
                final Token.Condition conditionToken = token.asCondition();
                final String condition = conditionToken.getCondition();
                formatter.addCondition(condition);
                
                if(isConditionOperator(conditionToken)) {
                    setupConditionOperator(formatter, conditionToken);
                    
                } else if(isConditionLocale(conditionToken)) {
                    setupConditionLocale(formatter, conditionToken);
                    
                } else if(isConditionDbNum(conditionToken)) {
                    setupConditionDbNum(formatter, conditionToken);
                    
                } else if(isConditionColor(conditionToken)) {
                    setupConditionColor(formatter, conditionToken);
                    
                }
                
            } else if(token instanceof Token.Word) {
                formatter.addTerm(new WordTerm<FormattedNumber>(token.asWord()));
                
            } else if(token instanceof Token.EscapedChar) {
                formatter.addTerm(new EscapedCharTerm<FormattedNumber>(token.asEscapedChar()));
                
            } else if(token instanceof Token.Underscore) {
                formatter.addTerm(new UnderscoreTerm<FormattedNumber>(token.asUnderscore()));
                
            } else if(token instanceof Token.Asterisk) {
                formatter.addTerm(new AsteriskTerm<FormattedNumber>(token.asAsterisk()));
                
            } else if(token instanceof Token.Factor) {
                // 因子を数値の書式に分解する
                final List<Token> list = convertFactor(token.asFactor());
                for(Token item : list) {
                    
                    if(item instanceof Token.Formatter) {
                        if(item.getValue().equals("#")) {
                            formatter.addTerm(NumberTerm.sharp());
                            
                        } else if(item.getValue().equals("0")) {
                            formatter.addTerm(NumberTerm.zero());
                            
                        } else if(item.getValue().equals("?")) {
                            formatter.addTerm(NumberTerm.question());
                            
                        } else {
                            logger.warn("unknown formatter : '{}'", item.getValue());
                        }
                        
                    } else if(item instanceof Token.Factor) {
                        if(Utils.startsWithIgnoreCase(item.getValue(), "E")) {
                            formatter.addTerm(NumberTerm.exponnet(item));
                            
                        } else if(item.getValue().equals("General")) {
                            formatter.addTerm(NumberTerm.general());
                            
                        } else {
                            formatter.addTerm(new OtherTerm<FormattedNumber>(item));
                            
                        }
                        
                    } else if(item instanceof Token.Symbol) {
                        if(item == Token.SYMBOL_COLON) {
                            formatter.addTerm(NumberTerm.separator(item.asSymbol()));
                            
                        } else {
                            formatter.addTerm(NumberTerm.symbol(item.asSymbol()));
                        }
                        
                    } else if(item instanceof Token.Digits) {
                        formatter.addTerm(NumberTerm.digits(item.asDigits()));
                        
                    } else {
                        formatter.addTerm(new OtherTerm<FormattedNumber>(item));
                    }
                    
                }
                
            } else {
                formatter.addTerm(new OtherTerm<FormattedNumber>(token));
            }
        }
        
        // 書式に付加情報を設定する
        setupFormat(formatter);
        
        return formatter;
    }
    
    private List<Token> convertFactor(final Token.Factor factor) {
        
        final String item = factor.getValue();
        final int itemLength = item.length();
        
        final List<Token> list = new ArrayList<>();
        
        int idx = 0;
        // フォーマット以外の文字列を積む
        StringBuilder noTermChar = new StringBuilder();
        
        while(idx < itemLength) {
            
            String matchChars = null;
            for(String chars : SORTED_NUMBER_TERM_CHARS) {
                if(Utils.startsWithIgnoreCase(item, chars, idx)) {
                    matchChars = item.substring(idx, idx + chars.length());
                    break;
                }
            }
            
            if(matchChars == null) {
                // フォーマット出ない場合は、文字列としてバッファに追加する。
                noTermChar.append(item.charAt(idx));
                idx++;
                
            } else {
                // 数値の書式の場合
                
                if(noTermChar.length() > 0) {
                    // 今まで積んだバッファを、文字列として分割する。
                    list.add(Token.factor(noTermChar.toString()));
                    noTermChar = new StringBuilder();
                }
                
                if(Utils.equalsAny(matchChars, DIGITS_START_CHARS)) {
                    // 数字として切り出す。
                    StringBuilder digits = new StringBuilder();
                    digits.append(matchChars);
                    
                    for(int i=idx+1; i < itemLength; i++) {
                        final String str = String.valueOf(item.charAt(i));
                        if(Utils.equalsAny(str, DIGITS_CHARS)) {
                            digits.append(str);
                        } else {
                            break;
                        }
                    }
                    
                    list.add(Token.digits(digits.toString()));
                    idx += digits.length();
                    
                } else {
                    if(Utils.equalsAny(matchChars, FORMAT_CHARS)) {
                        list.add(Token.formatter(matchChars));
                        
                    } else if(Utils.equalsAny(matchChars, SYMBOL_CHARS)) {
                        if(matchChars.equals(".")) {
                            list.add(Token.SYMBOL_DOT);
                            
                        } else if(matchChars.equals(",")) {
                            list.add(Token.SYMBOL_COLON);
                            
                        } else if(matchChars.equals("%")) {
                            list.add(Token.SYMBOL_PERCENT);
                            
                        } else if(matchChars.equals("/")) {
                            list.add(Token.SYMBOL_SLASH);
                            
                        } else {
                            logger.warn("unknown symbol : '{}'", matchChars);
                        }
                        
                    } else {
                        list.add(Token.factor(matchChars));
                    }
                    
                    idx += matchChars.length();
                    
                }
                
            }
            
        }
        
        if(noTermChar.length() > 0) {
            list.add(Token.factor(noTermChar.toString()));
            noTermChar = new StringBuilder();   
        }
        
        return list;
        
    }
    
    /**
     * 構築した項に対してインデックス番号などを付与する。
     * @param formatter
     */
    private void setupFormat(final ConditionNumberFormatter formatter) {
        
        if(formatter.containsSymbolTerm(Token.SYMBOL_SLASH)) {
            // 分数として処理する
            setupFormatAsFraction(formatter);
        } else {
            // 数値として処理する
            setupFormatAsDecimal(formatter);
        }
        
    }
    
    private void setupFormatAsFraction(final ConditionNumberFormatter formatter) {
        
        final int termSize = formatter.getTerms().size();
        
        // 分数の区切り記号の位置
        int slashIndex = -1;
        for(int i=0; i < termSize; i++) {
            
            final Term<FormattedNumber> term = formatter.getTerms().get(i);
            if(isSymbolTerm(term, Token.SYMBOL_SLASH)) {
                slashIndex = i;
                break;
            }
            
        }
        
        // 分子、帯分数の情報の設定
        boolean wholeType = false;
        if(slashIndex > 0) {
            PartType partType = PartType.Numerator;
            int countNumeratorTerm = 0;
            int countWholeNumberTerm = 0;
            boolean foundFirst = false;
            
            for(int i=slashIndex-1; i >= 0; i--) {
                final Term<FormattedNumber> term = formatter.getTerms().get(i);
                
                if(term instanceof NumberTerm.FormattedTerm) {
                    final NumberTerm.FormattedTerm formattedTerm = (NumberTerm.FormattedTerm) term;
                    formattedTerm.setPart(partType);
                    
                    if(partType.equals(PartType.Numerator)) {
                        countNumeratorTerm++;
                        formattedTerm.setIndex(countNumeratorTerm);
                        
                    } else if(partType.equals(PartType.WholeNumber)) {
                        countWholeNumberTerm++;
                        formattedTerm.setIndex(countWholeNumberTerm);
                    }
                    
                    if(!foundFirst) {
                        foundFirst = true;
                    }
                    
                } else {
                    // 連続する書式の間に他の文字が入る場合は、帯分数として処理する。
                    if(foundFirst) {
                        partType = PartType.WholeNumber;
                    }
                }
                
            }
            
            if(countWholeNumberTerm > 0) {
                // 帯分数かどうか
                wholeType = true;
            }
        }
        
        // 分母の処理
        boolean exactDenom=false;
        int denominator = -1;
        if(slashIndex < termSize) {
            int countDenominatorTerm = 0;
            int exactDenominator = -1;
            for(int i=termSize-1; i > slashIndex; i--) {
                
                final Term<FormattedNumber> term = formatter.getTerms().get(i);
                if(term instanceof NumberTerm.FormattedTerm) {
                    final NumberTerm.FormattedTerm formattedTerm = (NumberTerm.FormattedTerm) term;
                    formattedTerm.setPart(PartType.Denominator);
                    
                    countDenominatorTerm++;
                    formattedTerm.setIndex(countDenominatorTerm);
                    
                } else if(term instanceof NumberTerm.DigitsTerm) {
                    final NumberTerm.DigitsTerm digitsTerm = (NumberTerm.DigitsTerm) term;
                    exactDenominator = digitsTerm.getToken().intValue();
                    
                }
            }
            
            if(exactDenominator > 0) {
                denominator = exactDenominator;
                exactDenom = true;
                
            } else if(countDenominatorTerm > 0) {
                if(countDenominatorTerm >= 5) {
                    // 5桁以上は対応していない
                    denominator = (int) Math.pow(10, 5);
                } else {
                    denominator = (int) Math.pow(10, countDenominatorTerm);
                }
            } else {
                denominator = 10;
            }
        }
        
        // 最後の項かどうかのフラグを設定する
        boolean foundFistWholeNumber = false;
        boolean foundFistNumerator = false;
        boolean foundFistDenominator = false;
        for(Term<FormattedNumber> term : formatter.getTerms()) {
            
            if(term instanceof NumberTerm.FormattedTerm) {
                final NumberTerm.FormattedTerm formattedTerm = (NumberTerm.FormattedTerm) term;
                
                if(formattedTerm.getPartType().equals(PartType.WholeNumber) && !foundFistWholeNumber) {
                    formattedTerm.setLastPart(true);
                    foundFistWholeNumber = true;
                    
                } else if(formattedTerm.getPartType().equals(PartType.Numerator) && !foundFistNumerator) {
                    formattedTerm.setLastPart(true);
                    foundFistNumerator = true;
                    
                } else if(formattedTerm.getPartType().equals(PartType.Denominator) && !foundFistDenominator) {
                    formattedTerm.setLastPart(true);
                    foundFistDenominator = true;
                }
                
            }
        }
        
        formatter.setNumberFactory(NumberFactory.fractionNumber(denominator, exactDenom, wholeType));
        
    }
    
    private void setupFormatAsDecimal(final ConditionNumberFormatter formatter) {
        
        PartType partType = PartType.Integer;
        boolean foundFirst = false;
        int countDecimalTerm = 0;   // 小数部分の書式のカウント
        boolean foundPercentTerm = false;
        
        for(Term<FormattedNumber> term : formatter.getTerms()) {
            
            if(isSymbolTerm(term, Token.SYMBOL_PERCENT)) {
                foundPercentTerm = true;
                
            } else if(isSymbolTerm(term, Token.SYMBOL_DOT)) {
                partType = PartType.Decimal;
                foundFirst = false;
                
            } else if(term instanceof NumberTerm.ExponentTerm) {
                partType = PartType.Exponent;
                foundFirst = false;
                
            } else if(term instanceof NumberTerm.FormattedTerm) {
                final NumberTerm.FormattedTerm formattedTerm = (NumberTerm.FormattedTerm) term;
                formattedTerm.setPart(partType);
                
                if(partType.equals(PartType.Decimal)) {
                    // 小数部分の場合のインデックス番号を振る
                    countDecimalTerm++;
                    formattedTerm.setIndex(countDecimalTerm);
                    
                } else if(!foundFirst) {
                    // 整数部分、指数部分の先頭の書式、最後の桁となる
                    formattedTerm.setLastPart(true);
                    foundFirst = true;
                }
                
            }
        }
        
        // 最後から探索し、インデックス番号を振る
        foundFirst = false;
        int countIntegerTerm = 0;
        int countExponentTerm = 0;
        
        final int termSize = formatter.getTerms().size();
        for(int i=0; i < termSize; i++) {
            final Term<FormattedNumber> term = formatter.getTerms().get(termSize-i-1);
            
            if(term instanceof NumberTerm.FormattedTerm) {
                
                final NumberTerm.FormattedTerm formattedTerm = (NumberTerm.FormattedTerm) term;
                
                if(formattedTerm.getPartType().equals(PartType.Decimal)) {
                    // 小数部分の場合、はじめに見つかったものが、最後の桁
                    if(!foundFirst) {
                        formattedTerm.setLastPart(true);
                        foundFirst = true;
                    }
                    
                } else if(formattedTerm.getPartType().equals(PartType.Integer)) {
                    countIntegerTerm++;
                    formattedTerm.setIndex(countIntegerTerm);
                    
                } else if(formattedTerm.getPartType().equals(PartType.Exponent)) {
                    countExponentTerm++;
                    formattedTerm.setIndex(countExponentTerm);
                }
                
            }
            
        }
        
        // 桁区切りの判定処理
        boolean useSeparator = false;
        boolean inIntegerPater = false;
        for(Term<FormattedNumber> term : formatter.getTerms()) {
            if(term instanceof NumberTerm.FormattedTerm) {
                final NumberTerm.FormattedTerm formattedTerm = (NumberTerm.FormattedTerm) term;
                // 整数の書式でかつ途中の書式の場合
                if(formattedTerm.getPartType().equals(PartType.Integer)) {
                    if(formattedTerm.isLastPart() && formattedTerm.getIndex() > 1) {
                        inIntegerPater = true;
                    } else if(formattedTerm.getIndex() <= 1) {
                        inIntegerPater = false;
                    }
                }
            }
            
            if(term instanceof NumberTerm.SeparatorTerm) {
                if(inIntegerPater) {
                    useSeparator = true;
                }
                
            }
        }
        
        // 最後の書式の直後の区切り文字の判定
        int indexLastFormatterdTerm = -1;
        for(int i=0; i < termSize; i++) {
            final Term<FormattedNumber> term = formatter.getTerms().get(termSize-i-1);
            if(term instanceof NumberTerm.FormattedTerm) {
                indexLastFormatterdTerm = termSize-i-1;
                break;
            }
        }
        
        int countLastColon = 0;
        if(indexLastFormatterdTerm >= 0 && indexLastFormatterdTerm+1 < termSize) {
            // 最後の書式の直後の連続するカンマの個数をカウントする
            for(int i=indexLastFormatterdTerm+1; i < termSize; i++) {
                final Term<FormattedNumber> term = formatter.getTerms().get(i);
                if(term instanceof NumberTerm.SeparatorTerm) {
                    countLastColon++;
                } else {
                    break;
                }
            }
        }
        
        if(countExponentTerm > 0) {
            // 指数の場合
            formatter.setNumberFactory(NumberFactory.exponentNumber(countDecimalTerm, useSeparator));
            
        } else if(foundPercentTerm) {
            // 百分率の場合
            formatter.setNumberFactory(NumberFactory.percentNumber(countDecimalTerm, useSeparator, countLastColon));
            
        } else {
            formatter.setNumberFactory(NumberFactory.decimalNumber(countDecimalTerm, useSeparator, countLastColon));
            
        }
        
    }
    
    private static boolean isSymbolTerm(final Term<FormattedNumber> term, final Token.Symbol symbol) {
        
        if(!(term instanceof NumberTerm.SymbolTerm)) {
            return false;
        }
        
        final NumberTerm.SymbolTerm symbolTerm = (NumberTerm.SymbolTerm) term;
        return symbolTerm.getToken().equals(symbol);
        
    }
    
    private static String[] toArray(final String[]... arrays) {
        List<String> list = new ArrayList<>();
        for(String[] array : arrays) {
            list.addAll(Arrays.asList(array));
        }
        
        return list.toArray(new String[list.size()]);
    }
    
}
