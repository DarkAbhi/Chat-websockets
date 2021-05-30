package com.darkabhi.chatnode.chat

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.darkabhi.chatnode.databinding.ReceivedImageBinding
import com.darkabhi.chatnode.databinding.ReceivedMessageBinding
import com.darkabhi.chatnode.databinding.SentImageBinding
import com.darkabhi.chatnode.databinding.SentMessageBinding
import org.json.JSONObject

/**
 * Created by Abhishek AN <abhishek@iku.earth> on 5/30/2021.
 */
class ChatAdapter : RecyclerView.Adapter<ChatAdapter.BaseViewHolder>() {

    private val typeMessageSent = 0
    private val typeMessageReceived = 1
    private val typeImageSent = 2
    private val typeImageReceived = 3
    private var dataList = mutableListOf<JSONObject>()

    inner class SentMessageHolder(private val itemBinding: SentMessageBinding) :
        BaseViewHolder(itemBinding.root) {
        override fun bindView(variable: JSONObject) {
            try {
                itemBinding.sentText.text = variable.getString("message")
            } catch (e: Exception) {

            }
        }
    }

    inner class SentImageHolder(private val itemBinding: SentImageBinding) :
        BaseViewHolder(itemBinding.root) {
        override fun bindView(variable: JSONObject) {
            try {
                itemBinding.sentImage.load(getBitmapFromString(variable.getString("image")))
            } catch (e: Exception) {

            }
        }
    }

    private fun getBitmapFromString(image: String): Bitmap {
        val bytes = Base64.decode(image, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    inner class ReceivedMessageHolder(private val itemBinding: ReceivedMessageBinding) :
        BaseViewHolder(itemBinding.root) {
        override fun bindView(variable: JSONObject) {
            try {
                itemBinding.receivedName.text = variable.getString("name")
                itemBinding.receivedText.text = variable.getString("message")
            } catch (e: Exception) {

            }
        }
    }

    inner class ReceivedImageHolder(private val itemBinding: ReceivedImageBinding) :
        BaseViewHolder(itemBinding.root) {
        override fun bindView(variable: JSONObject) {
            try {
                itemBinding.receivedNameImage.text = variable.getString("name")
                itemBinding.receivedImage.load(getBitmapFromString(variable.getString("image")))
            } catch (e: Exception) {

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val holder: BaseViewHolder?
        return when (viewType) {
            typeMessageSent -> {
                val itemBinding = SentMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                holder = SentMessageHolder(itemBinding)
                holder
            }
            typeMessageReceived -> {
                val itemBinding = ReceivedMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                holder = ReceivedMessageHolder(itemBinding)
                holder
            }
            typeImageSent -> {
                val itemBinding = SentImageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                holder = SentImageHolder(itemBinding)
                holder
            }
            typeImageReceived -> {
                val itemBinding = ReceivedImageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                holder = ReceivedImageHolder(itemBinding)
                holder
            }
            else -> {
                val itemBinding = SentMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                holder = SentMessageHolder(itemBinding)
                holder
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bindView(dataList[position])
    }

    override fun getItemCount(): Int = dataList.size

    override fun getItemViewType(position: Int): Int {
        val message = dataList[position]
        try {
            return if (message.getBoolean("isSent")) {
                if (message.has("message"))
                    typeMessageSent
                else
                    typeImageSent
            } else {
                if (message.has("message"))
                    typeMessageReceived
                else typeImageReceived
            }
        } catch (e: Exception) {
            Log.e("ERROR", e.toString())
        }
        return -1
    }

    abstract class BaseViewHolder(itemBinding: View) : RecyclerView.ViewHolder(itemBinding) {
        abstract fun bindView(variable: JSONObject)
    }

    fun submitItem(data: JSONObject) {
        dataList.add(data)
        notifyDataSetChanged()
    }
}