package cn.tinkling.t9;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.BitSet;

/**
 * T9 匹配工具类
 */
public final class T9Matcher {

    private T9Matcher() {
    }

    /**
     * T9 匹配
     *
     * @param t9Key        t9 key.
     * @param t9Constraint the constraint.
     * @return the match info.
     * @see T9MatchInfo
     */
    @NonNull
    public static T9MatchInfo matches(@Nullable String t9Key, char t9Constraint) {
        T9MatchInfo matchInfo = new T9MatchInfo();

        if (!TextUtils.isEmpty(t9Key)) {
            char initial = T9Utils.convertDigitToInitial(t9Constraint);
            int index = t9Key.indexOf(initial);
            if (index >= 0) {
                int start = T9Utils.getWordsCount(t9Key, 0, index);
                matchInfo.set(start, 1);
            }
        }
        return matchInfo;
    }

    /**
     * T9 匹配
     *
     * @param t9Key        t9 key.
     * @param t9Constraint the constraint.
     * @return the match info.
     * @see T9MatchInfo
     */
    @NonNull
    public static T9MatchInfo matches(@Nullable String t9Key, @Nullable String t9Constraint) {
        if (TextUtils.isEmpty(t9Key) || TextUtils.isEmpty(t9Constraint))
            return new T9MatchInfo();

        if (t9Constraint.length() == 1) {
            return matches(t9Key, t9Constraint.charAt(0));
        }

        return matchesName(t9Key, 0, t9Key.length(), t9Constraint);
    }

    private static int matchesName(String t9Key, int begin, int end, int start, String t9Constraint,
                                   int cStart, BitSet bitSet) {
        int nextInitialCharIndex = start + 1;

        do {
            if (nextInitialCharIndex >= end ||
                T9Utils.isInitial(t9Key.charAt(nextInitialCharIndex))) {
                if (nextInitialCharIndex == end) {
                    if (t9Key.regionMatches(start + 1, t9Constraint, cStart + 1,
                            -1 + (t9Constraint.length() - cStart))) {
                        bitSet.set(start - 1,
                                ((start - begin) + t9Constraint.length()) - cStart);
                        return 1;
                    } else {
                        return 0;
                    }
                }
                break;
            }
            nextInitialCharIndex++;
        } while (true);

        if (T9Utils.convertDigitToInitial(t9Constraint.charAt(cStart + 1)) ==
            t9Key.charAt(nextInitialCharIndex)) {
            if (t9Constraint.length() == cStart + 2) {
                bitSet.set(start - begin);
                bitSet.set(nextInitialCharIndex - begin);
                return 2;
            }

            int matchCount = matchesName(t9Key, begin, end, nextInitialCharIndex, t9Constraint,
                    cStart + 1, bitSet);
            if (matchCount > 0) {
                bitSet.set(start - begin);
                return matchCount + 1;
            }
        }

        int spanLength = nextInitialCharIndex - start;
        for (int i = nextInitialCharIndex - 1; t9Key.charAt(i) == ' '; ) {
            i--;
            spanLength--;
        }

        if (t9Constraint.length() - cStart <= spanLength) {
            if (t9Key.regionMatches(start + 1, t9Constraint, cStart + 1,
                    -1 + (t9Constraint.length() - cStart))) {
                bitSet.set(start - begin, ((start - begin) + t9Constraint.length()) - cStart);
                return 1;
            } else {
                return 0;
            }
        }

        if (T9Utils.convertDigitToInitial(t9Constraint.charAt(cStart + spanLength)) ==
            t9Key.charAt(nextInitialCharIndex)
            && t9Key.regionMatches(start + 1, t9Constraint, cStart + 1, spanLength - 1)) {
            if (1 + (cStart + spanLength) == t9Constraint.length()) {
                bitSet.set(start - begin, 1 + (nextInitialCharIndex - begin));
                return 2;
            }

            int matchCount = matchesName(t9Key, begin, end, nextInitialCharIndex, t9Constraint,
                    cStart + spanLength, bitSet);
            if (matchCount > 0) {
                bitSet.set(start - begin, nextInitialCharIndex - begin);
                return matchCount + 1;
            }
        }

        return 0;
    }

    private static T9MatchInfo matchesName(String t9Key, int start, int end, String t9Constraint) {
        T9MatchInfo matchInfo = new T9MatchInfo();
        if (end - start < t9Constraint.length())
            return matchInfo;

        final int maxLength = 1 + (end - t9Constraint.length());
        final char first = T9Utils.convertDigitToInitial(t9Constraint.charAt(0));
        int startIndex = start;

        BitSet bitSet = null;
        while (true) {

            if (startIndex >= maxLength) {
                break;
            }

            int index = t9Key.indexOf(first, startIndex);
            if (index < 0 || index >= maxLength) {
                break;
            }

            if (bitSet == null) {
                bitSet = T9Utils.getReusableBitSet();
            }
            bitSet.clear();

            int matchCount = matchesName(t9Key, start, end, index, t9Constraint, 0, bitSet);
            if (matchCount > 0) {
                setMatchResult(t9Key, matchInfo, bitSet);
                break;
            }

            startIndex = index + 1;
        }

        if (bitSet != null) {
            T9Utils.recycleBitSet(bitSet);
        }

        return matchInfo;
    }

    private static void setMatchResult(String t9Key, T9MatchInfo matchInfo, BitSet bitSet) {
        int wordCount = 0;
        int start = -1;

        final int LEN = t9Key.length();
        for (int i = 0; i < LEN; i++) {
            char c = t9Key.charAt(i);
            if (i == 0 || c == ' ' || T9Utils.isInitial(c)) {
                if (bitSet.get(i) && c != ' ') {
                    if (start == -1) {
                        start = wordCount;
                    }
                } else if (start > -1) {
                    matchInfo = checkMatchInfo(matchInfo);
                    matchInfo.set(start, wordCount - start);
                    start = -1;
                }

                wordCount++;
            }
        }

        if (start > -1) {
            matchInfo = checkMatchInfo(matchInfo);
            matchInfo.set(start, wordCount - start);
        }
    }

    @NonNull
    private static T9MatchInfo checkMatchInfo(@NonNull T9MatchInfo matchInfo) {
        if (matchInfo.found()) {
            T9MatchInfo info = new T9MatchInfo();
            matchInfo.setNext(info);
            matchInfo = info;
        }
        return matchInfo;
    }

    /**
     * 电话号码匹配
     *
     * @param phoneNumber 电话号码
     * @param constraint  the constraint
     * @return the match info.
     * @see T9MatchInfo
     */
    @NonNull
    public static T9MatchInfo matchesNumber(@Nullable String phoneNumber,
                                            @Nullable String constraint) {
        T9MatchInfo matchInfo = new T9MatchInfo();
        if (TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(constraint))
            return matchInfo;

        int index = phoneNumber.indexOf(constraint);
        if (index >= 0) {
            matchInfo.set(index, constraint.length());
        }

        return matchInfo;
    }

}
