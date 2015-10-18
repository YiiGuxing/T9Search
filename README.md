# T9Search
Android T9 search.

提供T9匹配，支持多音字匹配。

注意：由于生成汉字拼音的方案有多种，因此本库并不包含汉字拼音的生成，需要自己选择合适的方案生成拼音。
另外值得注意的是，使用多音字的话将会使匹配串的长度呈倍数增加。

![preview](https://github.com/Tinkling/T9Search/blob/master/preview.png?raw=true)

===========================================================

生成匹配串

        String t9Key = T9Utils.buildT9Key("重新称重", new PinyinProvider() {
            @Override
            public String[] getPinyin(char input) {
                // 在这里生成拼音，如果需要支持多音字匹配，返回多个拼音就行了。
                // 由于多音字的拼音可能只是声调不同，去除声调后是一样的(如：“啊”字)，所以建议做一下去重复操作。
                return new String[]{/*拼音...*/};
            }
        });
        // ...
        
匹配

        String t9Key;
        String constraint;
        // ...
        T9MatchInfo matchInfo = T9Matcher.matches(t9Key, constraint);
        // ...
        
具体使用方法请看 [示例](https://github.com/Tinkling/T9Search/tree/master/sample) 。

###Gradle
---------

        compile 'cn.tinkling.t9:t9search:1.0'

###Maven
--------

        <dependency>
            <groupId>cn.tinkling.t9</groupId>
            <artifactId>t9search</artifactId>
            <version>1.0</version>
            <type>aar</type>
        </dependency>
        
