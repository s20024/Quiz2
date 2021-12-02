package ja.ac.it_college.std.s20024.quiz2

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import ja.ac.it_college.std.s20024.quiz2.classes.DatabaseDialog
import ja.ac.it_college.std.s20024.quiz2.classes.DatabaseHelper
import ja.ac.it_college.std.s20024.quiz2.classes.HelperDialog
import ja.ac.it_college.std.s20024.quiz2.classes.UpdateDialog
import ja.ac.it_college.std.s20024.quiz2.databinding.ActivityMainBinding
import ja.ac.it_college.std.s20024.quiz2.functions.getStringData
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var oldVersion: String
    private val helper = DatabaseHelper(this)
    private val tempurl = "https://script.google.com/macros/s/AKfycbznWpk2m8q6lbLWSS6qaz3uS6j3L4zPwv7CqDEiC433YOgAdaFekGJmjoAO60quMg6l/exec?f="
    private var doUpdateDbTf = false
    private var dataSize = 0

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        var fastMode = pref.getBoolean("fastMode", true)

        binding = ActivityMainBinding.inflate(layoutInflater)
        oldVersion = pref.getString("version", "0000").toString()
        dataSize = helper.getDataSize()

        setContentView(binding.root)

        binding.modeSwitch.isChecked = fastMode

        binding.modeSwitch.setOnClickListener {
            fastMode = binding.modeSwitch.isChecked
            pref.edit().putBoolean("fastMode", fastMode).apply()
        }

        binding.versionImage.setOnClickListener { getVersion() }

        doUpdateDbTf = pref.getBoolean("update", false)
        if (doUpdateDbTf) { binding.databaseImage.visibility = View.VISIBLE }
        else { binding.databaseImage.visibility = View.GONE }
        binding.databaseImage.setOnClickListener { getData() }

        binding.secondPicker.minValue = 3
        binding.secondPicker.maxValue = 60
        binding.secondPicker.value = 10

        binding.countPicker.minValue = 1
        binding.countPicker.maxValue = dataSize
        binding.countPicker.value = 10

        binding.nextButton.setOnClickListener {
            if (dataSize <= 1) {
                if (doUpdateDbTf) {printToast(getString(R.string.pleaseUpdateDatabase))}
                else { printToast(getString(R.string.pleaseVersionUp)) }
            }
            else {
                if (binding.falseSwitch.isChecked) {
                    val datalist = helper.pickUpDataFalse(binding.countPicker.value)
                    if (datalist.size == 0) {
                        printToast(getString(R.string.noQuizNow)) }
                    else {
                        val intent = Intent(this, ExamActivity::class.java)
                        intent.apply {
                            putExtra("dataset", datalist)
                            putExtra("secondSet", binding.secondPicker.value)
                            putExtra("fastModeSet", binding.modeSwitch.isChecked)
                        }
                        startActivity(intent)
                    }
                } else {
                    val datalist = helper.pickUpData(binding.countPicker.value)
                    val intent = Intent(this, ExamActivity::class.java)
                    intent.apply {
                        putExtra("dataset", datalist)
                        putExtra("secondSet", binding.secondPicker.value)
                        putExtra("fastModeSet", binding.modeSwitch.isChecked)
                    }
                    startActivity(intent)
                }
            }
        }

        binding.fastModeHelper.setOnClickListener{
            val helperDialog = HelperDialog(getString(R.string.fastModeHelper))
            helperDialog.show(supportFragmentManager, "fastModeHelper")
        }
        binding.falseModeHelper.setOnClickListener{
            val helperDialog = HelperDialog(getString(R.string.falseModeHelper))
            helperDialog.show(supportFragmentManager, "falseModeHelper")
        }
        binding.versionHelper.setOnClickListener {
            val helperDialog = HelperDialog(getString(R.string.versionUpHelper))
            helperDialog.show(supportFragmentManager, "versionHelper")
        }
        binding.databaseHelper.setOnClickListener {
            val helperDialog = HelperDialog(getString(R.string.databaseUpHelper))
            helperDialog.show(supportFragmentManager, "databaseHelper")
        }
    }

    @UiThread
    private fun getVersion() {
        lifecycleScope.launch {
            val updateDialog = UpdateDialog(getString(R.string.versionLoading))
            updateDialog.show(supportFragmentManager,  "version_update")
            val result = getStringData(tempurl + "version")
            getVersionPost(result)
            updateDialog.dismiss()
        }
    }

    @UiThread
    private fun getVersionPost(result: String) {
        val newVersion = JSONObject(result).getString("version")
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        if (newVersion != oldVersion) {
            oldVersion = newVersion
            doUpdateDbTf = true
            pref.edit().apply {
                putString("version", newVersion)
                putBoolean("update", doUpdateDbTf)
            }.apply()
            binding.databaseImage.visibility = View.VISIBLE
            val databaseDialog = DatabaseDialog(getString(R.string.whichUpdateDatabase), getString(R.string.yes), {getData()}, getString(R.string.no), {printToast(getString(R.string.pleaseUpdateDatabase))})
            databaseDialog.show(supportFragmentManager,  "database_update")
        } else {
            if (doUpdateDbTf) { printToast(getString(R.string.pleaseUpdateDatabase)) }
            else { printToast(getString(R.string.versionLatest)) }
        }
    }

    @UiThread
    private fun getData() {
        lifecycleScope.launch {
            val updateDialog = UpdateDialog(getString(R.string.databaseLoading))
            updateDialog.show(supportFragmentManager,  "version_update")
            val result = getStringData(tempurl + "data")
            getDataPost(result)
            updateDialog.dismiss()
            binding.countPicker.value = 10
        }
    }

    @UiThread
    private fun getDataPost(result: String) {
        val rootData = JSONArray(result)
        helper.onMyUpgrade(rootData)
        binding.databaseImage.visibility = View.GONE
        doUpdateDbTf = false
        dataSize = helper.getDataSize()
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        pref.edit().putBoolean("update", doUpdateDbTf).apply()
        binding.countPicker.maxValue = dataSize
    }

    private fun printToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return true
    }
}