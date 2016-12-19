package com.github.mygreen.cellformatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mygreen.cellformatter.lang.ArgUtils;
import com.github.mygreen.cellformatter.lang.Utils;
import com.github.mygreen.cellformatter.term.AsteriskTerm;
import com.github.mygreen.cellformatter.term.DateTerm;
import com.github.mygreen.cellformatter.term.EscapedCharTerm;
import com.github.mygreen.cellformatter.term.LocaelSymbolTerm;
import com.github.mygreen.cellformatter.term.OtherTerm;
import com.github.mygreen.cellformatter.term.Term;
import com.github.mygreen.cellformatter.term.UnderscoreTerm;
import com.github.mygreen.cellformatter.term.WordTerm;
import com.github.mygreen.cellformatter.tokenizer.Token;
import com.github.mygreen.cellformatter.tokenizer.TokenStore;


/**
 * 書式を解析して{@link ConditionDateFormatter}のインスタンスを作成するクラス。
 * @author T.TSUCHIE
 *
 */
public class ConditionDateFormatterFactory extends ConditionFormatterFactory<ConditionDateFormatter> {
    
    private static Logger logger = LoggerFactory.getLogger(ConditionDateFormatterFactory.class);
    
    /**
     * 日時の書式かどうかを決定するためのキーワード。
     */
    private static final String[] DATE_DECISTION_CHARS = {
        "yy", "yyyy",
        "m", "mm", "mmm", "mmmm", "mmmmm",
        "d", "dd", "ddd", "dddd",
        "g", "gg", "ggg",
        /*"e",*/ "ee",  // e単体だと指数と区別がつかないので除外する。
        "aaa", "aaaa",
        "r", "rr",
        "h", "hh",
        "s", "ss",
        "am/pm", "a/p",
        "q", "qq",    // OpenOffice用
        "nn", "nnn",  // OpenOffice用
        "ww",         // OpenOffice用
    };
    
    /**
     * 日時で使用するフォーマット用の文字
     */
    private static final String[] DATE_TERM_CHARS = {
        "yy", "yyyy",
        "m", "mm", "mmm", "mmmm", "mmmmm",
        "d", "dd", "ddd", "dddd",
        "g", "gg", "ggg",
        "e", "ee",
        "aaa", "aaaa",
        "r", "rr",
        "h", "hh",
        "s", "ss",
        "am/pm", "a/p",
        "q", "qq",  // OpenOffice用
        "nn",       // OpenOffice用
        "ww",       // OpenOffice用
    };
    
    /**
     * {@link #DATE_TERM_CHARS}を、検索用に並び替えたもの。
     * ・フォーマットのキーワードを①文字列の長い順、辞書順に並び変え、比較していく。
     */
    private static final List<String> SORTED_DATE_CHARS = Utils.reverse(DATE_TERM_CHARS);
    
    /**
     * 経過時間の時刻のパターン
     */
    private static final Pattern PATTERN_ELAPSED_TIME = Pattern.compile("\\[([h]+|[m]+|[s]+)\\]", Pattern.CASE_INSENSITIVE);
    
    /**
     * 日時の書式かどうか判定する。
     * @param store
     * @return
     */
    public boolean isDatePattern(final TokenStore store) {
        
        if(store.containsInFactor("General")) {
            return false;
        }
        
        if(store.containsAnyInFactorIgnoreCase(DATE_DECISTION_CHARS)) {
            return true;
        }
        
        // [h][m][s]の形式のチェック
        for(Token token : store.getTokens()) {
            if(!(token instanceof Token.Condition)) {
                continue;
            }
            
            final Token.Condition condition = token.asCondition();
            final String value = condition.getValue();
            if(PATTERN_ELAPSED_TIME.matcher(value).matches()) {
                return true;
            }
            
        }
        
        return false;
    }
    
