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
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.hdd.instaFashion.R
import com.hdd.instaFashion.presentation.activity.AddressPickerActivity.Companion.RESULT_ADDRESS
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

class AddShopActivity : AppCompatActivity() {

    companion object {
        val REQUEST_ADDRESS = 132
    }

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

    private lateinit var addAdd: Button
    private lateinit var btnCancel: Button

    private lateinit var iv_add_img: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_store)

        // for binding add restaurant details
        addRestaurantName = findViewById(R.id.aar_add_restaurant_name)
        addRestaurantDescription = findViewById(R.id.aar_add_restaurant_description)
        addPhone = findViewById(R.id.aar_add_restaurant_phone_number)
        addAddress = findViewById(R.id.aar_add_restaurant_address)
        addOpeningTime = findViewById(R.id.aar_add_restaurant_opening_time_schedule)
        addClosingTime = findViewById(R.id.aar_add_restaurant_closing_time_schedule)
        map = findViewById(R.id.aar_map_address)

        restaurantImage = findViewById(R.id.aar_restaurant_image)
        restaurantCoverImage = findViewById(R.id.aar_add_restaurant_cover_image)
        iv_add_img = findViewById(R.id.aar_add_restaurant_image)

        addAdd = findViewById(R.id.aar_btnAddRestaurant)
        btnCancel = findViewById(R.id.aar_btnCancel)

        val hourList = mutableListOf<String>()

        val minuteList = mutableListOf<String>()

        val categoryList = mutableListOf<String>("AM", "PM")

        generateList(hourList,minuteList)

        iv_add_img.setOnClickListener {
            loadPopUpMenu()
        }

        btnCancel.setOnClickListener {
            super.onBackPressed()
        }

        addOpeningTime.setOnClickListener {
            isOpeningTimeSelected=true
            openTimePickerDialog(hourList,minuteList, categoryList)
        }

        addClosingTime.setOnClickListener {
            isOpeningTimeSelected=false
            openTimePickerDialog(hourList,minuteList, categoryList)
        }

        map.setOnClickListener {
            val intent = Intent(this,AddressPickerActivity::class.java)
            intent.putExtra(AddressPickerActivity.ARG_LAT_LNG,MyLatLng(27.678616119670615, 84.4358566124604))
            val pinList=ArrayList<Pin>()
            pinList.add(Pin(MyLatLng(27.678579704941686, 84.42964182567329),"Bharatpur Airport"))
            intent.putExtra(AddressPickerActivity.ARG_LIST_PIN,  pinList)
            intent.putExtra(AddressPickerActivity.ARG_ZOOM_LEVEL,  18.0f)
            startActivityForResult(intent,REQUEST_ADDRESS)
        }

        addAdd.setOnClickListener {
            if (addRestaurantName.text.isEmpty()){
                addRestaurantName.error="Restaurant Name is required"
                addRestaurantName.requestFocus()
            }
            if (addRestaurantDescription.text.isEmpty()){
                addRestaurantDescription.error="Restaurant Description is required"
                addRestaurantDescription.requestFocus()
            }
            if (addPhone.text.isEmpty()){
                addPhone.error="Restaurant Phone is required"
                addPhone.requestFocus()
            }
            if (addAddress.text.isEmpty()){
                addAddress.error="Restaurant Address is required"
                addAddress.requestFocus()
            }
            if (addOpeningTime.text.isEmpty()){
                addOpeningTime.error="Restaurant Opening Time is required"
                addOpeningTime.requestFocus()
            }
            if (addClosingTime.text.isEmpty()){
                addClosingTime.error="Restaurant Closing Time is required"
                addClosingTime.requestFocus()
            }
            addRestaurantDetails(addRestaurantName.text.toString(),addRestaurantDescription.text.toString(),addAddress.text.toString(),addOpeningTime.text.toString(),addClosingTime.text.toString(),addPhone.text.toString())
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

    private fun addRestaurantDetails(restaurantName:String,restaurantDescription: String,restaurantAddress:String,restaurantOpeningTime: String,restaurantClosingTime: String,restaurantPhone:String) {
        if (imageUrl!=null){
            val file = File(imageUrl!!)
            val mimeType = getMimeType(file.path);
            val reqFile = RequestBody.create(mimeType!!.toMediaTypeOrNull(), file)
            val reqRestaurantName = RequestBody.create("multipart/fetch-data".toMediaTypeOrNull(), restaurantName)
            val reqRestaurantDescription = RequestBody.create("multipart/fetch-data".toMediaTypeOrNull(), restaurantDescription)
            val reqRestaurantAddress = RequestBody.create("multipart/fetch-data".toMediaTypeOrNull(), restaurantAddress)
            val reqRestaurantOpeningTime = RequestBody.create("multipart/fetch-data".toMediaTypeOrNull(), restaurantOpeningTime)
            val reqRestaurantClosingTime = RequestBody.create("multipart/fetch-data".toMediaTypeOrNull(), restaurantClosingTime)
            val reqRestaurantPhone = RequestBody.create("multipart/fetch-data".toMediaTypeOrNull(), restaurantPhone)
            val body = MultipartBody.Part.createFormData("image",file.name,reqFile)
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val shopRepository = ShopRepository()
                    shopRepository.addRestaurantDetail(
                        body,
                        reqRestaurantName,
                        reqRestaurantDescription,
                        reqRestaurantAddress,
                        reqRestaurantOpeningTime,
                        reqRestaurantClosingTime,
                        reqRestaurantPhone
                    )
                } catch (ex: Exception) {
                    withContext(Dispatchers.Main) {
                        print(ex)
                    }
                }
            }
            super.onBackPressed()
        } else {
            Toast.makeText(this, "Restaurant Image is required", Toast.LENGTH_SHORT).show()
        }
    }

    // Load pop up menu
    private fun loadPopUpMenu() {
        val popupMenu = PopupMenu(this, iv_add_img)
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

        if (requestCode == REQUEST_ADDRESS && resultCode == Activity.RESULT_OK) {
            val address: Address = data?.getParcelableExtra<Address>(RESULT_ADDRESS)!!
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
                val cursor =
                    contentResolver.query(selectedImage!!, filePathColumn, null, null, null)
                cursor!!.moveToFirst()
                val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                imageUrl = cursor.getString(columnIndex)
                restaurantImage.setImageBitmap(BitmapFactory.decodeFile(imageUrl))
                cursor.close()
            } else if (requestCode == REQUEST_CAMERA_CODE && data != null) {
                val imageBitmap = data.extras?.get("data") as Bitmap
                val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                val file = bitmapToFile(imageBitmap, "$timeStamp.jpg")
                imageUrl = file!!.absolutePath
                restaurantImage.setImageBitmap(BitmapFactory.decodeFile(imageUrl))
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

}