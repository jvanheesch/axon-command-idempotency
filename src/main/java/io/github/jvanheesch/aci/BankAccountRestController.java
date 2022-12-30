package io.github.jvanheesch.aci;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;

import static io.github.jvanheesch.aci.Commands.CreateBankAccount;
import static io.github.jvanheesch.aci.Commands.DepositMoney;
import static io.github.jvanheesch.aci.Queries.GetBankAccount;

@RequestMapping("/bankaccounts")
@RestController
public class BankAccountRestController {
    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    public BankAccountRestController(CommandGateway commandGateway, QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    @PostMapping("/create")
    public void create(@RequestParam String id) {
        commandGateway.sendAndWait(new CreateBankAccount(id));
    }

    @GetMapping("/{id}")
    public BankAccount get(@PathVariable String id) throws ExecutionException, InterruptedException {
        return queryGateway.query(new GetBankAccount(id), BankAccount.class).get();
    }

    @PostMapping("/{id}/deposit")
    public void deposit(@PathVariable String id, @RequestParam BigDecimal amount) {
        commandGateway.sendAndWait(new DepositMoney(id, amount));
    }
}
