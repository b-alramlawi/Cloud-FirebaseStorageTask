package com.example.cloud_firebasestoragetask.ui.screen

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainScreen() {
    var pdfFileUri by remember { mutableStateOf<Uri?>(null) }
    var uploading by remember { mutableStateOf(false) }
    var downloadUrl by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val storageRef = Firebase.storage.reference
    val scaffoldState = rememberScaffoldState()

    // Use rememberLauncherForActivityResult to launch a PDF selection intent
    val selectPdf = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            pdfFileUri = uri
        })

    Scaffold(content = {
        Column(
            modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (pdfFileUri != null) {
                val pdfFileName = pdfFileUri!!.path?.let { it1 -> File(it1).name }
                Card(modifier = Modifier.fillMaxWidth(), elevation = 10.dp) {
                    Text(
                        "Selected file: $pdfFileName",
                        modifier = Modifier.padding(20.dp),
                        style = TextStyle(color = Color.Green)
                    )
                }
            }

            Button(
                onClick = { selectPdf.launch("application/pdf") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(com.example.cloud_firebasestoragetask.R.drawable.pdf),
                    contentDescription = "Download PDF",
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(30.dp)
                )
                Text(
                    "Select PDF",
                    modifier = Modifier.padding(10.dp),
                )
            }

            Button(
                onClick = {
                    pdfFileUri?.let {
                        val pdfFile = it.path?.let { it1 -> File(it1) }
                        val pdfRef = storageRef.child("pdfs/${pdfFile?.name}")
                        uploading = true

                        pdfRef.putFile(it).addOnSuccessListener { taskSnapshot ->
                            uploading = false
                            taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                                downloadUrl = uri.toString()
                                Toast.makeText(
                                    context, "PDF file uploaded successfully!", Toast.LENGTH_LONG
                                ).show()
                                CoroutineScope(Dispatchers.Main).launch {
                                    scaffoldState.snackbarHostState.showSnackbar(
                                        message = "PDF file uploaded successfully!",
                                        duration = SnackbarDuration.Short,
                                    )
                                }
                            }
                        }.addOnFailureListener { e ->
                            uploading = false
                            Toast.makeText(context, "Failed to upload PDF file", Toast.LENGTH_LONG)
                                .show()
                            CoroutineScope(Dispatchers.Main).launch {
                                scaffoldState.snackbarHostState.showSnackbar(
                                    message = "Failed to upload PDF file: ${e.message}",
                                    duration = SnackbarDuration.Short,
                                )
                            }
                        }
                    }
                }, enabled = pdfFileUri != null && !uploading, modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (uploading) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .padding(end = 8.dp)
                                .background(
                                    color = Color.Transparent, shape = RoundedCornerShape(50)
                                )
                        )
                    } else {
                        Image(
                            painter = painterResource(com.example.cloud_firebasestoragetask.R.drawable.upload),
                            contentDescription = "Upload PDF",
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(30.dp)
                        )
                    }

                    Text(
                        text = if (uploading) "Uploading..." else "Upload PDF",
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            if (downloadUrl != null) {
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl))
                        context.startActivity(intent)
                    }, modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(com.example.cloud_firebasestoragetask.R.drawable.download),
                        contentDescription = "Download PDF",
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(30.dp)
                    )
                    Text("Download PDF", modifier = Modifier.padding(7.dp))
                }
            }
        }
    })
}