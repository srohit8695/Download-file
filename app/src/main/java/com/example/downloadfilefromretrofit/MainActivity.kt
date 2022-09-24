package com.example.downloadfilefromretrofit

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.downloadfilefromretrofit.databinding.ActivityMainBinding
import com.example.downloadfilefromretrofit.utils.DownloadUrlFile
import java.io.File

class MainActivity : AppCompatActivity() {

    private var downloader: Downloader? = null
    lateinit var binding: ActivityMainBinding
    var PERMISSION_MULTILE_REQUEST=101
    private val handler: Handler = Handler()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPermission()

        binding.pauseDownload.setOnClickListener{
            downloader?.pauseDownload()
        }

        binding.resumeDownload.setOnClickListener{
            getDownloader()
            downloader?.resumeDownload()
        }

        binding.startDownload.setOnClickListener {
//            DownloadUrlFile.downloadFileFromUrl("https://speed.hetzner.de/100MB.bin")

            try {
                getDownloader()
                downloader?.download()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }




}

    private fun getDownloader() {
        downloader = Downloader.Builder(
            this,
            "https://speed.hetzner.de/100MB.bin"
        ).downloadListener(object : OnDownloadListener {
            override fun onStart() {
                handler.post {  }
            }

            override fun onPause() {
//                handler.post { current_status_txt.text = "onPause" }
            }

            override fun onResume() {
//                handler.post { current_status_txt.text = "onResume" }
            }

            override fun onProgressUpdate(percent: Int, downloadedSize: Int, totalSize: Int) {
                handler.post {
//                    current_status_txt.text = "onProgressUpdate"
                    binding.percentTxt.text = percent.toString().plus("%")
                    binding.sizeTxt.text = getSize(downloadedSize)
                    binding.totalSizeTxt.text = getSize(totalSize)
                    binding.downloadProgress.progress = percent
                }
            }

            override fun onCompleted(file: File?) {
//                handler.post { current_status_txt.text = "onCompleted" }
//                Log.d(TAG, "onCompleted: file --> $file")
            }

            override fun onFailure(reason: String?) {
//                handler.post { current_status_txt.text = "onFailure: reason --> $reason" }
//                Log.d(TAG, "onFailure: reason --> $reason")
            }

            override fun onCancel() {
//                handler.post { current_status_txt.text = "onCancel" }
//                Log.d(TAG, "onCancel")
            }
        }).build()
    }

    fun getSize(size: Int): String {
        var s = ""
        val kb = (size / 1024).toDouble()
        val mb = kb / 1024
        val gb = kb / 1024
        val tb = kb / 1024
        if (size < 1024) {
            s = "$size Bytes"
        } else if (size >= 1024 && size < 1024 * 1024) {
            s = String.format("%.2f", kb) + " KB"
        } else if (size >= 1024 * 1024 && size < 1024 * 1024 * 1024) {
            s = String.format("%.2f", mb) + " MB"
        } else if (size >= 1024 * 1024 * 1024 && size < 1024 * 1024 * 1024 * 1024) {
            s = String.format("%.2f", gb) + " GB"
        } else if (size >= 1024 * 1024 * 1024 * 1024) {
            s = String.format("%.2f", tb) + " TB"
        }
        return s
    }

    private fun checkPermission(){
        if( ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)+
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED){
            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
                Toast.makeText(this,"\"Read write permission not granted! \\nPlease grant permission to proceed further.\"",Toast.LENGTH_SHORT).show()
                requestPermissions(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), PERMISSION_MULTILE_REQUEST
                )
            }
        }

    }


    }









