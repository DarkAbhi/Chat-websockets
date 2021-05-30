package com.darkabhi.chatnode

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.darkabhi.chatnode.chat.ChatActivity
import com.darkabhi.chatnode.databinding.ActivityMainBinding
import com.darkabhi.chatnode.utils.AppUtils.checkStoragePermission

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val requestStoragePermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (it.containsValue(false)) {
                // User denied one or more permissions
                Toast.makeText(this, "Denied", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "OK", Toast.LENGTH_LONG).show()
                openChat()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.enterRoomBtn.setOnClickListener {
            if (this.checkStoragePermission())
                openChat()
            else requestStoragePermission.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }
    }

    fun openChat() {
        val names = listOf("Abhishek", "John", "Maggie", "Doe", "Jane")
        startActivity(
            Intent(this, ChatActivity::class.java).putExtra(
                "NAME",
                names.random()
            )
        )
    }
}