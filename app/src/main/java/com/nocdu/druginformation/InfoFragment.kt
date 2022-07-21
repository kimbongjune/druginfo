package com.nocdu.druginformation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nocdu.druginformation.databinding.FragmentInfoBinding
import com.nocdu.druginformation.databinding.FragmentSearchBinding


class InfoFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentInfoBinding.inflate(inflater, container, false).root
    }
}