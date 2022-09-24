package com.example.downloadfilefromretrofit.utils

import android.os.Environment
import android.util.Log
import com.example.downloadfilefromretrofit.network.RetrofitInstance
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.net.URI


object DownloadUrlFile {
    val TAG="DownloadUrlFileStatus"
    var retrofitService= RetrofitInstance.api

    fun downloadFileFromUrl(fileUrlString: String){

        try {
            if (fileUrlString.isNotEmpty()) {
                var fileName= File(URI(fileUrlString).path).name
                Log.e(TAG, "Start Download File name: $fileName")
                var call: Call<ResponseBody> = retrofitService.downloadFileWithDynamicUrlSync(fileUrlString)
                Log.e(TAG, "Call api")
                call.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>, response: Response<ResponseBody>,
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            Log.e(TAG, "Server contacted and has file $fileName")

                            val writtenToDisk: Boolean = writeResponseBodyToDisk(
                                response.body()!!,
                                fileName
                            )
                            if (writtenToDisk) {
                                Log.e(TAG, "Success")
                                Log.e(TAG, "File download was a success? $writtenToDisk")
                            } else {
                                Log.e(TAG, "Failed to download file")
                            }

                        } else {
                            Log.d(TAG, "server contact failed")
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.e(TAG, "error")
                    }

                })



            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    //write file in download folder
    private fun writeResponseBodyToDisk(body: ResponseBody, fileName: String): Boolean {
        return try {
            val futureStudioIconFile = File(PublicDownloadStorageDir.getPublicDownloadStorageDir(), fileName)
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {
                val fileReader = ByteArray(4096)
                val fileSize = body.contentLength()
                var fileSizeDownloaded: Long = 0
                inputStream = body.byteStream()
                outputStream = FileOutputStream(futureStudioIconFile)
                while (true) {
                    val read: Int = inputStream.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    outputStream!!.write(fileReader, 0, read)
                    fileSizeDownloaded += read.toLong()
                    //Log.e(TAG, "File download: $fileSizeDownloaded of $fileSize")
                }
                outputStream!!.flush()
                true
            } catch (e: IOException) {
                e.message?.let { Log.e(TAG, it) }
                false
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        }
        catch (e: IOException) {
            e.message?.let { Log.e(TAG, it) }
            false
        }
    }

    @Throws(IOException::class)
    private fun downloadImage(body: ResponseBody) {
        var count: Int
        val data = ByteArray(1024 * 4)
        val fileSize = body.contentLength()
        val inputStream: InputStream = BufferedInputStream(body.byteStream(), 1024 * 8)
        val outputFile =
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "journaldev-image-downloaded.jpg")
        val outputStream: OutputStream = FileOutputStream(outputFile)
        var total: Long = 0
        var downloadComplete = false
        //int totalFileSize = (int) (fileSize / (Math.pow(1024, 2)));
        while (inputStream.read(data).also { count = it } != -1) {
            total += count.toLong()
            val progress = ((total * 100).toDouble() / fileSize.toDouble()).toInt()
            outputStream.write(data, 0, count)
            downloadComplete = true
        }
        outputStream.flush()
        outputStream.close()
        inputStream.close()
    }

}