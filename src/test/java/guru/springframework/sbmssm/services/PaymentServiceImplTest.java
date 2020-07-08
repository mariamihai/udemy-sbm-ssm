package guru.springframework.sbmssm.services;

import guru.springframework.sbmssm.domain.Payment;
import guru.springframework.sbmssm.domain.PaymentEvent;
import guru.springframework.sbmssm.domain.PaymentState;
import guru.springframework.sbmssm.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PaymentServiceImplTest {

    @Autowired
    private PaymentService classUnderTest;

    @Autowired
    private PaymentRepository paymentRepository;

    private Payment payment;

    @BeforeEach
    void setUp() {
        payment = Payment.builder().amount(new BigDecimal("10.00")).build();
    }

    @Test
    @Transactional
    public void preAuth() {
        Payment savedPayment = classUnderTest.newPayment(this.payment);
        assertEquals(savedPayment.getState(), PaymentState.NEW);

        StateMachine<PaymentState, PaymentEvent> stateMachine = classUnderTest.preAuth(payment.getId());
        Payment preAuthPayment = paymentRepository.getOne(savedPayment.getId());

        assertEquals(stateMachine.getState().getId(), PaymentState.PRE_AUTH);
        assertEquals(preAuthPayment.getState(), PaymentState.PRE_AUTH);
    }
}