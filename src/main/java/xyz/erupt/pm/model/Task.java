package xyz.erupt.pm.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import xyz.erupt.annotation.Erupt;
import xyz.erupt.annotation.EruptField;
import xyz.erupt.annotation.EruptI18n;
import xyz.erupt.annotation.Vis;
import xyz.erupt.annotation.config.QueryExpression;
import xyz.erupt.annotation.constant.AnnotationConst;
import xyz.erupt.annotation.sub_erupt.Filter;
import xyz.erupt.annotation.sub_erupt.Layout;
import xyz.erupt.annotation.sub_erupt.Power;
import xyz.erupt.annotation.sub_field.Edit;
import xyz.erupt.annotation.sub_field.EditType;
import xyz.erupt.annotation.sub_field.Readonly;
import xyz.erupt.annotation.sub_field.View;
import xyz.erupt.annotation.sub_field.ViewType;
import xyz.erupt.annotation.sub_field.sub_edit.*;
import xyz.erupt.annotation.vis.BoardView;
import xyz.erupt.annotation.vis.CalendarView;
import xyz.erupt.annotation.vis.GanttView;
import xyz.erupt.annotation.vis.TimelineView;
import xyz.erupt.jpa.model.MetaModelUpdateVo;
import xyz.erupt.pm.constant.TaskPriority;
import xyz.erupt.pm.constant.TaskStatus;
import xyz.erupt.pm.constant.TaskType;
import xyz.erupt.pm.proxy.MyTaskFilter;
import xyz.erupt.pm.proxy.TaskProxy;
import xyz.erupt.upms.model.EruptUser;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

/**
 * A work item. Sub-tasks hang off {@link #parent} (gantt tree), finish-to-start dependencies live
 * in {@link #predecessors} (gantt links) and {@link #milestone} rows are drawn as diamonds.
 * {@link TaskProxy} fills the reporter, keeps colour / status / progress consistent and notifies
 * the assignee.
 *
 * @author YuePeng
 */
@EruptI18n
@Erupt(
        name = "Task",
        desc = "Work items with sub-tasks, dependencies and milestones",
        orderBy = "Task.createTime desc",
        dataProxy = TaskProxy.class,
        power = @Power(export = true, importable = true),
        layout = @Layout(tableLeftFixed = 1),
        vis = {
                @Vis(code = "board", title = "Board", type = Vis.Type.BOARD,
                        boardView = @BoardView(groupField = "status", swimlaneField = "assignee.name", sumField = "estimateHours")),
                @Vis(code = "gantt", title = "Gantt", type = Vis.Type.GANTT,
                        ganttView = @GanttView(startDateField = "startDate", endDateField = "dueDate", pidField = "parent.id",
                                progressField = "progress", colorField = "color",
                                dependencyField = "predecessors", milestoneField = "milestone")),
                @Vis(code = "calendar", title = "Calendar", type = Vis.Type.CALENDAR,
                        calendarView = @CalendarView(dateField = "dueDate", colorField = "color")),
                @Vis(code = "timeline", title = "Timeline", type = Vis.Type.TIMELINE,
                        timelineView = @TimelineView(dateField = "createTime", colorField = "color")),
                @Vis(code = "table", title = "Table", type = Vis.Type.TABLE),
                // the caller's own work across every project: the filter handler injects the session user id
                @Vis(code = "mine", title = "My Tasks", desc = "Tasks assigned to me, across all projects", type = Vis.Type.BOARD,
                        filter = @Filter(conditionHandler = MyTaskFilter.class),
                        boardView = @BoardView(groupField = "status", swimlaneField = "project.name"))
        }
)
@Entity
@Table(name = "pm_task", indexes = {
        @Index(name = "idx_task_project", columnList = "project_id"),
        @Index(name = "idx_task_assignee", columnList = "assignee_id")
})
@Getter
@Setter
public class Task extends MetaModelUpdateVo {

    @ManyToOne(optional = false)
    @EruptField(
            views = @View(title = "Project", column = "name", width = "130px"),
            edit = @Edit(title = "Project", notNull = true, type = EditType.REFERENCE_TABLE, search = @Search,
                    referenceTableType = @ReferenceTableType(label = "name"))
    )
    private Project project;

    @ManyToOne
    @EruptField(
            views = @View(title = "Iteration", column = "name", width = "120px"),
            edit = @Edit(title = "Iteration", type = EditType.REFERENCE_TABLE, search = @Search,
                    referenceTableType = @ReferenceTableType(label = "name", dependField = "project", dependColumn = "project.id"))
    )
    private Iteration iteration;

    @ManyToOne
    @EruptField(
            views = {
                    @View(title = "Parent", column = "title", width = "160px"),
                    // hidden id column: the gantt resolves pidField = "parent.id" from the row key parent_id
                    @View(title = "Parent ID", column = "id", show = false)
            },
            edit = @Edit(title = "Parent", desc = "Sub-tasks nest under their parent on the gantt",
                    type = EditType.REFERENCE_TABLE,
                    referenceTableType = @ReferenceTableType(label = "title"))
    )
    private TaskRef parent;

