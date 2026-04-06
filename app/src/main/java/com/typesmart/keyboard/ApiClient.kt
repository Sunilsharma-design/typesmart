package com.typesmart.keyboard

import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val API_URL = "https://api.myapp.com/rewrite"
    private val gson = Gson()

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    data class RewriteRequest(val text: String, val action: String)
    data class RewriteResponse(val result: String?)

    fun rewriteText(text: String, action: String): Result<String> {
        return try {
            val payload = gson.toJson(RewriteRequest(text = text, action = action))
            val requestBody = payload.toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return Result.failure(IOException("API error: HTTP ${response.code}"))
                }

                val body = response.body?.string().orEmpty()
                val parsed = gson.fromJson(body, RewriteResponse::class.java)
                val resultText = parsed?.result?.trim().orEmpty()
                if (resultText.isEmpty()) {
                    Result.failure(IOException("Empty response from API"))
                } else {
                    Result.success(resultText)
                }
            }
        } catch (e: Exception) {
            val message = when (e) {
                is IOException -> "Network error. Check internet and try again."
                else -> "Unexpected error. Please retry."
            }
            Result.failure(IOException(message, e))
        }
    }
}
