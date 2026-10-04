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
import xyz.erupt.annotation.vis.BoardView;
import xyz.erupt.jpa.model.MetaModelUpdateVo;
import xyz.erupt.pm.constant.RiskImpact;
import xyz.erupt.pm.constant.RiskProbability;
import xyz.erupt.pm.constant.RiskStatus;
import xyz.erupt.upms.model.EruptUser;

/**
 * A project risk with probability, impact, owner and mitigation plan.
 *
 * @author YuePeng
 */
@EruptI18n
@Erupt(
        name = "Risk",
        desc = "Risk register per project",
        orderBy = "Risk.createTime desc",
        power = @Power(export = true),
        vis = {
                @Vis(code = "table", title = "Table", type = Vis.Type.TABLE),
                @Vis(code = "board", title = "Board", type = Vis.Type.BOARD,
                        boardView = @BoardView(groupField = "status", swimlaneField = "project.name"))
        }
)
@Entity
@Table(name = "pm_risk", indexes = @Index(name = "idx_risk_project", columnList = "project_id"))
@Getter
@Setter
public class Risk extends MetaModelUpdateVo {

    @ManyToOne(optional = false)
    @EruptField(
            views = @View(title = "Project", column = "name", width = "130px"),
            edit = @Edit(title = "Project", notNull = true, type = EditType.REFERENCE_TABLE, search = @Search,
                    referenceTableType = @ReferenceTableType(label = "name"))
    )
    private Project project;

    @Column(nullable = false)
    @EruptField(
            views = @View(title = "Title", sortable = true),
            edit = @Edit(title = "Title", notNull = true, search = @Search(operator = QueryExpression.LIKE))
    )
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(length = AnnotationConst.CODE_LENGTH, nullable = false)
    @EruptField(
            views = @View(title = "Probability", width = "100px"),
            edit = @Edit(title = "Probability", notNull = true, type = EditType.CHOICE, search = @Search,
                    choiceType = @ChoiceType(fetchHandler = RiskProbability.H.class))
    )
    private RiskProbability probability = RiskProbability.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(length = AnnotationConst.CODE_LENGTH, nullable = false)
    @EruptField(
            views = @View(title = "Impact", width = "100px"),
            edit = @Edit(title = "Impact", notNull = true, type = EditType.CHOICE, search = @Search,
                    choiceType = @ChoiceType(fetchHandler = RiskImpact.H.class))
    )
    private RiskImpact impact = RiskImpact.MODERATE;

    @Enumerated(EnumType.STRING)
    @Column(length = AnnotationConst.CODE_LENGTH, nullable = false)
    @EruptField(
            views = @View(title = "Status", width = "100px", sortable = true),
            edit = @Edit(title = "Status", notNull = true, type = EditType.CHOICE, search = @Search,
                    choiceType = @ChoiceType(fetchHandler = RiskStatus.H.class))
    )
    private RiskStatus status = RiskStatus.OPEN;

    @ManyToOne
    @EruptField(
            views = @View(title = "Owner", column = "name", width = "100px"),
            edit = @Edit(title = "Owner", type = EditType.REFERENCE_TABLE, search = @Search,
                    referenceTableType = @ReferenceTableType(label = "name"))
    )
    private EruptUser owner;

    @EruptField(
            views = @View(title = "Mitigation"),
            edit = @Edit(title = "Mitigation", type = EditType.TEXTAREA)
    )
    private String mitigation;

}
