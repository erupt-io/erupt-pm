package xyz.erupt.pm.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import xyz.erupt.annotation.Erupt;
import xyz.erupt.annotation.EruptField;
import xyz.erupt.annotation.EruptI18n;
import xyz.erupt.annotation.sub_erupt.Power;
import xyz.erupt.annotation.sub_field.Edit;
import xyz.erupt.annotation.sub_field.EditType;
import xyz.erupt.annotation.sub_field.Readonly;
import xyz.erupt.annotation.sub_field.View;
import xyz.erupt.annotation.sub_field.sub_edit.NumberType;
import xyz.erupt.annotation.sub_field.sub_edit.ReferenceTableType;
import xyz.erupt.annotation.sub_field.sub_edit.Search;
import xyz.erupt.jpa.model.MetaModelUpdateVo;
import xyz.erupt.pm.proxy.WorklogProxy;
import xyz.erupt.upms.model.EruptUser;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Hours booked on a task by a user. {@link WorklogProxy} keeps {@code Task.spentHours} in sync.
 *
 * @author YuePeng
 */
@EruptI18n
@Erupt(
        name = "Worklog",
        desc = "Hours booked on tasks",
        orderBy = "Worklog.date desc",
        dataProxy = WorklogProxy.class,
        power = @Power(export = true)
)
@Entity
@Table(name = "pm_worklog", indexes = @Index(name = "idx_worklog_task", columnList = "task_id"))
@Getter
@Setter
public class Worklog extends MetaModelUpdateVo {

    @ManyToOne(optional = false)
    @EruptField(
            views = @View(title = "Task", column = "title"),
            edit = @Edit(title = "Task", notNull = true, type = EditType.REFERENCE_TABLE, search = @Search,
                    referenceTableType = @ReferenceTableType(label = "title"))
    )
    private TaskRef task;

    @ManyToOne
    @EruptField(
            views = @View(title = "User", column = "name", width = "100px"),
            edit = @Edit(title = "User", desc = "Filled with the current user", type = EditType.REFERENCE_TABLE,
                    readonly = @Readonly, referenceTableType = @ReferenceTableType(label = "name"))
    )
    private EruptUser user;

    @Column(nullable = false)
    @EruptField(
            views = @View(title = "Date", width = "110px", sortable = true),
            edit = @Edit(title = "Date", notNull = true, search = @Search)
    )
    private LocalDate date;

    @Column(nullable = false)
    @EruptField(
            views = @View(title = "Hours", width = "90px"),
            edit = @Edit(title = "Hours", notNull = true, type = EditType.NUMBER, numberType = @NumberType(min = 0, precision = 1))
    )
    private BigDecimal hours;

    @EruptField(
            views = @View(title = "Note"),
            edit = @Edit(title = "Note", type = EditType.TEXTAREA)
    )
    private String note;

}
