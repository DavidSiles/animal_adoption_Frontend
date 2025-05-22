package com.example.animal_adoption.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.animal_adoption.R

val Poppins = FontFamily.Default

@Composable
fun FirstScreen(navController: NavHostController) {

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.fondo2),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Transparent
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.logotuons2),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(350.dp)
                        .offset(y = (-140).dp)
                        .padding(bottom = 40.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(Modifier.height(100.dp))

                CustomButton(
                    onClick = { navController.navigate("UserRegister") },
                    text = "I want to adopt",
                    color = Color(0xFFFF7043),
                    modifier = Modifier.fillMaxWidth(0.8f)
                )

                Spacer(Modifier.height(20.dp))

                CustomButton(
                    onClick = { navController.navigate("ShelterRegister") },
                    text = "I am a shelter",
                    color = Color(0xFF4285F4),
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
            }
        }
    }

}

@Composable
fun CustomButton(
    onClick: () -> Unit,
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = Color.White
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        ),
        modifier = modifier.height(60.dp)
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.5.sp
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FirstScreenPreview() {
    MaterialTheme {
        FirstScreen(navController = rememberNavController())
    }
}