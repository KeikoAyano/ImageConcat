package io.bina.imageconcat.core

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.net.Uri
import android.widget.Toast
import androidx.core.graphics.createBitmap

enum class ConcatDirection {
    HORIZONTAL,
    VERTICAL
}

fun ConcatImages(
    context: Context,
    images: List<Uri>,
    direction: ConcatDirection): Bitmap? {
    if (images.isEmpty()) {
        return null;
    }

    val bitmaps = images.mapNotNull { uri ->
        context.contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream)
        }
    }

    if (bitmaps.isEmpty()) {
        return null
    }

    val outputWidth: Int
    val outputHeight: Int

    when (direction) {
        ConcatDirection.VERTICAL -> {
            outputWidth = bitmaps.maxOf { it.width }
            outputHeight = bitmaps.sumOf { it.height }
        }

        ConcatDirection.HORIZONTAL -> {
            outputWidth = bitmaps.sumOf { it.width }
            outputHeight = bitmaps.maxOf { it.height }
        }
    }

    val output = Bitmap.createBitmap(
        outputWidth,
        outputHeight,
        Bitmap.Config.ARGB_8888
    )

    val canvas = Canvas(output)
    var currentX = 0f
    var currentY = 0f

    bitmaps.forEach { bitmap ->
        canvas.drawBitmap(bitmap, currentX, currentY, null)

        when (direction) {
            ConcatDirection.VERTICAL ->
                currentY += bitmap.height

            ConcatDirection.HORIZONTAL ->
                currentX += bitmap.width
        }
    }

    return output
}

/*
fun ConcatImagesCV(
    context: Context,
    images: List<Uri>,
    direction: ConcatDirection): Bitmap? {
    // OpenCV image concat
    // now has bug

    if (images.size < 2) {
        Toast.makeText(
            context,
            "Please select at least 2 images",
            Toast.LENGTH_SHORT
        ).show();

        return null;
    }
    // read image
    try {
        var img_width = 0;
        var img_height = 0;

        val images: List<org.opencv.core.Mat> = images.mapNotNull { uri ->
            val img: org.opencv.core.Mat = org.opencv.core.Mat();
            val img_cvt: org.opencv.core.Mat = org.opencv.core.Mat();

            context.contentResolver.openInputStream(uri)?.use { stream ->
                val bitmapImage = BitmapFactory.decodeStream(stream);
                org.opencv.android.Utils.bitmapToMat(bitmapImage, img);
                img.convertTo(img_cvt, org.opencv.core.CvType.CV_8UC4);

                android.util.Log.d("IMG_SIZE", "${img_cvt.height()} ${img_cvt.cols()} ${img_cvt.channels()}")

                if (img_width == 0 && img_height == 0) {
                    img_width = img_cvt.width()
                    img_height = img_cvt.height()
                }
                else if (img_width != img_cvt.width() || img_height != img_cvt.height()) {
                    Toast.makeText(
                        context,
                        io.bina.imageconcat.R.string.img_same_shape,
                        Toast.LENGTH_SHORT
                    ).show();
                    return null;
                }
            }

            img_cvt
        }

        val out_img: org.opencv.core.Mat = org.opencv.core.Mat();
        android.util.Log.d("IMAGE_DIMENSION", "${out_img.type()}");

        when (direction) {
            ConcatDirection.VERTICAL -> {
                org.opencv.core.Core.vconcat(
                    images, out_img
                );
            }

            ConcatDirection.HORIZONTAL -> {
                org.opencv.core.Core.hconcat(
                    images, out_img
                )
            }
        }
        val out_bitmap: Bitmap = createBitmap(out_img.rows(), out_img.cols());

        org.opencv.android.Utils.matToBitmap(out_img, out_bitmap)

        return out_bitmap;
    }
    catch (e: org.opencv.core.CvException) {
        return null;
    }
}
*/