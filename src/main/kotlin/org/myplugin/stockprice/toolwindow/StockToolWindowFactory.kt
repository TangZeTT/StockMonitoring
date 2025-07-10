// src/main/kotlin/stockPrice/StockToolWindowFactory.kt
package org.myplugin.stockprice.toolwindow

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

// src/main/kotlin/stockPrice/StockToolWindowFactory.kt
// ... (imports)
class StockToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        // 传入 project 对象
        val stockToolWindow = StockToolWindow(project)
        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(stockToolWindow.getContent(), "", false)
        toolWindow.contentManager.addContent(content)
    }
}
    