package guru.springframework.sbmssm.repository;

import guru.springframework.sbmssm.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
