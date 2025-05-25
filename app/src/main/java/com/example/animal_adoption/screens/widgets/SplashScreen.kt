package com.example.animal_adoption.screens.widgets

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.animal_adoption.R
import kotlinx.coroutines.delay
import java.time.format.TextStyle

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
) {
    // Animación de escala para el logo
    val scale = remember { Animatable(0.8f) }
    val alpha = remember { Animatable(0f) }

    // Ejecutar animaciones al cargar la pantalla
    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 800,
                easing = LinearEasing
            )
        )
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 1000
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            // Elemento decorativo (opcional, inspirado en apps de citas)
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .offset(y = (-100).dp)
                    .background(
                        color = Color.White.copy(alpha = 0.1f),
                        shape = CircleShape
                    )
                    .alpha(alpha.value),
                contentAlignment = Alignment.Center // Center the content inside the Box
            ) {
                // Logo con animación de escala
                Image(
                    painter = painterResource(R.drawable.logotuons2),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(200.dp)
                        .scale(scale.value)
                        .alpha(alpha.value),
                    contentScale = ContentScale.Fit
                )
            }

            // Texto de la aplicación con estilo moderno
            Text(
                text = "Animal Adoption",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                ),
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .alpha(alpha.value)
            )

            // Subtítulo o eslogan
            Text(
                text = "Encuentra tu compañero perfecto",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                ),
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .alpha(alpha.value)
            )

            // Indicador de carga animado
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .size(40.dp)
                    .alpha(alpha.value),
                color = Color.White,
                strokeWidth = 4.dp
            )
        }


    }
}

// Tema personalizado para el preview
@Composable
private fun AnimalAdoptionTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFFFF6F61), // Coral
            secondary = Color(0xFFFFA07A), // Salmón claro
            surface = Color.White,
            onSurface = Color.Black,
            background = Color.White
        ),
        content = content
    )
}

// Preview de la pantalla splash
@Preview(showBackground = true, name = "Splash Screen Preview")
@Composable
fun SplashScreenPreview() {
    AnimalAdoptionTheme {
        SplashScreen()
    }
}