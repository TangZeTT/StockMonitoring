// src/main/kotlin/org/example/stockprice/StockSettingsState.kt
package org.myplugin.stockprice.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

/**
 * 这个服务负责持久化存储插件的设置。
 * @State 注解定义了存储的位置和名称。
 */
@State(
    name = "org.example.stockprice.StockSettingsState",
    storages = [Storage("StockMonitorPluginSettings.xml")]
)
class StockSettingsState : PersistentStateComponent<StockSettingsState> {

    // 这里是实际要存储的数据。使用 Set 可以自动处理重复项。
    var stockSymbols: MutableSet<String> = mutableSetOf("AAPL", "GOOGL", "TSLA", "MSFT")

    override fun getState(): StockSettingsState {
        return this
    }

    override fun loadState(state: StockSettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }

    companion object {
        fun getInstance(): StockSettingsState {
            return ApplicationManager.getApplication().getService(StockSettingsState::class.java)
        }
    }
}