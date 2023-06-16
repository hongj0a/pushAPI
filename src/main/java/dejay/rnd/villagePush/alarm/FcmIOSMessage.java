package dejay.rnd.villagePush.alarm;

import com.google.api.client.util.Key;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class FcmIOSMessage {
    @Key("validate_only")
    private boolean validateOnly;

    @Key("message")
    private Message message;

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Message {
        private Notification notification;
        private String topic;
        private Data data;
        private boolean content_available;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Notification {
        private String title;
        private String body;
        private String image;

    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Data{
        private String id1;
        private String id2;
        private String type;
    }

}
