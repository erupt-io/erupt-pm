package xyz.erupt.pm.constant;

/**
 * Module-wide string constants: module name, notice scene codes and the gantt colour palette.
 *
 * @author YuePeng
 */
public final class PmConst {

    private PmConst() {
    }

    public static final String MODULE = "erupt-pm";

    /** Notice scene: a task was assigned (or re-assigned) to a user. */
    public static final String SCENE_TASK_ASSIGNED = "pm-task-assigned";

    /** Notice scene: a task is due within the next two days. */
    public static final String SCENE_TASK_DUE_SOON = "pm-task-due-soon";

    /** Days ahead the due-soon job looks. */
    public static final int DUE_SOON_DAYS = 2;

}
