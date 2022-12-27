package io.github.jvanheesch.aci;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import java.math.BigDecimal;

import static io.github.jvanheesch.aci.Commands.CreateBankAccount;
import static io.github.jvanheesch.aci.Commands.DepositMoney;
import static io.github.jvanheesch.aci.Events.BankAccountCreated;
import static io.github.jvanheesch.aci.Events.MoneyDeposited;
import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
public class BankAccount {
    @AggregateIdentifier
    private String id;
    private BigDecimal balance = BigDecimal.ZERO;

    @CommandHandler
    public BankAccount(CreateBankAccount createBankAccount) {
        apply(new BankAccountCreated(createBankAccount.bankAccountId()));
    }

    // required by axon framework
    BankAccount() {
    }

    @CommandHandler
    public void handle(DepositMoney command) throws InterruptedException {
        Thread.sleep(15000);

        apply(new MoneyDeposited(command.bankAccountId(), command.amount()));
    }

    @EventSourcingHandler
    public void handle(BankAccountCreated event) {
        id = event.bankAccountId();
    }

    @EventSourcingHandler
    public void handle(MoneyDeposited event) {
        balance = balance.add(event.amount());
    }
}
