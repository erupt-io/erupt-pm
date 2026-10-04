package xyz.erupt.pm.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import xyz.erupt.annotation.Erupt;
import xyz.erupt.annotation.EruptField;
import xyz.erupt.annotation.EruptI18n;
import xyz.erupt.annotation.config.QueryExpression;
import xyz.erupt.annotation.constant.AnnotationConst;
import xyz.erupt.annotation.sub_erupt.Power;
import xyz.erupt.annotation.sub_field.Edit;
import xyz.erupt.annotation.sub_field.EditType;
import xyz.erupt.annotation.sub_field.View;
import xyz.erupt.annotation.sub_field.sub_edit.ChoiceType;
import xyz.erupt.annotation.sub_field.sub_edit.ReferenceTableType;
import xyz.erupt.annotation.sub_field.sub_edit.Search;
import xyz.erupt.jpa.model.BaseModel;
import xyz.erupt.pm.constant.TaskStatus;

import java.time.LocalDate;

/**
 * Read-only projection of {@link Task} used as the target of the predecessor picker, the same
 * trick upms uses for {@code EruptRole.users}: a self-referencing many-to-many on the full entity
 * would drag every task's predecessors (and theirs) into each row. Same table, a handful of columns.
 *
 * @author YuePeng
 */
@EruptI18n
@Erupt(
        name = "Task Reference",
        orderBy = "TaskRef.dueDate asc",
        power = @Power(add = false, edit = false, delete = false, export = false, importable = false)
)
@Entity
@Table(name = "pm_task")
@Getter
@Setter
public class TaskRef extends BaseModel {

    @ManyToOne
    @EruptField(
            views = @View(title = "Project", column = "name", width = "140px"),
            edit = @Edit(title = "Project", type = EditType.REFERENCE_TABLE, search = @Search,
                    referenceTableType = @ReferenceTableType(label = "name"))
    )
    private Project project;

    @EruptField(
            views = @View(title = "Title"),
            edit = @Edit(title = "Title", search = @Search(operator = QueryExpression.LIKE))
    )
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(length = AnnotationConst.CODE_LENGTH)
    @EruptField(
            views = @View(title = "Status", width = "100px"),
            edit = @Edit(title = "Status", type = EditType.CHOICE, search = @Search,
                    choiceType = @ChoiceType(fetchHandler = TaskStatus.H.class))
    )
    private TaskStatus status;

    @EruptField(
            views = @View(title = "Due", width = "110px"),
            edit = @Edit(title = "Due")
    )
    private LocalDate dueDate;

}
