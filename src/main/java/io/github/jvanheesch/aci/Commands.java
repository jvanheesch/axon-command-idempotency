package io.github.jvanheesch.aci;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;

public class Commands {
    public record CreateBankAccount(@TargetAggregateIdentifier String bankAccountId) {}
    public record DepositMoney(@TargetAggregateIdentifier String bankAccountId, BigDecimal amount) {}
}
