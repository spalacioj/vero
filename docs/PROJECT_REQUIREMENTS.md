# Vero — Product Requirements and Backlog

## 1. Product vision

Vero is a private, multi-account personal-finance web application. It helps a user record income, expenses, and transfers; understand their current financial position; and explore spending patterns over time.

The first release is deliberately a learning-focused MVP. It must be complete enough for one person to use their own data, but it does not attempt bank synchronisation, shared household finances, or investment tracking.

## 2. Users and scope

### Primary user

An individual who manages their own cash, bank, savings, and credit-card accounts.

### Roles

| Role | Capabilities |
| --- | --- |
| Visitor | Register or log in. |
| Authenticated user | Access and manage only their own accounts, categories, transactions, dashboard, and reports. |

There is no administrator role in the MVP.

### MVP assumptions

- A user has one base currency, chosen when registering, using an ISO 4217 code such as `COP`.
- A user may own multiple accounts.
- All amounts are positive values. Whether an amount increases or decreases a balance is determined by its entry type.
- The app is a transaction ledger, not a bank connection. Users enter data manually.

## 3. Functional requirements

### Authentication and privacy

- **FR-01:** A visitor can register with display name, unique email, password, and base currency.
- **FR-02:** A registered user can log in and log out.
- **FR-03:** Passwords are never stored in plain text; only a secure password hash is stored.
- **FR-04:** Every application endpoint, except registration and login, requires authentication.
- **FR-05:** A user can never read or change another user's data.

### Password policy

- Passwords must be between 12 and 72 UTF-8 bytes; `12345` is rejected.
- Passwords on the application's common-password blocklist are rejected.
- Passwords may contain spaces, Unicode, and any other characters. The MVP does not impose arbitrary upper-case, number, or symbol rules.
- Passwords must not equal the email address or its local part.
- Passwords are hashed with BCrypt before storage. BCrypt generates a unique salt for every hash; verification compares the supplied password to that hash.
- Passwords are never logged, returned by an API, or stored in browser storage.

### Accounts and categories

- **FR-06:** A user can create, list, edit, and archive accounts.
- **FR-07:** Supported account types are `CASH`, `CHECKING`, `SAVINGS`, and `CREDIT_CARD`.
- **FR-08:** An account has a name and initial balance. Its current balance is calculated from the initial balance plus its ledger entries.
- **FR-09:** A user can create, list, edit, and archive income and expense categories.
- **FR-10:** A category has a name, type (`INCOME` or `EXPENSE`), and optional display colour.

### Transactions and transfers

- **FR-11:** A user can create, view, edit, and delete an income or expense transaction.
- **FR-12:** A transaction includes account, matching category, amount, date, and optional description.
- **FR-13:** A user can filter transactions by account, category, type, and date range.
- **FR-14:** A user can transfer money between two different accounts.
- **FR-15:** A transfer creates two linked entries of the same amount and date: `TRANSFER_OUT` from the source and `TRANSFER_IN` to the destination.
- **FR-16:** Editing or deleting a transfer updates or removes both linked entries as one operation.

### Dashboard and analytics

- **FR-17:** The dashboard shows total net balance, balance by account, current-month income, current-month expenses, net change, and recent transactions.
- **FR-18:** The dashboard shows a current-month expense-by-category chart.
- **FR-19:** The analytics page supports a date range and optional account filter.
- **FR-20:** Analytics show income versus expense over time and expense distribution by category.

## 4. Business and data rules

- Money uses `BigDecimal` in Java and `NUMERIC(19,2)` in PostgreSQL; never `double` or `float`.
- Transaction amounts must be greater than zero.
- Income and expense entries require a category of the same type.
- Transfer entries cannot have a category and must have a transfer identifier.
- A transfer source and destination account must be different and owned by the same user.
- Account and category names must be unique per user, as defined by the database constraints.
- Account deletion is blocked when it has transactions; the account is archived instead.
- Category deletion is blocked when it is used by a transaction; the category is archived instead.
- Dates are stored as dates for transactions; timestamps are stored in UTC.

## 5. Pages and user experience

