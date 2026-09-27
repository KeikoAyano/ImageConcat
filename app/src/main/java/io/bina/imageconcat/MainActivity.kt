package io.bina.imageconcat

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var pick_image: Button;
    private lateinit var concat_image: Button;
    private lateinit var output_img: ImageView;
    private lateinit var concat_orientation: RadioGroup;

    private val imageUris = mutableListOf<Uri>();

    private lateinit var images_display: RecyclerView;
    private lateinit var imagesAdapter: io.bina.imageconcat.adapter.ImageAdapter;

    private lateinit var imagePicker: ActivityResultLauncher<PickVisualMediaRequest>;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // check OpenCv load
        /*
        if (!org.opencv.android.OpenCVLoader.initLocal()) {
            android.widget.Toast.makeText(
                this,
                R.string.OpenCVInitError,
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
        */

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        pick_image = findViewById<android.widget.Button>(R.id.pick_image);
        concat_image = findViewById<Button>(R.id.concat_image);
        output_img = findViewById<ImageView>(R.id.output_img);
        concat_orientation = findViewById<RadioGroup>(R.id.concat_orientation);
        // image picker
        imagePicker =  registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()) {
                uri: Uri? ->

                if (imageUris.size > resources.getInteger(R.integer.ITEMS_NUM_LIMIT)) {
                    android.widget.Toast.makeText(
                        this,
                        io.bina.imageconcat.R.string.MaxItemNumPrompt,
                        android.widget.Toast.LENGTH_SHORT
                    ).show();
                    return@registerForActivityResult;
                }

                if (uri != null && uri !in imageUris ) {
                    imageUris.add(uri);
                    imagesAdapter.notifyItemInserted(imageUris.lastIndex)
                }
                else if (uri != null) {
                    android.widget.Toast.makeText(
                        this,
                        R.string.image_duplicate,
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
                else {
                    android.widget.Toast.makeText(
                        this,
                        R.string.cancel_dialog,
                        android.widget.Toast.LENGTH_SHORT).show();
                }
        };

        imagesAdapter = io.bina.imageconcat.adapter.ImageAdapter(imageUris, this);

        images_display = findViewById<RecyclerView>(R.id.images_display);
        images_display.adapter = imagesAdapter;
        images_display.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        pick_image.setOnClickListener (
            io.bina.imageconcat.core.CreateImagePickListener(this, imagePicker)
        )

        concat_image.setOnClickListener (
            io.bina.imageconcat.core.ConcatImageListener(
                this, imageUris, concat_orientation
            )
        )

    }
}