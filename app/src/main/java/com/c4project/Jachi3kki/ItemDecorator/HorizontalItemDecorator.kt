import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class HorizontalItemDecorator(
    context: Context,
    resId: Int,
    val paddingTop: Int,
    val paddingBottom: Int
) : RecyclerView.ItemDecoration() {
    private var mDivider: Drawable? = null

    init {
        mDivider = ContextCompat.getDrawable(context, resId)
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val top = parent.paddingTop + paddingTop
        val bottom = parent.height - parent.paddingBottom - paddingBottom
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val left = child.right + params.rightMargin
            val right = left + (mDivider?.intrinsicHeight ?: 0)
            mDivider?.let {
                it.setBounds(left, top, right, bottom)
                it.draw(c)
            }
        }
    }
}