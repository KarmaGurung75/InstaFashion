package com.hdd.instaFashion.presentation.activity

import android.app.Dialog
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.hdd.instaFashion.R
import com.hdd.instaFashion.data.models.Recipe
import com.hdd.instaFashion.data.models.ReviewRating
import com.hdd.instaFashion.data.remoteDataSource.ServiceBuilder
import com.hdd.instaFashion.domain.adapter.ReviewRatingAdapter
import com.hdd.instaFashion.domain.adapter.SetFragmentAdapter
import com.hdd.instaFashion.presentation.fragments.ViewRecipeDirectionsFragment
import com.hdd.instaFashion.presentation.fragments.ViewRecipeIngredientsFragment
import com.hdd.instaFashion.presentation.fragments.ViewRecipePreparationFragment
import com.hdd.instaFashion.repository.ProductRepository
import com.hdd.instaFashion.repository.ReviewRatingRepository
import com.hdd.instaFashion.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
class ProductDetailsActivity : AppCompatActivity() {
    private lateinit var recipe: Recipe

    // for recipe image layout
    private lateinit var ril_recipe_name:TextView
    private lateinit var ril_recipe_image:ImageView
    private lateinit var ril_recipe_share:ImageView
    private lateinit var ril_recipe_description:TextView
    private lateinit var pil_saved: ImageView

    // for recipe description layout
    private lateinit var tabTitleList: ArrayList<String>
    private lateinit var fragmentList: ArrayList<Fragment>
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    // for review-rating layout
    private lateinit var ard_add_review: Button
    private lateinit var reviewRecyclerView: RecyclerView
    private lateinit var ril_hashtag: TextView
    private lateinit var reviewList: MutableList<ReviewRating>
    private lateinit var reviewAdapter: ReviewRatingAdapter
    private lateinit var yourRatingStar: LinearLayout

