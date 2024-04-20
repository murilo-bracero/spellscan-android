package com.example.spellscanapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.spellscanapp.databinding.FragmentCardAnalysisBinding

class CardAnalysisFragment : Fragment() {

    private lateinit var binding: FragmentCardAnalysisBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCardAnalysisBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
}