package com.bitflip.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bitflip.app.R
import com.bitflip.app.ui.theme.AccentBlue
import com.bitflip.app.ui.theme.BgPrimary
import com.bitflip.app.ui.theme.BgSurface
import com.bitflip.app.ui.theme.BorderColor
import com.bitflip.app.ui.theme.TextMuted
import com.bitflip.app.ui.theme.TextPrimary

@Composable
fun AboutScreen() {
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App Icon
        Image(
            painter = painterResource(id = R.drawable.app_icon),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .border(2.dp, AccentBlue, CircleShape)
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // App Title
        Text(
            text = "BitFlip",
            color = TextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // App Description
        Text(
            text = "Base conversions & binary arithmetic v3.0\n(Binary, Decimal, Octal, Hex)",
            color = TextMuted,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        HorizontalDivider(color = BorderColor, thickness = 1.dp)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Developer Info Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BgSurface, RoundedCornerShape(16.dp))
                .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Developer",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Saad Khan",
                    color = AccentBlue,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // GitHub Button
                Button(
                    onClick = { uriHandler.openUri("https://github.com/cybersaad") },
                    colors = ButtonDefaults.buttonColors(containerColor = BgPrimary),
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Code,
                        contentDescription = "GitHub",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "GitHub", color = TextPrimary, fontSize = 16.sp)
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // LinkedIn Button
                Button(
                    onClick = { uriHandler.openUri("https://www.linkedin.com/in/saadkhan301/") },
                    colors = ButtonDefaults.buttonColors(containerColor = BgPrimary),
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = "LinkedIn",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "LinkedIn", color = TextPrimary, fontSize = 16.sp)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        HorizontalDivider(color = BorderColor, thickness = 1.dp)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Version and Copyright
        Text(
            text = "Version 3.0",
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = "© 2026",
            color = TextMuted,
            fontSize = 14.sp
        )
    }
}

