# Idempotency of command handlers in Axon Framework

- Command failures are often transient (e.g. service down -> `NoHandlerForCommandException`). As such, it makes sense to
  introduce the concept of a `RetryScheduler`
- Problem: retrying commands is not always a safe operation, as the initial "failed" command may have produced side
  effects
    - this is particularly common in case of
      timeout (`COMMAND_TIMEOUT("AXONIQ-4002", Status.CANCELLED, HttpStatus.GATEWAY_TIMEOUT)`)
- To enable safe retries, the CH must be idempotent
    - Some operations are inherently idempotent (e.g. `markDeleted()`)
    - Many operations are not inherently idempotent (e.g. `DepositMoney`)
    - All operations can be made idempotent by adding a uniquifier
      (see [Building on Quicksand by Pat Helland](https://arxiv.org/pdf/0909.1788.pdf))
- `ApplicationIT` demonstrates that, in the absence of a uniquifier, the use of a `RetryScheduler` can cause side
  effects to be triggered multiple times.
    - the test fails when
      using `org.axonframework:axon-spring-boot-starter:4.6.2` (`git checkout e2ff3a8e523888da98f4f4d749fcc985d2229cd5 && mvn verify`)
    - the test still fails after
      replacing `org.axonframework:axon-spring-boot-starter:4.6.2` (`git checkout 605660d798036ea3ccbbd4c35e6c4620840262c3 && mvn verify`)
      with `com.github.jvanheesch.AxonFramework:axon-spring-boot-starter:8de17c281d890a4598d640c788aad4557c7ef787`
    - the test passes after adding `idempotent = true` to `@Aggregate` for `BankAccount`
      aggregate (`git checkout 804754d41a78fdfb41880b30a16ddccca8cbe8e3 && mvn verify`)