package xyz.erupt.pm.proxy;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import xyz.erupt.annotation.fun.FilterHandler;
import xyz.erupt.pm.service.PmAuth;

/**
 * Row filter of the "My Tasks" view of {@code Task}: only rows assigned to the session user. The HQL alias is the
 * entity's simple name; the interpolated value is the numeric session user id, never request input.
 *
 * @author YuePeng
 */
@Component
public class MyTaskFilter implements FilterHandler {

    @Resource
    private PmAuth pmAuth;

    @Override
    public String filter(String condition, String[] params) {
        Long uid = pmAuth.uid();
        return null == uid ? "1 = 0" : "Task.assignee.id = " + uid;
    }

}
