package com.example.qr_scanner

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import android.content.res.Resources
import android.util.TypedValue
import android.widget.ImageView

class History : Fragment() {
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        // Bind the view elements using findViewById
        viewPager = view.findViewById(R.id.viewPager)
        tabLayout = view.findViewById(R.id.tabLayout)
        val button: ImageView = view.findViewById(R.id.setting)
        button.setOnClickListener() {
            // Navigate to the second fragment
            navigateToSecondFragment()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the ViewPager2 adapter
        val viewPagerAdapter = ViewPagerAdapter(requireActivity())
        viewPager.adapter = viewPagerAdapter

        // Define the titles for the tabs
        val tabTitles = arrayOf("Scan", "Create")

        // Attach the TabLayout with the ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        // Set background to the default selected tab and apply rounded corners
        // Make sure the first tab is selected by default with background and margins
        val firstTab = tabLayout.getTabAt(0)
        firstTab?.view?.setBackgroundResource(R.drawable.tab_background_selected)

        // Convert 3dp to pixels
        val marginInPx = dpToPx(3)

        // Change the background color and apply margins when the tab is selected/unselected
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                tab.view.setBackgroundResource(R.drawable.tab_background_selected)
                setTabMargins(tab.view, marginInPx)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                tab.view.setBackgroundColor(resources.getColor(android.R.color.transparent))
                setTabMargins(tab.view, marginInPx)
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                tab.view.setBackgroundResource(R.drawable.tab_background_selected)
                setTabMargins(tab.view, marginInPx)
            }
        })
    }

    // Function to convert dp to pixels
    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), Resources.getSystem().displayMetrics).toInt()
    }

    // Function to set margins
    private fun setTabMargins(tabView: View, marginInPx: Int) {
        val layoutParams = tabView.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.setMargins(marginInPx, marginInPx, marginInPx, marginInPx)
        tabView.layoutParams = layoutParams
    }
    private fun navigateToSecondFragment() {
        val secondFragment = Setting()
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, secondFragment)
        transaction.addToBackStack(null) // Optional: adds the transaction to the back stack
        transaction.commit()
    }
}
