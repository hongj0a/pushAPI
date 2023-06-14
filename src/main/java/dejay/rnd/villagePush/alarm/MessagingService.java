package dejay.rnd.villagePush.alarm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.net.HttpHeaders;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessagingService {
    private final ObjectMapper objectMapper;

    private final String API_URL = "https://fcm.googleapis.com/v1/projects/village-385001/messages:send";

    public Response sendTopicMessage(String topic, String title, String body, Long id, String id2, String type, String image) throws Exception {

        String message = makeMessage(topic, title, body, id, id2, type);
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();

        return client.newCall(request).execute();

    }
    private String makeMessage(String topic, String title, String body, Long id, String id2, String type) throws JsonProcessingException {
        FcmMessage fcmMessage = FcmMessage.builder()
                  .message(FcmMessage.Message.builder()
                        .topic(topic)
                        .notification(
                            FcmMessage.Notification.builder()
                                .title(title)
                                .body(body)
                                .image("https://www.iconpacks.net/icons/1/free-home-icon-163-thumb.png")
                                .build()
                        ).data(
                                FcmMessage.Data.builder()
                                        .id1(String.valueOf(id))
                                        .id2(id2)
                                        .type(type)
                                        .build()
                          ).apns(
                            FcmMessage.Apns.builder()
                                .payload(
                                        FcmMessage.Payload.builder()
                                                .aps(FcmMessage.Aps.builder().sound("default").build())
                                                .build()
                                )
                                .build()
                        ).android(
                                FcmMessage.Android.builder()
                                        .priority("high")
                                        .build()
                          )
                        .build())
                .validateOnly(false)
                .build();
        log.info(objectMapper.writeValueAsString(fcmMessage));
        return objectMapper.writeValueAsString(fcmMessage);

    }

    private String getAccessToken() throws Exception {
        String firebaseConfigPath = "firebase/billige-firebase-adminsdk-ymbms-4052299ccd.json";

        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

        // accessToken 생성
        googleCredentials.refreshIfExpired();

        // GoogleCredential의 getAccessToken으로 토큰 받아온 뒤, getTokenValue로 최종적으로 받음
        // REST API로 FCM에 push 요청 보낼 때 Header에 설정하여 인증을 위해 사용
        return googleCredentials.getAccessToken().getTokenValue();
    }

    // 구독 요청 시
    public void subScribe(FirebaseApp firebaseApp, String topicName, List<String> tokenList) {
        FirebaseMessaging.getInstance(firebaseApp).subscribeToTopicAsync(
                tokenList,
                topicName
        );
    }
    // 구독 취소
    public void unSubscribe(FirebaseApp firebaseApp, String topicName, List<String> tokenList) {
        FirebaseMessaging.getInstance(firebaseApp).unsubscribeFromTopicAsync(
                tokenList,
                topicName
        );
    }


}
