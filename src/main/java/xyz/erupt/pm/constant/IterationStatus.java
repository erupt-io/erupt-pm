package xyz.erupt.pm.constant;

import lombok.Getter;
import xyz.erupt.annotation.fun.ChoiceFetchHandler;
import xyz.erupt.annotation.fun.VLModel;

import java.util.List;

/**
 * Lifecycle of an iteration (sprint).
 *
 * @author YuePeng
 */
@Getter
public enum IterationStatus implements LabeledEnum {

    PLANNED("Planned", "#8c8c8c"),
    ACTIVE("Active", "#1890ff"),
    CLOSED("Closed", "#52c41a");

    private final String label;

    private final String color;

    IterationStatus(String label, String color) {
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
