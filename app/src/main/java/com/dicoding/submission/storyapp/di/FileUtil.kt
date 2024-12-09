package com.dicoding.submission.storyapp.di

import android.content.ContentResolver
import android.net.Uri
import java.io.File

object FileUtil {
    fun fromUri(contentResolver: ContentResolver, uri: Uri): File {
        val inputStream = contentResolver.openInputStream(uri) ?: throw IllegalArgumentException("File not found")
        val file = File.createTempFile("upload", ".jpg", null)
        inputStream.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file
    }
}