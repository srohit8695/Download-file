package com.example.downloadfilefromretrofit

internal interface IDownload {
    fun download()
    fun cancelDownload()
    fun pauseDownload()
    fun resumeDownload()
}