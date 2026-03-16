# DeliveryHero Refactoring Task

## Goal
- Refactor `DeliveryHero` from a **learning-oriented architecture project** into a **problem-solving backend portfolio project**.
- Set the core flow as **order creation → stock deduction → order acceptance/rejection → delivery assignment/start/completion**.
- Make sure the README clearly shows **problem → solution → validation → lessons learned**.

---

## Working Principles
- Do not add features blindly.
- Solve **authorization boundaries**, **state transition integrity**, and **concurrency validation** first.
- Split changes into **small PRs/commits** whenever possible.
- Only write README statements that are backed by actual implementation and tests.

---

## P0. Must-have work to make this look like a strong portfolio project

### 1) Separate authorization boundaries for order and delivery flows
- [x] Review the current authentication subject resolution
    - [x] Check how `LoginUserArgumentResolver` distinguishes user/rider
    - [x] List endpoints that are currently exposed without proper authorization checks
- [x] Separate role-based principal or annotations
    - [x] Decide whether to keep the single `@LoginUser` approach
    - [x] Prefer `@LoginCustomerId`, `@LoginOwnerId`, `@LoginRiderId`, or a `LoginPrincipal(id, role)` structure
- [x] Redefine order state-changing APIs based on actor roles
    - [x] Only customers can cancel their own orders
    - [x] Only store owners can accept/reject orders
    - [x] Only riders or internal flow can complete delivery-related processing
- [x] Add authorization checks to delivery query APIs
    - [x] Verify that a rider can only access their own deliveries
    - [x] Return 401/403 for unauthorized access

**Done when**
- Customer, owner, and rider responsibilities are not mixed.
- You can tell who is allowed to call an endpoint just by looking at the controller signature.
- You can confidently say in the README that authorization boundaries are clearly separated.

**Validation**
- [x] 401 test for unauthenticated requests
- [x] 403 test when another user tries to cancel someone else’s order
- [x] 403 test when another rider tries to read/complete a delivery they do not own

---

### 2) Clarify order and delivery state transition rules
- [ ] Create a table of current order states and delivery states
- [x] Define rules that block invalid transitions
    - [x] A cancelled order cannot be completed
    - [x] An unaccepted order cannot create a delivery
    - [x] An unassigned delivery cannot be started
    - [x] A not-yet-started delivery cannot be completed
- [x] Check whether state transition responsibility is inside the domain layer
- [x] Move scattered validation logic from service/controller into domain objects when needed
- [x] Consider expanding order states if necessary
    - [x] `PENDING`, `ACCEPTED`, `REJECTED`, `CANCELLED`, `COMPLETED`
- [x] Consider expanding delivery states if necessary
    - [x] `PENDING`, `ASSIGNED`, `PICKED_UP`, `DELIVERED`

**Done when**
- Invalid request sequences are blocked at the domain level.
- State transition rules can be explained with a README table.

**Validation**
- [x] Domain tests for order state transitions
- [x] Domain tests for delivery state transitions

---

### 3) Add stock deduction concurrency validation
- [x] Review the current stock deduction flow and lock application point
- [x] Clarify the intention of `PESSIMISTIC_WRITE` in code/comments/docs
- [x] Add concurrent order test scenario
    - [x] Two concurrent orders against a product with stock quantity = 1
    - [x] Verify that only one succeeds and one fails
- [x] Check whether an oversell failure case can also be reproduced
- [ ] Document the reason for choosing the lock and its trade-offs
    - [x] Advantage: integrity first
    - [ ] Disadvantage: possible throughput drop under lock contention

**Done when**
- You can write in the README that stock integrity is validated with a concurrency test.
- You can explain in an interview why you chose this locking strategy.

**Validation**
- [x] Concurrency test passes
- [x] Confirm failure-case logs or exception messages

---

## P1. High-impact work that improves portfolio quality

### 4) Clean up the API contract (name-based → ID-based)
- [x] Review request structure based on `storeName` and `productName`
- [x] Consider migrating to `storeId` and `productId`
- [ ] Document risks of duplicated or mutable names
- [x] Refactor request/response DTOs and service method signatures
- [x] Update Swagger documentation

