package com.example.computerclub.presentation.booking.model

import androidx.compose.ui.graphics.Color
import com.example.computerclub.domain.model.SeatLayout
import org.junit.Assert.assertEquals
import org.junit.Test

class ClubSeatLayoutMapperTest {

    @Test
    fun `SeatLayout maps to UI ClubSeatLayout`() {
        val layout = SeatLayout(
            id = "PS5-1",
            label = "PS5 Room 1",
            room = "PlayStation зона",
            x = 285,
            y = 345,
            width = 62,
            height = 42,
            color = "not-a-color",
            displayText = "PS5-1"
        )

        val uiModel = layout.toUiModel()

        assertEquals("PS5-1", uiModel.id)
        assertEquals("PS5 Room 1", uiModel.label)
        assertEquals("PlayStation зона", uiModel.room)
        assertEquals(285, uiModel.x)
        assertEquals(345, uiModel.y)
        assertEquals(62, uiModel.width)
        assertEquals(42, uiModel.height)
        assertEquals("PS5-1", uiModel.displayText)
    }

    @Test
    fun `invalid HEX color uses default color`() {
        val layout = SeatLayout(
            id = "BAD",
            label = "Bad color",
            room = "test",
            x = 0,
            y = 0,
            width = 50,
            height = 42,
            color = "wrong-color",
            displayText = "BAD"
        )

        val uiModel = layout.toUiModel()

        assertEquals(Color(0xFFFFF1B8), uiModel.color)
    }
}
