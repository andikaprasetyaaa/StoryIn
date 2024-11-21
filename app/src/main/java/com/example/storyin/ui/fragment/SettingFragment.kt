package com.example.storyin.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.storyin.R

class SettingFragment : Fragment(R.layout.fragment_setting) {

    private lateinit var tvLocalizeSettings: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvLocalizeSettings = view.findViewById(R.id.tv_localize_settings)

        tvLocalizeSettings.setOnClickListener {
            val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
            startActivity(intent)
        }
    }
}