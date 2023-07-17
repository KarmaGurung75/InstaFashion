package com.hdd.instaFashion.presentation.activity

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Address
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
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
import com.hdd.instaFashion.data.models.IngredientSchema
import com.hdd.instaFashion.data.models.PreparationSchema
import com.hdd.instaFashion.data.models.Recipe
import com.hdd.instaFashion.data.remoteDataSource.ServiceBuilder
import com.hdd.instaFashion.domain.adapter.RecipeHashtagAdapter
import com.hdd.instaFashion.domain.adapter.SetFragmentAdapter
import com.hdd.instaFashion.presentation.fragments.AddRecipeDirectionsFragment
import com.hdd.instaFashion.presentation.fragments.AddRecipeIngredientsFragment
import com.hdd.instaFashion.presentation.fragments.AddRecipePreparationFragment
import com.hdd.instaFashion.repository.ProductRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date

class AddProductActivity : AppCompatActivity() {
    companion object {
        val REQUEST_ADDRESS = 132
    }
    private lateinit var editRequiredRecipeId: String
    private lateinit var previewRecipeData: Recipe
    private lateinit var getRecipe: Recipe

    private var isEditMode: Boolean=false
    private var isImageUpload: Boolean=false
    //Initialize
    private lateinit var tabTitleList: ArrayList<String>
    private lateinit var fragmentList: ArrayList<Fragment>
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var recipe_profile_image: ImageView
    private lateinit var recipe_upload_image: ImageView
    private lateinit var recipe_profile_name: TextView

    // for recipe add and discard
    private lateinit var recipe_post: ImageView
    private lateinit var recipe_discard: ImageView

    // for upload recipe
    private lateinit var recipe_title: EditText
    private lateinit var recipe_description: EditText
    private lateinit var next_add_recipe_button: Button
    private lateinit var recipeUploadLinearLayout: LinearLayout
    //for preview recipe
    private lateinit var preview_recipe_title: TextView
    private lateinit var preview_recipe_description: TextView
    private lateinit var preview_recipe_upload_image: ImageView
    private lateinit var preview_add_recipe_button: Button
    private lateinit var recipePreviewLinearLayout: LinearLayout
    //add more recipe details layout
    private lateinit var segment2LinearLayout: LinearLayout
    private lateinit var aar_ll_post_recipe: LinearLayout

    private lateinit var adapter : RecipeHashtagAdapter
    private lateinit var recipeHashtagList : MutableList<String>
    private lateinit var addressListInDirection : MutableList<String>
    private lateinit var priceInItemQty : MutableList<IngredientSchema>

    // next page Tag
    private lateinit var fdr_btnAddHashtag : Button
    private lateinit var fdr_etAddHashtagName : EditText
    private lateinit var fdr_recyclerView : RecyclerView

    //Option
    private lateinit var input_preparation_time:TextView
    private lateinit var input_cooking_time:TextView
    private lateinit var input_total_time:TextView
    private lateinit var input_serving_people:TextView
    private lateinit var input_quantity_pound:TextView


    private lateinit var pdol_btnOption:Button
    private lateinit var preparationLayout:LinearLayout
    private lateinit var pdol_etOptionName: EditText

