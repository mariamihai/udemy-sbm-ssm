package guru.springframework.sbmssm.services;

import guru.springframework.sbmssm.domain.Payment;
import guru.springframework.sbmssm.domain.PaymentEvent;
import guru.springframework.sbmssm.domain.PaymentState;
import guru.springframework.sbmssm.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {

    public static final String PAYMENT_ID_HEADER = "payment_id";

    private final PaymentRepository paymentRepository;
    private final StateMachineFactory<PaymentState, PaymentEvent> factory;
    private final PaymentStateChangeInterceptor paymentStateChangeInterceptor;

    @Override
    public Payment newPayment(Payment payment) {
        payment.setState(PaymentState.NEW);

        return paymentRepository.save(payment);
    }

    @Override
    @Transactional
    public StateMachine<PaymentState, PaymentEvent> preAuth(Long paymentId) {
        StateMachine<PaymentState, PaymentEvent> stateMachine = build(paymentId);
        sendEventToStateMachine(paymentId, stateMachine, PaymentEvent.PRE_AUTHORIZE);

        return stateMachine;
    }

    @Override
    @Transactional
    public StateMachine<PaymentState, PaymentEvent> auth(Long paymentId) {
        StateMachine<PaymentState, PaymentEvent> stateMachine = build(paymentId);
        sendEventToStateMachine(paymentId, stateMachine, PaymentEvent.AUTHORIZE);

        return stateMachine;
    }

    /**
     * @deprecated (Not needed anymore.)
     */
    @Deprecated
    @Override
    @Transactional
    public StateMachine<PaymentState, PaymentEvent> declineAuth(Long paymentId) {
        StateMachine<PaymentState, PaymentEvent> stateMachine = build(paymentId);
        sendEventToStateMachine(paymentId, stateMachine, PaymentEvent.AUTH_DECLINED);

        return stateMachine;
    }

    private void sendEventToStateMachine(Long paymentId, StateMachine<PaymentState, PaymentEvent> stateMachine, PaymentEvent event) {
        Message<PaymentEvent> message = MessageBuilder.withPayload(event)
                .setHeader(PAYMENT_ID_HEADER, paymentId)
                .build();

        stateMachine.sendEvent(message);
    }

    private StateMachine<PaymentState, PaymentEvent> build(Long paymentId) {
        Payment payment = paymentRepository.getOne(paymentId);

        StateMachine<PaymentState, PaymentEvent> stateMachine = factory.getStateMachine(Long.toString(payment.getId()));

        stateMachine.stop();

        stateMachine.getStateMachineAccessor()
            .doWithAllRegions(stateMachineAccessor -> {
                stateMachineAccessor.addStateMachineInterceptor(paymentStateChangeInterceptor);
                stateMachineAccessor.resetStateMachine(new DefaultStateMachineContext<>(payment.getState(), null, null, null));
            });

        stateMachine.start();

        return stateMachine;
    }
}
