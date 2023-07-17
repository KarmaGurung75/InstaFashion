package com.hdd.instaFashion.domain.adapter

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hdd.instaFashion.R
import com.hdd.instaFashion.data.models.Notification
import com.hdd.instaFashion.presentation.activity.ProductDetailsActivity
import com.hdd.instaFashion.presentation.activity.ViewOtherProfileActivity
import com.hdd.instaFashion.presentation.activity.ViewPostActivity

class NotificationAdapter(private val notificationList: MutableList<Notification>) :
    RecyclerView.Adapter<NotificationViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.notification_item_layout, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notificationList[position]
        Glide.with(holder.itemView.context).load(notification.otherUser!!.profile).circleCrop()
            .into(holder.nil_iv_profile)
        holder.nil_tv_overdue.text = notification.createdAt
        holder.nil_tv_message.text = generateStatus(notification.otherUser.fullname!!, notification.message!!)
        holder.an_main_ll.setOnClickListener {
            val related = notification.related
            when (related) {
                "recipe" -> {
                    val intent = Intent(holder.itemView.context, ProductDetailsActivity::class.java)
                    val bundle = Bundle()
                    bundle.putString("recipeId", notification.relatedRecipe)
                    intent.putExtras(bundle)
                    ContextCompat.startActivity(holder.itemView.context, intent, null)
                }
                "user" -> {
                    val intent = Intent(holder.itemView.context, ViewOtherProfileActivity::class.java)
                    val bundle = Bundle()
                    bundle.putString("userId", notification.otherUser._id)
                    intent.putExtras(bundle)
                    ContextCompat.startActivity(holder.itemView.context, intent, null)
                }
                "post" -> {
                    val intent = Intent(holder.itemView.context, ViewPostActivity::class.java)
                    val bundle = Bundle()
                    bundle.putString("postId", notification.relatedPost)
                    intent.putExtras(bundle)
                    ContextCompat.startActivity(holder.itemView.context, intent, null)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    private fun generateStatus(name: String, status: String): SpannableString {
        val mergeNameAndStatus = SpannableString("$name $status")
        val boldSpan = StyleSpan(Typeface.BOLD)
        mergeNameAndStatus.setSpan(boldSpan, 0, name.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        return mergeNameAndStatus
    }
}

class NotificationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val nil_tv_message: TextView = view.findViewById(R.id.nil_tv_message)
    val nil_iv_profile: ImageView = view.findViewById(R.id.nil_iv_profile)
    val nil_tv_overdue: TextView = view.findViewById(R.id.nil_tv_overdue)
    val an_main_ll: LinearLayout = view.findViewById(R.id.an_main_ll)
}