package xyz.erupt.pm.service;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import xyz.erupt.upms.service.EruptUserService;
import xyz.erupt.core.module.MetaUserinfo;

/**
 * Session user helpers; every accessor tolerates the no-session case (scheduled jobs, seeders).
 *
 * @author YuePeng
 */
@Service
public class PmAuth {

    @Resource
    private EruptUserService eruptUserService;

    /**
     * @return the cached user info, or null when there is no logged-in session
     */
    public MetaUserinfo userinfo() {
        try {
            return eruptUserService.getSimpleUserInfo();
        } catch (Exception e) {
            return null;
        }
    }

    public Long uid() {
        MetaUserinfo info = this.userinfo();
        return null == info ? null : info.getId();
    }

    public String username() {
        MetaUserinfo info = this.userinfo();
        return null == info ? "system" : info.getUsername();
    }

}
