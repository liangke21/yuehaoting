package com.example.yuehaoting.main.ui.featured

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.yuehaoting.databinding.MainNavigationFeaturedBinding
import com.example.yuehaoting.searchFor.SearchActivity


class FeaturedFragment : Fragment() {

    private lateinit var dashboardViewModel: FeaturedViewModel
    private var _binding: MainNavigationFeaturedBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProvider(this).get(FeaturedViewModel::class.java)

        _binding = MainNavigationFeaturedBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        val intent = Intent(activity, SearchActivity::class.java)

        activity?.startActivity(intent)
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}