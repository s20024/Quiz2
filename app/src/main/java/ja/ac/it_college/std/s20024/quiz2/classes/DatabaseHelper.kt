package ja.ac.it_college.std.s20024.quiz2.classes

import android.annotation.SuppressLint
import android.content.Context
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.json.JSONArray

class DatabaseHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object{
        private const val DATABASE_NAME = "quizLesson7.db"
        private const val DATABASE_VERSION = 1
    }
    override fun onCreate(db: SQLiteDatabase) {
        val sb = """
            create table quizLesson7(
            _id integer primary key,
            question text,
            answers integer,
            choices text,
            correct boolean
            );
        """.trimIndent()
        db.execSQL(sb)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        println("Default Upgrade")
    }

    fun onMyUpgrade(rootData: JSONArray) {
        val db = this.writableDatabase
        val dbSize = getDataSize()
        for(i in dbSize until rootData.length()) {
            val data = rootData.getJSONObject(i)
            val sqlInsert = """
                insert into quizLesson7(_id, question, answers, choices, correct)
                values (?, ?, ?, ?, false)
            """.trimIndent()

            val stmt = db.compileStatement(sqlInsert)
            stmt.bindLong(1, i.toLong())
            stmt.bindString(2, data.getString("question"))
            stmt.bindLong(3, data.getLong("answers"))
            stmt.bindString(4, data.getJSONArray("choices").toString())
            stmt.executeInsert()
        }
    }

    fun setCorrect(correct: Boolean, id: Int) {
        val db = this.writableDatabase
        val sqlUpdate: String
        if (correct) {
            sqlUpdate = """
            update quizLesson7
            set correct = true
            where _id = ?
        """.trimIndent()
        }
        else {
            sqlUpdate = """
            update quizLesson7
            set correct = false
            where _id = ?
        """.trimIndent()
        }


        val stmt = db.compileStatement(sqlUpdate)
        stmt.bindLong(1, id.toLong())
        stmt.executeUpdateDelete()
    }

    fun pickUpData(num: Int): ArrayList<QuizData> {
        val dbSize = DatabaseUtils.queryNumEntries(this.readableDatabase, "quizLesson7").toInt()
        val randomList = (0 until dbSize).toList().shuffled()
        return getData(num, randomList)
    }

    @SuppressLint("Range", "Recycle")
    fun pickUpDataFalse(num: Int): ArrayList<QuizData> {
        var dataCount = 0
        val db = this.readableDatabase
        val sqlCount = """
            select count(*) as count from quizLesson7
            where correct = false
        """.trimIndent()

        var cursor = db.rawQuery(sqlCount, arrayOf())

        while (cursor.moveToNext()) {
            dataCount = cursor.getInt(cursor.getColumnIndex("count"))
        }

        val idList = arrayListOf<Int>()
        val sqlSelectFalse = """
            select _id from quizLesson7
            where correct = false
        """.trimIndent()

        cursor = db.rawQuery(sqlSelectFalse, arrayOf())

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndex("_id"))
            idList.add(id)
        }
        val resultNum = if (num <= dataCount) num else dataCount

        return getData(resultNum, idList.shuffled().toList())
    }

    @SuppressLint("Range")
    fun getData(num: Int, list: List<Int>): ArrayList<QuizData> {
        val datalist = arrayListOf<QuizData>()
        val db = this.readableDatabase
        val sqlSelect = """
            select * from quizLesson7
            where _id = ?
        """.trimIndent()

        for (i in 0 until num) {
            val getId =  list[i].toString()
            val cursor = db.rawQuery(sqlSelect, arrayOf(getId))

            while (cursor.moveToNext()) {
                val id = cursor.getString(cursor.getColumnIndex("_id"))
                val question = cursor.getString(cursor.getColumnIndex("question"))
                val answers = cursor.getInt(cursor.getColumnIndex("answers"))
                val choices = mutableListOf<String>()
                val tempChoices = JSONArray(cursor.getString(cursor.getColumnIndex("choices")))
                for (j in 0 until tempChoices.length()) {
                    if (tempChoices.getString(j) != "") {
                        choices.add(tempChoices.getString(j))
                    }
                }
                datalist.add(QuizData(id, question, answers, choices))
            }
            cursor.close()
        }
        return datalist
    }

    fun getDataSize(): Int {
        val db = this.readableDatabase
        return DatabaseUtils.queryNumEntries(db, "quizLesson7").toInt()
    }
}