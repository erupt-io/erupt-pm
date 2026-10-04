package xyz.erupt.pm;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import xyz.erupt.jpa.dao.EruptDao;
import xyz.erupt.pm.constant.*;
import xyz.erupt.pm.model.*;
import xyz.erupt.upms.model.EruptUser;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * One realistic project so the views have something to show on first boot: two iterations,
 * a dozen tasks with sub-tasks, finish-to-start dependencies and a release milestone, a few
 * worklogs and two risks. Runs only while the project table is empty.
 *
 * @author YuePeng
 */
@Slf4j
@Component
public class PmDemoData {

    @Resource
    private EruptDao eruptDao;

    @Transactional
    public void seed() {
        if (!eruptDao.lambdaQuery(Project.class).list().isEmpty()) {
            return;
        }
        EruptUser admin = eruptDao.lambdaQuery(EruptUser.class).eq(EruptUser::getAccount, "erupt").one();
        LocalDate today = LocalDate.now();
        LocalDate monday = today.minusDays(today.getDayOfWeek().getValue() - 1L);

        Project project = new Project();
        project.setKey("SHOP");
        project.setName("Online Shop Relaunch");
        project.setOwner(admin);
        project.setStatus(ProjectStatus.ACTIVE);
        project.setStartDate(monday.minusWeeks(2));
        project.setEndDate(monday.plusWeeks(6));
        project.setColor("#1890ff");
        project.setDescription("Rebuild the storefront on the new design system and migrate checkout to the payment gateway v2.");
        eruptDao.persist(project);

        Iteration sprint1 = this.iteration(project, "Sprint 1 - Foundations", "Design system and catalogue API in place",
                monday.minusWeeks(2), monday.minusDays(1), IterationStatus.CLOSED);
        Iteration sprint2 = this.iteration(project, "Sprint 2 - Checkout", "Checkout flow end to end on gateway v2",
                monday, monday.plusWeeks(2).minusDays(1), IterationStatus.ACTIVE);

        // sprint 1: done
        Task design = this.task(project, sprint1, null, "Design system tokens and components", TaskType.STORY,
                TaskStatus.DONE, TaskPriority.HIGH, admin, monday.minusWeeks(2), monday.minusWeeks(1), 24, "design");
        Task catalogueApi = this.task(project, sprint1, null, "Catalogue API", TaskType.STORY,
                TaskStatus.DONE, TaskPriority.HIGH, admin, monday.minusWeeks(2), monday.minusDays(2), 32, "backend");
        this.task(project, sprint1, catalogueApi, "Product listing endpoint", TaskType.TASK,
                TaskStatus.DONE, TaskPriority.MEDIUM, admin, monday.minusWeeks(2), monday.minusWeeks(1).minusDays(2), 12, "backend");
        this.task(project, sprint1, catalogueApi, "Search and facets", TaskType.TASK,
                TaskStatus.DONE, TaskPriority.MEDIUM, admin, monday.minusWeeks(1).minusDays(1), monday.minusDays(2), 16, "backend");

        // sprint 2: in flight
        Task productPage = this.task(project, sprint2, null, "Product page", TaskType.STORY,
                TaskStatus.IN_PROGRESS, TaskPriority.HIGH, admin, monday, monday.plusDays(4), 20, "frontend", design, catalogueApi);
        productPage.setProgress(40);
        eruptDao.merge(productPage);
        Task cart = this.task(project, sprint2, null, "Shopping cart", TaskType.STORY,
                TaskStatus.IN_PROGRESS, TaskPriority.HIGH, admin, monday.plusDays(2), monday.plusDays(7), 16, "frontend", productPage);
        Task gateway = this.task(project, sprint2, null, "Payment gateway v2 integration", TaskType.STORY,
                TaskStatus.TODO, TaskPriority.URGENT, admin, monday.plusDays(3), monday.plusDays(10), 30, "backend");
        this.task(project, sprint2, gateway, "Tokenised card payments", TaskType.TASK,
                TaskStatus.TODO, TaskPriority.URGENT, admin, monday.plusDays(3), monday.plusDays(7), 16, "backend");
        this.task(project, sprint2, gateway, "Webhook reconciliation", TaskType.TASK,
                TaskStatus.TODO, TaskPriority.HIGH, admin, monday.plusDays(7), monday.plusDays(10), 10, "backend");
        Task checkout = this.task(project, sprint2, null, "Checkout flow", TaskType.STORY,
                TaskStatus.TODO, TaskPriority.URGENT, admin, monday.plusDays(8), monday.plusDays(13), 24, "frontend", cart, gateway);
        this.task(project, sprint2, null, "Price rounding bug on discounted items", TaskType.BUG,
                TaskStatus.IN_REVIEW, TaskPriority.MEDIUM, admin, monday, monday.plusDays(1), 3, "backend");
        this.task(project, null, null, "Regression test suite", TaskType.TASK,
                TaskStatus.TODO, TaskPriority.MEDIUM, null, monday.plusDays(10), monday.plusDays(13), 12, "qa", checkout);
        Task release = this.task(project, null, null, "Public launch", TaskType.EPIC,
                TaskStatus.TODO, TaskPriority.URGENT, admin, monday.plusDays(14), monday.plusDays(14), null, "docs", checkout);
        release.setMilestone(true);
        eruptDao.merge(release);

        this.worklog(design, admin, monday.minusWeeks(2).plusDays(1), 6, "Token audit");
        this.worklog(design, admin, monday.minusWeeks(2).plusDays(2), 8, "Button, input, card");
        this.worklog(catalogueApi, admin, monday.minusWeeks(1), 7, "Listing endpoint");
        this.worklog(productPage, admin, monday, 5, "Gallery and variants");

        this.risk(project, "Gateway v2 sandbox unstable", RiskProbability.MEDIUM, RiskImpact.SEVERE, RiskStatus.MITIGATING, admin,
                "Keep gateway v1 adapter behind a feature flag until v2 passes two full checkout runs.");
        this.risk(project, "Design hand-off later than planned", RiskProbability.LOW, RiskImpact.MODERATE, RiskStatus.OPEN, admin,
                "Weekly design review; front-end starts from the tokens already shipped.");
        log.info("erupt-pm demo data created: 1 project, 2 iterations, 13 tasks");
    }

