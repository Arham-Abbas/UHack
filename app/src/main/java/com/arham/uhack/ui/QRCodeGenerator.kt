package com.arham.uhack.ui

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix

fun generateQRCode(data: String, width: Int, height: Int): Bitmap {
    val result: BitMatrix = try {
        MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, width, height, null)} catch (e: IllegalArgumentException) {
        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    }
    val w = result.width
    val h = result.height
    val pixels = IntArray(w * h)
    for (y in 0 until h) {
        val offset = y * w
        for (x in 0 until w) {
            pixels[offset + x] = if (result[x, y]) -0x1000000 else -0x1
        }
    }
    val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    bitmap.setPixels(pixels, 0, width, 0, 0, w, h)
    return bitmap
}