package com.devapps.storyapp.customview

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.devapps.storyapp.R

class PasswordEditText : AppCompatEditText {

    private lateinit var lockImage: Drawable
    private var isPassValid: Boolean = false

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
        lockImage = ContextCompat.getDrawable(context, R.drawable.ic_lock) as Drawable
        onShowVisibilityIcon(lockImage)

        addTextChangedListener(onTextChanged = { _: CharSequence?, _: Int, _: Int, _: Int ->
            validatePassword()
        })
    }

    private fun validatePassword() {
        val pass = text?.trim()
        when {
            pass.isNullOrEmpty() -> {
                isPassValid = false
                error = resources.getString(R.string.input_pass)
            }

            pass.length < 8 -> {
                isPassValid = false
                error = resources.getString(R.string.pass_length)
            }

            else -> {
                isPassValid = true
                error = null
            }
        }
    }

    private fun onShowVisibilityIcon(icon: Drawable) {
        setButtonDrawables(startOfTheText = icon)
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }
}
