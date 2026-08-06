package com.apricot.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    // Componenti piccolissimi (es. piccoli badge, tooltip)
    extraSmall = RoundedCornerShape(4.dp),

    // Componenti piccoli (es. Snackbars, alcuni chip piccoli)
    small = RoundedCornerShape(8.dp),

    // Componenti medi (es. Card delle ricette, Campi di testo, Dialoghi)
    medium = RoundedCornerShape(16.dp),

    // Componenti grandi (es. Modali che salgono dal basso / Bottom Sheets, Navigation Drawer)
    large = RoundedCornerShape(24.dp),

    // Componenti extra grandi (es. Schermate intere che si sovrappongono)
    extraLarge = RoundedCornerShape(32.dp)
)