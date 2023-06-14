package dejay.rnd.villagePush.alarm;

import dejay.rnd.villagePush.domain.Admin;
import dejay.rnd.villagePush.domain.Alarm;
import dejay.rnd.villagePush.domain.User;
import dejay.rnd.villagePush.repository.AdminRepository;
import dejay.rnd.villagePush.repository.AlarmRepository;
import dejay.rnd.villagePush.repository.UserRepository;
import dejay.rnd.villagePush.util.PushUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/fcm")
@RequiredArgsConstructor
public class MessageController {
    private final MessagingService messagingService;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final AlarmRepository alarmRepository;

    /**
     * 파라미터 정리
     * user --> 알림 보내는 쪽 유저 정보
     * hostIdx[] 알림 받는 쪽 유저 리스트
     * target_idx --> 게시글 상세페이지 이동을 위한 idx
     * message --> string으로 만들어서 보냄
     * < push type definition >
     *  0  : 상세로 이동이 필요 없는 알람
     *  10 : 렌탈게시글
     *  20 : 후기게시글
     *  30 : 일대일문의 게시글
     *  40 : 공지사항 게시글
     *  50 : 채팅방 상세
     *  60 : 이의신청 게시글
     */
    @PostMapping("/send")
    public void sendTopicMessage(@RequestParam(value = "userIdx", required = false) Long userIdx,
                                 @RequestParam (value = "adminIdx", required = false) Long adminIdx,
                                 @RequestParam (value = "hostIdxes", required = false) Long[] hostIdxes,
                                 @RequestParam (value = "title") String title,
                                 @RequestParam (value = "message") String message,
                                 @RequestParam (value = "targetIdx", required = false) Long targetIdx,
                                 @RequestParam (value= "targetIdx2", required = false) String targetIdx2,
                                 @RequestParam (value = "type") String type,
                                 @RequestParam (value="topicType", required = false) String topicType) throws Exception {

        /**
         * ROLE
         * 1. 알림 보내고
         * 2. 알림테이블에 저장
         * (topicType : 주제어)
         * 주제어가 안 넘어오면 topic에 userEmail 넣어서 개별 발송
         * 주제어가 넘어오면 topic에 주제어 넣어서 발송
         */

        //전체 유저 테이블에 넣기
        //단 마케팅, 활동알림 동의 한 사람만 구해서

        List<User> allUsers = userRepository.findAllByMarketingNoticeTypeNotInAndActivityNoticeYn(new int[]{0}, true);
        User sendUser = null;
        Admin sender = null;
        if (targetIdx2 == null)  {targetIdx2 = "0";};
        if ( StringUtils.isEmpty(topicType) ) {

            if (userIdx != null) {
                sendUser = userRepository.findByUserIdx(userIdx);
            }
            if (adminIdx != null) {
                sender = adminRepository.findByAdminIdx(adminIdx);
            }

            for (int i = 0; i < hostIdxes.length; i++) {
                User findUser = userRepository.findByUserIdx(hostIdxes[i]);
                if ( null != findUser ) {
                    Response response = messagingService.sendTopicMessage("village_" + findUser.getUserIdx(), title, message, targetIdx, targetIdx2, type, null);

                    if (response.code() == 200) {
                        Alarm alarm = new Alarm();
                        alarm.setUser(sendUser);

                        alarm.setAdmin(sender);
                        alarm.setReadYn(false);
                        alarm.setContent(message);
                        alarm.setCreateAt(PushUtil.getNowDate());
                        alarm.setTargetIdx(Long.valueOf(targetIdx));
                        alarm.setType(Integer.valueOf(type));
                        alarm.setHostIdx(findUser.getUserIdx());

                        alarmRepository.save(alarm);
                    }
                }
            }
        } else {
            //뭔가 전체알람일 때..
            //전체 유저 사이즈 구해서 알람테이블 insert...
            Response response = messagingService.sendTopicMessage( topicType, title, message ,targetIdx, targetIdx2, type,null);

            if (response.code() == 200) {
                for (int i = 0; i < allUsers.size(); i++) {

                    Alarm alarm = new Alarm();
                    alarm.setUser(sendUser);
                    alarm.setAdmin(sender);
                    alarm.setReadYn(false);
                    alarm.setContent(message);
                    alarm.setCreateAt(PushUtil.getNowDate());
                    alarm.setTargetIdx(Long.valueOf(targetIdx));
                    alarm.setType(Integer.valueOf(type));
                    alarm.setHostIdx(allUsers.get(i).getUserIdx());

                    alarmRepository.save(alarm);
                }
            }
        }

    }


}