    /**
     * {@link ConditionDateFormatter}インスタンスを作成する。
     * @param store
     * @return
     * @throws IllegalArgumentException store is null.
     */
    @Override
    public ConditionDateFormatter create(final TokenStore store) {
        ArgUtils.notNull(store, "store");
        
        final ConditionDateFormatter formatter = new ConditionDateFormatter(store.getConcatenatedToken());
        
        for(Token token : store.getTokens()) {
            
            if(token instanceof Token.Condition) {
                // 条件の場合
                final Token.Condition conditionToken = token.asCondition();
                final String condition = conditionToken.getCondition();
                
                if(PATTERN_ELAPSED_TIME.matcher(token.getValue()).matches()) {
                    // [h][m][s]などの経過時刻のパターン
                    if(Utils.startsWithIgnoreCase(condition, "h")) {
                        formatter.addTerm(DateTerm.elapsedHour(condition));
                        
                    } else if(Utils.startsWithIgnoreCase(condition, "m")) {
                        formatter.addTerm(DateTerm.elapsedMinute(condition));
                        
                    } else if(Utils.startsWithIgnoreCase(condition, "s")) {
                        formatter.addTerm(DateTerm.elapsedSecond(condition));
                        
                    }
                    continue;
                }
                
                formatter.addCondition(condition);
                
                if(isConditionOperator(conditionToken)) {
                    setupConditionOperator(formatter, conditionToken);
                    
                } else if(isConditionLocale(conditionToken)) {
                    setupConditionLocale(formatter, conditionToken);
                    
                } else if(isConditionLocaleSymbol(conditionToken)) {
                    final LocaleSymbol localeSymbol = setupConditionLocaleSymbol(formatter, conditionToken);
                    formatter.addTerm(new LocaelSymbolTerm<Calendar>(localeSymbol));
                    
                } else if(isConditionDbNum(conditionToken)) {
                    setupConditionDbNum(formatter, conditionToken);
                    
                } else if(isConditionColor(conditionToken)) {
                    setupConditionColor(formatter, conditionToken);
                    
                }
                
            } else if(token instanceof Token.Word) {
                formatter.addTerm(new WordTerm<Calendar>(token.asWord()));
                
            } else if(token instanceof Token.EscapedChar) {
                formatter.addTerm(new EscapedCharTerm<Calendar>(token.asEscapedChar()));
                
            } else if(token instanceof Token.Underscore) {
                formatter.addTerm(new UnderscoreTerm<Calendar>(token.asUnderscore()));
                
            } else if(token instanceof Token.Asterisk) {
                formatter.addTerm(new AsteriskTerm<Calendar>(token.asAsterisk()));
                
            } else if(token instanceof Token.Factor) {
                // 因子を日時用の書式に分解する
                final List<Token> list = convertFactor(token.asFactor());
                for(Token item : list) {
                    
                    if(item instanceof Token.Formatter) {
                        final String formatterItem = item.asFormatter().getValue();
                        
                        if(Utils.equalsAnyIgnoreCase(formatterItem, new String[]{"am/pm", "a/p"})) {
                            formatter.addTerm(DateTerm.amPm(formatterItem));
                            
                        } else if(Utils.startsWithIgnoreCase(formatterItem, "w")) {
                            formatter.addTerm(DateTerm.weekNumber(formatterItem));
                            
                        } else if(Utils.startsWithIgnoreCase(formatterItem, "y")) {
                            formatter.addTerm(DateTerm.year(formatterItem));
                            
                        } else if(Utils.startsWithIgnoreCase(formatterItem, "g")) {
                            formatter.addTerm(DateTerm.eraName(formatterItem));
                            
                        } else if(Utils.startsWithIgnoreCase(formatterItem, "e")) {
                            formatter.addTerm(DateTerm.eraYear(formatterItem));
                            
                        } else if(Utils.startsWithIgnoreCase(formatterItem, "r")) {
                            formatter.addTerm(DateTerm.eraNameYear(formatterItem));
                            
                        } else if(Utils.startsWithIgnoreCase(formatterItem, "m")) {
                            // 月か分かの判定は、全ての書式を組み立て後に行う。
                            formatter.addTerm(DateTerm.month(formatterItem));
                            
                        } else if(Utils.startsWithIgnoreCase(formatterItem, "d")) {
                            formatter.addTerm(DateTerm.day(formatterItem));
                            
                        } else if(Utils.startsWithIgnoreCase(formatterItem, "a")) {
                            formatter.addTerm(DateTerm.weekName(formatterItem));
                            
                        } else if(Utils.startsWithIgnoreCase(formatterItem, "n")) {
                            formatter.addTerm(DateTerm.weekNameForOO(formatterItem));
                            
                        } else if(Utils.startsWithIgnoreCase(formatterItem, "h")) {
                            final boolean halfHour = store.containsAnyInFactorIgnoreCase(new String[]{"am/pm", "a/p"});
                            formatter.addTerm(DateTerm.hour(formatterItem, halfHour));
                            
                        } else if(Utils.startsWithIgnoreCase(formatterItem, "s")) {
                            formatter.addTerm(DateTerm.second(formatterItem));
                            
                        } else if(Utils.startsWithIgnoreCase(formatterItem, "q")) {
                            formatter.addTerm(DateTerm.quater(formatterItem));
                            
                        } else {
                            // ここには到達しない
                            if(logger.isWarnEnabled()) {
                                logger.warn("unknown date format terms '{}'.", formatterItem);
                            }
                            formatter.addTerm(new OtherTerm<Calendar>(item));
                        }
                        
                    } else {
                        formatter.addTerm(new OtherTerm<Calendar>(item));
                    }
                    
                }
            } else {
                formatter.addTerm(new OtherTerm<Calendar>(token));
            }
        }
        
        // 書式'm'の項を分に変換する処理を行う
        convertMinuteTerm(formatter);
        
        return formatter;
        
    }
    
