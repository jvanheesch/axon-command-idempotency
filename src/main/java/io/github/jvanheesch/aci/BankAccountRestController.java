package io.github.jvanheesch.aci;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

import static io.github.jvanheesch.aci.Commands.CreateBankAccount;
import static io.github.jvanheesch.aci.Commands.DepositMoney;

@RequestMapping("/bankaccounts")
@RestController
public class BankAccountRestController {
    private final CommandGateway commandGateway;

    public BankAccountRestController(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @PostMapping("/create")
    public void create(@RequestParam String id) {
        commandGateway.sendAndWait(new CreateBankAccount(id));
    }

    @PostMapping("/{id}/deposit")
    public void deposit(@PathVariable String id, @RequestParam BigDecimal amount) {
        commandGateway.sendAndWait(new DepositMoney(id, amount));
    }
}