    private var isRecipeSaved: Boolean = false
    private var selectedRating: Int = 0

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)
        //Extract the data…
        val bundle = intent.extras
        val requiredRecipeId = bundle!!.getString("recipeId")!!
        getRecipeById(requiredRecipeId)

        // for recipe image layouts
        ril_recipe_name=findViewById(R.id.ril_recipe_name)
        ril_recipe_image=findViewById(R.id.ril_recipe_image)
        ril_recipe_share=findViewById(R.id.ril_recipe_share)
        ril_recipe_description=findViewById(R.id.ril_recipe_description)
        pil_saved = findViewById(R.id.ril_recipe_saved)
        reviewRecyclerView = findViewById(R.id.reviewRecyclerView)
        yourRatingStar = findViewById(R.id.yourRatingStar)
        ril_hashtag = findViewById(R.id.ril_hashtag)

        // for Saved recipe action button
        pil_saved.setOnClickListener {
            if (isRecipeSaved) {
                setColor(R.color.unlikeColor)
                savedRecipe(requiredRecipeId, false, R.color.unlikeColor)
            } else {
                setColor(R.color.likeColor)
                savedRecipe(requiredRecipeId, true, R.color.likeColor)
            }
        }

        // for share recipe
        ril_recipe_share.setOnClickListener {
            showAlertDialog(requiredRecipeId)
        }

        // for recipe description layout
        viewPager = findViewById(R.id.rdl_viewPager)
        tabLayout = findViewById(R.id.rdl_tabLayout)

        // for recipe review-rating layout
        ard_add_review = findViewById(R.id.ard_add_review)

        // add review button
        ard_add_review.setOnClickListener {
            val reviewDialog = Dialog(this)
            reviewDialog.setContentView(R.layout.add_review_layout)
            reviewDialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val writeReview: EditText = reviewDialog.findViewById(R.id.arl_writeReview)
            val cancel: Button = reviewDialog.findViewById(R.id.arl_cancel)
            val ok: Button = reviewDialog.findViewById(R.id.arl_ok)
            val yourRatingStar: LinearLayout = reviewDialog.findViewById(R.id.ratingStarContainer)
            cancel.setOnClickListener {
                reviewDialog.dismiss()
            }
//            setRating(starRating)
            for (starValue in 0 until yourRatingStar.childCount) {
                val starPosition: Int = starValue
                yourRatingStar.getChildAt(starValue).setOnClickListener {
                    for (starValue in 0 until yourRatingStar.childCount) {
                        val starOnClick: ImageView =
                            yourRatingStar.getChildAt(starValue) as ImageView
                        starOnClick.imageTintList =
                            ColorStateList.valueOf(resources.getColor(R.color.starNotSelectedColor))
                        if (starValue <= starPosition) {
                            starOnClick.imageTintList =
                                ColorStateList.valueOf(resources.getColor(R.color.starSelectedColor))
                        }
                    }
                    selectedRating = starPosition + 1
                }
            }
            ok.setOnClickListener {
                if (writeReview.text.isNotEmpty()) {
                    addReview(requiredRecipeId, writeReview.text.toString(), selectedRating)
                    reviewDialog.dismiss()
                } else {
                    writeReview.error = "This field is required"
                }
            }
            reviewDialog.show()
        }
    }

    private fun shareRecipe(recipeId: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val productRepository= ProductRepository()
                val response = productRepository.shareRecipe(recipeId!!)
                if(response.success == true){
                    Toast.makeText(this@ProductDetailsActivity, "Product Shared", Toast.LENGTH_SHORT).show()
                }
            }catch (ex:Exception){
                print(ex)
            }
        }
    }

    private fun savedRecipe(recipeId: String, updateValue: Boolean,updateColor: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userRepository= UserRepository()
                val response=userRepository.savedRecipe(recipeId)
                if(response.success==true){
                    withContext(Dispatchers.Main){
                        isRecipeSaved=updateValue
                        setColor(updateColor)
                    }
                }
            }catch (ex:Exception){
                print(ex)
            }
        }
    }

    private fun showAlertDialog(recipeId: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Share Product")
        builder.setMessage("Do you want to share this product?")
        builder.setIcon(R.drawable.share_icon)
        //performing Positive action
        builder.setPositiveButton("Yes") { _, _ ->
            shareRecipe(recipeId)
            finish()
        }
        //performing cancel action
        builder.setNeutralButton("Cancel") { _, _ ->
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun setColor(givenColor: Int) {
        pil_saved.setColorFilter(resources.getColor(givenColor))
    }

    private fun getRecipeById(recipeId: String) {
        reviewList= mutableListOf<ReviewRating>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val productRepository= ProductRepository()
                val response=productRepository.getRecipeById(recipeId!!)
                if(response.success==true){
                    recipe = response.data!!
                    withContext(Dispatchers.Main){
                        setAvgRating(recipe.avgRating!! - 1)
                        ril_recipe_name.text = recipe.title
                        ril_recipe_description.text= recipe.description
                        if(recipe.hashtag?.size != 0){
                            ril_hashtag.visibility = View.VISIBLE
                            var hashtagText = ""
                            for(hashtag in recipe.hashtag!!){
                                hashtagText = "$hashtagText #$hashtag"
                            }
                            ril_hashtag.text = hashtagText
                        }
                        Glide.with(this@ProductDetailsActivity).load(recipe.image).into(ril_recipe_image)

                        tabTitleList = arrayListOf<String>("Details", "Price", "Location")
                        fragmentList = arrayListOf<Fragment>(
                            ViewRecipePreparationFragment(recipe.preparation!!),
                            ViewRecipeIngredientsFragment(recipe.ingredients!!),
                            ViewRecipeDirectionsFragment(recipe.direction!!)
                        )

                        // setting up adapter class for view pager2 in PDL
                        val adapter = SetFragmentAdapter(fragmentList, supportFragmentManager, lifecycle)
                        viewPager.adapter = adapter
                        TabLayoutMediator(tabLayout, viewPager) {
                                tab, position -> tab.text = tabTitleList[position]
                        }.attach()
                        reviewList = recipe.review!!
                        reviewAdapter = ReviewRatingAdapter(recipe._id,reviewList.asReversed())

                        val linearLayoutManager= object : LinearLayoutManager(this@ProductDetailsActivity) {
                            override fun canScrollVertically(): Boolean {
                                return true
                            }
                        }
                        reviewRecyclerView.layoutManager= linearLayoutManager
                        reviewRecyclerView.adapter=reviewAdapter
                    }
                }
            }catch (ex:Exception){
                print(ex)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setAvgRating(starPosition: Int):Int {
        for (starValue in 0 until yourRatingStar.childCount) {
            val starOnClick:ImageView=yourRatingStar.getChildAt(starValue) as ImageView
            starOnClick.imageTintList=ColorStateList.valueOf(resources.getColor(R.color.starNotSelectedColor))
            if (starValue<=starPosition){
                starOnClick.imageTintList=ColorStateList.valueOf(resources.getColor(R.color.starSelectedColor))
            }
        }
        return starPosition+1
    }

    private fun addReview(recipeId:String,reviewMessage:String,ratingNumber:Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val reviewRepository = ReviewRatingRepository()
                val response = reviewRepository.addReviewRating(recipeId, ReviewRating(review = reviewMessage, rating = ratingNumber))
                if (response.success==true){
                    withContext(Dispatchers.Main){
                        reviewList.add(ReviewRating(review = reviewMessage,user = ServiceBuilder.user, rating = selectedRating))
                        reviewAdapter.notifyDataSetChanged()
                        Toast.makeText(this@ProductDetailsActivity,"ReviewRating Added Successfully",Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (ex:Exception){
                print(ex)
            }
        }

    }
}