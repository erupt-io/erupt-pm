# AGENTS.md

This file provides guidance to coding agents (Claude Code, Codex, ...) when working with code in this repository.

## Commands

```bash
# Run the application: http://localhost:8080, erupt / erupt
mvn spring-boot:run

# Run on MySQL (credentials come from the environment only: MYSQL_USER, MYSQL_PASSWORD)
mvn spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=mysql

# Build
mvn clean package
```

Java 17, system `mvn` (no wrapper), parent `spring-boot-starter-parent` 3.5.16, erupt at `erupt.version`
2.3.0 in `pom.xml`. There are no tests: the repo has no `src/test`, although `spring-boot-starter-test`
is declared.

## What this is

A runnable Spring Boot application (`xyz.erupt.pm.PmApplication`), not a library: project management on
erupt with projects, iterations, tasks (sub-tasks, dependencies, milestones), worklogs and a risk
register. Every screen is generated from annotations; the repo contains no frontend code. It doubles as
a showcase of erupt's multi-view `@Vis` system and of `@EruptScope`.

erupt modules used: `erupt-spring-boot-starter`, `erupt-job` (the daily reminder), `erupt-notice`
(in-app notifications), `erupt-comment` (discussion on records), `erupt-revision` (field-level history
with rollback).

## Architecture

- **Registration and seeding.** `EruptPmAutoConfiguration` is a `@Configuration` found by component scan
  (there is no `AutoConfiguration.imports`); it implements `EruptModule` and builds the menus in
  `initMenus()` (root `$pm`). `PmInitializer` runs on `ApplicationReadyEvent` and idempotently seeds the
  notice scenes (`PmConst.SCENE_*`), the `EruptJob` row for `job/DueSoonJob`, and, only while the project
  table is empty, the demo data of `PmDemoData` (two projects with iterations, tasks, worklogs, risks).
- **Models (`model/`, tables `pm_*`).** `Project`, `Iteration`, `Task`, `Worklog`, `Risk`. `TaskRef` is a
  read-only projection entity on the same table as `Task` (`pm_task`), used as the target of the
  predecessor picker so a self-referencing many-to-many does not drag every task's predecessors into
  each row. Status, priority and type enums live in `constant/` and implement `LabeledEnum`.
- **Views.** `Task` declares its views with `@Vis`: board, gantt (sub-task tree, finish-to-start
  dependencies, milestones, drag to reschedule), calendar, timeline, table, and "My Tasks", a board
  filtered to the session user by `proxy/MyTaskFilter` (a `FilterHandler` on that single `@Vis`).
  Iteration and Risk carry further gantt, calendar and board views.
- **Project scope.** `scope/ProjectScope` is a one-field form (a project choice) picked in the page
  header. Iteration, Task, TaskRef, Risk and Worklog declare `@EruptScope(ProjectScope.class)`; because
  their `project` reference shares the scope field's name, erupt binds it: lists and pickers show the
  picked project only, the add form is prefilled and writes land in that project. Worklog has no project
  column, so it declares the scope with `bind = false` and `WorklogProxy.beforeFetch` filters on
  `task.project.id`. An empty picker spans every project.
- **Behaviour in proxies and services.** `proxy/TaskProxy` fills the reporter, keeps status and progress
  in sync and notifies the assignee; `proxy/WorklogProxy` with `service/WorklogService` keeps the
  denormalised `Task.spentHours` equal to the sum of the task's worklogs. `service/PmNotifyService` sends
  notices in its own transaction, under a synthetic user when nobody is logged in (the due-soon job),
  so a delivery failure never rolls back the business change. `service/PmAuth` holds session-user
  helpers that tolerate the no-session case.
- **i18n.** Labels are English keys translated in `src/main/resources/i18n/erupt-pm.i18n.csv`
  (`key,zh-CN,zh-TW,en-US`); models are marked `@EruptI18n`.

## Local data and configuration

`src/main/resources/application.yml`: the default profile uses an H2 file database at `./data/pm` and
stores attachments under `./data/attachment` (`erupt.upload-path`); the `mysql` profile reads
`MYSQL_USER` / `MYSQL_PASSWORD` from the environment. `data/`, `.erupt/` and `target/` are git-ignored.
