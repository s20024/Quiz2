package ja.ac.it_college.std.s20024.quiz2

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.core.os.postDelayed
import ja.ac.it_college.std.s20024.quiz2.classes.DatabaseHelper
import ja.ac.it_college.std.s20024.quiz2.classes.QuizData
import ja.ac.it_college.std.s20024.quiz2.databinding.ActivityExamBinding

class ExamActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExamBinding
    private lateinit var datalist: ArrayList<QuizData>
    private var fastMode: Boolean = true
    private var i = 0
    private lateinit var timer : MyCountUpTimer
    private lateinit var downTimer : MyCountDownTimer
    private var userAnswersList = mutableListOf<Int>()
    private var answers = 0

    // 全てラジオボタンでするってなったのでここ書きました。　汚くてごめんなさい。w
    private var button0 = false
    private var button1 = false
    private var button2 = false
    private var button3 = false
    private var button4 = false
    private var button5 = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExamBinding.inflate(layoutInflater)
        datalist = intent.extras?.get("dataset") as ArrayList<QuizData>
        val second = intent.getIntExtra("secondSet", 10)
        timer = MyCountUpTimer(datalist.size * (second + 5).toLong() * 1000L, 100)
        downTimer = MyCountDownTimer(second.toLong() * 1000L, 100)
        fastMode = intent.getBooleanExtra("fastModeSet", true)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            timer.cancel()
            downTimer.cancel()
            finish()
        }
        if (fastMode) { binding.decideButton.text = getString(R.string.next) }

        binding.radioButton0.setOnClickListener{ onclick(0) }
        binding.radioButton1.setOnClickListener{ onclick(1) }
        binding.radioButton2.setOnClickListener{ onclick(2) }
        binding.radioButton3.setOnClickListener{ onclick(3) }
        binding.radioButton4.setOnClickListener{ onclick(4) }
        binding.radioButton5.setOnClickListener{ onclick(5) }
    }

    private fun onclick(n: Int) {
        when (n) {
            0 -> { button0 = !button0; binding.radioButton0.isChecked = button0 }
            1 -> { button1 = !button1; binding.radioButton1.isChecked = button1 }
            2 -> { button2 = !button2; binding.radioButton2.isChecked = button2 }
            3 -> { button3 = !button3; binding.radioButton3.isChecked = button3 }
            4 -> { button4 = !button4; binding.radioButton4.isChecked = button4 }
            5 -> { button5 = !button5; binding.radioButton5.isChecked = button5 }
        }

        if (n in userAnswersList) {userAnswersList.remove(n)}
        else {userAnswersList.add(n)}
        if (fastMode) {if (userAnswersList.size == answers) {printResult()}}
    }

    override fun onResume() {
        super.onResume()

        timer.start()
        viewQuestion()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        userAnswersList = mutableListOf()
        timer.cancel()
        downTimer.cancel()
        finish()
        return true
    }

    inner class MyCountUpTimer(millisInPast: Long, countUpInterval: Long):
        CountDownTimer(millisInPast, countUpInterval) {
        private val fastTime = millisInPast
        private var countTime = 0L
        override fun onTick(millisUnitFinished: Long) {
            val minute = (fastTime - millisUnitFinished) / 1000L / 60L
            val second = (fastTime - millisUnitFinished) / 1000L % 60L
            countTime = fastTime - millisUnitFinished
            binding.timerText.text = getString(R.string.timeFormat).format(minute, second)
        }
        override fun onFinish() {}
        fun getTime(): Long { return countTime }
    }

    inner class MyCountDownTimer(private val millisInFuture: Long, countDownInterval: Long):
        CountDownTimer(millisInFuture, countDownInterval) {
        override fun onTick(millisUitFinished: Long) {
            binding.timerProgress.progress = ((millisUitFinished.toDouble() / millisInFuture.toDouble()) * 100).toInt()
        }
        override fun onFinish() {
            binding.timerProgress.progress = 0
            printResult()
        }
    }

    private fun viewQuestion() {
        downTimer.cancel()
        downTimer.start()

        val data = datalist[i]

        userAnswersList = mutableListOf()
        answers = data.getAnswers()

        for (j in 0 until 6) {toWhite(j)}

        val id = data.getId()
        val question = data.getQuestion()
        val choices = data.getShuffledList()

        button0 = false
        button1 = false
        button2 = false
        button3 = false
        button4 = false
        button5 = false

        binding.radioButton0.isChecked = false
        binding.radioButton1.isChecked = false
        binding.radioButton2.isChecked = false
        binding.radioButton3.isChecked = false
        binding.radioButton4.isChecked = false
        binding.radioButton5.isChecked = false

        binding.radioButton0.isClickable = true
        binding.radioButton1.isClickable = true
        binding.radioButton2.isClickable = true
        binding.radioButton3.isClickable = true
        binding.radioButton4.isClickable = true
        binding.radioButton5.isClickable = true

        binding.decideButton.isClickable = true

        binding.questionNumView.text = getString(R.string.questionFormat).format(i + 1)
        binding.questionView.text = question
        binding.idView.text = id

        binding.radioButton0.text = choices[0]
        binding.radioButton1.text = choices[1]
        binding.radioButton2.text = choices[2]
        binding.radioButton3.text = choices[3]

        when (choices.size) {
            4 -> {
                binding.radioButton4.visibility = View.GONE
                binding.radioButton5.visibility = View.GONE
            }
            6 -> {
                binding.radioButton4.visibility = View.VISIBLE
                binding.radioButton5.visibility = View.VISIBLE
                binding.radioButton4.text = choices[4]
                binding.radioButton5.text = choices[5]
            }
            5 -> {
                binding.radioButton4.visibility = View.VISIBLE
                binding.radioButton4.text = choices[4]
                binding.radioButton5.visibility = View.GONE
            }
        }

        binding.decideButton.setOnClickListener {
            if (fastMode) { printResult() }
            else {
                if (userAnswersList.size == answers) { printResult() }
                else{ Toast.makeText(this, getString(R.string.pleaseCountAnswer) + answers.toString() , Toast.LENGTH_SHORT).show() }
            }
        }
    }

    private fun printResult() {
        downTimer.cancel()

        binding.radioButton0.isClickable = false
        binding.radioButton1.isClickable = false
        binding.radioButton2.isClickable = false
        binding.radioButton3.isClickable = false
        binding.radioButton4.isClickable = false
        binding.radioButton5.isClickable = false

        binding.decideButton.isClickable = false

        val data = datalist[i]
        val answerList = data.getAnswersList()

        for (j in 0 until 6) {
            if (j in answerList) {toGreen(j)}
            else {toGray(j)}
        }

        val helper = DatabaseHelper(this)
        data.setUserAnswersList(userAnswersList, helper)
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(1000L) {
            nextQuestion()
        }
    }

    private fun nextQuestion() {
        ++i
        if (i >= datalist.size) {
            timer.cancel()
            downTimer.cancel()
            val intent = Intent(this, ResultActivity::class.java)
            intent.apply {
                putExtra("dataSet", datalist)
                putExtra("timeSet", timer.getTime())
            }
            startActivity(intent)
        }
        else { viewQuestion() }
    }

    private fun toWhite(n: Int) {
        when(n) {
            0 -> binding.radioButton0.setBackgroundColor(Color.argb(0,0,0,0))
            1 -> binding.radioButton1.setBackgroundColor(Color.argb(0,0,0, 0))
            2 -> binding.radioButton2.setBackgroundColor(Color.argb(0,0,0, 0))
            3 -> binding.radioButton3.setBackgroundColor(Color.argb(0,0,0, 0))
            4 -> binding.radioButton4.setBackgroundColor(Color.argb(0,0,0, 0))
            5 -> binding.radioButton5.setBackgroundColor(Color.argb(0,0,0, 0))
        }
    }
    private fun toGray(n: Int) {
        when(n) {
            0 -> binding.radioButton0.setBackgroundColor(Color.rgb(200,200,200))
            1 -> binding.radioButton1.setBackgroundColor(Color.rgb(200,200,200))
            2 -> binding.radioButton2.setBackgroundColor(Color.rgb(200,200,200))
            3 -> binding.radioButton3.setBackgroundColor(Color.rgb(200,200,200))
            4 -> binding.radioButton4.setBackgroundColor(Color.rgb(200,200,200))
            5 -> binding.radioButton5.setBackgroundColor(Color.rgb(200,200,200))
        }
    }
    private fun toGreen(n: Int) {
        when(n) {
            0 -> binding.radioButton0.setBackgroundColor(Color.rgb(155, 222, 86))
            1 -> binding.radioButton1.setBackgroundColor(Color.rgb(155, 222, 86))
            2 -> binding.radioButton2.setBackgroundColor(Color.rgb(155, 222, 86))
            3 -> binding.radioButton3.setBackgroundColor(Color.rgb(155, 222, 86))
            4 -> binding.radioButton4.setBackgroundColor(Color.rgb(155, 222, 86))
            5 -> binding.radioButton5.setBackgroundColor(Color.rgb(155, 222, 86))
        }
    }
}