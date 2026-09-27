package io.bina.imageconcat.core
import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.RadioGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import io.bina.imageconcat.MainActivity
import io.bina.imageconcat.R


class CreateImagePickListener(val context: android.content.Context,
                              val imagePicker: ActivityResultLauncher<PickVisualMediaRequest>
): View.OnClickListener {

    override fun onClick(v: View?) {
        /* Click the image picker
        * select image from Photo
         */

        this.imagePicker.launch(
            PickVisualMediaRequest(PickVisualMedia.ImageOnly)
        )
    }

}

class ConcatImageListener(
    val context: android.content.Context,
    val images: List<Uri>,
    val orientation: RadioGroup,
    ): View.OnClickListener {

    override fun onClick(v: View?) {
        /*
        Concat the image while clicking button
         */

        val direction = when (orientation.checkedRadioButtonId) {
            R.id.radio_vertical -> io.bina.imageconcat.core.ConcatDirection.VERTICAL
            R.id.radio_horizontal -> io.bina.imageconcat.core.ConcatDirection.HORIZONTAL
            else -> return ;
        }

        val result = io.bina.imageconcat.core.ConcatImages(context, images, direction);
        // val result = io.bina.imageconcat.core.ConcatImagesCV(context, images, direction);  // OpenCV code has some bugs

        if (result != null ) {
            // out_imgview.setImageBitmap(result);
            // save image
            val contentResolver = context.contentResolver;
            val img_name = "image${System.currentTimeMillis()}.png";
            val full_path = "${android.os.Environment.DIRECTORY_PICTURES}/ImageConcat/${img_name}"

            val values = android.content.ContentValues().apply {
                put(android.provider.MediaStore.Images.Media.DISPLAY_NAME, img_name);
                put(android.provider.MediaStore.Images.Media.MIME_TYPE, "image/png");

                if ( android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.Q ) {
                    put(android.provider.MediaStore.Images.Media.RELATIVE_PATH,
                        android.os.Environment.DIRECTORY_PICTURES + "/ImageConcat");
                    put( android.provider.MediaStore.Images.Media.IS_PENDING, 1);
                }
            }
            val uri = contentResolver.insert(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values
            );
            if (uri != null) {
                contentResolver.openOutputStream(uri)?.use { outputStream ->

                    result.compress(
                        android.graphics.Bitmap.CompressFormat.PNG,
                        95,
                        outputStream
                    )
                }

                // Make the image visible to other apps / Gallery
                if (android.os.Build.VERSION.SDK_INT >=
                    android.os.Build.VERSION_CODES.Q
                ) {
                    values.clear()

                    values.put(
                        android.provider.MediaStore.Images.Media.IS_PENDING,
                        0
                    )

                    contentResolver.update(
                        uri,
                        values,
                        null,
                        null
                    )
                }

                AlertDialog.Builder(context)
                    .setTitle(io.bina.imageconcat.R.string.info_dialog)
                    .setMessage("Save to ${full_path}")
                    .show();

            }
        }
        else {
            AlertDialog.Builder(context)
                .setTitle(io.bina.imageconcat.R.string.warn_dialog)
                .setMessage("Cannot concat Image")
                .show();
        }
    }
}