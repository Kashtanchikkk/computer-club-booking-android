package com.example.computerclub.data.model

import org.junit.Assert.assertEquals
import org.junit.Test

class SeatLayoutMapperTest {

    @Test
    fun `SeatLayoutDto maps to domain SeatLayout`() {
        val dto = SeatLayoutDto(
            id = "D1-1",
            label = "Duo 1",
            room = "комната для двух игроков",
            x = 470,
            y = 70,
            width = 50,
            height = 42,
            color = "#BDEBFF",
            displayText = "D1-1"
        )

        val domain = dto.toDomain()

        assertEquals("D1-1", domain.id)
        assertEquals("Duo 1", domain.label)
        assertEquals("комната для двух игроков", domain.room)
        assertEquals(470, domain.x)
        assertEquals(70, domain.y)
        assertEquals(50, domain.width)
        assertEquals(42, domain.height)
        assertEquals("#BDEBFF", domain.color)
        assertEquals("D1-1", domain.displayText)
    }
}
