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
public val nutrition: ImageVector
    get() {
        if (_nutrition != null) {
            return _nutrition!!
        }
        _nutrition =
            ImageVector.Builder(
                name = "nutrition",
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
                        moveTo(7.04f, 18.96f)
                        quadTo(5f, 16.93f, 5f, 14f)
                        quadTo(5f, 11.65f, 6.39f, 9.79f)
                        reflectiveQuadTo(10.03f, 7.27f)
                        quadTo(9.53f, 7.15f, 9.05f, 6.91f)
                        quadTo(8.58f, 6.68f, 8.2f, 6.3f)
                        quadTo(7.38f, 5.47f, 7.14f, 4.34f)
                        reflectiveQuadTo(7.03f, 2.02f)
                        quadTo(8.2f, 1.9f, 9.34f, 2.14f)
                        reflectiveQuadTo(11.3f, 3.2f)
                        quadToRelative(0.57f, 0.57f, 0.84f, 1.3f)
                        quadToRelative(0.26f, 0.72f, 0.34f, 1.52f)
                        quadTo(12.8f, 5.25f, 13.26f, 4.56f)
                        reflectiveQuadTo(14.3f, 3.3f)
                        quadTo(14.58f, 3.02f, 15f, 3.02f)
                        reflectiveQuadTo(15.7f, 3.3f)
                        reflectiveQuadTo(15.98f, 4f)
                        quadToRelative(0f, 0.42f, -0.28f, 0.7f)
                        quadTo(15.15f, 5.25f, 14.73f, 5.91f)
                        reflectiveQuadTo(14.1f, 7.32f)
                        quadToRelative(2.2f, 0.7f, 3.55f, 2.54f)
                        reflectiveQuadTo(19f, 14f)
                        quadToRelative(0f, 2.93f, -2.04f, 4.96f)
                        reflectiveQuadTo(12f, 21f)
                        quadTo(9.08f, 21f, 7.04f, 18.96f)
                        close()
                        moveToRelative(8.5f, -1.42f)
                        quadTo(17f, 16.08f, 17f, 14f)
                        reflectiveQuadTo(15.54f, 10.46f)
                        reflectiveQuadTo(12f, 9f)
                        quadTo(9.93f, 9f, 8.46f, 10.46f)
                        quadTo(7f, 11.93f, 7f, 14f)
                        reflectiveQuadToRelative(1.46f, 3.54f)
                        reflectiveQuadTo(12f, 19f)
                        reflectiveQuadToRelative(3.54f, -1.46f)
                        close()
                        moveTo(12f, 14f)
                        close()
                    }
                }
                .build()
        return _nutrition!!
    }

private var _nutrition: ImageVector? = null