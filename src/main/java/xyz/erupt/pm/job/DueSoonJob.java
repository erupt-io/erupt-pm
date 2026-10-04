package xyz.erupt.pm.job;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import xyz.erupt.job.handler.EruptJobHandler;
import xyz.erupt.jpa.dao.EruptDao;
import xyz.erupt.pm.constant.PmConst;
import xyz.erupt.pm.constant.TaskStatus;
import xyz.erupt.pm.model.Task;
import xyz.erupt.pm.service.PmNotifyService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Daily reminder: every assignee gets one notice listing their open tasks due within
 * {@link PmConst#DUE_SOON_DAYS} days (overdue ones included).
 *
 * @author YuePeng
 */
@Service
public class DueSoonJob implements EruptJobHandler {

    public static final String CODE = "pm-due-soon";

    @Resource
    private EruptDao eruptDao;

    @Resource
    private PmNotifyService notifyService;

    @Override
    public String name() {
        return "Task due-soon reminder";
    }

    /**
     * Daily at 08:30.
     */
    @Override
    public String cron() {
        return "0 30 8 * * ?";
    }

    @Override
    public String exec(String code, String param) {
        LocalDate until = LocalDate.now().plusDays(PmConst.DUE_SOON_DAYS);
        List<Task> due = eruptDao.lambdaQuery(Task.class)
                .ne(Task::getStatus, TaskStatus.DONE)
                .isNotNull(Task::getAssignee)
                .le(Task::getDueDate, until)
                .list();
        Map<Long, List<Task>> byAssignee = due.stream().collect(Collectors.groupingBy(it -> it.getAssignee().getId()));
        byAssignee.forEach((uid, tasks) -> notifyService.notify(PmConst.SCENE_TASK_DUE_SOON, List.of(uid),
                tasks.size() + " task(s) due soon",
                tasks.stream().map(it -> "- " + it.getTitle() + " (due " + it.getDueDate() + ")").collect(Collectors.joining("\n"))));
        return "Reminded " + byAssignee.size() + " assignee(s) about " + due.size() + " task(s)";
    }

}
