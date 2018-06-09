package com.random.app.codeplay

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log.d
import android.view.View
import com.getbase.floatingactionbutton.FloatingActionButton
import com.getbase.floatingactionbutton.FloatingActionsMenu
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.os.Build
import android.widget.Toast
import java.nio.file.Files.size
import android.app.Activity
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.ClipData
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.nio.file.Files.size
import java.util.*
import kotlin.math.log

import android.graphics.BitmapRegionDecoder
import android.graphics.Rect
import android.webkit.JavascriptInterface
import com.google.android.gms.common.util.IOUtils
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import java.io.File
import java.io.FileInputStream


class MainActivity : AppCompatActivity() {

    private val PERMISSION_WRITE_REQUEST = 1
    private val RC_CHOOSE_PHOTO = 1
    private val RC_TAKE_PHOTO = 2
    private lateinit var fabAddGallery: FloatingActionButton
    private lateinit var fabAddCamera: FloatingActionButton
    private val INDEX_FILE = "file:///android_asset/index.html"

    var PICK_IMAGE_MULTIPLE = 1

    lateinit var codePlayDB:CodePlayDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupPermissions()
        initFam()
        codePlayDB = Room.databaseBuilder(this,
                CodePlayDB::class.java, "codeplaydb")
        .fallbackToDestructiveMigration().build()
        initWebView()
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            /*if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Permission to access the microphone is required for this app to record audio.")
                        .setTitle("Permission required")

                builder.setPositiveButton("OK"
                ) { dialog, id ->
                    d("Clicked")
                    makeRequest()
                }

                val dialog = builder.create()
                dialog.show()*/
            makeRequest()
        } else {
//            startActivity(Intent(this, MainActivity::class.java))
//            finish()
        }

    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
                PERMISSION_WRITE_REQUEST)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_WRITE_REQUEST -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    d("Main", "Permission has been denied by user")
                } else {
                    d("Main", "Permission has been granted by user")
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
        }
    }

    private fun initFam() {
        fbAdd.setOnClickListener { EasyImage.openChooserWithDocuments(this, "Choose from", PICK_IMAGE_MULTIPLE) }
//        famAdd.setOnFloatingActionsMenuUpdateListener(object : FloatingActionsMenu.OnFloatingActionsMenuUpdateListener {
//            override fun onMenuExpanded() {
//                rlFamBg.visibility = View.VISIBLE
//            }
//
//            override fun onMenuCollapsed() {
//                rlFamBg.visibility = View.GONE
//            }
//        })
//
//
//        fabAddGallery = com.getbase.floatingactionbutton.FloatingActionButton(this)
//        fabAddGallery.title = "Gallery"
//        fabAddGallery.setIcon(R.drawable.ic_collections_black_24dp)
//        fabAddGallery.colorNormal = ContextCompat.getColor(this, R.color.colorAccent)
//        fabAddGallery.setOnClickListener { takePictureFromGallery() }
//        fabAddGallery.size = com.getbase.floatingactionbutton.FloatingActionButton.SIZE_MINI
//
//        fabAddCamera = com.getbase.floatingactionbutton.FloatingActionButton(this)
//        fabAddCamera.title = "Camera"
//        fabAddCamera.setIcon(R.drawable.ic_photo_camera_black_24dp)
//        fabAddCamera.colorNormal = ContextCompat.getColor(this, R.color.colorAccent)
//        fabAddCamera.setOnClickListener{ }
//        fabAddCamera.size = com.getbase.floatingactionbutton.FloatingActionButton.SIZE_MINI
//
//        famAdd.addButton(fabAddGallery)
//        famAdd.addButton(fabAddCamera)
    }

    private fun takeFromCamera() {

    }

    private fun takePictureFromGallery() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            makeRequest()
        } else {

            val intent = Intent()
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            intent.action = Intent.ACTION_OPEN_DOCUMENT
            //            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addFlags(FLAG_GRANT_READ_URI_PERMISSION)
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE)
        }

    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


        EasyImage.handleActivityResult(requestCode, resultCode, data, this, object : DefaultCallback() {
            override fun onImagePicked(imageFile: File?, source: EasyImage.ImageSource?, type: Int) {

//                val inputStream = FileInputStream(imageFile)
                val byteArray = IOUtils.toByteArray(imageFile)
                processImageFromMSFTAzure(byteArray)

//                val mImageUri = imageFile?.pa
////                    val decoder = BitmapRegionDecoder.newInstance(mImageUri.toString(), false)
////                    val region = decoder.decodeRegion(Rect(10, 10, 50, 50), null)
//                val options = BitmapFactory.Options()
//                options.inPreferredConfig = Bitmap.Config.ARGB_8888
//                val bitmap = BitmapFactory.decodeStream(FileInputStream(imageFile),null,options)
////                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, mImageUri)
//                    processBitmap(bitmap)
            }

        })


        super.onActivityResult(requestCode, resultCode, data)
    }


    private fun processBitmap(bitmap: Bitmap) {
        Log.d("Result", " uri :${bitmap.height}")

        val visionImage = FirebaseVisionImage.fromBitmap(bitmap)

        val textDetector = FirebaseVision.getInstance()
                .visionTextDetector

        val result = textDetector.detectInImage(visionImage)
                .addOnSuccessListener {
                    var fullString = ""
                    for (block in it.getBlocks()) {
                        fullString = fullString + block.text + " \n "
                    }
                    Log.d("full", fullString)
                }
                .addOnFailureListener { }

        displayCode("(+ 45 56)")
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        codeView.settings.javaScriptEnabled = true
        codeView.loadUrl(INDEX_FILE)
        codeView.addJavascriptInterface(this, "Android")
    }

    private fun displayCode(code: String) {
        val url = "javascript:loadCode('$code');"
        codeView.loadUrl(url)

    }

    private fun processImageFromMSFTAzure(data: ByteArray) {
        //pass it like this

//        var requestFile = RequestBody.create(MediaType.parse("image/*"), file);

        // MultipartBody.Part is used to send also the actual file name
//        var body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
//        var imageBody = RequestBody.create(MediaType.parse("image"), file);
//        val `in` = FileInputStream(File(file.getPath()))
//        val buf: ByteArray
//        buf = ByteArray(`in`.available())
//        while (`in`.read(buf) !== -1);
//        val requestBody = RequestBody
//                .create(MediaType.parse("application/octet-stream"), buf)
        val requestBody = RequestBody
                .create(MediaType.parse("application/octet-stream"), data)



        val retrofit = Retrofit.Builder()
                .baseUrl("https://westcentralus.api.cognitive.microsoft.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        var networkService = retrofit.create(NetworkService::class.java)

        val postImage = networkService.azurePostImage(requestBody)
//        val response = postImage.execute()
        postImage.enqueue(object : Callback<Response<ResponseBody>> {
            override fun onFailure(call: Call<Response<ResponseBody>>?, t: Throwable?) {
            }

            override fun onResponse(call: Call<Response<ResponseBody>>?, response: Response<Response<ResponseBody>>?) {
                if (response?.code() == 202) {
                    Handler().postDelayed({

                        val getImageTextDetails = networkService.azureGetImageText(response.headers().get("Operation-Location"))
                        getImageTextDetails.enqueue(object : Callback<Response<ResponseBody>> {
                            override fun onFailure(call: Call<Response<ResponseBody>>?, t: Throwable?) {

                            }

                            override fun onResponse(call: Call<Response<ResponseBody>>?, response: Response<Response<ResponseBody>>?) {
                                Log.d("result", "")
                            }

                        })
                    }, 10000)
                }
            }

        })


    }

    @JavascriptInterface
    fun saveSnippet(name:String, snippet:String) {
        codePlayDB.getSnippetDao().saveSnippet(Snippet(name, snippet))
    }

    @JavascriptInterface
    fun loadSnippet():String {
        val gson = Gson()
        val list = codePlayDB.getSnippetDao().getAllSnippets()
        list?.let { return gson.toJson(it) }
        return ""
    }


}
