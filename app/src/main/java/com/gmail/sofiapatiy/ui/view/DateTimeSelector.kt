package com.gmail.sofiapatiy.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.adapters.ListenerUtil
import com.gmail.sofiapatiy.R
import com.gmail.sofiapatiy.databinding.ViewCustomDatetimeSelectorBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DateTimeSelector(context: Context, attrs: AttributeSet) :
    ConstraintLayout(context, attrs) {

    private val binding =
        ViewCustomDatetimeSelectorBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        initXmlAttributes(attrs)
    }

    private fun initXmlAttributes(attrs: AttributeSet) =
        context.theme.obtainStyledAttributes(attrs, R.styleable.DateTimeSelector, 0, 0).apply {
            try {
                getString(R.styleable.DateTimeSelector_dateTimeSelectorLabel)?.let {
                    binding.textDescription.text = it
                }
            } finally {
                recycle()
            }
        }

    fun setOnDateSelectListener(listener: View.OnClickListener) {
        val oldListener = ListenerUtil.trackListener(
            binding.datePicker,
            listener,
            R.id.datePickerListener
        )
        if (oldListener != null) {
            binding.datePicker.setOnClickListener(null)
        }
        binding.datePicker.setOnClickListener(listener)
    }

    fun setOnTimeSelectListener(listener: View.OnClickListener) {
        val oldListener = ListenerUtil.trackListener(
            binding.timePicker,
            listener,
            R.id.timePickerListener
        )
        if (oldListener != null) {
            binding.timePicker.setOnClickListener(null)
        }
        binding.timePicker.setOnClickListener(listener)
    }

    fun setDateButtonText(s: String) {
        binding.datePicker.text = s
    }

    fun setTimeButtonText(s: String) {
        binding.timePicker.text = s
    }
}