    private Iteration iteration(Project project, String name, String goal, LocalDate start, LocalDate end, IterationStatus status) {
        Iteration it = new Iteration();
        it.setProject(project);
        it.setName(name);
        it.setGoal(goal);
        it.setStartDate(start);
        it.setEndDate(end);
        it.setStatus(status);
        eruptDao.persist(it);
        return it;
    }

    private Task task(Project project, Iteration iteration, Task parent, String title, TaskType type, TaskStatus status,
                      TaskPriority priority, EruptUser assignee, LocalDate start, LocalDate due, Integer estimate,
                      String tag, Task... predecessors) {
        Task task = new Task();
        task.setProject(project);
        task.setIteration(iteration);
        task.setParent(null == parent ? null : this.ref(parent));
        task.setTitle(title);
        task.setType(type);
        task.setStatus(status);
        task.setPriority(priority);
        task.setColor(priority.getColor());
        task.setAssignee(assignee);
        task.setReporter(assignee);
        task.setStartDate(start);
        task.setDueDate(due);
        task.setEstimateHours(null == estimate ? null : BigDecimal.valueOf(estimate));
        task.setProgress(TaskStatus.DONE == status ? 100 : 0);
        task.setTags("[\"" + tag + "\"]");
        Set<TaskRef> refs = new HashSet<>();
        for (Task p : predecessors) {
            refs.add(this.ref(p));
        }
        task.setPredecessors(refs);
        eruptDao.persist(task);
        return task;
    }

    private TaskRef ref(Task task) {
        return eruptDao.find(TaskRef.class, task.getId());
    }

    private void worklog(Task task, EruptUser user, LocalDate date, int hours, String note) {
        Worklog w = new Worklog();
        w.setTask(this.ref(task));
        w.setUser(user);
        w.setDate(date);
        w.setHours(BigDecimal.valueOf(hours));
        w.setNote(note);
        eruptDao.persist(w);
        task.setSpentHours(task.getSpentHours().add(w.getHours()));
        eruptDao.merge(task);
    }

    private void risk(Project project, String title, RiskProbability probability, RiskImpact impact, RiskStatus status,
                      EruptUser owner, String mitigation) {
        Risk r = new Risk();
        r.setProject(project);
        r.setTitle(title);
        r.setProbability(probability);
        r.setImpact(impact);
        r.setStatus(status);
        r.setOwner(owner);
        r.setMitigation(mitigation);
        eruptDao.persist(r);
    }

}
