package com.arham.uhack.data

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import android.graphics.Canvas
import android.graphics.Matrix
import androidx.core.graphics.drawable.toBitmap
import com.arham.uhack.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QRCode {

    companion object {
        var qrCodeBitmap: Bitmap? = null
        private var result: BitMatrix? = null
        private var width: Int? = null
        suspend fun generateQRCode(context: Context, data: String, width: Int, height: Int) {
            this.width = width
            withContext(Dispatchers.IO) {
                result = try {
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
            }
        }

        fun updateColour(context: Context, foreground: Int, background: Int) {
            val w = result?.width
            val h = result?.height
            val pixels = IntArray(h?.let { w?.times(it) } ?: 0 )
            for (y in 0 until h!!) {
                val offset = y * w!!
                for (x in 0 until w) {
                    pixels[offset + x] = if (result?.get(x, y) == true) foreground else background
                }
            }

            qrCodeBitmap = w?.let { Bitmap.createBitmap(it, h, Bitmap.Config.ARGB_8888) }
            width?.let {
                if (w != null) {
                    qrCodeBitmap!!.setPixels(pixels, 0, it, 0, 0, w, h)
                }
            }

            val appIcon = AppCompatResources.getDrawable(context, R.drawable.uhack)?.toBitmap() // Replace with your app icon resource
            val resizedAppIcon = appIcon?.let {
                Bitmap.createScaledBitmap(it, 128, 128, false) // Resize to 128x128
            }
            resizedAppIcon?.let {
                val iconWidth = it.width
                val iconHeight = it.height
                val qrWidth = qrCodeBitmap!!.width
                val qrHeight = qrCodeBitmap!!.height

                val canvas = Canvas(qrCodeBitmap!!)
                val matrix = Matrix()
                matrix.postTranslate(
                    (qrWidth - iconWidth) / 2f,
                    (qrHeight - iconHeight) / 2f
                )
                canvas.drawBitmap(it, matrix, null)
            }
        }
    }
}