package xyz.erupt.pm;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import xyz.erupt.core.annotation.EruptScan;
import xyz.erupt.core.module.EruptModule;
import xyz.erupt.core.module.EruptModuleInvoke;
import xyz.erupt.core.module.MetaMenu;
import xyz.erupt.core.module.ModuleInfo;
import xyz.erupt.pm.constant.PmConst;
import xyz.erupt.pm.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Registers the project-management menu tree on first boot. Menu codes are stable, so a re-run
 * only fills in what is missing.
 *
 * @author YuePeng
 */
@Configuration
@ComponentScan
@EruptScan
@EntityScan
public class EruptPmAutoConfiguration implements EruptModule {

    static {
        EruptModuleInvoke.addEruptModule(EruptPmAutoConfiguration.class);
    }

    public static final String MENU_ROOT = "$pm";

    @Override
    public ModuleInfo info() {
        return ModuleInfo.builder().name(PmConst.MODULE).description("Project management").build();
    }

    @Override
    public List<MetaMenu> initMenus() {
        List<MetaMenu> menus = new ArrayList<>();
        MetaMenu root = MetaMenu.createRootMenu(MENU_ROOT, "Project Management", "fa fa-tasks", 10);
        menus.add(root);
        menus.add(MetaMenu.createEruptClassMenu(Project.class, root, 20));
        menus.add(MetaMenu.createEruptClassMenu(Iteration.class, root, 30));
        menus.add(MetaMenu.createEruptClassMenu(Task.class, root, 40));
        menus.add(MetaMenu.createEruptClassMenu(Worklog.class, root, 50));
        menus.add(MetaMenu.createEruptClassMenu(Risk.class, root, 60));
        return menus;
    }

}
