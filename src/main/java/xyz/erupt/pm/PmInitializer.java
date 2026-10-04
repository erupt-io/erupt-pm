package xyz.erupt.pm;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import xyz.erupt.job.model.EruptJob;
import xyz.erupt.jpa.dao.EruptDao;
import xyz.erupt.notice.model.NoticeScene;
import xyz.erupt.pm.constant.PmConst;
import xyz.erupt.pm.job.DueSoonJob;

import java.util.Map;

/**
 * Idempotent seeding of what the module needs straight after the first boot: notice scenes,
 * the reminder job and (when the project table is empty) a demo data set.
 *
 * @author YuePeng
 */
@Slf4j
@Component
public class PmInitializer {

    private static final Map<String, String> SCENES = Map.of(
            PmConst.SCENE_TASK_ASSIGNED, "Task assigned",
            PmConst.SCENE_TASK_DUE_SOON, "Task due soon"
    );

    @Resource
    private EruptDao eruptDao;

    @Resource
    private PmDemoData pmDemoData;

    /**
     * Self-reference so each step below runs in its own transaction.
     */
    @Lazy
    @Resource
    private PmInitializer self;

    @EventListener(ApplicationReadyEvent.class)
    public void seed() {
        this.step("notice scenes", () -> self.seedScenes());
        this.step("scheduled job", () -> self.seedJob());
        this.step("demo data", () -> pmDemoData.seed());
    }

    private void step(String name, Runnable action) {
        try {
            action.run();
        } catch (Exception e) {
            log.error("erupt-pm initialisation failed ({}): {}", name, e.getMessage(), e);
        }
    }

    @Transactional
    public void seedScenes() {
        SCENES.forEach((code, name) -> {
            NoticeScene scene = new NoticeScene();
            scene.setCode(code);
            scene.setName(name);
            eruptDao.persistIfNotExist(NoticeScene.class, scene, "code", code);
        });
    }

    @Transactional
    public void seedJob() {
        EruptJob job = new EruptJob();
        job.setCode(DueSoonJob.CODE);
        job.setHandler(DueSoonJob.class.getName());
        job.setName("Task due-soon reminder");
        job.setCron("0 30 8 * * ?");
        job.setStatus(true);
        job.setRecordLog(true);
        job.setRemark("Notifies assignees of open tasks due within two days");
        eruptDao.persistIfNotExist(EruptJob.class, job, "code", DueSoonJob.CODE);
    }

}
