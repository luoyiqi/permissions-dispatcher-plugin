package com.github.shiraji.permissionsdispatcherplugin.actions

import com.github.shiraji.permissionsdispatcherplugin.handlers.GeneratePMCodeHandler
import com.github.shiraji.permissionsdispatcherplugin.models.GeneratePMCodeModel
import com.github.shiraji.permissionsdispatcherplugin.views.GeneratePMCodeDialog
import com.intellij.codeInsight.CodeInsightActionHandler
import com.intellij.codeInsight.actions.CodeInsightAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.PsiJavaFile

class GeneratePMCodeAction : CodeInsightAction() {
    var model: GeneratePMCodeModel? = null
    override fun getHandler(): CodeInsightActionHandler {
        return GeneratePMCodeHandler(model!!)
    }

    override fun update(e: AnActionEvent?) {
        e ?: return
        super.update(e)

        val project = e.getData(CommonDataKeys.PROJECT)
        val file = e.getData(CommonDataKeys.PSI_FILE)
        if (project == null || file == null || file !is PsiJavaFile) {
            e.presentation.isEnabledAndVisible = false
            return
        }

        e.presentation.isEnabledAndVisible = GeneratePMCodeModel(project).isActivityOrFragment(file.classes[0])
    }

    override fun actionPerformed(e: AnActionEvent?) {
        val project = e?.getData(CommonDataKeys.PROJECT) ?: return
        val dialog = GeneratePMCodeDialog(project)
        if (dialog.showAndGet()) {
            model = GeneratePMCodeModel(project)

            model?.apply {
                permissions = dialog.selectedPermissions
                if (dialog.needsPermissionCheckBox.isSelected) needsPermissionMethodName = dialog.needsPermissionTextField.text
                if (dialog.onShowRationaleCheckBox.isSelected) onShowRationaleMethodName = dialog.onShowRationaleTextField.text
                if (dialog.onPermissionDeniedCheckBox.isSelected) onPermissionDeniedMethodName = dialog.onPermissionDeniedTextField.text
                if (dialog.onNeverAskAgainCheckBox.isSelected) onNeverAskAgainMethodName = dialog.onNeverAskAgainTextField.text
            }

            super.actionPerformed(e)
        }
    }
}