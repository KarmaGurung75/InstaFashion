package com.hdd.instaFashion.presentation.activity

import android.app.Activity
import android.app.Dialog
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
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.hdd.instaFashion.R
import com.hdd.instaFashion.data.models.Restaurant
import com.hdd.instaFashion.utils.ExtensionFunction.hide
import com.hdd.instaFashion.utils.ExtensionFunction.show
import com.hdd.instaFashion.data.remoteDataSource.ServiceBuilder
import com.hdd.instaFashion.repository.ShopRepository
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
import java.util.*

class ViewShopActivity : AppCompatActivity() {

    private var isUserWantToUploadCoverImage: Boolean=false
    private var isUserWantToUploadImage: Boolean=false
    private var isEditModeEnabled: Boolean=false
    private var isOpeningTimeSelected: Boolean=true

    // for add restaurant details
    private lateinit var addRestaurantName: EditText
    private lateinit var addRestaurantDescription: EditText
    private lateinit var addPhone: EditText
    private lateinit var addAddress: EditText
    private lateinit var addOpeningTime: EditText
    private lateinit var addClosingTime: EditText
    private lateinit var restaurantImage: ImageView
    private lateinit var restaurantCoverImage: ImageView
    private lateinit var map: ImageView

    // for add restaurant details
    private lateinit var viewRestaurantName: TextView
    private lateinit var viewRestaurantDescription: TextView
    private lateinit var viewPhone: TextView
    private lateinit var viewAddress: TextView
    private lateinit var viewOpeningTime: TextView
    private lateinit var viewClosingTime: TextView

    // for button container layout
    private lateinit var buttonContainerLayout: LinearLayout
    private lateinit var addAdd: Button
    private lateinit var btnCancel: Button

    private lateinit var iv_add_img: ImageView
    private lateinit var avr_edit_icon: ImageView
    private lateinit var avr_delete_icon: ImageView

