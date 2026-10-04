package xyz.erupt.pm.constant;

import lombok.Getter;
import xyz.erupt.annotation.fun.ChoiceFetchHandler;
import xyz.erupt.annotation.fun.VLModel;

import java.util.List;

/**
 * Lifecycle of a project.
 *
 * @author YuePeng
 */
@Getter
public enum ProjectStatus implements LabeledEnum {

    PLANNING("Planning", "#8c8c8c"),
    ACTIVE("Active", "#1890ff"),
    ON_HOLD("On Hold", "#faad14"),
    DONE("Done", "#52c41a");

    private final String label;

    private final String color;

    ProjectStatus(String label, String color) {
        this.label = label;
        this.color = color;
    }

    public static class H implements ChoiceFetchHandler<Void> {
        @Override
        public List<VLModel> fetch(String[] params) {
            return LabeledEnum.vls(values());
        }
    }

}
