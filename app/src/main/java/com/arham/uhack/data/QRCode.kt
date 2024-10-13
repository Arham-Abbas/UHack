package com.arham.uhack.data

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import com.arham.uhack.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QRCode {

    companion object {
        var qrCodeBitmap: Bitmap? = null

        suspend fun generateQRCode(context: Context, data: String, width: Int, height: Int) {
            withContext(Dispatchers.IO) {
                val result: BitMatrix = try {
                    MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, width, height, null)
                } catch (e: IllegalArgumentException) {
                    // Handle the error, e.g., log it or show a message
                    Toast.makeText(
                        context,
                        context.getString(R.string.error_qr),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@withContext // Return early from the function
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
                qrCodeBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
                qrCodeBitmap!!.setPixels(pixels, 0, width, 0, 0, w, h)
            }
        }
    }
}