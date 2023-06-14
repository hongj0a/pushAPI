package dejay.rnd.villagePush.alarm;

import com.google.api.client.util.Key;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class FcmMessage {
    @Key("validate_only")
    private boolean validateOnly;

    @Key("message")
    private Message message;

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Apns{
        private Payload payload;

    }
    @Builder
    @AllArgsConstructor
    @Getter
    public static class Payload{
        private Aps aps;

    }
    @Builder
    @AllArgsConstructor
    @Getter
    public static class Aps{
        private String sound;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Message {
        private Notification notification;
        private String topic;
        private Apns apns;
        private Android android;
        private Data data;
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

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Android{
        private String priority;
        private Notification notification;

    }

}
