package cn.tinkling.t9search.sample;

import cn.tinkling.t9.T9MatchInfo;

public class Contact {

    public String name;
    public String phoneNumber;
    public String t9Key;
    public T9MatchInfo nameMatchInfo;
    public T9MatchInfo phoneNumberMatchInfo;

    public Contact(String name, String phoneNumber, String t9Key) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.t9Key = t9Key;
    }

    public Contact(Contact contact) {
        this(contact.name, contact.phoneNumber, contact.t9Key);
    }
}
