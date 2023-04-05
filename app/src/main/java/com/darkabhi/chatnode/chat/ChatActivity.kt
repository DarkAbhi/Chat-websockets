package com.darkabhi.chatnode.chat

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.darkabhi.chatnode.config.AppConfig.SERVER_PATH
import com.darkabhi.chatnode.databinding.ActivityChatBinding
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private var name: String = ""
    private lateinit var webSocket: WebSocket
    private lateinit var chatAdapter: ChatAdapter

    private val getImage = registerForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) {
            try {
                val inputStream = contentResolver.openInputStream(it)
                val image = BitmapFactory.decodeStream(inputStream)
                sendImage(image)
            } catch (e: Exception) {
                Log.e("ERROR", e.toString())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        name = intent.getStringExtra("NAME")!!
        initSocket()
    }

    private fun initSocket() {
        val client = OkHttpClient()
        val request = Request.Builder().url(SERVER_PATH).build()
        webSocket = client.newWebSocket(request, SocketListener())
    }

    inner class SocketListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)
            runOnUiThread {
                Toast.makeText(this@ChatActivity, "SUCCESS CONNECTION", Toast.LENGTH_LONG).show()
                initView()
            }
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket, text)
            runOnUiThread {
                try {
                    val jsonObject = JSONObject(text)
                    if (jsonObject.getString("name") != name) {
                        jsonObject.put("isSent", false)
                        chatAdapter.submitItem(jsonObject)
                        binding.chatRv.smoothScrollToPosition(chatAdapter.itemCount - 1)
                    }
                } catch (e: Exception) {
                    Log.e("ERROR", e.toString())
                }
            }
        }
    }

    private fun initView() {
        chatAdapter = ChatAdapter()

        binding.chatRv.apply {
            adapter = chatAdapter
        }
        binding.editText.addTextChangedListener {
            if (it.toString().trim().isEmpty()) binding.attach.visibility =
                View.VISIBLE else binding.attach.visibility = View.GONE
        }
        binding.sendBtn.setOnClickListener {
            val jsonObject = JSONObject()
            try {
                jsonObject.put("name", name)
                jsonObject.put("message", binding.editText.text.toString())
                jsonObject.put("isSent", true)
                webSocket.send(jsonObject.toString())
                chatAdapter.submitItem(jsonObject)
                binding.editText.text.clear()
                binding.chatRv.smoothScrollToPosition(chatAdapter.itemCount - 1)
            } catch (e: Exception) {
                Log.e("ERROR", e.toString())
            }
        }
        binding.attach.setOnClickListener {
            getImage.launch("image/*")
        }
    }

    private fun sendImage(image: Bitmap?) {
        val outputStream = ByteArrayOutputStream()
        image?.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
        val base64String = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
        val jsonObject = JSONObject()
        try {
            jsonObject.put("name", name)
            jsonObject.put("image", base64String)
            jsonObject.put("isSent", true)
            webSocket.send(jsonObject.toString())
            chatAdapter.submitItem(jsonObject)
            binding.chatRv.smoothScrollToPosition(chatAdapter.itemCount - 1)
        } catch (e: Exception) {
            Log.e("ERROR", e.toString())
        }
    }
}