package com.sifedin.tinderclone.viewmodel

import android.app.Activity
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import java.util.concurrent.TimeUnit

class AuthViewModel() : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    private val CLOUDINARY_CLOUD_NAME = "dqrzpu6xw"
    private val CLOUDINARY_UPLOAD_PRESET = "hola_dating_app_preset"

    var phoneNumber by mutableStateOf("")
    var verificationCode by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    var name by mutableStateOf("")
    var gender by mutableStateOf<String?>(null)
    var birthday by mutableStateOf<String?>(null)
    var interestedIn by mutableStateOf<String?>(null)

    var photoUris by mutableStateOf<List<Uri>>(emptyList())
    private var verificationId: String? = null

    fun sendVerificationCode(
        activity: Activity,
        onCodeSent: () -> Unit
    ) {
        if (phoneNumber.isBlank()) {
            errorMessage = "Please enter your phone number"
            return
        }
        isLoading = true
        errorMessage = null

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d("AuthViewModel", "Auto verification")
                signIn(credential) {}
            }

            override fun onVerificationFailed(e: FirebaseException) {
                isLoading = false
                errorMessage = e.message ?: "Verification failed"
            }

            override fun onCodeSent(
                id: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                verificationId = id
                isLoading = false
                onCodeSent()
            }
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyCode(onSuccess: () -> Unit) {
        val id = verificationId ?: return
        if (verificationCode.isBlank()) {
            errorMessage = "Enter SMS code"
            return
        }

        isLoading = true
        val credential = PhoneAuthProvider.getCredential(id, verificationCode)
        signIn(credential, onSuccess)
    }

    private fun signIn(
        credential: PhoneAuthCredential,
        onSuccess: () -> Unit
    ) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener {
                isLoading = false
                if (it.isSuccessful) onSuccess()
                else errorMessage = it.exception?.message ?: "Login failed"
            }
    }

    fun saveProfile(onSuccess: () -> Unit) {
        val user = auth.currentUser
        if (user == null || name.isBlank() || gender.isNullOrEmpty() || photoUris.isEmpty() || birthday.isNullOrEmpty() || interestedIn.isNullOrEmpty()) {
            errorMessage = "Profile data incomplete or no photos selected"
            return
        }

        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            val photoUrls = mutableListOf<String>()
            val context = (storage as FirebaseStorage).app.applicationContext

            try {
                for (uri in photoUris) {
                    val url = uploadPhotoToCloudinary(context, uri)
                    photoUrls.add(url)
                }

                withContext(Dispatchers.Main) {
                    saveProfileDocument(user.uid, photoUrls, onSuccess)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isLoading = false
                    errorMessage = "Upload failed: ${e.message}"
                    Log.e("AuthViewModel", "Cloudinary Upload Error", e)
                }
            }
        }
    }

    private suspend fun uploadPhotoToCloudinary(context: android.content.Context, uri: Uri): String = withContext(Dispatchers.IO) {
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val tempFile = File(context.cacheDir, "${UUID.randomUUID()}.jpg")
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(tempFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("upload_preset", CLOUDINARY_UPLOAD_PRESET)
            .addFormDataPart(
                "file",
                tempFile.name,
                tempFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            )
            .build()

        val request = Request.Builder()
            .url("https://api.cloudinary.com/v1_1/$CLOUDINARY_CLOUD_NAME/image/upload")
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                tempFile.delete()
                val errorBody = response.body?.string() ?: ""
                throw Exception("Cloudinary upload failed: ${response.code}. Details: $errorBody")
            }

            val responseBody = response.body?.string() ?: throw Exception("Empty response from Cloudinary")
            val json = JSONObject(responseBody)

            tempFile.delete()

            return@withContext json.getString("secure_url")
        }
    }

    private fun saveProfileDocument(
        uid: String,
        photoUrls: List<String>,
        onSuccess: () -> Unit
    ) {
        val profileData = hashMapOf(
            "name" to name,
            "gender" to gender,
            "birthday" to birthday,
            "interestedin" to interestedIn,
            "phoneNumber" to phoneNumber,
            "photos" to photoUrls,
            "createdAt" to FieldValue.serverTimestamp()
        )

        firestore.collection("users")
            .document(uid)
            .set(profileData)
            .addOnSuccessListener {
                isLoading = false
                onSuccess()
            }
            .addOnFailureListener { e ->
                isLoading = false
                errorMessage = e.message ?: "Failed to save profile"
            }
    }
}