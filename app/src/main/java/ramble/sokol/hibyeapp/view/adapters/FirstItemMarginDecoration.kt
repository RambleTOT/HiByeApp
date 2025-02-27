package ramble.sokol.hibyeapp.view.adapters

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class FirstItemMarginDecoration(private val marginStart: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        // Проверяем, является ли элемент первым
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.left = marginStart // Устанавливаем отступ для первого элемента
        }
    }
}