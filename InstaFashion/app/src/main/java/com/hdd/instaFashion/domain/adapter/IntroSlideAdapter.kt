package com.hdd.instaFashion.domain.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hdd.instaFashion.R
import com.hdd.instaFashion.data.models.IntroSlide

class IntroSlideAdapter(private val introSlideList:List<IntroSlide> ):RecyclerView.Adapter<IntroSlideAdapter.IntroSlideViewHolder>() {
    inner class IntroSlideViewHolder(view: View):RecyclerView.ViewHolder(view){
        private val textTitle = view.findViewById<TextView>(R.id.textTitle)
        private val textDescription = view.findViewById<TextView>(R.id.textDescription)
        private val imageIcon = view.findViewById<ImageView>(R.id.imageSlideIcon)

        fun bind(introSlide: IntroSlide){
            textTitle.text=introSlide.title
            textDescription.text=introSlide.description
            imageIcon.setImageResource(introSlide.icon)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntroSlideViewHolder {
        return IntroSlideViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.slide_item_container,parent,false)
        )
    }

    override fun onBindViewHolder(holder: IntroSlideViewHolder, position: Int) {
       holder.bind(introSlideList[position])
    }

    override fun getItemCount(): Int {
       return introSlideList.size
    }
}