| Page | Purpose |
| --- | --- |
| Register | Create an account and choose base currency. |
| Login | Authenticate the user. |
| Dashboard | Show financial summary, balances, recent activity, and a summary chart. |
| Transactions | List, filter, add, edit, and delete income/expense transactions. |
| Transfer | Move money between accounts. |
| Accounts | Manage accounts and view their balances. |
| Categories | Manage income and expense categories. |
| Analytics | View detailed charts with filters. |

The React frontend is responsible for forms, navigation, tables, filters, and charts. The Spring Boot API is responsible for authentication, validation, calculations, ownership checks, and database access.

## 6. Technical requirements

- Backend: Java 21, Spring Boot, Maven, Spring Data JPA, Spring Validation, Spring Security.
- Database: PostgreSQL in Docker Compose.
- Schema management: Flyway SQL migrations only; Hibernate validates the schema but does not create it.
- Frontend: React with TypeScript; use a chart library such as Recharts.
- API: JSON REST endpoints under `/api`.
- Database credentials stay in the untracked `.env` file; only `.env.example` is committed.
- The application must return clear validation errors and must not expose passwords or internal error details.
- Unit tests cover business rules; integration tests cover persistence and authenticated API behaviour.

## 7. User-story backlog

Status key: `[ ]` not started, `[-]` in progress, `[x]` complete.

### Foundation

- [x] **US-00:** As a developer, I can start PostgreSQL with Docker Compose and start Spring Boot against it, so development uses a repeatable local environment.
- [x] **US-00a:** As a developer, I have Flyway V1 that creates the initial ledger tables.

### Authentication

- [-] **US-01:** As a visitor, I can register, so I have a private finance workspace.
  - Acceptance: duplicate emails are rejected; password is hashed; base currency is saved.
- [-] **US-02:** As a registered user, I can log in and log out, so my data is protected.
  - Acceptance: invalid credentials are rejected; protected endpoints reject unauthenticated requests.
- [-] **US-03:** As an authenticated user, I can see only my own data.
  - Acceptance: changing an identifier to another user's record returns an access error and changes nothing.

### Accounts and categories

- [ ] **US-04:** As a user, I can manage my accounts and see each current balance.
  - Acceptance: account type is validated; accounts with transactions can be archived but not deleted.
- [ ] **US-05:** As a user, I can manage income and expense categories.
  - Acceptance: category type and colour are validated; used categories can be archived but not deleted.

### Ledger

- [ ] **US-06:** As a user, I can record and edit income and expenses.
  - Acceptance: amount, account, category, and date are validated; balance changes correctly.
- [ ] **US-07:** As a user, I can filter and review transactions.
  - Acceptance: filters can be combined and results are paginated and ordered by newest date.
- [ ] **US-08:** As a user, I can transfer money between my accounts.
  - Acceptance: both linked entries are created atomically; total net worth does not change.

### Insights and frontend

- [ ] **US-09:** As a user, I can view a dashboard summary of my finances.
  - Acceptance: all displayed values are derived from my ledger and respect the selected month.
- [ ] **US-10:** As a user, I can analyse spending by category and over time.
  - Acceptance: charts update when I change the date range or account filter.
- [ ] **US-11:** As a user, I can use the application from desktop and mobile-sized screens.
  - Acceptance: key forms, tables, and charts remain usable at common mobile widths.

## 8. Delivery order

1. Authentication API, schema migration V2, and register/login frontend pages.
2. Accounts and categories API plus management pages.
3. Transactions API plus transaction form and list page.
4. Transfers.
5. Dashboard summary API and dashboard page.
6. Analytics API and charts.
7. Tests, error handling, responsive polish, documentation, and deployment.

Each item is delivered as a vertical slice: database migration, backend API, tests, then its React interface. Do not build the entire backend before testing it through the frontend.

## 9. Explicitly out of scope for the MVP

- Bank account synchronisation or CSV import.
- Budgets, savings goals, recurring transactions, loans, investments, and receipt uploads.
- Shared accounts or household collaboration.
- Multiple currencies and exchange rates.
- Administrator tools and email verification/password reset.

## 10. Definition of done for the MVP

The MVP is complete when a registered user can securely manage accounts, categories, transactions, and transfers; view accurate dashboard and analytics data; use the React UI on desktop and mobile; and run the full app locally using the documented Docker and Spring Boot commands.
