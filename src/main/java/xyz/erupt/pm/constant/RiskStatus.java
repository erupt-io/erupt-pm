package xyz.erupt.pm.constant;

import lombok.Getter;
import xyz.erupt.annotation.fun.ChoiceFetchHandler;
import xyz.erupt.annotation.fun.VLModel;

import java.util.List;

/**
 * Board columns of a risk.
 *
 * @author YuePeng
 */
@Getter
public enum RiskStatus implements LabeledEnum {

    OPEN("Open", "#f5222d"),
    MITIGATING("Mitigating", "#faad14"),
    CLOSED("Closed", "#52c41a");

    private final String label;

    private final String color;

    RiskStatus(String label, String color) {
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
