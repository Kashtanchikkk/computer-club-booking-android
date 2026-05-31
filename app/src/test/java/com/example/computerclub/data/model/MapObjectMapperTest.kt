package com.example.computerclub.data.model

import com.example.computerclub.domain.model.MapObjectType
import org.junit.Assert.assertEquals
import org.junit.Test

class MapObjectMapperTest {

    @Test
    fun `MapObjectDto maps to domain MapObject`() {
        val dto = MapObjectDto(
            id = 3L,
            type = MapObjectTypeDto.SEAT,
            title = "PC-15",
            x = 550,
            y = 380,
            width = 50,
            height = 42,
            seatId = 15L
        )

        val domain = dto.toDomain()

        assertEquals(3L, domain.id)
        assertEquals(MapObjectType.SEAT, domain.type)
        assertEquals("PC-15", domain.title)
        assertEquals(550, domain.x)
        assertEquals(380, domain.y)
        assertEquals(50, domain.width)
        assertEquals(42, domain.height)
        assertEquals(15L, domain.seatId)
    }
}