    @Column(nullable = false)
    @EruptField(
            views = @View(title = "Title", sortable = true),
            edit = @Edit(title = "Title", notNull = true, search = @Search(operator = QueryExpression.LIKE))
    )
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(length = AnnotationConst.CODE_LENGTH, nullable = false)
    @EruptField(
            views = @View(title = "Type", width = "90px"),
            edit = @Edit(title = "Type", notNull = true, type = EditType.CHOICE, search = @Search,
                    choiceType = @ChoiceType(fetchHandler = TaskType.H.class))
    )
    private TaskType type = TaskType.TASK;

    @Enumerated(EnumType.STRING)
    @Column(length = AnnotationConst.CODE_LENGTH, nullable = false)
    @EruptField(
            views = @View(title = "Status", width = "110px", sortable = true),
            edit = @Edit(title = "Status", notNull = true, type = EditType.CHOICE, search = @Search,
                    choiceType = @ChoiceType(fetchHandler = TaskStatus.H.class))
    )
    private TaskStatus status = TaskStatus.TODO;

    @Enumerated(EnumType.STRING)
    @Column(length = AnnotationConst.CODE_LENGTH, nullable = false)
    @EruptField(
            views = @View(title = "Priority", width = "90px", sortable = true),
            edit = @Edit(title = "Priority", notNull = true, type = EditType.CHOICE, search = @Search,
                    choiceType = @ChoiceType(fetchHandler = TaskPriority.H.class))
    )
    private TaskPriority priority = TaskPriority.MEDIUM;

    @ManyToOne
    @EruptField(
            views = @View(title = "Assignee", column = "name", width = "100px"),
            edit = @Edit(title = "Assignee", type = EditType.REFERENCE_TABLE, search = @Search,
                    referenceTableType = @ReferenceTableType(label = "name"))
    )
    private EruptUser assignee;

    @ManyToOne
    @EruptField(
            views = @View(title = "Reporter", column = "name", width = "100px"),
            edit = @Edit(title = "Reporter", desc = "Filled with the creator", type = EditType.REFERENCE_TABLE,
                    readonly = @Readonly, referenceTableType = @ReferenceTableType(label = "name"))
    )
    private EruptUser reporter;

    @EruptField(
            views = @View(title = "Start", width = "110px", sortable = true),
            edit = @Edit(title = "Start", search = @Search)
    )
    private LocalDate startDate;

    @EruptField(
            views = @View(title = "Due", width = "110px", sortable = true),
            edit = @Edit(title = "Due", search = @Search)
    )
    private LocalDate dueDate;

    @EruptField(
            views = @View(title = "Estimate (h)", width = "100px"),
            edit = @Edit(title = "Estimate (h)", type = EditType.NUMBER, numberType = @NumberType(min = 0, precision = 1))
    )
    private BigDecimal estimateHours;

    @EruptField(
            views = @View(title = "Spent (h)", width = "90px"),
            edit = @Edit(title = "Spent (h)", desc = "Sum of worklogs", type = EditType.NUMBER, readonly = @Readonly)
    )
    private BigDecimal spentHours = BigDecimal.ZERO;

    @Column(nullable = false)
    @EruptField(
            views = @View(title = "Progress", width = "120px", type = ViewType.PROGRESS),
            edit = @Edit(title = "Progress", type = EditType.SLIDER, sliderType = @SliderType(max = 100, markPoints = {0, 50, 100}))
    )
    private Integer progress = 0;

    @Column(nullable = false)
    @EruptField(
            views = @View(title = "Milestone", width = "90px"),
            edit = @Edit(title = "Milestone", desc = "Drawn as a diamond on the gantt", type = EditType.BOOLEAN, search = @Search,
                    boolType = @BoolType(trueText = "Yes", falseText = "No"))
    )
    private Boolean milestone = false;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "pm_task_predecessor",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "predecessor_id"))
    @EruptField(
            views = @View(title = "Predecessors", show = false),
            edit = @Edit(title = "Predecessors", desc = "Tasks that must finish before this one starts",
                    type = EditType.TAB_TABLE_REFER)
    )
    private Set<TaskRef> predecessors;

    @EruptField(
            views = @View(title = "Tags", width = "140px"),
            edit = @Edit(title = "Tags", type = EditType.TAGS, search = @Search(operator = QueryExpression.LIKE),
                    tagsType = @TagsType(tags = {"frontend", "backend", "design", "qa", "docs"}))
    )
    private String tags;

    // hex colour derived from the priority by TaskProxy; feeds the gantt / calendar / timeline colorField
    @Column(length = 10)
    @EruptField(
            views = @View(title = "Color", show = false),
            edit = @Edit(title = "Color", show = false)
    )
    private String color;

    @Lob
    @EruptField(
            views = @View(title = "Description", type = ViewType.MARKDOWN, show = false),
            edit = @Edit(title = "Description", type = EditType.MARKDOWN)
    )
    private String description;

}
