// src/main/kotlin/org/example/stockprice/StockSettingsConfigurable.kt
package org.myplugin.stockprice.settings

import com.intellij.openapi.options.Configurable
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBList
import org.myplugin.stockprice.service.StockDataService
import javax.swing.DefaultListModel
import javax.swing.JComponent

class StockSettingsConfigurable : Configurable {

    // 1. 数据模型保持不变
    private val listModel = DefaultListModel<String>()

    // 2. 使用 lazy 来初始化整个 UI 面板，彻底移除 nullable 的 mainPanel
    private val mainPanel: JComponent by lazy {
        val stockList = JBList(listModel)
        val decorator = ToolbarDecorator.createDecorator(stockList)
            .setAddAction { anActionButton ->
                // anActionButton.contextComponent 是一个更好的父组件选择
                val parent = anActionButton.contextComponent
                val inputDialog = com.intellij.openapi.ui.Messages.showInputDialog(
                    parent,
                    "Enter stock symbol:",
                    "Add Stock",
                    com.intellij.openapi.ui.Messages.getQuestionIcon()
                )
                if (!inputDialog.isNullOrBlank()) {
                    listModel.addElement(inputDialog.trim().uppercase())
                }
            }
            .setRemoveAction {
                stockList.selectedValuesList.forEach { listModel.removeElement(it) }
            }

        // ToolbarDecorator 会自己创建一个包含所有按钮和列表的 JPanel
        decorator.createPanel()
    }

    override fun getDisplayName(): String = "Stock Price Monitor"

    // 3. createComponent 现在变得极其简单
    override fun createComponent(): JComponent {
        return mainPanel
    }

    override fun isModified(): Boolean {
        val settings = StockSettingsState.getInstance()
        val uiSymbols = listModel.elements().toList().toSet()
        return uiSymbols != settings.stockSymbols
    }

    override fun apply() {
        val settings = StockSettingsState.getInstance()
        settings.stockSymbols = listModel.elements().toList().toMutableSet()
        StockDataService.getInstance().notifySettingsChanged()
    }

    override fun reset() {
        val settings = StockSettingsState.getInstance()
        listModel.clear()
        settings.stockSymbols.sorted().forEach { listModel.addElement(it) }
    }
}