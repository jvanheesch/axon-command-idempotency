package io.github.jvanheesch.aci;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;

import java.math.BigDecimal;

class ApplicationIT {
    private final Network NETWORK = Network.newNetwork();

    private final GenericContainer<?> AXON_SERVER = new GenericContainer<>("axoniq/axonserver:4.6.7")
            .withNetwork(NETWORK)
            .withNetworkAliases("axonserver")
            .withExposedPorts(8124)
            .withEnv("AXONIQ_AXONSERVER_DEFAULT_COMMAND_TIMEOUT", "10000")
            .waitingFor(new LogMessageWaitStrategy()
                    .withRegEx(".*Started AxonServer.*"));
    private final GenericContainer<?> APP = new GenericContainer<>("docker.io/library/axon-command-idempotency:1.0-SNAPSHOT")
            .withNetwork(NETWORK)
            .withExposedPorts(8080)
            .withEnv("axon.axonserver.servers", "axonserver")
            .waitingFor(new LogMessageWaitStrategy()
                    .withRegEx(".*Started Application.*"));

    private String host;
    private Integer port;

    @BeforeEach
    void setup() {
        AXON_SERVER.start();
        APP.start();

        host = "localhost";
        port = APP.getMappedPort(8080);
    }

    @Test
    void givenBankAccount_whenDepositCommandTimesOut_commandIsRetriedResultingInTwoInvocations() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForObject(String.format("http://%s:%s/bankaccounts/create?id=1", host, port), null, String.class);
        // this call takes a while - see BankAccount.handle(DepositMoney command)
        restTemplate.postForObject(String.format("http://%s:%s/bankaccounts/1/deposit?amount=5", host, port), null, String.class);

        // this call also takes a while, as the aggregate is locked during the execution of the retried command
        BankAccount bankAccount = restTemplate.getForObject(String.format("http://%s:%s/bankaccounts/1", host, port), BankAccount.class);
        Assertions.assertEquals("1", bankAccount.getId());
        Assertions.assertEquals(new BigDecimal("5"), bankAccount.getBalance());
    }
}
