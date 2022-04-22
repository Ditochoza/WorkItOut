package es.ucm.fdi.workitout.repository

import es.ucm.fdi.workitout.R
import es.ucm.fdi.workitout.model.DatabaseResult
import es.ucm.fdi.workitout.model.Video
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException


class YoutubeAPI {

    private val YOUTUBE_API_KEY : String = "&key=AIzaSyDZ3H8bjLCeDuHqKXIC78DdcPQZ8jQznhg"
    private val BASE_URL: String = "https://www.googleapis.com/youtube/v3/videos?part=snippet%2CcontentDetails%2Cstatistics&id="
    private val client = OkHttpClient()


    suspend fun getVideoInfo(url:String): DatabaseResult<Video?> {
        return try {
            withContext(Dispatchers.IO) {
                var video = Video()
                val videoUrl = BASE_URL + url.substringAfter("?v=") + YOUTUBE_API_KEY
                val request = Request.Builder()
                    .url(videoUrl)
                    .build()


                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")


                    var responseData: String = response.body!!.string()
                    val json = JSONObject(responseData)
                    val items = json.getJSONArray("items").getJSONObject(0)
                    val snippet = items.getJSONObject("snippet")
                    video.url = url
                    video.title = snippet.getString("title")
                    video.title = video.title.substring(0,75) + "..."


                    val miniaturas = snippet.getJSONObject("thumbnails")
                    val miniatura = miniaturas.getJSONObject("medium")
                    video.thumbnail = miniatura.getString("url")

                }



                DatabaseResult.success(video)
            }
        } catch (e: Exception) {
            DatabaseResult.failed(R.string.yt_api_error)
        }
    }
}