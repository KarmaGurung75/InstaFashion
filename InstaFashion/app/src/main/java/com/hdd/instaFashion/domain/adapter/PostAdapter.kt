package com.hdd.instaFashion.domain.adapter

import android.app.Dialog
import android.content.Intent
import android.graphics.Typeface
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hdd.instaFashion.R
import com.hdd.instaFashion.data.models.Post
import com.hdd.instaFashion.data.remoteDataSource.ServiceBuilder
import com.hdd.instaFashion.presentation.activity.*
import com.hdd.instaFashion.repository.LikeRepository
import com.hdd.instaFashion.repository.PostRepository
import com.hdd.instaFashion.repository.ProductRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostAdapter(private val postList: MutableList<Post>,private val isArchived: Boolean = false) :
    RecyclerView.Adapter<PostViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.post_recycler_view, parent, false)
        return PostViewHolder(view)
    }
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postList[position]
        holder.bind(post)

        holder.itemView.setOnLongClickListener {
            val archiveDialog = Dialog(holder.itemView.context)
            archiveDialog.setContentView(R.layout.archive_product_layout)
            archiveDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            val archive: Button = archiveDialog.findViewById(R.id.arl_archive_recipe)
            val delete: Button = archiveDialog.findViewById(R.id.arl_delete_recipe)
            // archive
            if (isArchived){
                archive.text="Unarchive"
                archive.setOnClickListener {
                    archivedPost(post._id)
                    postList.remove(post)
                    notifyDataSetChanged()
                    Toast.makeText(holder.itemView.context, "Post Unarchived", Toast.LENGTH_SHORT).show()
                    archiveDialog.dismiss()
                }
            } else {
                archive.text = "Archive"
                archive.setOnClickListener {
                    archivedPost(post._id)
                    postList.remove(post)
                    notifyDataSetChanged()
                    Toast.makeText(holder.itemView.context, "Post archived", Toast.LENGTH_SHORT).show()
                    archiveDialog.dismiss()
                }
            }
            // delete review and rating
            delete.setOnClickListener {
                deletePost(post._id)
                postList.remove(post)
                notifyDataSetChanged()
                Toast.makeText(holder.itemView.context,"Post Deleted",Toast.LENGTH_SHORT).show()
                archiveDialog.dismiss()
            }
            archiveDialog.show()
            true
        }
    }
    override fun getItemCount(): Int {
        return postList.size
    }
    private fun archivedPost(postId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val postRepository = PostRepository()
                postRepository.archivePost(postId)
            } catch (ex: Exception) {
                print(ex)
            }
        }
    }
    private fun deletePost(postId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val postRepository= PostRepository()
                postRepository.deletePost(postId)
            } catch (ex: java.lang.Exception){
                print(ex)
            }
        }
    }
}
class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private var clickCounter: Int =0
    private var isLiked: Boolean = false
    private var likeCount: Int = 0
    private var loginUserId: String = ""
    private val tvUsername: TextView = view.findViewById(R.id.tvUsername)
    private val tvStatus: TextView = view.findViewById(R.id.tvStatus)
    private val tvPostType: TextView = view.findViewById(R.id.tvPostType)
    private val ivProfile: ImageView = view.findViewById(R.id.ivProfile)
    private val ivPost: ImageView = view.findViewById(R.id.ivPost)
    private val like: ImageView = view.findViewById(R.id.like)
    private val ivDoubleTapLike: ImageView = view.findViewById(R.id.ivDoubleTapLike)
    private val comment: ImageView = view.findViewById(R.id.comment)
    private val likeCounter: TextView = view.findViewById(R.id.like_counter)
    private val tvCreatedDate: TextView = view.findViewById(R.id.tvCreatedDate)
    private val recipeSetting: ImageButton = view.findViewById(R.id.recipeSetting)
    private lateinit var avd1:AnimatedVectorDrawableCompat
    private lateinit var avd2: AnimatedVectorDrawable

    fun bind(post: Post) {
        Glide.with(itemView.context).load(post.user?.profile).circleCrop().into(ivProfile)
//        tvUsername.text = post.user?.fullname
        tvUsername.visibility = View.GONE

        // click on profile
        ivProfile.setOnClickListener {
            val intent = Intent(itemView.context,ViewOtherProfileActivity::class.java)
            val bundle = Bundle()
            bundle.putString("userId", post.user!!._id)
            intent.putExtras(bundle)
            ContextCompat.startActivity(itemView.context,intent,null)
        }

        likeCount = post.likes!!.size
        tvCreatedDate.text = post.createdDate?.toString()
        setLikeCount()
        tvUsername.setTypeface(null, Typeface.BOLD);
        loginUserId = ServiceBuilder.user!!._id
        for (userId in post.likes!!){
            if (userId==loginUserId){
                like.setColorFilter(itemView.resources.getColor(R.color.likeColor))
                isLiked=true
            }
        }
        like.setOnClickListener {
            if (isLiked) {
                setColor(R.color.unlikeColor)
                updateLike(post._id!!,false,R.color.unlikeColor)
                likeCount -= 1
            } else {
                setColor(R.color.likeColor)
                updateLike(post._id!!,true,R.color.likeColor)
                likeCount += 1
            }
            setLikeCount()
        }
        val drawable: Drawable? = ivDoubleTapLike.drawable
        comment.setOnClickListener {
            val intent = Intent(itemView.context, AddCommentActivity::class.java)
            val bundle = Bundle()
            bundle.putString("myId", post._id)
            intent.putExtras(bundle)
            ContextCompat.startActivity(itemView.context,intent,null)
        }
        ivPost.setOnClickListener {
            clickCounter++
            val handler: Handler = Handler()
            handler.postDelayed({
                when (clickCounter) {
                    1 -> {
                        if (post.postType=="recipe" || post.postType=="share"){
                            goToRecipeDetailPage(post.relatedRecipe!!._id)
                        }
                    }
                    2 -> {
                        setColor(R.color.likeColor)
                        ivDoubleTapLike.alpha=0.8f
                        when (drawable) {
                            is AnimatedVectorDrawableCompat -> { avd1 = drawable
                                avd1.start() }
                            is AnimatedVectorDrawable -> { avd2= drawable
                                avd2.start()
                            }
                        }
                        if (!isLiked){
                            likeCount += 1
                            setLikeCount()
                            updateLike(post._id!!,true,R.color.likeColor)
                        }
                    }
                }
                clickCounter = 0
            }, 500)
        }

        if (post.postType=="recipe" || post.postType=="share") {
            if(post.postType=="recipe" ){
                tvPostType.text = generateStatus(post.user!!.fullname!! ,"created ${post.relatedRecipe?.title} recipe")
            }
            else{
                tvPostType.text = generateStatus(post.user!!.fullname!! ,"shared ${post.relatedRecipe?.title} recipe")
            }
            val recipe = post.relatedRecipe!!
            val hashtag = recipe.hashtag ?: mutableListOf()
            if(hashtag.isNotEmpty()){
                var hashtagText = ""
                for(hashtag in recipe.hashtag!!){
                    hashtagText = "$hashtagText #$hashtag"
                }
                tvStatus.text = hashtagText
            }
            else{
               tvStatus.visibility = View.INVISIBLE
            }
            Glide.with(itemView.context).load(post.relatedRecipe?.image).into(ivPost)
            itemView.setOnClickListener {
                goToRecipeDetailPage(post.relatedRecipe!!._id)
            }
//            tvStatus.visibility = View.INVISIBLE
            recipeSetting.setOnClickListener {
                if (post.user!!._id==ServiceBuilder.user!!._id){
                    openDeletePopUpMenu(post.relatedRecipe!!._id,post.postType!!,true)
                } else {
                    openReportPopUpMenu(post.relatedRecipe!!._id,post.postType!!)
                }
            }
        }
        else{
            tvPostType.text = generateStatus(post.user!!.fullname!! ,"added a post")

            tvStatus.text = post.status
            var hasImage=false
            if (post.image != "") {
                hasImage=true
                Glide.with(itemView.context).load(post.image).into(ivPost)
            } else {
                hasImage=false
                ivPost.visibility = View.GONE
            }
            recipeSetting.setOnClickListener {
                if (post.user!!._id==ServiceBuilder.user!!._id){
                    openDeletePopUpMenu(post._id,post.postType!!,hasImage)
                } else {
                    openReportPopUpMenu(post._id,post.postType!!)
                }
            }
        }
    }

    private fun generateStatus(name: String, status: String): SpannableString {
        val mergeNameAndStatus = SpannableString("$name $status")
        val boldSpan = StyleSpan(Typeface.BOLD)
        mergeNameAndStatus.setSpan(boldSpan, 0, name.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        return mergeNameAndStatus
    }

    private fun setLikeCount() {
        if(likeCount > 0){
            likeCounter.visibility = View.VISIBLE
            if(likeCount == 1 ){
                likeCounter.text = "$likeCount like"
            }
            else{
                likeCounter.text = "$likeCount likes"
            }
        }
        else{
            likeCounter.visibility = View.GONE
        }
    }
    private fun openReportPopUpMenu(key:String, postType:String) {
        val popMenu = PopupMenu(itemView.context, recipeSetting)
        popMenu.menuInflater.inflate(R.menu.post_report_menu, popMenu.menu)
        popMenu.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.menuHide) {
                if (postType=="recipe"){
//                    hideRecipe(key)
                    Toast.makeText(itemView.context, "Recipe Hide", Toast.LENGTH_SHORT).show()
                } else{
                    Toast.makeText(itemView.context, "Post Hide", Toast.LENGTH_SHORT).show()
//                    hidePost(key)
                }
            } else if (item.itemId == R.id.menuReport) {
                openBottomDialog(key,postType)
            }
            true
        }
        popMenu.show()
    }

    private fun openDeletePopUpMenu(key:String,postType:String,hasImage:Boolean) {
        val popMenu = PopupMenu(itemView.context, recipeSetting)
        popMenu.menuInflater.inflate(R.menu.post_delete_menu, popMenu.menu)
        popMenu.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.menuEditRecipe) {
                if (postType=="recipe"){
                    goToEditRecipePage(key)
                } else{
                    goToEditPostPage(key,hasImage)
                }
            } else if (item.itemId == R.id.menuDelete) {
                if (postType=="recipe"){
                    deleteRecipe(key)
                } else{
                    deletePost(key)
                }

            }
            true
        }
        popMenu.show()
    }

    private fun deletePost(postId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val postRepository= PostRepository()
                postRepository.deletePost(postId)
            } catch (ex: java.lang.Exception){
                print(ex)
            }
        }
    }
    private fun deleteRecipe(recipeId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val productRepository= ProductRepository()
                productRepository.deleteRecipe(recipeId)
            } catch (ex: java.lang.Exception){
                print(ex)
            }
        }
        Toast.makeText(itemView.context, "Recipe Deleted", Toast.LENGTH_SHORT).show()
    }

    private fun openBottomDialog(reportId:String,postType:String) {
        val bottomDialog = BottomSheetDialog(itemView.context)
        bottomDialog.setContentView(R.layout.bottom_sheet_dialog)
        val bottomSheetContainer:LinearLayout=bottomDialog.findViewById(R.id.bottomSheetContainer)!!

        for (clickedValue in 0 until bottomSheetContainer.childCount) {
            bottomSheetContainer.getChildAt(clickedValue).setOnClickListener {
                if (postType=="recipe"){
                    reportRecipe(reportId,getReportReason(clickedValue))
                } else{
                    reportPost(reportId,getReportReason(clickedValue))
                }
                bottomDialog.dismiss()
            }
        }
        bottomDialog.show()
    }

    private fun getReportReason(selectedIndex: Int): String {
        val reasonList= arrayOf<String>("Harassment","Impersonation","Misinformation","Self Harm","Threatening Violence","Copyright violations","Spam")
        return  reasonList[selectedIndex]
    }

    private fun reportRecipe(recipeId: String, reportReason: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val productRepository= ProductRepository()
                productRepository.reportRecipe(recipeId,reportReason)
            } catch (ex: java.lang.Exception){
                print(ex)
            }
        }
        Toast.makeText(itemView.context, "Thanks for reporting", Toast.LENGTH_SHORT).show()
    }

    private fun reportPost(postId: String, reportReason: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val postRepository= PostRepository()
                postRepository.reportPost(postId,reportReason)
            } catch (ex: java.lang.Exception){
                print(ex)
            }
        }
        Toast.makeText(itemView.context, "Thanks for reporting", Toast.LENGTH_SHORT).show()
    }

    private fun updateLike(postId: String, updateValue: Boolean,updateColor: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val likeRepository= LikeRepository()
                val response=likeRepository.updateLike(postId)
                if(response.success==true){
                    withContext(Dispatchers.Main){
                        isLiked=updateValue
                        setColor(updateColor)
                    }
                }
            }catch (ex:Exception){
                print(ex)
            }
        }
    }
    private fun goToRecipeDetailPage(key: String) {
        val intent = Intent(itemView.context, ProductDetailsActivity::class.java)
        val bundle = Bundle()
        bundle.putString("recipeId", key)
        intent.putExtras(bundle)
        ContextCompat.startActivity(itemView.context, intent, null)
    }
    private fun goToEditPostPage(key: String,boolValue:Boolean) {
        val intent = Intent(itemView.context, AddPostActivity::class.java)
        val bundle = Bundle()
        bundle.putString("editPostId", key)
        bundle.putBoolean("hasImage", boolValue)
        intent.putExtras(bundle)
        ContextCompat.startActivity(itemView.context, intent, null)
    }
    private fun goToEditRecipePage(key: String) {
        val intent = Intent(itemView.context, AddProductActivity::class.java)
        val bundle = Bundle()
        bundle.putString("editRecipeId", key)
        intent.putExtras(bundle)
        ContextCompat.startActivity(itemView.context, intent, null)
    }
    private fun setColor(givenColor: Int) {
        like.setColorFilter(itemView.resources.getColor(givenColor))
    }
}