package ja.ac.it_college.std.s20024.quiz2.classes

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class DatabaseDialog(
    private val message: String,
    private val okMessage: String,
    private val okSelected: () -> Unit,
    private val cancelMessage: String,
    private val cancelSelected: () -> Unit
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        builder.apply {
            setMessage(message)
            setPositiveButton(okMessage) { _, _ ->
                okSelected()
            }
            setNegativeButton(cancelMessage) { _, _ ->
                cancelSelected()
            }
        }
        return builder.create()
    }
}