package com.hdd.instaFashion.presentation.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.hdd.instaFashion.R
import com.hdd.instaFashion.data.models.User
import com.hdd.instaFashion.repository.UserRepository
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

class EditAccountActivity : AppCompatActivity() {

    private lateinit var fullname: TextView
    private lateinit var etFullname: EditText
    private lateinit var progressBar: ProgressBar

    private lateinit var bio: TextView
    private lateinit var userBio: EditText
    private lateinit var phone: EditText
    private lateinit var email: TextView
    private lateinit var address: EditText
    private lateinit var website: EditText
    private lateinit var newPassword: EditText
    private lateinit var oldPassword: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var btnUpdate: Button
    private lateinit var btnCancel: Button

    private lateinit var profileImage: ImageView
    private lateinit var iv_add_img: ImageView

    private var userId: String? = null
    private var requiredPassword: String? = null

    var savedPassword: String? = ""
    var token: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_account)


        fullname = findViewById(R.id.aea_fullname)
        progressBar = findViewById(R.id.aea_pb)

        etFullname = findViewById(R.id.aea_user_full_name)
        bio = findViewById(R.id.aea_bio)
        userBio = findViewById(R.id.aea_user_bio)
        phone = findViewById(R.id.aea_user_phone_number)
        email = findViewById(R.id.aea_user_email)
        address = findViewById(R.id.aea_user_address)
        website = findViewById(R.id.aea_user_website)
        newPassword = findViewById(R.id.aea_new_password)
        oldPassword = findViewById(R.id.aea_old_password)
        confirmPassword = findViewById(R.id.aea_confirm_password)
        btnUpdate = findViewById(R.id.btnUpdateProfile)
        btnCancel = findViewById(R.id.aea_btnCancel)

        profileImage = findViewById(R.id.aea_profile_image)
        iv_add_img = findViewById(R.id.aea_add_profile)

        iv_add_img.setOnClickListener {
            loadPopUpMenu()
        }
        btnCancel.setOnClickListener {
           super.onBackPressed()
        }

        btnUpdate.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            retrieveUserDetails()
            checkForEmailAndPassword()
            updateUser()
        }
        fetchData()

    }

    @SuppressLint("ResourceAsColor")
    private fun checkForEmailAndPassword() {
        if (!TextUtils.isEmpty(oldPassword.text)&& !TextUtils.isEmpty(newPassword.text)&&!TextUtils.isEmpty(confirmPassword.text)) {
            isPasswordChange=true
            if ( newPassword.length() >= 5 && confirmPassword.length() >= 5) {
                if (newPassword.text.toString() == confirmPassword.text.toString()) {
                    requiredPassword = newPassword.text.toString()
                } else {
                    confirmPassword.error = "Password doesn't match"
                    progressBar.visibility=View.INVISIBLE
                }
            } else {
                confirmPassword.error = "Password length should be greater than 4 characters"
                progressBar.visibility=View.INVISIBLE
            }
        } else {
            isPasswordChange=false
        }
    }

    private fun fetchData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userRepository = UserRepository()
                val response = userRepository.getUserProfile()
                if (response.success == true) {
                    val user = response.data!!
                    userId = user._id
                    withContext(Dispatchers.Main) {
                        Glide.with(this@EditAccountActivity).load(user.profile).circleCrop()
                            .into(profileImage)
                        fullname.text = user.fullname
                        etFullname.setText(user.fullname)
                        bio.text = user.bio
                        userBio.setText(user.bio)
                        phone.setText(user.phone)
                        website.setText(user.website)
                        email.text = user.email
                        address.setText(user.address)
                    }
                }
            } catch (ex: Exception) {
                print(ex)
            }
        }
    }

    private fun updateUser() {
        val user = User(
            fullname = etFullname.text.toString(),
            address = address.text.toString(),
            website = website.text.toString(),
            password = requiredPassword,
            bio = userBio.text.toString(),
            phone = phone.text.toString(),
        )
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userRepository = UserRepository()
                val response = userRepository.updateUserProfile(user)
                if (response.success == true) {
                    withContext(Dispatchers.Main) {
                        fullname.text = etFullname.text.toString()
                        progressBar.visibility = View.GONE
                        Toast.makeText(this@EditAccountActivity, "Profile Updated", Toast.LENGTH_SHORT).show()
                    }
                }
                if (imageUrl != null) {
                    uploadImage()
                    super.onBackPressed()
                }

                if (isPasswordChange) {
                    if (oldPassword.text.toString()==savedPassword){
                    changePassword(User(oldPassword = savedPassword, newPassword = requiredPassword!!))
                    } else{
                        oldPassword.error = "Old Password doesn't match"
                        progressBar.visibility=View.INVISIBLE
                    }
                }

            } catch (ex: Exception) {
                print(ex)
            }
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
    private var isPasswordChange: Boolean = false

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
                profileImage.setImageBitmap(BitmapFactory.decodeFile(imageUrl))
                cursor.close()
            } else if (requestCode == REQUEST_CAMERA_CODE && data != null) {
                val imageBitmap = data.extras?.get("data") as Bitmap
                val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                val file = bitmapToFile(imageBitmap, "$timeStamp.jpg")
                imageUrl = file!!.absolutePath
                profileImage.setImageBitmap(BitmapFactory.decodeFile(imageUrl))
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

    private fun uploadImage() {
        if (imageUrl != null) {
            val file = File(imageUrl!!)
            val mimeType = getMimeType(file.path);
//            val reqFile = RequestBody.create(mimeType?.let { it.toMediaTypeOrNull() }, file)
//            val reqFile = RequestBody.create(mimeType!!.toMediaTypeOrNull(), file)  // activity
            val reqFile = RequestBody.create(mimeType!!.toMediaTypeOrNull(), file)   // fragment
            val body = MultipartBody.Part.createFormData("profile", file.name, reqFile)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val userRepository = UserRepository()
                    val response = userRepository.uploadImage(body)
                    if (response.success == true) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@EditAccountActivity, "Uploaded", Toast.LENGTH_SHORT)
                                .show()
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

    // space for change password
    private fun changePassword(user: User){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userRepository = UserRepository()
                val response = userRepository.changePassword(user)
                if (response.success == true) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@EditAccountActivity, "Session has expired, Login Again!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@EditAccountActivity, LoginActivity::class.java))
                        Toast.makeText(this@EditAccountActivity, "Password Changed", Toast.LENGTH_SHORT).show()
                        val sharedPreferences = getSharedPreferences("userAuth", MODE_PRIVATE);
                        val editor = sharedPreferences.edit()
//                        editor.putString("password", "")
//                        editor.putString("token", "")
                        editor.clear()
                        editor.apply()
                        finish()
                    }
                }
            } catch (ex: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditAccountActivity,ex.toString(),Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun retrieveUserDetails() {
        val sharedPreferences = getSharedPreferences("userAuth", MODE_PRIVATE)
        savedPassword = sharedPreferences.getString("password", "")
    }

    // space for sensor
    //TODO: Implement sensor goes here
}