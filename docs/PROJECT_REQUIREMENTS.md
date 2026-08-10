# Vero — Project Requirements

## Functional requirements

### Authentication and privacy

- **FR-01:** A visitor can register with display name, unique email, password, and base currency.
- **FR-02:** A registered user can log in and log out.
- **FR-03:** Passwords are never stored in plain text; only a secure password hash is stored.
- **FR-04:** Every application endpoint, except registration and login, requires authentication.
- **FR-05:** A user can never read or change another user's data.

### Password requirements

- Passwords must be between 12 and 72 UTF-8 bytes; `12345` is rejected.
- Passwords on the application's common-password blocklist are rejected.
- Passwords may contain spaces, Unicode, and any other characters. The application does not impose arbitrary upper-case, number, or symbol rules.
- Passwords must not equal the email address or its local part.
- Passwords are hashed with BCrypt before storage and are never logged, returned by an API, or stored in browser storage.

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

## Business and data rules

- Money uses `BigDecimal` in Java and `NUMERIC(19,2)` in PostgreSQL; never `double` or `float`.
- Transaction amounts must be greater than zero.
- Income and expense entries require a category of the same type.
- Transfer entries cannot have a category and must have a transfer identifier.
- A transfer source and destination account must be different and owned by the same user.
- Account and category names must be unique per user, as defined by the database constraints.
- Account deletion is blocked when it has transactions; the account is archived instead.
- Category deletion is blocked when it is used by a transaction; the category is archived instead.
- Dates are stored as dates for transactions; timestamps are stored in UTC.

## Technical requirements

- The backend uses Java 21 and exposes JSON REST endpoints under `/api`.
- PostgreSQL runs locally with Docker Compose.
- Flyway SQL migrations own the schema; Hibernate validates the schema but does not create it.
- The future frontend uses React with TypeScript and includes responsive forms, tables, filters, and charts.
- Database credentials stay in the untracked `.env` file; only `.env.example` is committed.
- The application returns clear validation errors without exposing passwords or internal error details.
- Unit tests cover business rules; integration tests cover persistence and authenticated API behaviour.
