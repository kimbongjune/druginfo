package com.nocdu.druginformation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nocdu.druginformation.databinding.FragmentAlarmBinding
import com.nocdu.druginformation.databinding.FragmentHomeBinding


class AlarmFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentAlarmBinding.inflate(inflater, container, false).root
    }
}