    /**
     * 書式の因子を日時用とそれ以外に変換する。
     * @param factor
     * @return
     */
    private List<Token> convertFactor(final Token.Factor factor) {
        
        final String item = factor.getValue();
        final int itemLength = item.length();
        
        final List<Token> list = new ArrayList<>();
        
        int idx = 0;
        StringBuilder noTermChar = new StringBuilder(); // フォーマット以外の文字列を積む。
        while(idx < itemLength) {
            
            String matchChars = null;
            for(String chars : SORTED_DATE_CHARS) {
                if(Utils.startsWithIgnoreCase(item, chars, idx)) {
                    matchChars = item.substring(idx, idx + chars.length());
                    break;
                }
            }
            
            if(matchChars == null) {
                // フォーマットでない場合は、文字列としてバッファに追加する。
                noTermChar.append(item.charAt(idx));
                idx++;
            } else {
                if(noTermChar.length() > 0) {
                    // 今まで積んだバッファを、文字列として分割する。
                    list.add(Token.factor(noTermChar.toString()));
                    noTermChar = new StringBuilder();
                }
                
                list.add(Token.formatter(matchChars));
                idx += matchChars.length();
                
            }
        }
        
        if(noTermChar.length() > 0) {
            list.add(Token.factor(noTermChar.toString()));
        }
        
        return list;
    }
    
    /**
     * 組み立てた項の中で、月の項を分に変換する。
     * 
     * @param formatter
     */
    private void convertMinuteTerm(final ConditionDateFormatter formatter) {
        
        final int termSize = formatter.getTerms().size();
        for(int i=0; i < termSize; i++) {
            final Term<Calendar> term = formatter.getTerms().get(i);
            if(!(term instanceof DateTerm.MonthTerm)) {
                continue;
            }
            
            if(isMinuteTerm(i, formatter.getTerms())) {
                // '分'の項に入れ替える
                final DateTerm.MonthTerm monthTerm = (DateTerm.MonthTerm) term;
                formatter.getTerms().set(i, DateTerm.minute(monthTerm.getFormat()));
            }
            
        }
        
    }
    
    /**
     * 現在の項が'分'が判定する。
     * ・現在の直前の項が、'h'（時間）を示すフォーマットがあるかどうか。
     * ・現在の直後の項が、's'（秒）を示すフォーマットがあるかどうか。
     * 
     * @param currentTermIdx
     * @param terms
     * @return
     */
    private boolean isMinuteTerm(final int currentTermIdx, final List<Term<Calendar>> terms) {
        
        final int termSize = terms.size();
        
        // 直前の項のチェック
        if(currentTermIdx -1 > 0) {
            DateTerm beforeTerm = null;
            for(int i=currentTermIdx-1; i >= 0; i--) {
                final Term<Calendar> term = terms.get(i);
                if(term instanceof DateTerm) {
                    beforeTerm = (DateTerm) term;
                    break;
                }
                
            }
            
            if(beforeTerm != null) {
                if(beforeTerm instanceof DateTerm.HourTerm || beforeTerm instanceof DateTerm.ElapsedHourTerm) {
                    return true;
                }
            }
            
        }
        
        // 直後の項のチェック
        if(currentTermIdx +1 < termSize) {
            DateTerm afterTerm = null;
            for(int i=currentTermIdx+1; i < termSize; i++) {
                final Term<Calendar> term = terms.get(i);
                if(term instanceof DateTerm) {
                    afterTerm = (DateTerm) term;
                    break;
                }
            }
            
            if(afterTerm != null) {
                if(afterTerm instanceof DateTerm.SecondTerm || afterTerm instanceof DateTerm.ElapsedSecondTerm) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
}
