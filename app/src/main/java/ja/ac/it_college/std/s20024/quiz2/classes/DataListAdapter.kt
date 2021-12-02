package ja.ac.it_college.std.s20024.quiz2.classes

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ja.ac.it_college.std.s20024.quiz2.R

class DataListAdapter(private val datalist: ArrayList<QuizData>):
RecyclerView.Adapter<DataListAdapter.ViewHolder>() {

    private var listener: (Int) -> Unit = {}

    fun setOnItemClickListener(listener: (Int) -> Unit) {this.listener = listener}

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val idView: TextView = itemView.findViewById(R.id.cellIdView)
        val questionView: TextView = itemView.findViewById(R.id.cellQuestionView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.datalist_cell, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = datalist[position]
        holder.idView.text = data.getId()

        if (data.getCorrect()) { holder.idView.setBackgroundColor(Color.rgb(155, 222, 86)) }
        else { holder.idView.setBackgroundColor(Color.rgb(200, 200, 200)) }

        if (16 <= data.getQuestion().length) { holder.questionView.text = data.getQuestion().substring(0, 15) + "・・・" }
        else { holder.questionView.text = data.getQuestion() }

        holder.itemView.setOnClickListener{ listener(position) }
    }

    override fun getItemCount(): Int {
        return datalist.size
    }
}