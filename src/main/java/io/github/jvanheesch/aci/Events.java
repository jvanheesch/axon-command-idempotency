package io.github.jvanheesch.aci;

import java.math.BigDecimal;

public class Events {
    public record BankAccountCreated(String bankAccountId) {}
    public record MoneyDeposited(String bankAccountId, BigDecimal amount) {}
}
