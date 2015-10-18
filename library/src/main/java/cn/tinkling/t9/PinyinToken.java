package cn.tinkling.t9;

@Deprecated
public class PinyinToken {

    /**
     * Separator between target string for each source char
     */
    public static final String SEPARATOR = " ";

    /**
     * Type of ASCII.
     */
    public static final int LATIN = 1;
    /**
     * Type of PINYIN.
     */
    public static final int PINYIN = 2;
    /**
     * Type of UNKNOWN.
     */
    public static final int UNKNOWN = 3;

    public PinyinToken() {
    }

    public PinyinToken(final int type, final String source, final String target) {
        this.type = type;
        this.source = source;
        this.target = target;
    }

    /**
     * Type of this token, ASCII, PINYIN or UNKNOWN.
     */
    public int type;
    /**
     * Original string before translation.
     */
    public String source;
    /**
     * Translated string of source. For Han, target is corresponding Pinyin.
     * Otherwise target is original string in source.
     */
    public String target;
}