    //Address and price
    private lateinit var pdapl_btnAddress : Button
    private lateinit var pdapl_etAddressName : EditText
    private lateinit var pdapl_etPrice : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)
         val isOptionEditMode:Boolean=false
         val preparationSchema: PreparationSchema = PreparationSchema()
        recipe_profile_image = findViewById(R.id.recipe_profile_image)
        recipe_profile_name = findViewById(R.id.recipe_profile_name)
        // for upload recipe

        //Extract the data…
        val bundle = intent.extras
        val recipeId = bundle?.getString("editRecipeId")
        if (recipeId != null) {
            editRequiredRecipeId=recipeId
            isEditMode=true
            getRecipeById(editRequiredRecipeId)
        } else{
            editRequiredRecipeId= ""
            isEditMode=false
        }

        // for recipe add and discard
        recipe_discard = findViewById(R.id.recipe_discard)
        recipe_post = findViewById(R.id.recipe_post)

        recipe_title = findViewById(R.id.recipe_title)
        recipe_description = findViewById(R.id.recipe_description)
        recipe_upload_image = findViewById(R.id.recipe_upload_image)
        next_add_recipe_button = findViewById(R.id.next_add_recipe_button)
        recipeUploadLinearLayout = findViewById(R.id.recipeUploadLinearLayout)
        //for preview recipe
        preview_recipe_title = findViewById(R.id.preview_recipe_title)
        preview_recipe_description = findViewById(R.id.preview_recipe_description)
        preview_recipe_upload_image = findViewById(R.id.preview_recipe_upload_image)
        preview_add_recipe_button = findViewById(R.id.preview_add_recipe_button)
        recipePreviewLinearLayout = findViewById(R.id.recipePreviewLinearLayout)
        // add more recipe details layout
        segment2LinearLayout = findViewById(R.id.segment2LinearLayout)
        aar_ll_post_recipe = findViewById(R.id.aar_ll_post_recipe)

        // next page Tag
        fdr_btnAddHashtag=findViewById(R.id.fdr_btnAddHashtag)
        fdr_etAddHashtagName=findViewById(R.id.fdr_etAddHashtagName)
        fdr_recyclerView=findViewById(R.id.fdr_recyclerView)

        // Options Section
        pdol_btnOption=findViewById<Button>(R.id.pdol_btnOption)
        preparationLayout= findViewById(R.id.preparationLayout)
        preparationLayout.visibility = View.GONE
        pdol_etOptionName=findViewById(R.id.pdol_etOptionName)

        input_preparation_time= findViewById(R.id.input_preparation_time)
        input_cooking_time= findViewById(R.id.input_cooking_time)
        input_total_time= findViewById(R.id.input_total_time)
        input_serving_people= findViewById(R.id.input_serving_people)
        input_quantity_pound= findViewById(R.id.input_quantity_pound)

        // Address & Price
        pdapl_btnAddress =findViewById(R.id.pdapl_btnAddress)
        pdapl_etAddressName =findViewById(R.id.pdapl_etAddressName)
        pdapl_etPrice =findViewById(R.id.pdapl_etPrice)


        // ----------------------------------------------------------------------------------------

        recipeHashtagList = mutableListOf<String>()
        addressListInDirection = mutableListOf()
        priceInItemQty = mutableListOf()

        adapter = RecipeHashtagAdapter(recipeHashtagList)
        fdr_recyclerView.adapter=adapter
        adapter.notifyDataSetChanged()

        fdr_btnAddHashtag.setOnClickListener {
            if (fdr_etAddHashtagName.text.isNotEmpty()) {
                recipeHashtagList.add(fdr_etAddHashtagName.text.toString())
                adapter.notifyDataSetChanged()
                fdr_etAddHashtagName.setText("")
                updateRecipeHashtag(ServiceBuilder.recipeId, recipeHashtagList)
            } else {
                fdr_etAddHashtagName.error = "This is required"
            }
        }

        fdr_recyclerView.layoutManager = LinearLayoutManager(this@AddProductActivity)
        fdr_recyclerView.adapter=adapter


        //-------------------------------------------------------------------------------------------

        // Option
        var isNotFilled = true

        // in case of update or edit
        if (isOptionEditMode){
            input_preparation_time.text="${preparationSchema.preparation}"
            input_cooking_time.text="${preparationSchema.cooking}"
            input_total_time.text="${preparationSchema.preparation!!+preparationSchema.cooking!!}"
            input_serving_people.text= "${preparationSchema.serving}"
            input_quantity_pound.text="${preparationSchema.yield}"
            isNotFilled=false
            preparationLayout.visibility = View.VISIBLE
            pdol_btnOption.text = "Edit Details"
        }

        pdol_btnOption.setOnClickListener {
            if (isNotFilled) {
                val reviewDialog = Dialog(this)
                reviewDialog.setContentView(R.layout.add_preparation_dialog)
                reviewDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
                val etUpdatePreparationTime: EditText = reviewDialog.findViewById(R.id.etAddPreparationTime)
                val etUpdateCookingTime: EditText = reviewDialog.findViewById(R.id.etAddCookingTime)
                val etUpdateServingPerson: EditText = reviewDialog.findViewById(R.id.etAddServingPerson)
                val etUpdateQtyPound: EditText = reviewDialog.findViewById(R.id.etAddQtyPound)
                val cancel: Button = reviewDialog.findViewById(R.id.apd_cancel)
                val ok: Button = reviewDialog.findViewById(R.id.apd_ok)
                cancel.setOnClickListener {
                    reviewDialog.dismiss()
                    pdol_btnOption.text = "Add Details"
                }
                ok.setOnClickListener {
                    if (
                        etUpdateCookingTime.text.isNotEmpty() && etUpdatePreparationTime.text.isNotEmpty() && etUpdateServingPerson.text.isNotEmpty() && etUpdateQtyPound.text.isNotEmpty()
                    ){
                        input_preparation_time.text = "${etUpdatePreparationTime.text}"
                        input_cooking_time.text = "${etUpdateCookingTime.text}"
                        input_total_time.text = "${etUpdatePreparationTime.text.toString()+etUpdateCookingTime.text.toString()}"
                        input_serving_people.text = "${etUpdateServingPerson.text}"
                        input_quantity_pound.text = "${etUpdateQtyPound.text}"
                        isNotFilled = false
                        preparationLayout.visibility = View.VISIBLE
                        updateRecipePreparation(
                            ServiceBuilder.recipeId,
                            etUpdatePreparationTime.text.toString(),
                            etUpdateCookingTime.text.toString(),
                            etUpdateServingPerson.text.toString(),
                            etUpdateQtyPound.text.toString(),
                        )
                        pdol_btnOption.text = "Edit Details"
                        reviewDialog.dismiss()
                    } else{
                        etUpdatePreparationTime.error="This is required"
                        etUpdateCookingTime.error="This is required"
                        etUpdateServingPerson.error="This is required"
                        etUpdateQtyPound.error="This is required"
                    }
                }
                reviewDialog.show()
            }
            else {
                pdol_btnOption.setOnClickListener {
                    val reviewDialog = Dialog(this)
                    reviewDialog.setContentView(R.layout.update_preparation_dialog)
                    reviewDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
                    val etUpdatePreparationTime: EditText = reviewDialog.findViewById(R.id.etUpdatePreparationTime)
                    val etUpdateCookingTime: EditText = reviewDialog.findViewById(R.id.etUpdateCookingTime)
                    val etUpdateServingPerson: EditText = reviewDialog.findViewById(R.id.etUpdateServingPerson)
                    val etUpdateQtyPound: EditText = reviewDialog.findViewById(R.id.etUpdateQtyPound)
                    val cancel: Button = reviewDialog.findViewById(R.id.upd_cancel)
                    val btnUpdate: Button = reviewDialog.findViewById(R.id.upd_update)
                    etUpdatePreparationTime.setText(input_preparation_time.text)
                    etUpdateCookingTime.setText(input_cooking_time.text)
                    etUpdateServingPerson.setText(input_serving_people.text)
                    etUpdateQtyPound.setText(input_quantity_pound.text)
                    cancel.setOnClickListener {
                        reviewDialog.dismiss()
                    }
                    btnUpdate.setOnClickListener {
                        if (etUpdateCookingTime.text.isNotEmpty() && etUpdatePreparationTime.text.isNotEmpty() && etUpdateServingPerson.text.isNotEmpty() && etUpdateQtyPound.text.isNotEmpty()){
                            preparationLayout.visibility = View.VISIBLE
                            input_preparation_time.text = "${etUpdatePreparationTime.text}"
                            input_cooking_time.text = "${etUpdateCookingTime.text}"
                            input_total_time.text = "${etUpdatePreparationTime.text.toString()+etUpdateCookingTime.text.toString()}"
                            input_serving_people.text = "${etUpdateServingPerson.text}"
                            input_quantity_pound.text = "${etUpdateQtyPound.text}"
                            isNotFilled = false
                            updateRecipePreparation(
                                ServiceBuilder.recipeId,
                                etUpdatePreparationTime.text.toString(),
                                etUpdateCookingTime.text.toString(),
                                etUpdateServingPerson.text.toString(),
                                etUpdateQtyPound.text.toString(),
                            )
                            preparationLayout.visibility = View.VISIBLE
                            pdol_btnOption.text = "Edit Details"
                            reviewDialog.dismiss()
                        } else {
                            etUpdatePreparationTime.error="This is required"
                            etUpdateCookingTime.error="This is required"
                            etUpdateServingPerson.error="This is required"
                            etUpdateQtyPound.error="This is required"
                        }

                    }
                    reviewDialog.show()
                }
            }
        }


        // for profile layout
        Glide.with(this@AddProductActivity).load(ServiceBuilder.user!!.profile).circleCrop()
            .into(recipe_profile_image)
        recipe_profile_name.text = ServiceBuilder.user!!.fullname

        recipe_discard.setOnClickListener {
            showAlertDialog()
        }

        recipe_post.setOnClickListener {
            if (editRequiredRecipeId.isEmpty()){
                postRecipe(address = pdapl_etAddressName.text.toString(), price = pdapl_etPrice.text.toString().toInt())
                super.onBackPressed()
            }
            super.onBackPressed()
        }

        if (fdr_etAddHashtagName.text.isNotEmpty()) {
            recipeHashtagList.add(fdr_etAddHashtagName.text.toString())
            adapter.notifyDataSetChanged()
            fdr_etAddHashtagName.setText("")
        } else {
            fdr_etAddHashtagName.error = "This is required"
        }


        //=============================
        pdapl_btnAddress.setOnClickListener {
            val intent = Intent(this,AddressPickerActivity::class.java)
            intent.putExtra(AddressPickerActivity.ARG_LAT_LNG,MyLatLng(27.678616119670615, 84.4358566124604))
            val pinList=ArrayList<Pin>()
            pinList.add(Pin(MyLatLng(27.678579704941686, 84.42964182567329),"Bharatpur Airport"))
            intent.putExtra(AddressPickerActivity.ARG_LIST_PIN,  pinList)
            intent.putExtra(AddressPickerActivity.ARG_ZOOM_LEVEL,  18.0f)
            startActivityForResult(intent,REQUEST_ADDRESS)
        }




        recipe_upload_image.setOnClickListener {
            loadPopUpMenu()
        }
        next_add_recipe_button.setOnClickListener {
            when {
                recipe_title.text.isEmpty() -> {recipe_title.error="Product Name is required"
                    recipe_title.requestFocus()}
                recipe_description.text.isEmpty() -> { recipe_description.error="Product Description is required"
                    recipe_description.requestFocus()}
                isImageUpload == false -> {if (editRequiredRecipeId.isEmpty()){
                    Toast.makeText(this,"Image is required",Toast.LENGTH_SHORT).show()
                }}
                //61fd3c37d9ebcb532d5b5f52
                else -> {
                    addRecipe(ServiceBuilder.recipeId!!,recipe_title.text.toString(), recipe_description.text.toString())
                    recipeUploadLinearLayout.visibility=View.GONE
                    recipePreviewLinearLayout.visibility=View.VISIBLE
                    segment2LinearLayout.visibility=View.VISIBLE
                    aar_ll_post_recipe.visibility=View.VISIBLE
                    next_add_recipe_button.visibility=View.GONE
                    preview_add_recipe_button.visibility=View.VISIBLE
                    hideKeyboard(next_add_recipe_button)
                    showPreviewData(recipe_title.text.toString(), recipe_description.text.toString())
                }
            }
        }
        preview_add_recipe_button.setOnClickListener {
            recipeUploadLinearLayout.visibility=View.VISIBLE
            recipePreviewLinearLayout.visibility=View.GONE
            segment2LinearLayout.visibility=View.GONE
            preview_add_recipe_button.visibility=View.GONE
            next_add_recipe_button.visibility=View.VISIBLE
        }
    }


    override fun onBackPressed() {
        if (ServiceBuilder.recipeId !="") {
            showAlertDialog()
        } else {
            super.onBackPressed()
        }
    }



    private fun discardRecipe() {
        if (ServiceBuilder.recipeId !=""){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val productRepository= ProductRepository()
                val response=productRepository.discardRecipe(ServiceBuilder.recipeId!!)
                if(response.success==true){
                    withContext(Dispatchers.Main){
                        super.onBackPressed()
                    }
                }
            }catch (ex:Exception){
                print(ex)
            }
        }
        }
    }
    private fun postRecipe(address: String?, price: Int?) {
        addressListInDirection.add(address!!)
        priceInItemQty.add(IngredientSchema(quantity = price!!))
        if (editRequiredRecipeId.isEmpty()){
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val productRepository= ProductRepository()
                    productRepository.updateRecipeDirection(ServiceBuilder.recipeId!!, recipe = Recipe(direction = addressListInDirection))
                    productRepository.updateRecipeIngredients(ServiceBuilder.recipeId!!, recipe = Recipe(ingredients = priceInItemQty))
                    val response=productRepository.postRecipe(ServiceBuilder.recipeId!!)
                    ServiceBuilder.recipeId=""
                    if(response.success==true){
                        withContext(Dispatchers.Main){
                            super.onBackPressed()
                        }
                    }
                }catch (ex:Exception){
                    print(ex)
                }
            }
        }
    }

    private fun updateRecipeHashtag(recipeId: String?, recipeHashtagList: MutableList<String>) {
        val recipe = Recipe(hashtag = recipeHashtagList)
        try {
            val productRepository = ProductRepository()
            CoroutineScope(Dispatchers.IO).launch {
                productRepository.updateRecipeHashtag(recipeId!!,recipe)
                withContext(Dispatchers.Main) {

                }
            }
        } catch (ex: Exception) {
            println(ex)
        }
    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Discard Product")
        builder.setIcon(R.drawable.cross)
        builder.setMessage("Are you sure to discard this product?")

        //performing Positive action
        builder.setPositiveButton("Yes") { _, _ ->
            if (editRequiredRecipeId.isEmpty()){
                discardRecipe()
                super.onBackPressed()
            } else {
                Toast.makeText(this, "Changes are saved", Toast.LENGTH_SHORT).show()
                super.onBackPressed()
            }
        }

        //performing cancel action
        builder.setNeutralButton("Cancel") { _, _ ->
        }
        //performing negative action
        builder.setNegativeButton("No") { _, _ ->
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    // Load pop up menu
    private fun loadPopUpMenu() {
        val popupMenu = PopupMenu(this, recipe_upload_image)
        popupMenu.menuInflater.inflate(R.menu.gallery_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menuCamera -> openCamera()
                R.id.menuGallery -> openGallery()
            }
            true
        }
        popupMenu.show()
    }
    private var REQUEST_GALLERY_CODE = 0
    private var REQUEST_CAMERA_CODE = 1
    private var imageUrl: String? = null
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_GALLERY_CODE)
    }
    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, REQUEST_CAMERA_CODE)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AddShopActivity.REQUEST_ADDRESS && resultCode == Activity.RESULT_OK) {
            val address: Address = data?.getParcelableExtra<Address>(AddressPickerActivity.RESULT_ADDRESS)!!
            val cityName: String = address.locality
            val subAdminArea: String = address.subAdminArea
            val countryName: String = address.countryName
            pdapl_etAddressName.setText("$cityName, $subAdminArea, $countryName")
        }

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_GALLERY_CODE && data != null) {
                val selectedImage: Uri? = data.data
                val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                val contentResolver = contentResolver
                val cursor =
                    contentResolver.query(selectedImage!!, filePathColumn, null, null, null)
                cursor!!.moveToFirst()
                val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                imageUrl = cursor.getString(columnIndex)
                recipe_upload_image.setImageBitmap(BitmapFactory.decodeFile(imageUrl))
                preview_recipe_upload_image.setImageBitmap(BitmapFactory.decodeFile(imageUrl))
                isImageUpload=true
                cursor.close()
            } else if (requestCode == REQUEST_CAMERA_CODE && data != null) {
                val imageBitmap = data.extras?.get("data") as Bitmap
                val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                val file = bitmapToFile(imageBitmap, "$timeStamp.jpg")
                imageUrl = file!!.absolutePath
                recipe_upload_image.setImageBitmap(BitmapFactory.decodeFile(imageUrl))
                preview_recipe_upload_image.setImageBitmap(BitmapFactory.decodeFile(imageUrl))
                isImageUpload=true
            }
        }
    }
    private fun getMimeType(url: String?): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }

    private fun addRecipe(prevRecipeId:String,recipeName:String,recipeDescription: String) {
        if (isImageUpload) {
            if (imageUrl!=null){
                val file = File(imageUrl!!)
                val mimeType = getMimeType(file.path);
                val reqFile = RequestBody.create(mimeType!!.toMediaTypeOrNull(), file)
//              val reqFiles = RequestBody.create(mimeType!!.toMediaTypeOrNull(), file)   // fragment
                val reqRecipeTitle = RequestBody.create("multipart/fetch-data".toMediaTypeOrNull(), recipeName)
                val reqRecipeDescription = RequestBody.create("multipart/fetch-data".toMediaTypeOrNull(),recipeDescription)
                val reqPrevRecipeId = RequestBody.create("multipart/fetch-data".toMediaTypeOrNull(), prevRecipeId)
                val body = MultipartBody.Part.createFormData("image",file.name,reqFile)
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val productRepository = ProductRepository()
                        val response = productRepository.addRecipe(
                            body,
                            reqRecipeTitle,
                            reqRecipeDescription,
                            reqPrevRecipeId
                        )
                        if (response.success == true) {
                            withContext(Dispatchers.Main) {
                                previewRecipeData = response.data!!
                                ServiceBuilder.recipeId = previewRecipeData._id
                            }
                        }
                    } catch (ex: Exception) {
                        withContext(Dispatchers.Main) {
                            print(ex)
                        }
                    }
                }
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val productRepository = ProductRepository()
                        val response = productRepository.updateRecipeWithoutImage(prevRecipeId,Recipe(title =recipeName, description =  recipeDescription))
                        if (response.success == true) {
                            withContext(Dispatchers.Main) {
                                previewRecipeData = response.data!!
                                ServiceBuilder.recipeId = previewRecipeData._id
                            }
                        }
                    } catch (ex: Exception) {
                        withContext(Dispatchers.Main) {
                            print(ex)
                        }
                    }
                }
            }
        }
    }

    // Option Function

    private fun updateRecipePreparation(recipeId: String?, preparation: String, cooking: String, serving: String, yieldQty: String) {
        val preparationSchema = PreparationSchema(preparation, cooking, serving, yieldQty)
        val recipe = Recipe(preparation = preparationSchema)
        try {
            val productRepository = ProductRepository()
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.Main) {
                    productRepository.updateRecipePreparation(recipeId!!,recipe)
                }
            }
        } catch (ex: Exception) {
            println(ex)
        }
    }

    private fun showPreviewData(inputTitle: String, inputDescription: String) {
        preview_recipe_title.text = inputTitle
        preview_recipe_description.text = inputDescription
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun getRecipeById(recipeId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val productRepository = ProductRepository()
                val response = productRepository.getRecipeById(recipeId!!)
                if (response.success == true) {
                    getRecipe = response.data!!
                    withContext(Dispatchers.Main) {
                        ServiceBuilder.recipeId=getRecipe._id
                        recipe_title.setText(getRecipe.title)
                        recipe_description.setText(getRecipe.description)
                        isImageUpload=true
                        Glide.with(this@AddProductActivity).load(getRecipe.image).into(recipe_upload_image)
                        Glide.with(this@AddProductActivity).load(getRecipe.image).into(preview_recipe_upload_image)

                        tabTitleList =
                            arrayListOf<String>("Preparation", "Ingredients", "Directions")
                        fragmentList = arrayListOf<Fragment>(
                            AddRecipePreparationFragment(isEditMode,getRecipe.preparation!!),
                            AddRecipeIngredientsFragment(isEditMode, getRecipe.ingredients!!),
                            AddRecipeDirectionsFragment(isEditMode, getRecipe.direction!!)
                        )

                        // setting up adapter class for view pager2 in PDL
                        val adapter =
                            SetFragmentAdapter(fragmentList, supportFragmentManager, lifecycle)
                        viewPager.adapter = adapter
                        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                            tab.text = tabTitleList[position]
                        }.attach()
                    }

                }
            } catch (ex: Exception) {
                print(ex)
            }
        }
    }

    private fun bitmapToFile(bitmap: Bitmap, fileNameToSave: String): File? {
        var file: File? = null
        return try {
            file = File(
                getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    .toString() + File.separator + fileNameToSave
            )
            file.createNewFile()
            //Convert bitmap to byte array
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos) // YOU can also save it in JPEG
            val bitMapData = bos.toByteArray()
            //write the bytes in file
            val fos = FileOutputStream(file)
            fos.write(bitMapData)
            fos.flush()
            fos.close()
            file
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            file // it will return null
        }
    }
}