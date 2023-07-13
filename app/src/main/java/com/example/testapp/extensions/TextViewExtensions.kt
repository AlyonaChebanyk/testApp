package com.example.testapp.extensions

import android.graphics.LinearGradient
import android.graphics.Shader
import android.widget.TextView

fun TextView.setGradient(colorStart: Int, colorEnd: Int){
    val paint = paint
    val width = paint.measureText(text.toString())

    val textShader = LinearGradient(
        0f,
        0f,
        width,
        textSize,
        intArrayOf(colorStart, colorEnd),
        null,
        Shader.TileMode.CLAMP
    )
    paint.shader = textShader
}