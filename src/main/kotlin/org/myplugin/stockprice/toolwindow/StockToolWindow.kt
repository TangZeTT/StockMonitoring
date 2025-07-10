// src/main/kotlin/org/example/stockprice/StockToolWindow.kt
package org.myplugin.stockprice.toolwindow

import com.intellij.openapi.project.Project
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.UIUtil
import org.myplugin.stockprice.service.StockDataListener
import java.awt.BorderLayout
import java.awt.Component
import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.SwingUtilities
import javax.swing.table.DefaultTableCellRenderer

class StockToolWindow(project: Project) {

    private val tableModel = StockTableModel()
    private val table = JBTable(tableModel)
    private val mainPanel: JPanel

    init {
        // 1. 设置表格外观和行为
        table.setShowGrid(true) // 显示网格线
        table.setRowHeight(25)  // 增加行高

        // 2. (关键) 设置自定义的单元格渲染器，用于显示颜色和格式化
        table.columnModel.getColumn(1).cellRenderer = PriceCellRenderer()

        // 3. 将表格放入一个可滚动的面板中
        val scrollPane = JBScrollPane(table)

        // 4. 使用 BorderLayout 来布局主面板
        mainPanel = JPanel(BorderLayout())
        mainPanel.add(scrollPane, BorderLayout.CENTER)

        // 5. 订阅数据更新消息
        val connection = project.messageBus.connect()
        connection.subscribe(StockDataListener.TOPIC, object : StockDataListener {
            override fun onDataUpdated(prices: Map<String, Double>) {
                // 在UI线程更新数据模型
                SwingUtilities.invokeLater {
                    tableModel.updateData(prices)
                }
            }
        })
    }

    fun getContent(): JPanel {
        return mainPanel
    }

    /**
     * 自定义单元格渲染器，用于美化价格显示
     */
    private class PriceCellRenderer : DefaultTableCellRenderer() {
        override fun getTableCellRendererComponent(
            table: JTable?,
            value: Any?,
            isSelected: Boolean,
            hasFocus: Boolean,
            row: Int,
            column: Int
        ): Component {
            // 调用父类方法获取默认的组件样式
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column)

            if (table == null || value !is Double) {
                return this
            }

            val model = table.model as StockTableModel
            val stock = model.getStockAt(row)

            // 根据价格涨跌设置前景色
            foreground = when {
                stock.price > stock.previousPrice -> JBColor.GREEN.darker()
                stock.price < stock.previousPrice -> JBColor.RED
                else -> UIUtil.getTableForeground() // 默认颜色
            }

            // 格式化文本，保留两位小数
            text = String.format("%.2f", value)
            horizontalAlignment = RIGHT // 右对齐

            return this
        }
    }
}