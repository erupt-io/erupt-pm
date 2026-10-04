# erupt-pm · Project Management on Erupt

A small but complete project-management application built on the [Erupt](https://www.erupt.xyz)
low-code framework: projects, iterations, tasks with sub-tasks / dependencies / milestones,
worklogs and a risk register. Every screen is generated from annotations — there is no frontend
code in this repository.

It doubles as a showcase of Erupt's multi-view (`@Vis`) system: the same task table is shown as a
board, a gantt, a calendar, a timeline and a plain table.

## Quick start

```bash
mvn spring-boot:run
```

Open <http://localhost:8080> and log in with `erupt` / `erupt`. The default profile uses a
file-backed H2 database under `./data`, and a demo project (two sprints, 13 tasks, worklogs,
risks) is seeded on the first boot. For MySQL run with `--spring.profiles.active=mysql` and
supply `MYSQL_USER` / `MYSQL_PASSWORD` through the environment.

Requires Java 17 and Erupt 2.3.0.

## Entities and views

| Entity | Views | Notes |
|---|---|---|
| Project | TABLE, CARD | Owner, schedule, status, colour |
| Iteration | TABLE, GANTT (grouped by project), CALENDAR | Sprint time boxes with a goal |
| Task | BOARD (status × assignee swimlanes, estimate sum), GANTT (sub-task tree, finish-to-start dependencies, milestone diamonds, colour by priority, drag to reschedule), CALENDAR (due date), TIMELINE (creation), TABLE, My Tasks (BOARD, status × project, filtered to the session user by `MyTaskFilter`) | `TaskProxy` fills the reporter, keeps status/progress in sync and notifies the assignee |
| Worklog | TABLE | Hours per task; `Task.spentHours` is re-totalled by `WorklogProxy` |
| Risk | TABLE, BOARD (status × project) | Probability, impact, owner, mitigation |

## Framework features demonstrated

- `@Vis` with `BoardView` swimlanes / `sumField`, `GanttView` `pidField` + `dependencyField` + `milestoneField` + `progressField` + `colorField`, `CalendarView`, `TimelineView`
- Self-referencing pickers through a lightweight projection entity (`TaskRef`, same table as `Task`)
- `ReferenceTableType.dependField` (iterations filtered by the chosen project)
- `DataProxy` lifecycle hooks and the pre-update snapshot (`OldEntityTL`)
- Row-level filtering with `FilterHandler`, both on the entity and on a single `@Vis` (My Tasks)
- `erupt-comment` (discussion on every record), `erupt-revision` (field-level history with rollback)
- `erupt-notice` scenes (task assigned, task due soon) and an `erupt-job` daily reminder
- Module registration (`EruptModule`), idempotent seeding on `ApplicationReadyEvent`, i18n CSV

## 中文说明

基于 erupt 的项目管理应用：项目 → 迭代 → 任务（子任务、前置依赖、里程碑）→ 工时，外加风险登记。
全部界面由注解生成。`mvn spring-boot:run` 启动，账号 `erupt` / `erupt`，首次启动自动写入演示数据。
任务同时提供看板、甘特图、日历、时间线和表格五种视图；"我的任务"跨项目聚合当前用户的工作。
