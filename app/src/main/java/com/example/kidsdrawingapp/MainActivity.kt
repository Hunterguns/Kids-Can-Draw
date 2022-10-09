package com.example.kidsdrawingapp

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get

class MainActivity : AppCompatActivity() {

    private var drawingView : DrawingView? = null
    private var ibBrush : ImageButton? = null
    private var mImageButtonCurrentPaint : ImageButton? = null
    private var ibGallery : ImageButton? = null

    val openGalleryLauncher : ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result->
            if(result.resultCode == RESULT_OK && result.data!=null){
                val imageBackground : ImageView = findViewById(R.id.backgroundImage)
                imageBackground.setImageURI(result.data?.data)
            }
        }

    val requestPermission : ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            permissions->
            permissions.entries.forEach{
                val permissionName = it.key
                val isGranted = it.value

                if(isGranted){
                    Toast.makeText(this, "Permission granted to read the storage files.", Toast.LENGTH_LONG).show()

                    val pickIntent : Intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    openGalleryLauncher.launch(pickIntent)
                }
                else{
                    if(permissionName == Manifest.permission.READ_EXTERNAL_STORAGE){
                        Toast.makeText(this, "Permission to read storage was denied without which the app cannot fetch image from the gallery.", Toast.LENGTH_LONG).show()

                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawingView = findViewById(R.id.drawingView)
        drawingView?.setSizeForBrush(20.toFloat())
        ibBrush = findViewById(R.id.ib_brush)
        ibGallery = findViewById(R.id.ib_gallery)

        val linearLayoutPaintcolors = findViewById<LinearLayout>(R.id.ll_paint_colors)
        mImageButtonCurrentPaint = linearLayoutPaintcolors[2] as ImageButton
        mImageButtonCurrentPaint!!.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.pallette_pressed)
        )

        ibBrush?.setOnClickListener{
            showBrushSizeChooserDialog()
        }

        ibGallery?.setOnClickListener{
            requestPermission()
        }

        val ibUndo : ImageButton = findViewById(R.id.ib_undo)
        ibUndo.setOnClickListener{
            drawingView?.onClickUndo()
        }

        val ibRedo : ImageButton = findViewById(R.id.ib_redo)
        ibRedo.setOnClickListener{
            drawingView?.onclickRedo()
        }

    }

    private fun requestPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            showRationaleDialog("Kids Drawing App", "Kids Drawing App needs to access your external storage because without the permission the app cannot fetch image from the gallery")
        }
        else{
            requestPermission.launch(arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            // TODO - Add permission to write to external storage
            ))
        }
    }

    fun showBrushSizeChooserDialog(){
        val brushDialog = Dialog(this)
        brushDialog.setContentView(R.layout.activity_dialog_brush)
        brushDialog.setTitle("Brush Size: ")
        val smallbtn :ImageButton = brushDialog.findViewById(R.id.ib_small_brush)
        val mediumbtn :ImageButton = brushDialog.findViewById(R.id.ib_medium_brush)
        val largebtn :ImageButton = brushDialog.findViewById(R.id.ib_large_brush)
        smallbtn.setOnClickListener{
            drawingView?.setSizeForBrush(10.toFloat())
            brushDialog.dismiss()
        }

        mediumbtn.setOnClickListener{
            drawingView?.setSizeForBrush(20.toFloat())
            brushDialog.dismiss()
        }

        largebtn.setOnClickListener{
            drawingView?.setSizeForBrush(30.toFloat())
            brushDialog.dismiss()
        }

        brushDialog.show()
    }

    fun paintClicked(view : View){
        if(mImageButtonCurrentPaint != view){
            val imageButton = view as ImageButton
            val colorTag = imageButton.tag.toString()
            drawingView?.setColor(colorTag)

            view.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pallette_pressed))
            mImageButtonCurrentPaint?.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pallette_normal))
            mImageButtonCurrentPaint = view

        }

    }

    fun showRationaleDialog(
        title: String,
        message: String
    ){
        val builder : AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("Cancel"){DialogInterface, _->
            DialogInterface.dismiss()
        }
        val AlertDialog : AlertDialog = builder.create()
        AlertDialog.show()
    }
}