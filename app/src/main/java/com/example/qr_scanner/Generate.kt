package com.example.qr_scanner

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.findNavController

class Generate : Fragment() {
    private lateinit var FraContainer:FrameLayout
    private val imageIds = arrayOf(
        R.id.image_a, R.id.image_b, R.id.image_c, R.id.image_d,
        R.id.image_e, R.id.image_f, R.id.image_g, R.id.image_h,
        R.id.image_i, R.id.image_j, R.id.image_k, R.id.image_l
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_generate, container, false)
        FraContainer = view.findViewById(R.id.fragment_container)
        // Set up click listeners for each image ID
        setUpImageClickListeners(view)

        // Set up the toolbar button click listener
        val button: ImageView = view.findViewById(R.id.toolbar)
        button.setOnClickListener {
            navigateToSecondFragment()
        }

        return view
    }

    private fun navigateToSecondFragment() {
        requireView().findNavController().navigate(R.id.action_nav_QR_to_setting2)
        if (FraContainer != null) {
            FraContainer.visibility = View.INVISIBLE
        }
    }

    private fun setUpImageClickListeners(view: View) {
        // Iterate over each image ID and set up a click listener
        for (imageId in imageIds) {
            val imageView: ImageView? = view.findViewById(imageId)
            imageView?.setOnClickListener {
                navigateToGenerateQR(imageId)
            } ?: run {
                // Log if the ImageView is not found
                Log.e("GenerateFragment", "ImageView with ID $imageId not found.")
            }
        }
    }

    private fun navigateToGenerateQR(id: Int) {
        val bundle = Bundle().apply {
            putInt("clicked_image_id", id)
        }

        // Ensure to use requireView() for navigation
        requireView().findNavController().navigate(R.id.action_nav_QR_to_generateQR, bundle)
        if (FraContainer != null) {
            FraContainer.visibility = View.INVISIBLE
        }
    }
}
