package org.galileo.easycache.core.event;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public interface PubSub {
    void sub(Consumer<PubSubBody> consumer, String... channels);
    void unSub(String... channels);
    void pub(String channel, PubSubBody msg);

    // channel:事件类型 分割符
    String channelTypeSplit = ":";
    // pub事件类型, 0-删除
    int TypeRemoveKey = 0;

    class PubSubBody {
        String channel;
        int type;
        Set<String> keys = new HashSet<>();
        
        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public Set<String> getKeys() {
            return keys;
        }

        public void setKeys(Set<String> keys) {
            this.keys = keys;
        }
    }
}
