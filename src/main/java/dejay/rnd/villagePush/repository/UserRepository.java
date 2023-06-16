package dejay.rnd.villagePush.repository;

import dejay.rnd.villagePush.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    User findByUserIdx(Long userIdx);
    List<User> findAllByMarketingNoticeYnAndActivityNoticeYn(boolean marketingYn, boolean activityYn);

    User getOne(Long userIdx);
}
