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
import xyz.erupt.annotation.sub_erupt.Power;
import xyz.erupt.annotation.sub_field.Edit;
import xyz.erupt.annotation.sub_field.EditType;
import xyz.erupt.annotation.sub_field.View;
import xyz.erupt.annotation.sub_field.sub_edit.ChoiceType;
import xyz.erupt.annotation.sub_field.sub_edit.ReferenceTableType;
import xyz.erupt.annotation.sub_field.sub_edit.Search;
import xyz.erupt.annotation.vis.CalendarView;
import xyz.erupt.annotation.vis.GanttView;
import xyz.erupt.jpa.model.MetaModelUpdateVo;
import xyz.erupt.pm.constant.IterationStatus;

import java.time.LocalDate;

/**
 * A time-boxed iteration (sprint) of a project.
 *
 * @author YuePeng
 */
@EruptI18n
@Erupt(
        name = "Iteration",
        desc = "Sprints: a time box with a goal, grouped by project on the gantt",
        orderBy = "Iteration.startDate desc",
        power = @Power(export = true),
        vis = {
                @Vis(code = "table", title = "Table", type = Vis.Type.TABLE),
                @Vis(code = "gantt", title = "Gantt", type = Vis.Type.GANTT,
                        ganttView = @GanttView(startDateField = "startDate", endDateField = "endDate", groupField = "project.name")),
                @Vis(code = "calendar", title = "Calendar", type = Vis.Type.CALENDAR,
                        calendarView = @CalendarView(dateField = "startDate", endDateField = "endDate", colorField = "project.color"))
        }
)
@Entity
@Table(name = "pm_iteration", indexes = @Index(name = "idx_iteration_project", columnList = "project_id"))
@Getter
@Setter
public class Iteration extends MetaModelUpdateVo {

    @ManyToOne(optional = false)
    @EruptField(
            views = {
                    @View(title = "Project", column = "name", width = "140px"),
                    // hidden colour column feeding the calendar's colorField = "project.color"
                    @View(title = "Project Color", column = "color", show = false)
            },
            edit = @Edit(title = "Project", notNull = true, type = EditType.REFERENCE_TABLE, search = @Search,
                    referenceTableType = @ReferenceTableType(label = "name"))
    )
    private Project project;

    @Column(nullable = false)
    @EruptField(
            views = @View(title = "Name", sortable = true),
            edit = @Edit(title = "Name", notNull = true, search = @Search(operator = QueryExpression.LIKE))
    )
    private String name;

    @EruptField(
            views = @View(title = "Goal"),
            edit = @Edit(title = "Goal", type = EditType.TEXTAREA)
    )
    private String goal;

    @Enumerated(EnumType.STRING)
    @Column(length = AnnotationConst.CODE_LENGTH, nullable = false)
    @EruptField(
            views = @View(title = "Status", width = "100px", sortable = true),
            edit = @Edit(title = "Status", notNull = true, type = EditType.CHOICE, search = @Search,
                    choiceType = @ChoiceType(fetchHandler = IterationStatus.H.class))
    )
    private IterationStatus status = IterationStatus.PLANNED;

    @Column(nullable = false)
    @EruptField(
            views = @View(title = "Start", width = "110px", sortable = true),
            edit = @Edit(title = "Start", notNull = true, search = @Search)
    )
    private LocalDate startDate;

    @Column(nullable = false)
    @EruptField(
            views = @View(title = "End", width = "110px", sortable = true),
            edit = @Edit(title = "End", notNull = true, search = @Search)
    )
    private LocalDate endDate;

}
