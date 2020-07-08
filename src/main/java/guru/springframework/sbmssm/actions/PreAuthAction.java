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
public class PreAuthAction implements Action<PaymentState, PaymentEvent> {

    @Override
    public void execute(StateContext<PaymentState, PaymentEvent> context) {
        log.debug("PreAuth was called");

        if(new Random().nextInt(10) < 8) {
            log.debug("PreAuth approved.");
            ActionsUtil.sendMessage(context, PaymentEvent.PRE_AUTH_APPROVED);
        } else {
            log.debug("PreAuth declined! No Credit.");
            ActionsUtil.sendMessage(context, PaymentEvent.PRE_AUTH_DECLINED);
        }
    }
}
