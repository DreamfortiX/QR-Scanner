package com.example.qr_scanner

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

class Result : Fragment() {
    private lateinit var resultTextView: TextView
    private lateinit var copyImageView: ImageButton
    private lateinit var share:ImageButton

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_result, container, false)
        resultTextView = view.findViewById(R.id.scannedTextView)
        copyImageView = view.findViewById(R.id.copy)
        share = view.findViewById(R.id.share)// Ensure you have an ImageView with this ID

        // Get the data from arguments
        val scannedData = arguments?.getString("scannedData")
        resultTextView.text = scannedData ?: "No data"

        // Set up the click listener for the copy image
        copyImageView.setOnClickListener {
            selectText()
            copyTextToClipboard(resultTextView.text.toString())
            highlightText()
        }
        share.setOnClickListener(){
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, scannedData)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

        return view
    }

    private fun copyTextToClipboard(text: String) {
        val clipboard: ClipboardManager =
            requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Scanned Text", text)
        clipboard.setPrimaryClip(clip)
    }

    private fun highlightText() {
        resultTextView.setTextColor(Color.BLUE)
    }
    private fun selectText() {
        // Create a SpannableString to highlight the text
        val spannable = SpannableString(resultTextView.text)
        spannable.setSpan(
            BackgroundColorSpan(Color.LTGRAY), // Highlight color
            0,
            spannable.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        resultTextView.text = spannable // Set the spannable text
    }
}
