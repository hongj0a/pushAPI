package dejay.rnd.villagePush.repository;

import dejay.rnd.villagePush.domain.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    Admin findByAdminIdx(Long adminIdx);

}
