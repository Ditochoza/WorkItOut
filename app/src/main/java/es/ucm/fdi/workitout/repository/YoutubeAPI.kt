package es.ucm.fdi.workitout.repository

import es.ucm.fdi.workitout.model.Video
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject


class YoutubeAPI {
    private val client = OkHttpClient()

    companion object {
        const val YOUTUBE_API_KEY : String = "&key=AIzaSyDZ3H8bjLCeDuHqKXIC78DdcPQZ8jQznhg"
        const val BASE_URL: String = "https://www.googleapis.com/youtube/v3/videos?part=snippet%2CcontentDetails%2Cstatistics&id="
    }

    fun fetchVideo(url: String): Video {
        val video = Video()
        val videoUrl = BASE_URL + url.substringAfter("?v=") + YOUTUBE_API_KEY
        val request = Request.Builder()
            .url(videoUrl)
            .build()

        client.newCall(request).execute().use { response ->
            val responseData: String = response.body!!.string()
            val json = JSONObject(responseData)
            val jsonArrayItems = json.getJSONArray("items")
            if (jsonArrayItems.length() > 0) {
                val items = jsonArrayItems.getJSONObject(0)
                val snippet = items.getJSONObject("snippet")
                video.url = url
                video.title = snippet.getString("title")
                video.description = snippet.getString("description")


                val thumbnails = snippet.getJSONObject("thumbnails")
                val thumbnail = thumbnails.getJSONObject("medium")
                video.thumbnail = thumbnail.getString("url")
            }
        }

        return video
    }
}