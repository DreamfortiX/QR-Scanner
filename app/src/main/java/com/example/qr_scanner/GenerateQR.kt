package com.example.qr_scanner

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.qr_scanner.databinding.FragmentGenerateQRBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class GenerateQR : Fragment() {

    // Declare binding variable
    private var _binding: FragmentGenerateQRBinding? = null
    private lateinit var qrCodeGenerator: QRCodeGenerator
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using binding
        _binding = FragmentGenerateQRBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        qrCodeGenerator = QRCodeGenerator()

        // Hide all layouts initially
        setAllLayoutsVisibility(View.GONE)
        binding.back.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        // Retrieve the passed image ID from the arguments safely
        val clickedImageId = requireArguments().getInt("clicked_image_id")
        when (clickedImageId) {
            R.id.image_a -> {
                binding.layoutText.visibility = View.VISIBLE
                binding.header.text = "Text"
                binding.textToQrBtn.setOnClickListener {
                    val text = binding.textToQr.text.toString()
                    if (text.isNotEmpty()) {
                        val bitmap = qrCodeGenerator.generateQRCode(text)
                        if (bitmap != null) {
                            navigateToShowQRCode(bitmap)
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Enter text to generate QR Code",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            R.id.image_b -> {
                binding.layoutWebsite.visibility = View.VISIBLE
                binding.header.text = "Website"
                binding.websiteQrBtn.setOnClickListener(){
                    val websiteUrl = binding.websiteUrl.text.toString()
                    if (websiteUrl.isNotEmpty()) {
                        val bitmap = qrCodeGenerator.generateWebsiteQRCode(websiteUrl)
                        if (bitmap != null) {
                            navigateToShowQRCode(bitmap)
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Enter text to generate QR Code",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            }
            R.id.image_c -> {
                binding.layoutWifi.visibility = View.VISIBLE
                binding.header.text = "Wifi"
              binding.wifiQrBtn.setOnClickListener()
              {
                  val wifiUsername = binding.wifiUsername.text.toString()
                  val wifiPassword = binding.wifiPassword.text.toString()
                  if (wifiUsername.isNotEmpty() && wifiPassword.isNotEmpty())
                  {
                      val bitmap = qrCodeGenerator.generateQRForWiFi(wifiUsername,wifiPassword)
                      if (bitmap != null) {
                          navigateToShowQRCode(bitmap)
                      }
                  } else {
                      Toast.makeText(
                          requireContext(),
                          "Enter text to generate QR Code",
                          Toast.LENGTH_SHORT
                      ).show()
                  }
              }
            }
            R.id.image_d -> {
                binding.layoutEvent.visibility = View.VISIBLE
                binding.header.text = "Event"
                val eventName = binding.eventName.text.toString()
                val eventStDate = binding.eventDateTime.text.toString()
                val eventEndDate = binding.eventDateEnd.text.toString()
                val eventLocation = binding.eventLocation.text.toString()
                val eventDescription = binding.eventDescription.text.toString()
                binding.eventQrBtn.setOnClickListener()
                {

                    if (eventName.isNotEmpty() && eventStDate.isNotEmpty() && eventEndDate.isNotEmpty() && eventLocation.isNotEmpty() && eventDescription.isNotEmpty()) {
                        val bitmap = qrCodeGenerator.generateQRForEvent(eventName, eventStDate, eventEndDate, eventLocation, eventDescription)
                        if (bitmap != null) {
                            navigateToShowQRCode(bitmap)
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Enter text to generate QR Code",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }

            }
            R.id.image_e -> {
                binding.layoutContact.visibility = View.VISIBLE
                binding.header.text = "Contact"
                val contactFName = binding.contactFName.text.toString()
                val contactLName = binding.contactLName.text.toString()
                val contactCompany = binding.contactCompany.text.toString()
                val contactJob = binding.contactJob.text.toString()
                val contactPhone = binding.contactPhone.text.toString()
                val contactEmail = binding.contactEmail.text.toString()
                val contactWebsite = binding.contactWebsite.text.toString()
                val contactAddress = binding.contactAddress.text.toString()
                val contactCity = binding.contactCity.text.toString()
                val contactCountry = binding.contactCountry.text.toString()
                binding.contactQrBtn.setOnClickListener()
                {
                    if (contactFName.isNotEmpty() && contactLName.isNotEmpty() && contactPhone.isNotEmpty() && contactEmail.isNotEmpty()) {
                        val bitmap = qrCodeGenerator.generateQRForContact(
                            contactFName, contactLName, contactCompany, contactJob,
                            contactPhone, contactEmail, contactWebsite, contactAddress,
                            contactCity, contactCountry
                        )
                        if (bitmap != null) {
                            navigateToShowQRCode(bitmap)
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Enter text to generate QR Code",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }
            R.id.image_f -> {
                binding.layoutBusiness.visibility = View.VISIBLE
                binding.header.text = "Business"
                binding.businessQrBtn.setOnClickListener()
                {
                    val companyName = binding.businessName.text.toString()
                    val industryType = binding.businessCategory.text.toString()
                    val companyPhone = binding.businessPhone.text.toString()
                    val companyEmail = binding.businessEmail.text.toString()
                    val companyWebsite = binding.businessWebsite.text.toString()
                    val companyAddress = binding.businessAddress.text.toString()
                    val companyCity = binding.businessCity.text.toString()
                    val companyCountry = binding.businessCountry.text.toString()

                    if (companyName.isNotEmpty() && companyPhone.isNotEmpty() && companyEmail.isNotEmpty() && companyWebsite.isNotEmpty()) {
                        val bitmap = qrCodeGenerator.generateQRForCompany(
                            companyName, industryType, companyPhone, companyEmail,
                            companyWebsite, companyAddress, companyCity, companyCountry
                        )
                        if (bitmap != null) {
                            navigateToShowQRCode(bitmap)
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Enter text to generate QR Code",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            R.id.image_g -> {
                binding.layoutLocation.visibility = View.VISIBLE
                binding.header.text = "Location"
                binding.locationQrBtn.setOnClickListener()
                {
                    val location = binding.location.text.toString()
                    if (location.isNotEmpty())
                    {
                        val bitmap = qrCodeGenerator.generateQRLocation(location)
                        if (bitmap != null) {
                            navigateToShowQRCode(bitmap)
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Enter text to generate QR Code",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            R.id.image_h -> {
                binding.layoutWhatsapp.visibility = View.VISIBLE
                binding.header.text = "Whatsapp"
                binding.whatsappQrBtn.setOnClickListener {
                    val phoneNumber = binding.whatsappNumber.text.toString()

                    if (phoneNumber.isNotEmpty()) {
                        val bitmap = qrCodeGenerator.generateQRForWhatsApp(phoneNumber)
                        if (bitmap != null) {
                            navigateToShowQRCode(bitmap)
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Enter text to generate QR Code",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }
            R.id.image_i -> {
                binding.layoutEmail.visibility = View.VISIBLE
                binding.header.text = "Email"
                binding.emailQrBtn.setOnClickListener()
                {
                    val email = binding.email.text.toString()
                    if (email.isNotEmpty()){
                        val bitmap = qrCodeGenerator.generateQRForEmail(email)
                        if (bitmap != null) {
                            navigateToShowQRCode(bitmap)
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Enter text to generate QR Code",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            R.id.image_j -> {
                binding.layoutTwitter.visibility = View.VISIBLE
                binding.header.text = "Twitter"
                binding.twitterQrBtn.setOnClickListener {
                    val username = binding.twitterUsername.text.toString() // Assuming there's an EditText for the Twitter username

                    if (username.isNotEmpty()) {
                        val bitmap = qrCodeGenerator.generateQRForTwitter(username) // Generate the QR code for the entered username

                        if (bitmap != null) {
                            navigateToShowQRCode(bitmap)
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Enter text to generate QR Code",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }
            R.id.image_k -> {
                binding.layoutInstagram.visibility = View.VISIBLE
                binding.header.text = "Instagram"
                binding.instagramQrBtn.setOnClickListener {
                    val username = binding.instagramUsername.text.toString() // Assuming there's an EditText for the Instagram username

                    if (username.isNotEmpty()) {
                        val bitmap = qrCodeGenerator.generateQRForInstagram(username) // Generate the QR code for the entered username
                        if (bitmap != null) {
                            navigateToShowQRCode(bitmap)
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Enter text to generate QR Code",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }
            R.id.image_l -> {
                binding.layoutPhone.visibility = View.VISIBLE
                binding.header.text = "Phone"
                binding.phoneQrBtn.setOnClickListener {
                    val phoneNumber = binding.phoneNumber.text.toString() // Assuming there's an EditText for the mobile number

                    if (phoneNumber.isNotEmpty()) {
                        val bitmap = qrCodeGenerator.generateQRForMobileNumber(phoneNumber) // Generate the QR code for the entered phone number

                        if (bitmap != null) {
                            navigateToShowQRCode(bitmap)
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Enter text to generate QR Code",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }
        }
    }

    // Function to hide all layouts initially
    private fun setAllLayoutsVisibility(visibility: Int) {
        binding.layoutText.visibility = visibility
        binding.layoutWebsite.visibility = visibility
        binding.layoutWifi.visibility = visibility
        binding.layoutEvent.visibility = visibility
        binding.layoutContact.visibility = visibility
        binding.layoutBusiness.visibility = visibility
        binding.layoutLocation.visibility = visibility
        binding.layoutWhatsapp.visibility = visibility
        binding.layoutEmail.visibility = visibility
        binding.layoutTwitter.visibility = visibility
        binding.layoutInstagram.visibility = visibility
        binding.layoutPhone.visibility = visibility
    }

    private fun navigateToShowQRCode(bitmap: Bitmap) {
        val filePath = saveBitmapToFile(requireContext(), bitmap)
        if (filePath != null) {
            val bundle = Bundle().apply {
                putString("qr_image_path", filePath)
            }
            requireView().findNavController()
                .navigate(R.id.action_generateQR_to_show_QR2, bundle)
        } else {
            Toast.makeText(requireContext(), "Failed to save QR code", Toast.LENGTH_SHORT).show()
        }
    }


    private fun saveBitmapToFile(context: Context, bitmap: Bitmap): String? {
        val cacheDir = context.cacheDir
        val file = File(cacheDir, "qr_code.png")

        return try {
            val fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
            file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // Avoid memory leaks
    }
}
