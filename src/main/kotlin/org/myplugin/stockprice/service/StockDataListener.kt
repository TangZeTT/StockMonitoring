package org.myplugin.stockprice.service

import com.intellij.util.messages.Topic
import java.util.*

interface StockDataListener : EventListener {
    companion object {
        // 创建一个唯一的 Topic，用于发布和订阅消息
        val TOPIC = Topic.create("Stock Data Updates", StockDataListener::class.java)
    }

    // 当数据更新时调用的方法
    fun onDataUpdated(prices: Map<String, Double>)
}