**Done when**
- The API contract looks production-oriented.
- Identifiers and display names are clearly separated.

**Validation**
- [x] Existing tests and docs updated
- [ ] Swagger example requests updated

---

### 5) Organize the testing strategy so it can be explained in the README
- [x] Classify current tests into three layers
    - [x] Domain tests
    - [x] Service tests with fakes/test doubles
    - [x] Integration tests
- [ ] Document the testing strategy around `ClockHolder`, fake repositories, and finder abstractions
- [ ] Separate authorization tests and concurrency tests into dedicated sections
- [x] Add one sentence explaining what risk each test type prevents

**Done when**
- The test strategy section in the README clearly communicates the design intent.

**Validation**
- [x] Test classification documented
- [ ] Core test list organized

---

### 6) Rewrite the README as a problem-solving portfolio document
- [x] Rewrite the one-line project summary
    - [x] Replace “delivery platform implementation” with a problem-focused description
- [x] Add a “Key Problems Solved” section
    - [x] Stabilizing time-dependent business logic tests
    - [x] Clarifying authorization boundaries and state transitions
    - [x] Maintaining stock integrity with concurrency control
- [ ] Add explanation under the architecture diagram
- [x] Add a state transition table or sequence diagram
- [x] Add a testing strategy section
- [x] Add a troubleshooting section
- [x] Separate future improvements into their own section

**Done when**
- A reader can understand what problems were solved just by reading the README.
- An interviewer can grasp the strengths of the project within 2–3 minutes.

**Validation**
- [x] README section order finalized
- [x] At least 3 key problem-solving cases included
- [ ] Remove any exaggerated statements not backed by implementation

---

## P2. Nice-to-have improvements

### 7) Review DB constraints and indexes
- [x] Summarize major current query patterns
- [x] Define index candidates
    - [x] For order list queries
    - [x] For store/product queries
    - [x] For status-based queries
- [x] Review necessary unique constraints while name-based logic still exists
- [x] Document why schema-level choices were made

**Done when**
- You can explain basic DB design choices from both performance and integrity perspectives.

---

### 8) Improve local execution and developer onboarding
- [x] Add `.env.example` or equivalent config guide
- [x] Document how to run locally
- [x] Document how to run tests
- [x] If possible, add Docker Compose or local DB startup guide

**Done when**
- Another developer can understand how to run the repository locally.

---

### 9) Add GitHub Actions or a minimal CI setup
- [x] Check whether at least `build/test` is automated
- [x] Add test execution for PRs if possible

**Done when**
- The portfolio feels more trustworthy and production-minded.

---

## Recommended Execution Order
1. Separate authorization boundaries
2. Refine order/delivery state transitions
3. Add concurrency tests
4. Clean up API contract (ID-based)
5. Document testing strategy
6. Fully rewrite the README
7. Improve indexes, local setup, and CI

---

## Suggested Branch Strategy
- `feat/auth-boundary`
- `feat/order-delivery-state`
- `test/order-concurrency`
- `refactor/api-contract-id-based`
- `docs/readme-portfolio`

---

## Suggested Commit Units
- [x] `refactor: separate role-based login principals`
- [x] `feat: add authorization checks for order state changes`
- [x] `feat: strengthen delivery state transition validation`
- [x] `test: add concurrent order scenario`
- [x] `refactor: change order creation request to ID-based contract`
- [x] `docs: add key problem-solving section to README`
- [x] `chore: add local run guide and docker compose`
- [x] `ci: add build and test workflow`
- [x] `docs: add DB review document`

---

## Final Completion Criteria
- [x] Authorization boundaries for order/delivery are explicit.
- [x] State transition rules for order/delivery are centralized in the domain layer.
- [x] Stock deduction concurrency control is proven by tests.
- [ ] The README contains at least 3 cases in the format **problem → solution → validation → lessons learned**.
- [ ] You can explain the project’s key problem-solving story in under 1 minute in an interview.

---

## One-line Interview Summary
> DeliveryHero is a backend portfolio project that models order, stock, and delivery flows in a food delivery platform using DDD and Hexagonal Architecture, while solving time-dependent test instability, role-based authorization boundaries, and stock concurrency problems.
