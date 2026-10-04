package xyz.erupt.pm.constant;

import lombok.Getter;
import xyz.erupt.annotation.fun.ChoiceFetchHandler;
import xyz.erupt.annotation.fun.VLModel;

import java.util.List;

/**
 * How likely a risk is to materialise.
 *
 * @author YuePeng
 */
@Getter
public enum RiskProbability implements LabeledEnum {

    LOW("Low", "#52c41a"),
    MEDIUM("Medium", "#faad14"),
    HIGH("High", "#f5222d");

    private final String label;

    private final String color;

    RiskProbability(String label, String color) {
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
