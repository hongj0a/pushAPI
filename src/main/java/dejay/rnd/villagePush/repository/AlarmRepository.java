package dejay.rnd.villagePush.repository;

import dejay.rnd.villagePush.domain.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
}
