package es.ucm.fdi.workitout.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.text.format.DateFormat
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

//Se carga la imagen en el ImageView
fun ImageView.loadResource(resource: String?, diskCacheStrategy: DiskCacheStrategy = DiskCacheStrategy.ALL) {
    Glide.with(context).load(resource).diskCacheStrategy(diskCacheStrategy).into(this)
}

//Se obtiene el fragment actual
val FragmentManager.currentFragment: Fragment?
    get() = primaryNavigationFragment?.childFragmentManager?.fragments?.first()

//Se obtiene el navController para realizar la navegación
fun FragmentManager.getNavController(container: Int): NavController? {
    val navHostFragment = this.findFragmentById(container) as NavHostFragment?
    return navHostFragment?.navController
}

fun ImageView.getByteArray(quality: Int): ByteArray {
    val bitmap = (this.drawable as BitmapDrawable).bitmap
    val baos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos)
    return baos.toByteArray()
}

fun FirebaseStorage.getImageRef(collection: String, name: String): StorageReference {
    val dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
    val fileName = name.filter { !it.isWhitespace() } + dateTime

    return getReference("images/$collection/$fileName")
}

//Comprueba que se cumpla la condición de error y de ser así muestra el error en el TextInputLayout
fun TextInputLayout.tilError(conditionError: Boolean, resError: Int): Boolean {
    return if (conditionError) {
        this.error = context.getString(resError)
        true
    } else {
        this.error = ""
        false
    }
}

fun TextView.tvError(conditionError: Boolean): Boolean {
    return if (conditionError) {
        this.visibility = View.VISIBLE
        true
    } else {
        this.visibility = View.GONE
        false
    }
}

fun Context.createAlertDialog(title: Any? = null, message: Int? = null, icon: Int? = null,
                              ok: Pair<Int,()->Unit>, cancel: Pair<Int,()->Unit>? = null,
                              neutral: Pair<Int, ()->Unit>? = null): MaterialAlertDialogBuilder {
    val builder = MaterialAlertDialogBuilder(this)
        .setPositiveButton(getString(ok.first)) { _,_ -> ok.second() }

    title?.let {
        if (it is Int) builder.setTitle(getString(it))
        if (it is String) builder.setTitle(it)
    }
    message?.let { builder.setMessage(getString(it)) }
    icon?.let { builder.setIcon(it) }
    neutral?.let { builder.setNeutralButton(getString(it.first)) { _, _ -> it.second() } }
    cancel?.let { builder.setNegativeButton(getString(it.first)) { _, _ -> it.second() } }

    return builder
}

fun FragmentActivity.createEditTextTimePicker(et: EditText, til: TextInputLayout, time: String,
                                              titleRes: Int, setDateTimePicked: (LocalDateTime) -> Unit) {
    et.setText(time)
    //Desactiva el error al hacer algún cambio
    et.doAfterTextChanged { til.error = "" }
    //Si hay botón de eliminar elimina el texto de hora al clickarse
    if (til.isEndIconVisible) til.setEndIconOnClickListener { et.setText("") }
    //Muestra el selector de hora
    til.setStartIconOnClickListener {
        val dateTime =
            if (et.text.isEmpty()) LocalDateTime.now()
            else timeStringToDateTime(et.string)
        val timeFormat =
            if (DateFormat.is24HourFormat(this)) TimeFormat.CLOCK_24H
            else TimeFormat.CLOCK_12H
        val timePicker = MaterialTimePicker.Builder()
            .setTitleText(titleRes)
            .setTimeFormat(timeFormat)
            .setHour(dateTime.hour)
            .setMinute(dateTime.minute)
            .build()

        //Guarda la hora seleccionada
        timePicker.addOnPositiveButtonClickListener {
            val dateTimePicked = LocalDateTime.now().withTime(timePicker.hour, timePicker.minute, 0)
            setDateTimePicked(dateTimePicked)

            et.setText(dateTimePicked.timeString())
        }

        timePicker.show(supportFragmentManager, null)
    }
}

val EditText.string: String
    get() = text.toString()

fun <E> List<E>.toArrayList(): ArrayList<E> = this.toCollection(ArrayList())

fun <T> QuerySnapshot.toObjectsArrayList(java: Class<T>) = toObjects(java).toArrayList()

//Se devuelve el color del atributo
@ColorInt
fun Context.getColorFromAttr(@AttrRes attrColor: Int): Int {
    val typedArray = theme.obtainStyledAttributes(intArrayOf(attrColor))
    val textColor = typedArray.getColor(0, 0)
    typedArray.recycle()
    return textColor
}

//Se establece el atributo de color de fondo
fun Activity.setBackgroundDefault(idRes: Int) {
    val root = findViewById<FragmentContainerView>(idRes)
    root.setBackgroundColor(getColorFromAttr(android.R.attr.colorBackground))
    window.navigationBarColor = getColorFromAttr(android.R.attr.colorBackground)
}

//Collector para SharedFlow
inline fun <T> Flow<T>.collectFlow(owner: LifecycleOwner, crossinline onCollect: suspend (T) -> Unit) =
    owner.lifecycleScope.launch { owner.repeatOnLifecycle(Lifecycle.State.STARTED) { collect { onCollect(it) } } }

//Collector para StateFlow
inline fun <T> Flow<T>.collectLatestFlow(owner: LifecycleOwner, crossinline onCollect: suspend (T) -> Unit) =
    owner.lifecycleScope.launch { owner.repeatOnLifecycle(Lifecycle.State.STARTED) { collectLatest { onCollect(it) } } }