package io.github.jvanheesch.aci;

import com.google.common.collect.MoreCollectors;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.modelling.command.Repository;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.List;

import static io.github.jvanheesch.aci.Queries.GetBankAccount;

@SuppressWarnings("unused")
@Component
public class BankAccountQueryHandler {
    private final EventSourcingRepository<BankAccount> repository;

    @SuppressWarnings("unchecked")
    public BankAccountQueryHandler(List<Repository<?>> repositories) {
        this.repository = repositories.stream()
                .filter(EventSourcingRepository.class::isInstance)
                .map(EventSourcingRepository.class::cast)
                .filter(repo -> repo.getAggregateFactory().getAggregateType() == BankAccount.class)
                .collect(MoreCollectors.onlyElement());
    }

    @QueryHandler
    public BankAccount handle(GetBankAccount query) {
        return repository.load(query.bankAccountId())
                .getWrappedAggregate()
                .getAggregateRoot();
    }
}
