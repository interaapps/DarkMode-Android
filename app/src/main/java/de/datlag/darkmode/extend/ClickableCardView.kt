package de.datlag.darkmode.extend

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import androidx.cardview.widget.CardView
import de.datlag.darkmode.R

open class ClickableCardView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0): CardView(context, attrs, defStyleAttr) {

    private var selectorStateList: ColorStateList? = null

    init {
        attrs?.let {  }
    }

    protected open fun getAttributes(attrs: AttributeSet?, defStyleAttr: Int) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ClickableCardView, defStyleAttr, 0)

        selectorStateList = typedArray.getColorStateList(R.styleable.ClickableCardView_cardBackgroundSelector)
        selectorStateList?.let { this.setCardBackgroundColor(it) }

        typedArray.recycle()
    }

}