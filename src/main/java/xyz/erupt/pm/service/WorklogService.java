package xyz.erupt.pm.service;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.erupt.pm.model.Task;
import xyz.erupt.pm.model.Worklog;

import java.math.BigDecimal;

/**
 * Keeps the denormalised {@code Task.spentHours} equal to the sum of the task's worklogs.
 *
 * @author YuePeng
 */
@Service
public class WorklogService {

    @Resource
    private EntityManager entityManager;

    @Transactional
    public void refreshSpent(Long taskId) {
        if (null == taskId) {
            return;
        }
        BigDecimal sum = entityManager.createQuery(
                        "select coalesce(sum(w.hours), 0) from " + Worklog.class.getSimpleName() + " w where w.task.id = :id", BigDecimal.class)
                .setParameter("id", taskId).getSingleResult();
        Task task = entityManager.find(Task.class, taskId);
        if (null != task) {
            task.setSpentHours(sum);
            entityManager.merge(task);
        }
    }

}
