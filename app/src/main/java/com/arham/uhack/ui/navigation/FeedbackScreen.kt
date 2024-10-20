package com.arham.uhack.ui.navigation

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.arham.uhack.R

@Composable
fun FeedbackScreen() {
    val context = LocalContext.current
    var feedbackText by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Your Image
                Image(
                    painter = painterResource(R.drawable.developer),
                    contentDescription = context.getString(R.string.description_developer),
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Your Name and Title
                Text(
                    text = context.getString(R.string.name_developer),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = context.getString(R.string.heading_developer),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Introductory Text
                Text(
                    text = context.getString(R.string.description_feedback),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        // Feedback Input Field
        OutlinedTextField(
            value = feedbackText,
            onValueChange = { feedbackText = it },
            label = { Text(context.getString(R.string.label_feedback)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp), // Increased height for multiple lines
            textStyle = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Submit Button
        Button(
            onClick = {
                val selectorIntent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse(context.getString(R.string.link_mail)) // Only email apps should handle this
                }
                val emailIntent = Intent(Intent.ACTION_SEND).apply {
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.email_developer)))
                    putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.subject_feedback))
                    putExtra(Intent.EXTRA_TEXT, feedbackText)
                    selector = selectorIntent // Set the selector intent
                }

                emailIntent.type = context.getString(R.string.type_mime)

                // Check if there is an email app available to handle the intent
                if (emailIntent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(Intent.createChooser(emailIntent, context.getString(R.string.title_email)))
                } else {
                    Toast.makeText(context, context.getString(R.string.error_email), Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = context.getString(R.string.button_feedback),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}