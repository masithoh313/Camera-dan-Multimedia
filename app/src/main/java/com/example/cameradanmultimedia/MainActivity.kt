package com.example.cameradanmultimedia

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.PopupMenu
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnLongClickListener {
            val popupMenu = PopupMenu(this, it)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_camera -> {
                        var i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(i, 123)
                        true
                    }
                    R.id.menu_gallery -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                                requestPermissions(permissions, PERMISSION_CODE)
                            } else {
                                pickImageFromGalery()
                            }
                        }
                        true
                    }
                    else -> false
                }
            }
            popupMenu.inflate(R.menu.menu)
            try {
                val fieldMPopupMenu = PopupMenu::class.java.getDeclaredField("mPopup")
                fieldMPopupMenu.isAccessible = true
                val mPopupMenu = fieldMPopupMenu.get(popupMenu)
                mPopupMenu.javaClass
                    .getDeclaredMethod("set Force show Icon", Boolean::class.java)
                    .invoke(mPopupMenu, true)
            } catch (e: Exception) {
                Log.e("Main", "error showing menu icons.", e)
            } finally {
                popupMenu.show()
            }
            true
        }
    }
    private fun pickImageFromGalery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }
    companion object {
        private val IMAGE_PICK_CODE = 1000
        private val PERMISSION_CODE = 1001
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGalery()
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            imageView.setImageURI (data?.data)
        }
    }
}


