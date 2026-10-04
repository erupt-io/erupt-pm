package xyz.erupt.pm.constant;

import lombok.Getter;
import xyz.erupt.annotation.fun.ChoiceFetchHandler;
import xyz.erupt.annotation.fun.VLModel;

import java.util.List;

/**
 * Board columns of a task; DONE and progress 100 are kept in sync by TaskProxy.
 *
 * @author YuePeng
 */
@Getter
public enum TaskStatus implements LabeledEnum {

    TODO("To Do", "#8c8c8c"),
    IN_PROGRESS("In Progress", "#1890ff"),
    IN_REVIEW("In Review", "#faad14"),
    DONE("Done", "#52c41a");

    private final String label;

    private final String color;

    TaskStatus(String label, String color) {
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
