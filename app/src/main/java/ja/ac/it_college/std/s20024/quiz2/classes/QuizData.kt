package ja.ac.it_college.std.s20024.quiz2.classes

import java.io.Serializable

class QuizData(
    private val id: String,
    private val question: String,
    private val answers: Int,
    choices: MutableList<String>
):Serializable{
    private val answersList = mutableListOf<Int>()
    private val choicesList = choices
    private val shuffledList = choices.shuffled()
    private var userAnswersList = mutableListOf<Int>()
    private var correct = false

    init {
        for (i in 0 until answers) {
            answersList.add(shuffledList.indexOf(choicesList[i]))
        }
    }

    fun getId(): String { return id }

    fun getQuestion(): String { return question }

    fun getAnswers(): Int { return answers }

    fun getAnswersList(): MutableList<Int> { return answersList }

    fun getShuffledList(): List<String> { return shuffledList }

    fun getCorrect(): Boolean{ return correct }

    fun setUserAnswersList(list: MutableList<Int>, helper: DatabaseHelper){
        if (answersList.sorted() == list.sorted()) { correct = true }
        userAnswersList = list
        setDatabaseTf(helper)
    }

    fun getUserAnswersList() : MutableList<Int> { return userAnswersList }

    private fun setDatabaseTf(helper: DatabaseHelper) {
        helper.setCorrect(correct, id.toInt())
    }
}