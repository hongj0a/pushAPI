package dejay.rnd.villagePush.alarm;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fcm")
@RequiredArgsConstructor
public class MessageController {
    private final MessagingService messagingService;
    @Operation(summary = "test hello", description = "hello api example")
    @PostMapping("/send")
    public void sendTopicMessage() throws Exception {
        messagingService.sendTopicMessage("test123123","test title", "test testaetaetasetasdtg!!!!!!!",null);
    }

}
