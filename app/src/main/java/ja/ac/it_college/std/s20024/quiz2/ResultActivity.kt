package ja.ac.it_college.std.s20024.quiz2

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import ja.ac.it_college.std.s20024.quiz2.classes.DataListAdapter
import ja.ac.it_college.std.s20024.quiz2.classes.QuizData
import ja.ac.it_college.std.s20024.quiz2.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val datalist = intent.extras?.get("dataSet") as ArrayList<QuizData>
        val time = intent.getLongExtra("timeSet", 0L)

        val minute = time / 1000L / 60L
        val second = time / 1000L % 60L

        binding.timeView.text = getString(R.string.timeFormat).format(minute, second)

        var score = 0
        for (i in datalist) { if (i.getCorrect()) { ++score } }

        binding.scoreView.text = getString(R.string.scoreFormat).format(score, datalist.size)

        binding.backMain.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
        val layout = LinearLayoutManager(this)
        val listAdapter = DataListAdapter(datalist)
        val decorator = DividerItemDecoration(this, layout.orientation)
        binding.recyclerView.apply {
            layoutManager = layout
            adapter = listAdapter
            addItemDecoration(decorator)
        }

        listAdapter.setOnItemClickListener { position ->
            reloadData(datalist[position])
        }

        reloadData(datalist[0])
    }

    private fun reloadData(data: QuizData) {
        val choices = data.getShuffledList()
        binding.resultIdView.text = data.getId()
        binding.resultQuestionView.text = data.getQuestion()
        binding.resultChoice0.text = choices[0]
        binding.resultChoice1.text = choices[1]
        binding.resultChoice2.text = choices[2]
        binding.resultChoice3.text = choices[3]

        for (i in (0 until 6)) { toWhite(i) }
        for (i in data.getUserAnswersList()) { toGray(i) }
        for (i in data.getAnswersList()) { toGreen(i) }

        when(choices.size) {
            6 -> {
                binding.resultChoice4.visibility = View.VISIBLE
                binding.resultChoice5.visibility = View.VISIBLE
                binding.resultChoice4.text = choices[4]
                binding.resultChoice5.text = choices[5]
            }
            5 -> {
                binding.resultChoice4.visibility = View.VISIBLE
                binding.resultChoice4.text = choices[4]
                binding.resultChoice5.visibility = View.GONE
            }
            else -> {
                binding.resultChoice4.visibility = View.GONE
                binding.resultChoice5.visibility = View.GONE
            }
        }
    }
    private fun toWhite(n: Int) {
        when(n) {
            0 -> binding.resultChoice0.setBackgroundColor(Color.argb(0,0,0,0))
            1 -> binding.resultChoice1.setBackgroundColor(Color.argb(0,0,0, 0))
            2 -> binding.resultChoice2.setBackgroundColor(Color.argb(0,0,0, 0))
            3 -> binding.resultChoice3.setBackgroundColor(Color.argb(0,0,0, 0))
            4 -> binding.resultChoice4.setBackgroundColor(Color.argb(0,0,0, 0))
            5 -> binding.resultChoice5.setBackgroundColor(Color.argb(0,0,0, 0))
        }
    }
    private fun toGray(n: Int) {
        when(n) {
            0 -> binding.resultChoice0.setBackgroundColor(Color.rgb(200,200,200))
            1 -> binding.resultChoice1.setBackgroundColor(Color.rgb(200,200,200))
            2 -> binding.resultChoice2.setBackgroundColor(Color.rgb(200,200,200))
            3 -> binding.resultChoice3.setBackgroundColor(Color.rgb(200,200,200))
            4 -> binding.resultChoice4.setBackgroundColor(Color.rgb(200,200,200))
            5 -> binding.resultChoice5.setBackgroundColor(Color.rgb(200,200,200))
        }
    }
    private fun toGreen(n: Int) {
        when(n) {
            0 -> binding.resultChoice0.setBackgroundColor(Color.rgb(155, 222, 86))
            1 -> binding.resultChoice1.setBackgroundColor(Color.rgb(155, 222, 86))
            2 -> binding.resultChoice2.setBackgroundColor(Color.rgb(155, 222, 86))
            3 -> binding.resultChoice3.setBackgroundColor(Color.rgb(155, 222, 86))
            4 -> binding.resultChoice4.setBackgroundColor(Color.rgb(155, 222, 86))
            5 -> binding.resultChoice5.setBackgroundColor(Color.rgb(155, 222, 86))
        }
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        return true
    }
}