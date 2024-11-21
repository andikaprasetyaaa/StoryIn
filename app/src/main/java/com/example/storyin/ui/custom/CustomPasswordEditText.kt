package com.example.storyin.ui.custom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.example.storyin.R

class CustomPasswordEditText : AppCompatEditText {
    private var isPasswordVisible = false

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        updatePasswordVisibility()

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty() && s.length < 8) {
                    error = context.getString(R.string.password_length_error)
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun updatePasswordVisibility() {
        inputType = if (isPasswordVisible) {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.ic_lock,
            0,
            if (isPasswordVisible) R.drawable.ic_hide_pass else R.drawable.ic_show_pass,
            0
        )

        setSelection(text?.length ?: 0)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val drawableRight = compoundDrawables[2]
        if (event.action == MotionEvent.ACTION_UP && drawableRight != null) {
            val touchable = event.x > (width - paddingRight - drawableRight.intrinsicWidth)
            if (touchable) {
                isPasswordVisible = !isPasswordVisible
                updatePasswordVisibility()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        hint = context.getString(R.string.password_hint)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }
}