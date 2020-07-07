package guru.springframework.sbmssm.config;

import guru.springframework.sbmssm.domain.PaymentEvent;
import guru.springframework.sbmssm.domain.PaymentState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StateMachineConfigTest {

    @Autowired
    StateMachineFactory<PaymentState, PaymentEvent> factory;

    @Test
    void testNewStateMachine() {
        StateMachine<PaymentState, PaymentEvent> stateMachine = factory.getStateMachine();

        stateMachine.start();
        assertEquals(PaymentState.NEW, stateMachine.getState().getId());

        stateMachine.sendEvent(PaymentEvent.PRE_AUTHORIZE);
        assertEquals(PaymentState.NEW, stateMachine.getState().getId());

        stateMachine.sendEvent(PaymentEvent.PRE_AUTH_APPROVED);
        assertEquals(PaymentState.PRE_AUTH, stateMachine.getState().getId());

        stateMachine.sendEvent(PaymentEvent.PRE_AUTH_DECLINED);
        assertEquals(PaymentState.PRE_AUTH, stateMachine.getState().getId());
    }
}