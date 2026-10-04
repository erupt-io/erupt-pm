package xyz.erupt.pm.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import xyz.erupt.core.context.MetaContext;
import xyz.erupt.core.context.MetaUser;
import xyz.erupt.notice.channel.EruptInternalNotice;
import xyz.erupt.notice.pojo.NoticeMessage;
import xyz.erupt.notice.service.EruptNoticeService;

import java.util.List;
import java.util.Objects;

/**
 * In-app notifications. Runs in its own transaction so a delivery failure never rolls back the
 * business change, and under a synthetic user when nobody is logged in (the due-soon job), because
 * {@link EruptNoticeService} records the sender from the current session.
 *
 * @author YuePeng
 */
@Slf4j
@Service
public class PmNotifyService {

    @Resource
    private EruptNoticeService eruptNoticeService;

    @Resource
    private EruptInternalNotice eruptInternalNotice;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void notify(String scene, List<Long> receivers, String title, String content) {
        List<Long> targets = receivers.stream().filter(Objects::nonNull).distinct().toList();
        if (targets.isEmpty()) {
            return;
        }
        boolean synthetic = null == MetaContext.getUser() || null == MetaContext.getUser().getUid();
        if (synthetic) {
            MetaContext.set(new MetaContext());
            MetaContext.register(new MetaUser(targets.get(0), "system", "System"));
        }
        try {
            NoticeMessage message = new NoticeMessage();
            message.setTitle(title);
            message.setContent(content);
            eruptNoticeService.send(eruptInternalNotice, scene, targets, message);
        } catch (Exception e) {
            log.error("Notice delivery failed scene={}: {}", scene, e.getMessage());
        } finally {
            if (synthetic) {
                MetaContext.remove();
            }
        }
    }

}
