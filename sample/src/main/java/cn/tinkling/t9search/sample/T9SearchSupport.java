package cn.tinkling.t9search.sample;

import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.tinkling.t9.T9MatchInfo;
import cn.tinkling.t9.T9Matcher;
import cn.tinkling.t9.T9Utils;

public final class T9SearchSupport {

    /**
     * 构建T9键
     *
     * @param input 输入
     * @return T9键
     */
    @NonNull
    public static String buildT9Key(String input) {
        return T9Utils.buildT9Key(PinyinHelper.get(input));
    }

    /**
     * 过虑
     */
    public static List<Contact> filter(List<Contact> contacts, String key) {
        ArrayList<Contact> filtered = new ArrayList<>();

        if (contacts != null && contacts.size() > 0) {
            for (Contact contact : contacts) {
                T9MatchInfo nameMatch = T9Matcher.matches(contact.t9Key, key);
                T9MatchInfo numberMatch = T9Matcher.matchesNumber(contact.phoneNumber, key);

                if (nameMatch.found() || numberMatch.found()) {
                    Contact c = new Contact(contact);
                    c.nameMatchInfo = nameMatch;
                    c.phoneNumberMatchInfo = numberMatch;
                    filtered.add(c);
                }
            }

            Collections.sort(filtered, COMPARATOR);
        }

        return filtered;
    }

    public static SpannableStringBuilder highLight(SpannableStringBuilder ssb,
                                                   T9MatchInfo matchInfo,
                                                   String text,
                                                   int color) {
        ssb.clear();
        if (!TextUtils.isEmpty(text)) {
            ssb.append(text);
        }

        final int maxLength = text.length();
        while (matchInfo != null) {
            int start = matchInfo.start();
            int end = start + matchInfo.length();
            if ((matchInfo.found() && start < maxLength && end <= maxLength)) {
                ssb.setSpan(new ForegroundColorSpan(color), start, end,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            matchInfo = matchInfo.next();
        }

        return ssb;
    }

    private static final Comparator<Contact> COMPARATOR = new Comparator<Contact>() {

        @Override
        public int compare(Contact left, Contact right) {
            T9MatchInfo leftNameMatch = left.nameMatchInfo;
            T9MatchInfo rightNameMatch = right.nameMatchInfo;
            if (leftNameMatch.found()) {
                if (rightNameMatch.found()) {
                    int lStart = leftNameMatch.start();
                    int rStart = rightNameMatch.start();

                    if (lStart < rStart) {
                        return -1;
                    } else if (lStart > rStart) {
                        return 1;
                    }

                    int lLen = getMatchLength(leftNameMatch);
                    int rLen = getMatchLength(rightNameMatch);

                    int temp = left.name.length() - lLen - (right.name.length() - rLen);
                    if (temp != 0) {
                        if (lLen < rLen) {
                            return 1;
                        } else if (lLen > rLen) {
                            return -1;
                        }
                        return temp;
                    } else if (lLen != rLen) {
                        if (left.name.length() > right.name.length()) {
                            return 1;
                        } else if (left.name.length() < right.name.length()) {
                            return -1;
                        }
                    }

                    return left.name.compareToIgnoreCase(right.name);

                } else {
                    return -1;
                }
            } else if (rightNameMatch.found()) {
                return 1;
            }

            T9MatchInfo leftNumberMatch = left.phoneNumberMatchInfo;
            T9MatchInfo rightNumberMatch = right.phoneNumberMatchInfo;
            if (leftNumberMatch.found()) {
                if (rightNumberMatch.found()) {
                    int lStart = leftNumberMatch.start();
                    int rStart = rightNumberMatch.start();

                    if (lStart < rStart) {
                        return -1;
                    } else if (lStart > rStart) {
                        return 1;
                    } else {
                        return left.phoneNumber.compareToIgnoreCase(right.phoneNumber);
                    }
                } else {
                    return -1;
                }
            } else if (rightNumberMatch.found()) {
                return 1;
            }

            return 0;
        }

        private int getMatchLength(T9MatchInfo matchInfo) {
            int len = 0;
            T9MatchInfo temp = matchInfo;
            do {
                len += temp.length();

                if (temp.hasNext()) {
                    temp = temp.next();
                } else {
                    break;
                }
            } while (true);

            return len;
        }
    };

}
