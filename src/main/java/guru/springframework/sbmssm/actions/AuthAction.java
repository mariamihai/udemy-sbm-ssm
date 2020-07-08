package guru.springframework.sbmssm.actions;

import guru.springframework.sbmssm.domain.PaymentEvent;
import guru.springframework.sbmssm.domain.PaymentState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.Random;

@Slf4j
@Component
public class AuthAction implements Action<PaymentState, PaymentEvent> {

    @Override
    public void execute(StateContext<PaymentState, PaymentEvent> context) {
        log.debug("Auth was called");

        if(new Random().nextInt(10) < 8) {
            log.debug("Auth approved.");
            ActionsUtil.sendMessage(context, PaymentEvent.AUTH_APPROVED);
        } else {
            log.debug("Auth declined!");
            ActionsUtil.sendMessage(context, PaymentEvent.AUTH_DECLINED);
        }
    }
}
