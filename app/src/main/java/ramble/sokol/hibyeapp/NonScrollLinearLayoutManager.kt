package ramble.sokol.hibyeapp

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager

class NonScrollLinearLayoutManager(context: Context) : LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false) {
    override fun canScrollVertically(): Boolean = false // Отключаем вертикальный скролл
}