package xyz.erupt.pm.constant;

import lombok.Getter;
import xyz.erupt.annotation.fun.ChoiceFetchHandler;
import xyz.erupt.annotation.fun.VLModel;

import java.util.List;

/**
 * Damage if the risk materialises.
 *
 * @author YuePeng
 */
@Getter
public enum RiskImpact implements LabeledEnum {

    MINOR("Minor", "#52c41a"),
    MODERATE("Moderate", "#faad14"),
    SEVERE("Severe", "#f5222d");

    private final String label;

    private final String color;

    RiskImpact(String label, String color) {
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
