package xyz.erupt.pm.constant;

import lombok.Getter;
import xyz.erupt.annotation.fun.ChoiceFetchHandler;
import xyz.erupt.annotation.fun.VLModel;

import java.util.List;

/**
 * Priority; its colour is copied into Task.color so gantt bars reflect it.
 *
 * @author YuePeng
 */
@Getter
public enum TaskPriority implements LabeledEnum {

    LOW("Low", "#8c8c8c"),
    MEDIUM("Medium", "#1890ff"),
    HIGH("High", "#fa8c16"),
    URGENT("Urgent", "#f5222d");

    private final String label;

    private final String color;

    TaskPriority(String label, String color) {
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
