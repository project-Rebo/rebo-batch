package hello.rebobatch.domain.store.repository;

import hello.rebobatch.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
}
