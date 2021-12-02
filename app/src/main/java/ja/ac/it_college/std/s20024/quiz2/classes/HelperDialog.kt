package ja.ac.it_college.std.s20024.quiz2.classes

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class HelperDialog(
    private val message: String,
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        builder.apply {
            setMessage(message)
            setPositiveButton("ok") { _, _ -> }
        }
        return builder.create()
    }
}