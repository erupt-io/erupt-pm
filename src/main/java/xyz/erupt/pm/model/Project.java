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
import xyz.erupt.annotation.sub_field.ViewType;
import xyz.erupt.annotation.sub_field.sub_edit.ChoiceType;
import xyz.erupt.annotation.sub_field.sub_edit.ReferenceTableType;
import xyz.erupt.annotation.sub_field.sub_edit.Search;
import xyz.erupt.annotation.vis.CardView;
import xyz.erupt.jpa.model.MetaModelUpdateVo;
import xyz.erupt.pm.constant.ProjectStatus;
import xyz.erupt.upms.model.EruptUser;

import java.time.LocalDate;

/**
 * A project: the container every iteration, task and risk belongs to.
 *
 * @author YuePeng
 */
@EruptI18n
@Erupt(
        name = "Project",
        desc = "Projects with their owner, schedule and status",
        orderBy = "Project.createTime desc",
        power = @Power(export = true),
        vis = {
                @Vis(code = "table", title = "Table", type = Vis.Type.TABLE),
                @Vis(code = "card", title = "Cards", type = Vis.Type.CARD, cardView = @CardView)
        }
)
@Entity
@Table(name = "pm_project")
@Getter
@Setter
public class Project extends MetaModelUpdateVo {

    @Column(length = AnnotationConst.CODE_LENGTH, nullable = false, unique = true)
    @EruptField(
            views = @View(title = "Key", width = "90px", sortable = true),
            edit = @Edit(title = "Key", desc = "Short code used as task prefix, e.g. ERP", notNull = true, search = @Search)
    )
    private String key;

    @Column(nullable = false)
    @EruptField(
            views = @View(title = "Name", sortable = true),
            edit = @Edit(title = "Name", notNull = true, search = @Search(operator = QueryExpression.LIKE))
    )
    private String name;

    @ManyToOne
    @EruptField(
            views = @View(title = "Owner", column = "name", width = "110px"),
            edit = @Edit(title = "Owner", type = EditType.REFERENCE_TABLE, search = @Search,
                    referenceTableType = @ReferenceTableType(label = "name"))
    )
    private EruptUser owner;

    @Enumerated(EnumType.STRING)
    @Column(length = AnnotationConst.CODE_LENGTH, nullable = false)
    @EruptField(
            views = @View(title = "Status", width = "100px", sortable = true),
            edit = @Edit(title = "Status", notNull = true, type = EditType.CHOICE, search = @Search,
                    choiceType = @ChoiceType(fetchHandler = ProjectStatus.H.class))
    )
    private ProjectStatus status = ProjectStatus.PLANNING;

    @EruptField(
            views = @View(title = "Start", width = "110px", sortable = true),
            edit = @Edit(title = "Start", search = @Search)
    )
    private LocalDate startDate;

    @EruptField(
            views = @View(title = "End", width = "110px", sortable = true),
            edit = @Edit(title = "End", search = @Search)
    )
    private LocalDate endDate;

    @EruptField(
            views = @View(title = "Color", width = "70px", type = ViewType.COLOR),
            edit = @Edit(title = "Color", type = EditType.COLOR)
    )
    private String color = "#1890ff";

    @Lob
    @EruptField(
            views = @View(title = "Description", type = ViewType.MARKDOWN, show = false),
            edit = @Edit(title = "Description", type = EditType.MARKDOWN)
    )
    private String description;

}
