package guru.springframework.sbmssm.actions;

import guru.springframework.sbmssm.domain.PaymentEvent;
import guru.springframework.sbmssm.domain.PaymentState;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateContext;

import static guru.springframework.sbmssm.services.PaymentServiceImpl.PAYMENT_ID_HEADER;

public class ActionsUtil {

    private ActionsUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static void sendMessage(StateContext<PaymentState, PaymentEvent> context, PaymentEvent event) {
        Message<PaymentEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(PAYMENT_ID_HEADER, context.getMessageHeader(PAYMENT_ID_HEADER))
                .build();
        context.getStateMachine().sendEvent(message);
    }
}
