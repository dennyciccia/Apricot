package com.apricot.app.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Suppress("CheckReturnValue")
public val temp_preferences_eco: ImageVector
    get() {
        if (_temp_preferences_eco != null) {
            return _temp_preferences_eco!!
        }
        _temp_preferences_eco =
            ImageVector.Builder(
                name = "temp_preferences_eco",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f,
            )
                .apply {
                    path(
                        fill = SolidColor(Color.Black),
                        fillAlpha = 1f,
                        stroke = null,
                        strokeAlpha = 1f,
                        strokeLineWidth = 1f,
                        strokeLineCap = StrokeCap.Butt,
                        strokeLineJoin = StrokeJoin.Bevel,
                        strokeLineMiter = 1f,
                        pathFillType = PathFillType.Companion.NonZero,
                    ) {
                        moveTo(11f, 6.75f)
                        verticalLineTo(4.25f)
                        quadTo(11f, 3.2f, 11.73f, 2.47f)
                        reflectiveQuadTo(13.5f, 1.75f)
                        horizontalLineTo(16f)
                        verticalLineToRelative(2.5f)
                        quadTo(16f, 5.3f, 15.28f, 6.02f)
                        reflectiveQuadTo(13.5f, 6.75f)
                        horizontalLineTo(11f)
                        close()
                        moveToRelative(-5.5f, 6f)
                        quadToRelative(-1.45f, 0f, -2.47f, -1.03f)
                        reflectiveQuadTo(2f, 9.25f)
                        verticalLineTo(5.75f)
                        horizontalLineTo(5.5f)
                        quadToRelative(1.45f, 0f, 2.48f, 1.02f)
                        reflectiveQuadTo(9f, 9.25f)
                        verticalLineToRelative(3.5f)
                        horizontalLineTo(5.5f)
                        close()
                        moveToRelative(10.5f, 9f)
                        quadToRelative(-0.97f, 0f, -1.86f, -0.3f)
                        reflectiveQuadTo(12.53f, 20.63f)
                        lineTo(11.7f, 21.45f)
                        quadTo(11.43f, 21.73f, 11f, 21.73f)
                        reflectiveQuadTo(10.3f, 21.45f)
                        quadToRelative(-0.28f, -0.27f, -0.28f, -0.7f)
                        reflectiveQuadToRelative(0.28f, -0.7f)
                        lineToRelative(0.82f, -0.82f)
                        quadTo(10.6f, 18.5f, 10.3f, 17.61f)
                        reflectiveQuadTo(10f, 15.75f)
                        quadToRelative(0f, -2.5f, 1.75f, -4.26f)
                        quadTo(13.5f, 9.73f, 16f, 9.73f)
                        horizontalLineToRelative(6.03f)
                        verticalLineToRelative(6.03f)
                        quadToRelative(0f, 2.5f, -1.76f, 4.25f)
                        reflectiveQuadTo(16f, 21.75f)
                        close()
                        moveToRelative(0f, -2f)
                        quadToRelative(1.68f, 0f, 2.82f, -1.18f)
                        quadToRelative(1.15f, -1.18f, 1.15f, -2.82f)
                        verticalLineToRelative(-4f)
                        horizontalLineTo(16f)
                        quadToRelative(-1.65f, 0f, -2.82f, 1.16f)
                        reflectiveQuadTo(12f, 15.75f)
                        quadToRelative(0f, 0.57f, 0.14f, 1.09f)
                        reflectiveQuadToRelative(0.41f, 0.96f)
                        lineTo(15.3f, 15.05f)
                        quadTo(15.58f, 14.78f, 16f, 14.78f)
                        quadToRelative(0.43f, 0f, 0.7f, 0.27f)
                        quadToRelative(0.28f, 0.28f, 0.28f, 0.7f)
                        reflectiveQuadToRelative(-0.28f, 0.7f)
                        lineTo(13.95f, 19.2f)
                        quadToRelative(0.45f, 0.28f, 0.96f, 0.41f)
                        reflectiveQuadTo(16f, 19.75f)
                        close()
                        moveToRelative(0.03f, -4.03f)
                        close()
                    }
                }
                .build()
        return _temp_preferences_eco!!
    }

private var _temp_preferences_eco: ImageVector? = null