package com.example.qr_scanner

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import java.io.File

class Show_QR : Fragment() {
    private lateinit var Show_Qr: ImageView
    private lateinit var scannedText:TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_show__q_r, container, false)

        // Initialize the ImageView
        Show_Qr = view.findViewById(R.id.show_QR)
        scannedText = view.findViewById(R.id.scannedText)

        // Retrieve the QR image path from the arguments
        val qrImagePath = arguments?.getString("qr_image_path")

        if (!qrImagePath.isNullOrEmpty()) {
            // Create a File object with the path
            val qrImageFile = File(qrImagePath)

            if (qrImageFile.exists()) {
                scannedText.text = qrImagePath
                // Decode the image file into a Bitmap
                val bitmap = BitmapFactory.decodeFile(qrImageFile.absolutePath)

                if (bitmap != null) {
                    Show_Qr.setImageBitmap(bitmap)
                } else {
                    Toast.makeText(requireContext(), "Bitmap is null", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "File does not exist", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "QR Image Path is null or empty", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}
