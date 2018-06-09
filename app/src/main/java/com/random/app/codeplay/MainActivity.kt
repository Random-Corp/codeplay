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
import android.app.ProgressDialog
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

import android.os.Looper

import android.webkit.JavascriptInterface
import android.webkit.WebView

import com.google.android.gms.common.util.IOUtils
import com.google.gson.GsonBuilder
import okhttp3.*
import org.json.JSONObject
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.concurrent.Executor
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {

    private val PERMISSION_WRITE_REQUEST = 1
    private val RC_CHOOSE_PHOTO = 1
    private val RC_TAKE_PHOTO = 2
    private val INDEX_FILE = "file:///android_asset/index.html"

    lateinit var handler:Handler

    var PICK_IMAGE_MULTIPLE = 1
    private lateinit var progressDialog: ProgressDialog

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
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
//        handler = Handler()
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
                processImageFromMSFTAzure(imageFile!!)


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
        WebView.setWebContentsDebuggingEnabled(true)
        codeView.settings.javaScriptEnabled = true
        codeView.loadUrl(INDEX_FILE)
        codeView.addJavascriptInterface(this, "Android")
    }

    private fun displayCode(code: String) {
        progressDialog.dismiss()
        val url = "javascript:loadCode('$code');"
        codeView.loadUrl(url)

    }

    private fun processImageFromMSFTAzure(imageFile:File) {

        progressDialog.setMessage("Loading image..")
        progressDialog.show()

        val client  = OkHttpClient()


        val request : Request = Request.Builder()
                .url("https://westcentralus.api.cognitive.microsoft.com/vision/v2.0/recognizeText?mode=Handwritten")
                .header("Content-Type", "application/octet-stream")
                .header("Ocp-Apim-Subscription-Key", "***REMOVED***")
                .post(RequestBody.create(null,IOUtils.toByteArray(imageFile)))
                .build()




            try {
                client.newCall(request).enqueue(object :  okhttp3.Callback {
                    override fun onFailure(call: okhttp3.Call?, e: IOException?) {

                    }

                    override fun onResponse(call: okhttp3.Call?, response: okhttp3.Response?) {
                        runOnUiThread{
                            Toast.makeText(this@MainActivity,"Processing",Toast.LENGTH_SHORT).show()
                        }

                        if(response?.code() == 202)
                        {
                            runOnUiThread { progressDialog.setMessage("Processing image..") }
                            Handler(Looper.getMainLooper()).postDelayed({
                                var request = Request.Builder()
                                        .url(response.headers().get("Operation-Location"))
                                        .header("Content-Type", "application/json")
                                        .header("Ocp-Apim-Subscription-Key", "***REMOVED***")
                                        .build()

                        client.newCall(request).enqueue(object : okhttp3.Callback {
                            override fun onFailure(call: okhttp3.Call?, e: IOException?) {

                            }
                            override fun onResponse(call:okhttp3.Call?, response: okhttp3.Response?) {
                                Log.d("json",response?.body().toString())
                                var responseBody = response?.body()
                                var jsonObject = JSONObject(responseBody?.string())
                                var gson = GsonBuilder().setPrettyPrinting().create();
	                            var json = gson.toJson(jsonObject);

                                var recognitionResultObj = jsonObject.getJSONObject("recognitionResult")
                                var linesArray = recognitionResultObj.getJSONArray("lines")
                                var code: String = ""
                                for (i in 0..linesArray.length()-1) {
                                    code += ((linesArray[i] as JSONObject)["text"] as String) + "\n"
                                }
                                Log.d("json",code)

                                runOnUiThread{
                                    displayCode(code)
                                }



                            }

                        })
                    }, 5000)
                        }
                    }

                })

            }catch (e: IOException){
                Log.d("fsdsdsds", e.message)
            }




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

    @JavascriptInterface
    fun deleteSnippets() {
        codePlayDB.getSnippetDao().nuke()
    }


}

