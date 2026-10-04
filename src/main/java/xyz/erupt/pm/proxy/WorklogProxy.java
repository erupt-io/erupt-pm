package xyz.erupt.pm.proxy;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import xyz.erupt.annotation.fun.DataProxy;
import xyz.erupt.pm.model.Worklog;
import xyz.erupt.pm.service.PmAuth;
import xyz.erupt.pm.service.WorklogService;
import xyz.erupt.upms.model.EruptUser;

import java.util.Optional;

/**
 * Stamps the current user on a worklog and re-totals {@code Task.spentHours} after every change.
 *
 * @author YuePeng
 */
@Component
public class WorklogProxy implements DataProxy<Worklog> {

    @Resource
    private PmAuth pmAuth;

    @Resource
    private WorklogService worklogService;

    @Override
    public void beforeAdd(Worklog worklog) {
        if (null == worklog.getUser()) {
            Optional.ofNullable(pmAuth.uid()).ifPresent(uid -> {
                EruptUser user = new EruptUser();
                user.setId(uid);
                worklog.setUser(user);
            });
        }
    }

    @Override
    public void afterAdd(Worklog worklog) {
        worklogService.refreshSpent(worklog.getTask().getId());
    }

    @Override
    public void afterUpdate(Worklog worklog) {
        worklogService.refreshSpent(worklog.getTask().getId());
    }

    @Override
    public void afterDelete(Worklog worklog) {
        worklogService.refreshSpent(worklog.getTask().getId());
    }

}
