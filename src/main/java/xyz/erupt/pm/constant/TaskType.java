package xyz.erupt.pm.constant;

import lombok.Getter;
import xyz.erupt.annotation.fun.ChoiceFetchHandler;
import xyz.erupt.annotation.fun.VLModel;

import java.util.List;

/**
 * Kind of work item.
 *
 * @author YuePeng
 */
@Getter
public enum TaskType implements LabeledEnum {

    TASK("Task", "#1890ff"),
    BUG("Bug", "#f5222d"),
    STORY("Story", "#52c41a"),
    EPIC("Epic", "#722ed1");

    private final String label;

    private final String color;

    TaskType(String label, String color) {
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
