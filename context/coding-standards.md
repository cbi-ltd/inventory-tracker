## Project conventions

- Controllers should be thin
- Business logic belongs in services
- Repositories should not contain business logic
___

## Things To Avoid

- Never change database migrations unless explicitly instructed
- Do not rename public REST endpoints
- Do not introduce new dependencies without asking
___

## Preferred Workflow

- Before writing code, explain the plan.
- Show a diff summary before making changes

___

## Architecture Rules

- This repository follows Clean Architecture.
- Never let controllers access repositories directly.
- Services communicate via REST only.
- DTOs must not contain business logic.
- Use any of MapStruct or ModelMapper for mappings.
- Follow existing package structure