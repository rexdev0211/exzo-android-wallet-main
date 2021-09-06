package io.exzocoin.chartview

import android.graphics.Canvas

interface ChartDraw {
    var isVisible: Boolean
    fun draw(canvas: Canvas)
}
