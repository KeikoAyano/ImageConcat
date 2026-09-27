package io.bina.imageconcat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlin.collections.mutableListOf
import kotlin.io.path.Path
import kotlin.io.path.name

class ImageAdapter(private val images: List<android.net.Uri>, private val context: Context)
    : RecyclerView.Adapter<ImageViewHolder>(){
    private val ITEMS_NUM_LIMIT: Int = context.resources.getInteger(io.bina.imageconcat.R.integer.ITEMS_NUM_LIMIT);
    private val IMG_ID: Int = context.resources.getInteger(
        io.bina.imageconcat.R.integer.ITEMS_NUM_LIMIT
    );
    private var imagesList: MutableList<android.net.Uri> = mutableListOf<android.net.Uri>();


    override fun onCreateViewHolder(
        viewGroup: ViewGroup, viewType: Int
    ): ImageViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(io.bina.imageconcat.R.layout.image_item, viewGroup, false)

        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ImageViewHolder,
        idx: Int
    ) {
        val fileName = images.get(idx).toString();
        val path = Path(fileName);

        holder.imageLabel.setText(path.name);
        holder.imageLabel.setTag(
            io.bina.imageconcat.R.integer.IMG_ID,
            fileName);
    }

    override fun getItemCount(): Int {

        if (this.images.size > this.ITEMS_NUM_LIMIT) {

            return this.ITEMS_NUM_LIMIT;
        }
        else
        {
            return this.images.size;
        }
    }

}

class ImageViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val imageLabel: TextView

    init {
        // Define click listener for the ViewHolder's View
        imageLabel = view.findViewById(io.bina.imageconcat.R.id.image_item)
    }
}