package xyz.erupt.pm.constant;

import xyz.erupt.annotation.fun.VLModel;

import java.util.Arrays;
import java.util.List;

/**
 * Shared contract for every business enum in this module so that a single helper can
 * turn any of them into the {@code @Edit(type = CHOICE)} option list, colour included.
 *
 * @author YuePeng
 */
public interface LabeledEnum {

    String name();

    String getLabel();

    String getColor();

    static List<VLModel> vls(LabeledEnum[] values) {
        return Arrays.stream(values)
                .map(it -> new VLModel(it.name(), it.getLabel(), null, it.getColor(), false))
                .toList();
    }

}
