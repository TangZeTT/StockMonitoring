// src/main/kotlin/org/example/stockprice/StockDataService.kt
package org.myplugin.stockprice.service

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import org.myplugin.stockprice.settings.StockSettingsState
import kotlin.time.Duration.Companion.seconds

class StockDataService : Disposable {
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    // 使用 SharedFlow 来触发立即刷新
    private val refreshSignal = MutableSharedFlow<Unit>(replay = 1)

    init {
        serviceScope.launch {
            // 初始触发一次
            refreshSignal.emit(Unit)
            // 监听刷新信号
            refreshSignal.collectLatest {
                while (isActive) {
                    fetchStockPrices()
                    // 等待5秒或直到下一次刷新信号
                    withTimeoutOrNull(5.seconds) { delay(Long.MAX_VALUE) }
                }
            }
        }
    }

    private fun fetchStockPrices() {
        // 从配置服务中获取股票列表
        val symbolsToFetch = StockSettingsState.getInstance().stockSymbols
        if (symbolsToFetch.isEmpty()) {
            println("No stock symbols configured.")
            return
        }

        // 模拟为配置的股票获取价格
        val updatedPrices = symbolsToFetch.associateWith {
            // 模拟一个基础价格 + 随机波动
            (100 + it.hashCode() % 200) * (1 + (Math.random() - 0.5) * 0.1)
        }

        println("Fetched new prices for ${symbolsToFetch.joinToString()}: $updatedPrices")
        val publisher = ApplicationManager.getApplication().messageBus.syncPublisher(StockDataListener.TOPIC)
        publisher.onDataUpdated(updatedPrices)
    }

    /**
     * 当设置改变时，由 Configurable 调用此方法
     */
    fun notifySettingsChanged() {
        serviceScope.launch {
            // 发送一个信号来立即触发一次新的数据获取
            refreshSignal.emit(Unit)
        }
    }

    override fun dispose() {
        serviceScope.cancel()
    }

    companion object {
        fun getInstance(): StockDataService {
            return ApplicationManager.getApplication().getService(StockDataService::class.java)
        }
    }
}