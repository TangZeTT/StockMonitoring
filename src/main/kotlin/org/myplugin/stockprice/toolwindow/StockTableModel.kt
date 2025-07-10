// src/main/kotlin/org/example/stockprice/StockTableModel.kt
package org.myplugin.stockprice.toolwindow

import javax.swing.table.AbstractTableModel

// 用于存储股票详细信息的数据类
data class StockInfo(
    val symbol: String,
    var price: Double,
    var previousPrice: Double = price // 默认上一次的价格就是当前价
)

class StockTableModel : AbstractTableModel() {

    private val columnNames = arrayOf("Symbol", "Price")
    private var stocks = mutableListOf<StockInfo>()

    override fun getRowCount(): Int = stocks.size

    override fun getColumnCount(): Int = columnNames.size

    override fun getColumnName(column: Int): String = columnNames[column]

    override fun getValueAt(rowIndex: Int, columnIndex: Int): Any {
        val stock = stocks[rowIndex]
        return when (columnIndex) {
            0 -> stock.symbol
            1 -> stock.price
            else -> ""
        }
    }

    // 允许 JTable 识别列的数据类型，这对于排序和渲染很重要
    override fun getColumnClass(columnIndex: Int): Class<*> {
        return when (columnIndex) {
            0 -> String::class.java
            1 -> Double::class.java
            else -> Any::class.java
        }
    }

    // 供外部调用的方法，用于更新整个表格数据
    fun updateData(newPrices: Map<String, Double>) {
        // --- 新增的清理逻辑 ---
        // 1. 获取新数据中所有股票代码的集合
        val newSymbols = newPrices.keys

        // 2. 找出在当前表格中存在，但在新数据中已不存在的股票，并将它们移除
        //    这是修复问题的关键！
        stocks.removeAll { stockInfo -> stockInfo.symbol !in newSymbols }
        // ----------------------

        // --- 现有的更新和添加逻辑 (稍作优化) ---
        newPrices.forEach { (symbol, newPrice) ->
            val existingStock = stocks.find { it.symbol == symbol }
            if (existingStock != null) {
                // 更新现有股票的价格
                existingStock.previousPrice = existingStock.price
                existingStock.price = newPrice
            } else {
                // 添加新股票
                stocks.add(StockInfo(symbol, newPrice))
            }
        }

        // 通知 JTable 数据已发生根本性变化（增、删、改），需要完全重绘
        fireTableDataChanged()
    }

    // 辅助方法，用于给渲染器获取整行数据
    fun getStockAt(row: Int): StockInfo = stocks[row]
}