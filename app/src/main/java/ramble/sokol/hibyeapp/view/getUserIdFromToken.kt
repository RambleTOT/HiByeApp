package ramble.sokol.hibyeapp.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import ramble.sokol.hibyeapp.R

fun getUserIdFromToken(accessToken: String): Int? {
    return try {
        val jwt: DecodedJWT = JWT.decode(accessToken)
        jwt.getClaim("userId").asInt()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun getExpFromToken(accessToken: String): Long? {
    return try {
        val jwt: DecodedJWT = JWT.decode(accessToken)
        jwt.getClaim("exp").asLong()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

class CustomCheckBox @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var isChecked: Boolean = false
    private val checkboxContainer: LinearLayout
    private val checkboxText: TextView

    // Слушатель для обработки изменений состояния
    private var onCheckedChangeListener: ((Boolean) -> Unit)? = null

    init {
        // Загружаем макет
        LayoutInflater.from(context).inflate(R.layout.filter_check_box, this, true)
        checkboxContainer = findViewById(R.id.checkboxContainer)
        checkboxText = findViewById(R.id.checkbox_custom_text)

        // Обработка кликов
        checkboxContainer.setOnClickListener {
            toggle()
        }
    }

    // Переключаем состояние
    fun toggle() {
        isChecked = !isChecked
        updateUI()
        onCheckedChangeListener?.invoke(isChecked)
    }

    // Обновляем внешний вид
    private fun updateUI() {
        if (isChecked) {
            checkboxContainer.isSelected = true
            checkboxText.setTextColor(resources.getColor(R.color.main_color, null))
        } else {
            checkboxContainer.isSelected = false
            checkboxText.setTextColor(resources.getColor(R.color.text_hint_color, null))
        }
    }

    // Возвращаем состояние
    fun isChecked(): Boolean {
        return isChecked
    }

    // Устанавливаем состояние
    fun setChecked(checked: Boolean) {
        if (isChecked != checked) {
            isChecked = checked
            updateUI()
        }
    }

    // Устанавливаем слушатель изменений состояния
    fun setOnCheckedChangeListener(listener: (Boolean) -> Unit) {
        this.onCheckedChangeListener = listener
    }
}

class NonScrollLinearLayoutManager(context: Context) : LinearLayoutManager(context, VERTICAL, false) {
    override fun canScrollVertically(): Boolean = false // Отключаем вертикальный скролл
}