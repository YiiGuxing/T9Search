package cn.tinkling.t9;

/**
 * 拼音提供者
 */
public interface PinyinProvider {

    /**
     * @param input 输入
     * @return 拼音
     */
    String[] getPinyin(char input);

}
