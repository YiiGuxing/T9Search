package cn.tinkling.t9search.sample;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.ArrayList;

import cn.tinkling.t9.PinyinToken;

/**
 * 汉字转拼音工具
 */
public final class PinyinHelper {

    private static final String TAG = "PinyinHelper";

    private static final HanyuPinyinOutputFormat FORMAT;

    static {
        FORMAT = new HanyuPinyinOutputFormat();
        FORMAT.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        FORMAT.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        FORMAT.setVCharType(HanyuPinyinVCharType.WITH_V);
    }

    private PinyinHelper() {
    }

    /**
     * 汉字转拼音
     */
    @NonNull
    public static ArrayList<PinyinToken> getPinyinTokens(final String input) {
        final ArrayList<PinyinToken> tokens = new ArrayList<>();
        if (TextUtils.isEmpty(input)) {
            // return empty tokens.
            return tokens;
        }

        final int inputLength = input.length();
        int tokenType = PinyinToken.LATIN;
        PinyinToken token = new PinyinToken();

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < inputLength; i++) {
            final char character = input.charAt(i);
            tokenize(character, token);
            if (token.type == PinyinToken.PINYIN) {
                if (sb.length() > 0) {
                    addPinyinToken(sb, tokens, tokenType);
                }
                tokens.add(token);
                token = new PinyinToken();
            } else {
                if (tokenType != token.type && sb.length() > 0) {
                    addPinyinToken(sb, tokens, tokenType);
                }
                sb.append(token.target);
            }
            tokenType = token.type;
        }
        if (sb.length() > 0) {
            addPinyinToken(sb, tokens, tokenType);
        }

        sb.setLength(0);

        return tokens;
    }

    private static void tokenize(final char character, final PinyinToken token) {
        token.source = Character.toString(character);

        // ASCII
        if (character < 128) {
            token.type = PinyinToken.LATIN;
            token.target = token.source;
            return;
        }

        // Extended Latin. Transcode these to ASCII equivalents
        if (character < 0x250 || (0x1e00 <= character && character < 0x1eff)) {
            token.type = PinyinToken.LATIN;
            token.target = token.source;
            return;
        }

        String target = transliterate(character);

        if (TextUtils.isEmpty(target) || TextUtils.equals(token.source, target) ||
            !Character.isLetter(target.charAt(0))) {
            token.type = PinyinToken.UNKNOWN;
            token.target = token.source;
        } else {
            token.type = PinyinToken.PINYIN;
            token.target = target;
        }
    }

    private static String transliterate(char character) {
        try {
            String[] pys = net.sourceforge.pinyin4j.PinyinHelper.toHanyuPinyinStringArray(character,
                    FORMAT);
            if (pys != null && pys.length > 0) {
                return pys[0];
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            Log.w(TAG, "Han-Latin/Names transliterate failed.");
        }

        return null;
    }

    private static void addPinyinToken(final StringBuilder sb, final ArrayList<PinyinToken> tokens,
                                       final int tokenType) {
        final String str = sb.toString();
        tokens.add(new PinyinToken(tokenType, str, str));
        sb.setLength(0);
    }
}
