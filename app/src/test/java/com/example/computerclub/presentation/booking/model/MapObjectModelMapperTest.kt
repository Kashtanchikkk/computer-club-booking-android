package com.example.computerclub.presentation.booking.model

import com.example.computerclub.domain.model.MapObject
import com.example.computerclub.domain.model.MapObjectType
import org.junit.Assert.assertEquals
import org.junit.Test

class MapObjectModelMapperTest {

    @Test
    fun `domain MapObject maps to UI model`() {
        val mapObject = MapObject(
            id = 1L,
            clubId = 1L,
            type = MapObjectType.ROOM,
            title = "VIP ROOM",
            x = 1017,
            y = 335,
            width = 115,
            height = 190
        )

        val uiModel = mapObject.toUiModel()

        assertEquals(1L, uiModel.id)
        assertEquals(MapObjectTypeModel.ROOM, uiModel.type)
        assertEquals("VIP ROOM", uiModel.title)
        assertEquals(1017, uiModel.x)
        assertEquals(335, uiModel.y)
        assertEquals(115, uiModel.width)
        assertEquals(190, uiModel.height)
    }

    @Test
    fun `seat map object maps to seat layout`() {
        val uiModel = MapObjectModel(
            id = 10L,
            type = MapObjectTypeModel.SEAT,
            title = "PC-1",
            x = 445,
            y = 288,
            width = 44,
            height = 38,
            seatId = 1L
        )

        val layout = uiModel.toSeatLayout()

        assertEquals("1", layout.id)
        assertEquals(1, layout.seatId)
        assertEquals("PC-1", layout.displayText)
        assertEquals(445, layout.x)
        assertEquals(288, layout.y)
        assertEquals(44, layout.width)
        assertEquals(38, layout.height)
    }
}
