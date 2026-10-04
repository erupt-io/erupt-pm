package xyz.erupt.pm.proxy;

import com.google.gson.JsonObject;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import xyz.erupt.annotation.fun.DataProxy;
import xyz.erupt.core.context.OldEntityTL;
import xyz.erupt.core.config.GsonFactory;
import xyz.erupt.pm.constant.PmConst;
import xyz.erupt.pm.constant.TaskStatus;
import xyz.erupt.pm.model.Task;
import xyz.erupt.pm.service.PmAuth;
import xyz.erupt.pm.service.PmNotifyService;
import xyz.erupt.upms.model.EruptUser;

import java.util.List;
import java.util.Optional;

/**
 * Keeps a task self-consistent on every save: reporter defaults to the creator, colour follows
 * priority, DONE and progress 100 imply each other, and a (re)assignment notifies the assignee.
 * The previous assignee is read from {@link OldEntityTL}, the pristine snapshot erupt parks
 * before an update.
 *
 * @author YuePeng
 */
@Slf4j
@Component
public class TaskProxy implements DataProxy<Task> {

    @Resource
    private PmAuth pmAuth;

    @Resource
    private PmNotifyService notifyService;

    @Override
    public void beforeAdd(Task task) {
        if (null == task.getReporter()) {
            Optional.ofNullable(pmAuth.uid()).ifPresent(uid -> {
                EruptUser reporter = new EruptUser();
                reporter.setId(uid);
                task.setReporter(reporter);
            });
        }
        this.normalize(task);
    }

    @Override
    public void afterAdd(Task task) {
        this.notifyAssignee(task, null);
    }

    @Override
    public void beforeUpdate(Task task) {
        this.normalize(task);
    }

    @Override
    public void afterUpdate(Task task) {
        this.notifyAssignee(task, this.previousAssigneeId());
    }

    private void normalize(Task task) {
        if (null != task.getPriority()) {
            task.setColor(task.getPriority().getColor());
        }
        if (TaskStatus.DONE == task.getStatus()) {
            task.setProgress(100);
        } else if (null != task.getProgress() && task.getProgress() >= 100) {
            task.setStatus(TaskStatus.DONE);
        }
    }

    private void notifyAssignee(Task task, Long previousAssignee) {
        Long assignee = null == task.getAssignee() ? null : task.getAssignee().getId();
        if (null == assignee || assignee.equals(previousAssignee) || assignee.equals(pmAuth.uid())) {
            return;
        }
        notifyService.notify(PmConst.SCENE_TASK_ASSIGNED, List.of(assignee),
                "Task assigned: " + task.getTitle(),
                String.format("%s assigned you the task \"%s\"%s.", pmAuth.username(), task.getTitle(),
                        null == task.getDueDate() ? "" : ", due " + task.getDueDate()));
    }

    /**
     * @return the assignee id held before this update, or null when it cannot be determined
     */
    private Long previousAssigneeId() {
        String snapshot = OldEntityTL.get();
        if (null == snapshot) {
            return null;
        }
        try {
            JsonObject json = GsonFactory.getGson().fromJson(snapshot, JsonObject.class);
            if (null == json || !json.has("assignee") || !json.get("assignee").isJsonObject()) {
                return null;
            }
            JsonObject assignee = json.getAsJsonObject("assignee");
            return assignee.has("id") && !assignee.get("id").isJsonNull() ? assignee.get("id").getAsLong() : null;
        } catch (Exception e) {
            log.debug("Could not parse the previous task state: {}", e.getMessage());
            return null;
        }
    }

}
