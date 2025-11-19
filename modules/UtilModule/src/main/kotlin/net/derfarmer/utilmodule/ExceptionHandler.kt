@file:Suppress("MemberVisibilityCanBePrivate")

package net.derfarmer.utilmodule

import net.derfarmer.moduleloader.gson
import java.io.InputStream
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse.BodyHandlers
import java.time.Duration

object ExceptionHandler : Thread.UncaughtExceptionHandler {

    private val client: HttpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build()

    override fun uncaughtException(thread: Thread, exception: Throwable) {
        val msg =
            StringBuilder("Got Exception in Thread ${thread.name} with Id: ${thread.threadId()} [Daemon: ${if (thread.isDaemon) "yes" else "no"}, Priority: ${thread.priority}]\n\n")
        msg.append("Message: ${exception.message}\n")
        msg.append("Stacktrace: \n")

        msg.append(exception.stackTraceToString())

        val pasteBuilder =
            StringBuilder("_________________________________________________________________________________________________________________________________________________\n\n")
        pasteBuilder.append(
            """
             ████     ████               ██          ██                ████████                    ██                      
            ░██░██   ██░██              ░██         ░██               ██░░░░░░   ██   ██          ░██                      
            ░██░░██ ██ ░██  ██████      ░██ ██   ██ ░██  █████       ░██        ░░██ ██   ██████ ██████  █████  ██████████ 
            ░██ ░░███  ░██ ██░░░░██  ██████░██  ░██ ░██ ██░░░██ █████░█████████  ░░███   ██░░░░ ░░░██░  ██░░░██░░██░░██░░██
            ░██  ░░█   ░██░██   ░██ ██░░░██░██  ░██ ░██░███████░░░░░ ░░░░░░░░██   ░██   ░░█████   ░██  ░███████ ░██ ░██ ░██
            ░██   ░    ░██░██   ░██░██  ░██░██  ░██ ░██░██░░░░              ░██   ██     ░░░░░██  ░██  ░██░░░░  ░██ ░██ ░██
            ░██        ░██░░██████ ░░██████░░██████ ███░░██████       ████████   ██      ██████   ░░██ ░░██████ ███ ░██ ░██
            ░░         ░░  ░░░░░░   ░░░░░░  ░░░░░░ ░░░  ░░░░░░       ░░░░░░░░   ░░      ░░░░░░     ░░   ░░░░░░ ░░░  ░░  ░░ \n\n
        """.trimIndent()
        )
        pasteBuilder.append(msg.toString())
        pasteBuilder.append("\n\n_________________________________________________________________________________________________________________________________________________")

        val paste = try {
            pasteBuilder.toString().haste() // Upload to haste server
        } catch (e: Throwable) {
            // If haste upload fails, fall back to inline text shortened
            HasteResult(URI("about:blank"), URI("about:blank"), "upload-failed: ${e.message}")
        }

        try {
            sendToDiscord(paste.url.toString(), exception.toString()) // call discord webhook
        } catch (e: Throwable) {
            // ignore discord send errors in uncaught handler to avoid recursion
        }
    }

    init {
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    private fun sendToDiscord(hasteURL: String, types: String) {
        val webhookUrl =
            System.getenv("DISCORD_WEBHOOK_URL") ?: throw IllegalStateException("DISCORD_WEBHOOK_URL not set")

        val json = """
        {
          "content": null,
          "embeds": [
            {
              "title": "Exception Caught",
              "description": "For more details, please refer to the following link:",
              "color": 5814783,
              "fields": [
                {"name": "Link", "value": "$hasteURL"},
                {"name": "Type", "value": "$types"}
              ]
            }
          ],
          "attachments": []
        }
    """.trimIndent()

        val request = HttpRequest.newBuilder()
            .uri(URI.create(webhookUrl))
            .header("Content-Type", "application/json")
            .timeout(Duration.ofSeconds(10))
            .POST(BodyPublishers.ofString(json))
            .build()

        val response = client.send(request, BodyHandlers.ofString())
        if (response.statusCode() !in 200..299) {
            throw RuntimeException("Discord webhook returned ${response.statusCode()}: ${response.body()}")
        }
    }

    fun String.haste(hasteServer: String = "https://haste.simplecloud.app"): HasteResult {
        val body = this

        val request = HttpRequest.newBuilder()
            .uri(URI.create("$hasteServer/documents"))
            .header("Content-Type", "application/text")
            .timeout(Duration.ofSeconds(10))
            .POST(BodyPublishers.ofString(body))
            .build()

        val response = client.send(request, BodyHandlers.ofString())
        if (response.statusCode() !in 200..299) {
            throw RuntimeException("Haste upload failed: ${response.statusCode()} ${response.body()}")
        }

        val key = gson.fromJson(response.body(), HasteResponse::class.java)?.key
            ?: throw IllegalStateException("Cannot parse Key from Haste Result")

        return HasteResult(URI("$hasteServer/$key"), URI("$hasteServer/raw/$key"), key)
    }

    fun InputStream.haste(hasteServer: String = "https://haste.simplecloud.app") =
        this.bufferedReader().readText().haste(hasteServer)

    class HasteResponse(val key: String)

    data class HasteResult(val url: URI, val rawUrl: URI, val key: String)
}