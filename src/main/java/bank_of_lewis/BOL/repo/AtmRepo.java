package bank_of_lewis.BOL.repo;

import bank_of_lewis.BOL.model.Atm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AtmRepo extends JpaRepository<Atm, Long> {

    Optional<Atm> findById(Long id);
    Optional<Atm> findByName(String name);

}
