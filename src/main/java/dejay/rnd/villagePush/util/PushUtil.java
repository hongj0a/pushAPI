package dejay.rnd.villagePush.util;

import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;


@Slf4j
public class PushUtil {

    public static Date getNowDate() {
        LocalDateTime date = LocalDateTime.now();
        Date now_date = Timestamp.valueOf(date);

        return now_date;
    }

}
