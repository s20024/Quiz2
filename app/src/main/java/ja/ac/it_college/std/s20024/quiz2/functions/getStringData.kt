package ja.ac.it_college.std.s20024.quiz2.functions

import androidx.annotation.WorkerThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

@WorkerThread
suspend fun getStringData(sourceUrl: String) : String {
    val res = withContext(Dispatchers.IO) {
        var result = ""
        val url = URL(sourceUrl)
        val con = url.openConnection() as? HttpURLConnection
        con?.apply {
            try {
                connectTimeout = 15000
                readTimeout = 15000
                requestMethod = "GET"
                connect()

                val stream = inputStream
                result = extendString(stream)
                stream.close()
            } catch(ex: SocketTimeoutException) {
                print("通信タイムアウト")
            }
            disconnect()
        }
        result
    }
    return res
}
fun extendString(stream: InputStream?): String {
    val reader = BufferedReader(InputStreamReader(stream, "UTF-8"))
    return reader.readText()
}