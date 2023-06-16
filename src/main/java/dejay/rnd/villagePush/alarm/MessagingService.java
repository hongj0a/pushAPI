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
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessagingService {
    private final ObjectMapper objectMapper;

    private final String API_URL = "https://fcm.googleapis.com/v1/projects/village-385001/messages:send";

    public void sendTopicMessage(String topic, String title, String body, Long id, String id2, String type, String image) throws Exception {

        String message = makeMessage(topic, title, body, id, id2, type);
        System.out.println("message = " + message);

        if ( StringUtils.isEmpty(message) ) {
            log.info("topic {}" ,topic);
        } else {
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(requestBody)
                    .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                    .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                    .build();

            Response resp = client.newCall(request).execute();

            resp.close();
        }

    }
    private String makeMessage(String topic, String title, String body, Long id, String id2, String type) throws JsonProcessingException {
        String result = "";
        if (topic.contains("ios")) {
           FcmIOSMessage fcmMessage = FcmIOSMessage.builder()
                    .message(FcmIOSMessage.Message.builder()
                            .topic(topic)
                            .notification(
                                    FcmIOSMessage.Notification.builder()
                                            .title(title)
                                            .body(body)
                                            .build()
                            )
                            .content_available(true)
                            .data(
                                    FcmIOSMessage.Data.builder()
                                            .id1(String.valueOf(id))
                                            .id2(id2)
                                            .type(type)
                                            .build()
                            )
                            .build())
                    .validateOnly(false)
                    .build();
            log.info(objectMapper.writeValueAsString(fcmMessage));
            result = objectMapper.writeValueAsString(fcmMessage);
        } else if (topic.contains("android")) {
            FcmMessage fcmMessage = FcmMessage.builder()
                    .message(FcmMessage.Message.builder()
                            .topic(topic)
                            .data(
                                    FcmMessage.Data.builder()
                                            .title(title)
                                            .body(body)
                                            .id1(String.valueOf(id))
                                            .id2(id2)
                                            .type(type)
                                            .build()
                            ).build())
                    .validateOnly(false)
                    .build();
            log.info(objectMapper.writeValueAsString(fcmMessage));
            result = objectMapper.writeValueAsString(fcmMessage);
        }
        return result;
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
