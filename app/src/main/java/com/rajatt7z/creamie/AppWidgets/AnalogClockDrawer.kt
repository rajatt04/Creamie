package com.rajatt7z.creamie.AppWidgets

import android.content.Context
import android.graphics.*
import java.util.Calendar

object AnalogClockDrawer {
    fun drawAnalogClock(context: Context, width: Int = 400, height: Int = 400): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = Math.min(centerX, centerY) * 0.90f
        
        // Background (White rounded square)
        // Follow Material Theme by using system colors if possible, but the image shows a white clock face.
        // Let's use a very light color or standard white.
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE 
            style = Paint.Style.FILL
        }
        val rectF = RectF(0f, 0f, width.toFloat(), height.toFloat())
        canvas.drawRoundRect(rectF, width * 0.25f, height * 0.25f, bgPaint)
        
        // Ticks
        val tickPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeCap = Paint.Cap.ROUND
        }
        
        for (i in 0 until 60) {
            val angle = Math.PI * i / 30.0 - Math.PI / 2.0
            val isHour = i % 5 == 0
            tickPaint.strokeWidth = if (isHour) radius * 0.035f else radius * 0.015f
            val tickLength = if (isHour) radius * 0.15f else radius * 0.08f
            val startRadius = radius * 0.95f
            
            val startX = centerX + Math.cos(angle) * startRadius
            val startY = centerY + Math.sin(angle) * startRadius
            val stopX = centerX + Math.cos(angle) * (startRadius - tickLength)
            val stopY = centerY + Math.sin(angle) * (startRadius - tickLength)
            
            // Skip drawing the hour ticks where the numbers 12, 3, 6, 9 are placed
            val skipTick = i == 0 || i == 15 || i == 30 || i == 45
            if (!skipTick) {
                // The image shows grey ticks for both, but maybe slightly darker for hours
                tickPaint.color = if (isHour) Color.DKGRAY else Color.parseColor("#BDBDBD") // LTGRAY
                canvas.drawLine(startX.toFloat(), startY.toFloat(), stopX.toFloat(), stopY.toFloat(), tickPaint)
            }
        }
        
        // Numbers: 12, 3, 6, 9
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#424242") // Dark Gray
            textSize = radius * 0.38f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        
        // Helper to draw centered text
        fun drawNumber(text: String, x: Float, y: Float) {
            val bounds = Rect()
            textPaint.getTextBounds(text, 0, text.length, bounds)
            canvas.drawText(text, x, y - bounds.exactCenterY(), textPaint)
        }
        
        val numberOffset = radius * 0.75f
        drawNumber("12", centerX, centerY - numberOffset)
        drawNumber("3", centerX + numberOffset, centerY)
        drawNumber("6", centerX, centerY + numberOffset)
        drawNumber("9", centerX - numberOffset, centerY)
        
        // Time
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)
        val millis = calendar.get(Calendar.MILLISECOND)
        
        // Hands
        val hourHandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#121212") // Almost black
            strokeWidth = radius * 0.08f
            strokeCap = Paint.Cap.ROUND
        }
        val minHandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#121212")
            strokeWidth = radius * 0.06f
            strokeCap = Paint.Cap.ROUND
        }
        val secHandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#FF9800") // Orange
            strokeWidth = radius * 0.02f
            strokeCap = Paint.Cap.ROUND
        }
        
        // Angles (smooth movement)
        val secAngle = Math.PI * (second + millis / 1000f) / 30.0 - Math.PI / 2.0
        val minAngle = Math.PI * (minute + second / 60f) / 30.0 - Math.PI / 2.0
        val hourAngle = Math.PI * (hour + minute / 60f) / 6.0 - Math.PI / 2.0
        
        // Draw Hour Hand
        val hourLength = radius * 0.45f
        canvas.drawLine(
            centerX, centerY,
            (centerX + Math.cos(hourAngle) * hourLength).toFloat(),
            (centerY + Math.sin(hourAngle) * hourLength).toFloat(),
            hourHandPaint
        )
        
        // Draw Minute Hand
        val minLength = radius * 0.7f
        canvas.drawLine(
            centerX, centerY,
            (centerX + Math.cos(minAngle) * minLength).toFloat(),
            (centerY + Math.sin(minAngle) * minLength).toFloat(),
            minHandPaint
        )
        
        // Draw Second Hand
        val secLength = radius * 0.85f
        val secTailLength = radius * 0.15f
        // Tail
        canvas.drawLine(
            centerX, centerY,
            (centerX - Math.cos(secAngle) * secTailLength).toFloat(),
            (centerY - Math.sin(secAngle) * secTailLength).toFloat(),
            secHandPaint
        )
        // Main hand
        canvas.drawLine(
            centerX, centerY,
            (centerX + Math.cos(secAngle) * secLength).toFloat(),
            (centerY + Math.sin(secAngle) * secLength).toFloat(),
            secHandPaint
        )
        
        // Center dots
        val centerDotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#FF9800")
            style = Paint.Style.FILL
        }
        canvas.drawCircle(centerX, centerY, radius * 0.05f, centerDotPaint)
        centerDotPaint.color = Color.WHITE
        canvas.drawCircle(centerX, centerY, radius * 0.02f, centerDotPaint)
        
        return bitmap
    }
}