    // for container layout
    private lateinit var viewRestaurantDetailsLayout: LinearLayout
    private lateinit var addRestaurantDetailsLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_shop)

        //Extract the data…
        val bundle = intent.extras
        val requiredRestaurantId = ServiceBuilder.user!!.restaurant!!._id
        getRestaurantDetailsById(requiredRestaurantId)

        // for images layout
        restaurantImage = findViewById(R.id.avr_restaurant_image)
        restaurantCoverImage = findViewById(R.id.avr_add_restaurant_cover_image)
        iv_add_img = findViewById(R.id.avr_add_restaurant_image)
        avr_edit_icon = findViewById(R.id.avr_edit_icon)
        avr_delete_icon = findViewById(R.id.avr_delete_icon)

        // for binding add restaurant details
        addRestaurantName = findViewById(R.id.avr_add_restaurant_name)
        addRestaurantDescription = findViewById(R.id.avr_add_restaurant_description)
        addPhone = findViewById(R.id.avr_add_restaurant_phone_number)
        addAddress = findViewById(R.id.avr_add_restaurant_address)
        addOpeningTime = findViewById(R.id.avr_add_restaurant_opening_time_schedule)
        addClosingTime = findViewById(R.id.avr_add_restaurant_closing_time_schedule)
        buttonContainerLayout = findViewById(R.id.avr_buttonContainerLayout)
        map = findViewById(R.id.avr_map_address)


        // for binding view restaurant details
        viewRestaurantName = findViewById(R.id.avr_view_restaurant_name)
        viewRestaurantDescription = findViewById(R.id.avr_view_restaurant_description)
        viewPhone = findViewById(R.id.avr_view_restaurant_phone_number)
        viewAddress = findViewById(R.id.avr_view_restaurant_address)
        viewOpeningTime = findViewById(R.id.avr_view_restaurant_opening_time_schedule)
        viewClosingTime = findViewById(R.id.avr_view_restaurant_closing_time_schedule)

        // for container layout
        viewRestaurantDetailsLayout = findViewById(R.id.avr_view_restaurant_details_layout)
        addRestaurantDetailsLayout = findViewById(R.id.avr_add_restaurant_details_layout)

        // for bottom container layout
        addAdd = findViewById(R.id.avr_btnAddRestaurant)
        btnCancel = findViewById(R.id.avr_btnCancel)

        val hourList = mutableListOf<String>()

        val minuteList = mutableListOf<String>()

        val categoryList = mutableListOf<String>("AM", "PM")

        generateList(hourList,minuteList)

        avr_edit_icon.setOnClickListener {
            isEditModeEnabled=true
            setEditMode()
        }

        avr_delete_icon.setOnClickListener {
            showDeleteAlertDialog(requiredRestaurantId)
        }

        iv_add_img.setOnClickListener {
            if (isEditModeEnabled){
                isUserWantToUploadImage=true
                loadPopUpMenu(iv_add_img)
            }
        }

        btnCancel.setOnClickListener {
            isEditModeEnabled=false
            setEditMode()
        }

        addOpeningTime.setOnClickListener {
            isOpeningTimeSelected=true
            openTimePickerDialog(hourList,minuteList, categoryList)
        }

        addClosingTime.setOnClickListener {
            isOpeningTimeSelected=false
            openTimePickerDialog(hourList,minuteList, categoryList)
        }

        restaurantCoverImage.setOnClickListener {
            if (isEditModeEnabled){
                isUserWantToUploadCoverImage=true
                loadPopUpMenu(restaurantCoverImage)
            }
        }

        map.setOnClickListener {
            val intent = Intent(this,AddressPickerActivity::class.java)
            intent.putExtra(AddressPickerActivity.ARG_LAT_LNG,MyLatLng(27.678616119670615, 84.4358566124604))
            val pinList=ArrayList<Pin>()
            pinList.add(Pin(MyLatLng(27.678579704941686, 84.42964182567329),"Bharatpur Airport"))
            intent.putExtra(AddressPickerActivity.ARG_LIST_PIN,  pinList)
            intent.putExtra(AddressPickerActivity.ARG_ZOOM_LEVEL,  18.0f)
            startActivityForResult(intent, AddShopActivity.REQUEST_ADDRESS)
        }

        addAdd.setOnClickListener {
            updateRestaurantDetails(requiredRestaurantId)
            isEditModeEnabled=false
            setEditMode()
        }

    }

    private fun getRestaurantDetailsById(restaurantId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val shopRepository= ShopRepository()
                val response=shopRepository.getRestaurantById(restaurantId)
                if(response.success==true){
                    val restaurant = response.data!!
                    withContext(Dispatchers.Main){
                        Glide.with(this@ViewShopActivity).load(restaurant.image).into(restaurantImage)

                        Glide.with(this@ViewShopActivity).load(restaurant.image).into(restaurantImage)
                        if (restaurant.coverImage!=""){
                            Glide.with(this@ViewShopActivity).load(restaurant.coverImage).into(restaurantCoverImage)
                        }
                        viewRestaurantName.text = restaurant.name
                        viewRestaurantDescription.text = restaurant.description
                        viewPhone.text = restaurant.phone
                        viewAddress.text = restaurant.address
                        viewOpeningTime.text = restaurant.openingTime
                        viewClosingTime.text = restaurant.closingTime

                        addRestaurantName.setText(restaurant.name)
                        addRestaurantDescription.setText(restaurant.description)
                        addPhone.setText(restaurant.phone)
                        addAddress.setText(restaurant.address)
                        addOpeningTime.setText(restaurant.openingTime)
                        addClosingTime.setText(restaurant.closingTime)

                    }
                }
            }catch (ex:Exception){
                print(ex)
            }
        }
    }

    private fun updateRestaurantDetails(restaurantId: String) {
        val restaurant = Restaurant(
            name=addRestaurantName.text.toString(),
            description  =addRestaurantDescription.text.toString(),
            phone=addPhone.text.toString(),
            openingTime = addOpeningTime.text.toString(),
            closingTime = addClosingTime.text.toString(),
            address = addAddress.text.toString(),
        )
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val shopRepository = ShopRepository()
                val response = shopRepository.updateRestaurantDetails(restaurantId,restaurant)
                if (response.success == true) {
                    withContext(Dispatchers.Main) {
                        viewRestaurantName.text=addRestaurantName.text.toString()
                        viewRestaurantDescription.text=addRestaurantDescription.text.toString()
                        viewPhone.text=addPhone.text.toString()
                        viewAddress.text=addAddress.text.toString()
                        viewOpeningTime.text=addOpeningTime.text.toString()
                        viewClosingTime.text=addClosingTime.text.toString()
                        Toast.makeText(this@ViewShopActivity, "Restaurant Details Updated", Toast.LENGTH_SHORT).show()
                    }
                }
                if (restaurantImageUrl != null) {
                    updateRestaurantImage(restaurantId)
                    super.onBackPressed()
                }
                if (restaurantCoverImageUrl != null) {
                    updateRestaurantCoverImage(restaurantId)
                    super.onBackPressed()
                }

            } catch (ex: Exception) {
                print(ex)
            }
        }
    }

    private fun updateRestaurantImage(restaurantId:String) {
        if (restaurantImageUrl != null) {
            val file = File(restaurantImageUrl!!)
            val mimeType = getMimeType(file.path);
            val reqFile = RequestBody.create(mimeType!!.toMediaTypeOrNull(), file)
            val body = MultipartBody.Part.createFormData("image", file.name, reqFile)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val shopRepository = ShopRepository()
                    shopRepository.updateRestaurantImage(restaurantId,body)
                    getRestaurantDetailsById(restaurantId)
                } catch (ex: Exception) {
                    withContext(Dispatchers.Main) {
                        print(ex)
                    }
                }
            }

        }
    }

    private fun updateRestaurantCoverImage(restaurantId:String) {
        if (restaurantCoverImageUrl != null) {
            val file = File(restaurantCoverImageUrl!!)
            val mimeType = getMimeType(file.path);
            val reqFile = RequestBody.create(mimeType!!.toMediaTypeOrNull(), file)
            val body = MultipartBody.Part.createFormData("image", file.name, reqFile)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val shopRepository = ShopRepository()
                    shopRepository.updateRestaurantCoverImage(restaurantId,body)
                    getRestaurantDetailsById(restaurantId)
                } catch (ex: Exception) {
                    withContext(Dispatchers.Main) {
                        print(ex)
                    }
                }
            }

        }
    }

    private fun setEditMode() {
        if (isEditModeEnabled){
            // list of things for their visibility ON
            iv_add_img.show()
            addRestaurantName.show()
            addRestaurantDescription.show()
            addRestaurantDetailsLayout.show()
            buttonContainerLayout.show()

            // list of things for their visibility OFF
            viewRestaurantName.hide()
            viewRestaurantDescription.hide()
            viewRestaurantDetailsLayout.hide()
        } else {
            // list of things for their visibility ON
            viewRestaurantName.show()
            viewRestaurantDescription.show()
            viewRestaurantDetailsLayout.show()

            // list of things for their visibility OFF
            iv_add_img.hide()
            addRestaurantName.hide()
            addRestaurantDescription.hide()
            addRestaurantDetailsLayout.hide()
            buttonContainerLayout.hide()
        }
    }

    private fun generateList(hourList: MutableList<String>, minuteList: MutableList<String>) {
        for (hour in 1..12){
            if(hour.toString().count()==1) {
                hourList.add("0$hour")
            }
            if (hour.toString().count()==1){
                continue
            }
            hourList.add(hour.toString())
        }

        for (minute in 1..61){
            if(minute%5==0) {
                if(minute.toString() == "5") {
                    minuteList.add("0$minute")
                }
                if (minute==5){
                    continue
                }
                minuteList.add(minute.toString())
            }
        }
    }

    private fun openTimePickerDialog(hourList:MutableList<String>,minuteList:MutableList<String>,categoryList:MutableList<String>) {
        val timeDialog = Dialog(this)
        timeDialog.setContentView(R.layout.time_picker_dialog)
        timeDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val hourPicker:NumberPicker = timeDialog.findViewById(R.id.hourPicker)
        val minutePicker:NumberPicker = timeDialog.findViewById(R.id.minutePicker)
        val categoryPicker:NumberPicker = timeDialog.findViewById(R.id.categoryPicker)
        val openingTime: TextView = timeDialog.findViewById(R.id.tpd_opening_time)
        val closingTime: TextView = timeDialog.findViewById(R.id.tpd_closing_time)
        val cancel: Button = timeDialog.findViewById(R.id.tpd_cancel)
        val btnOk: Button = timeDialog.findViewById(R.id.tpd_ok)

        hourPicker.minValue = 0
        hourPicker.maxValue = hourList.size - 1
        hourPicker.displayedValues = hourList.toTypedArray()
        hourPicker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        minutePicker.minValue = 0
        minutePicker.maxValue = minuteList.size - 1
        minutePicker.displayedValues = minuteList.toTypedArray()
        minutePicker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        categoryPicker.minValue = 0
        categoryPicker.maxValue = categoryList.size - 1
        categoryPicker.displayedValues = categoryList.toTypedArray()
        categoryPicker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        // set value
        openingTime.text=addOpeningTime.text
        closingTime.text=addClosingTime.text

        cancel.setOnClickListener {
            timeDialog.dismiss()
        }

        btnOk.setOnClickListener {
            if (isOpeningTimeSelected){
                val selectedOpeningTime = convertToTime(hourList[hourPicker.value],minuteList[minutePicker.value],categoryList[categoryPicker.value])
                openingTime.text = selectedOpeningTime
                addOpeningTime.setText(selectedOpeningTime)
            } else {
                val selectedClosingTime = convertToTime(hourList[hourPicker.value],minuteList[minutePicker.value],categoryList[categoryPicker.value])
                closingTime.text = selectedClosingTime
                addClosingTime.setText(selectedClosingTime)
            }
            timeDialog.dismiss()
        }
        timeDialog.show()
    }

    private fun convertToTime(hour: String, minute: String, category: String): String {
        return "$hour:$minute $category"
    }

    // Load pop up menu
    private fun loadPopUpMenu(where: View) {
        val popupMenu = PopupMenu(this, where)
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
    private var restaurantImageUrl: String? = null
    private var restaurantCoverImageUrl: String? = null

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
            val featureName: String = address.featureName
            val subAdminArea: String = address.subAdminArea
            val countryName: String = address.countryName
            addAddress.setText("$featureName, $cityName, $subAdminArea, $countryName")
        }


        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_GALLERY_CODE && data != null) {
                val selectedImage: Uri? = data.data
                val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                val contentResolver = contentResolver
                val cursor = contentResolver.query(selectedImage!!, filePathColumn, null, null, null)
                cursor!!.moveToFirst()
                val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                if (isUserWantToUploadCoverImage){
                    restaurantCoverImageUrl = cursor.getString(columnIndex)
                    restaurantCoverImage.setImageBitmap(BitmapFactory.decodeFile(restaurantCoverImageUrl))
                    isUserWantToUploadCoverImage=false
                }
                if(isUserWantToUploadImage){
                    restaurantImageUrl = cursor.getString(columnIndex)
                    restaurantImage.setImageBitmap(BitmapFactory.decodeFile(restaurantImageUrl))
                    isUserWantToUploadImage=false
                }
                cursor.close()

            }
            else if (requestCode == REQUEST_CAMERA_CODE && data != null) {
                val imageBitmap = data.extras?.get("data") as Bitmap
                val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                val file = bitmapToFile(imageBitmap, "$timeStamp.jpg")
                if (isUserWantToUploadCoverImage){
                    restaurantCoverImageUrl = file!!.absolutePath
                    restaurantCoverImage.setImageBitmap(BitmapFactory.decodeFile(restaurantCoverImageUrl))
                    isUserWantToUploadCoverImage=false
                }
                if(isUserWantToUploadImage){
                    restaurantImageUrl = file!!.absolutePath
                    restaurantImage.setImageBitmap(BitmapFactory.decodeFile(restaurantImageUrl))
                    isUserWantToUploadImage=false
                }
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

    private fun bitmapToFile( bitmap: Bitmap, fileNameToSave: String ): File? {
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

    private fun showDeleteAlertDialog(restaurantId:String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Restaurant?")
        builder.setMessage("Are you sure want to Delete?")
        builder.setIcon(R.drawable.ic_delete)
        //performing Positive action
        builder.setPositiveButton("Yes") { _, _ ->
            deleteRestaurant(restaurantId)
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

    private fun deleteRestaurant(restaurantId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val shopRepository = ShopRepository()
                val response = shopRepository.deleteRestaurantDetails(restaurantId)
                if (response.success == true) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@ViewShopActivity, "Restaurant Deleted Successfully", Toast.LENGTH_SHORT).show()
                        super.onBackPressed()
                        finish()
                    }
                }

            } catch (ex: Exception) {
                print(ex)
            }
        }
    }

}