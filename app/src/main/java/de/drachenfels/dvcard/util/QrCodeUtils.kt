package de.drachenfels.dvcard.util

import android.graphics.Bitmap
import android.graphics.Color

/**
 * Creates a mock QR code bitmap for previews
 */
fun createMockQrBitmap(size: Int): Bitmap {
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    
    // Create simple QR code pattern
    for (x in 0 until size) {
        for (y in 0 until size) {
            // Border
            val isEdge = x < size/10 || y < size/10 || x >= size*9/10 || y >= size*9/10
            
            // Alignment squares (top left, top right, bottom left)
            val isTopLeftSquare = x < size/4 && y < size/4
            val isTopRightSquare = x >= size*3/4 && y < size/4
            val isBottomLeftSquare = x < size/4 && y >= size*3/4
            
            // Inner squares
            val isInnerSquare = (x > size/6 && x < size/3 && y > size/6 && y < size/3) ||
                               (x > size*2/3 && x < size*5/6 && y > size/6 && y < size/3) ||
                               (x > size/6 && x < size/3 && y > size*2/3 && y < size*5/6)
            
            // Pattern elements
            val isPattern = ((x + y) % 8 < 4) && x > size/3 && x < size*2/3 && y > size/3 && y < size*2/3
            
            bitmap.setPixel(
                x, y, 
                when {
                    isEdge || isTopLeftSquare || isTopRightSquare || isBottomLeftSquare -> Color.BLACK
                    isInnerSquare -> Color.WHITE
                    isPattern -> Color.BLACK
                    else -> Color.WHITE
                }
            )
        }
    }
    
    return bitmap
}