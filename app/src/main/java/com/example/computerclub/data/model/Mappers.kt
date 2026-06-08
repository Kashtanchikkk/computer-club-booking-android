package com.example.computerclub.data.model

import com.example.computerclub.domain.model.Booking
import com.example.computerclub.domain.model.ComputerClubBranch
import com.example.computerclub.domain.model.MapObject
import com.example.computerclub.domain.model.MapObjectType
import com.example.computerclub.domain.model.Seat
import com.example.computerclub.domain.model.SeatLayout
import com.example.computerclub.domain.model.SeatType
import com.example.computerclub.domain.model.User

fun UserDto.toDomain() = User(
    id = id,
    email = email,
    name = name,
    role = role
)

fun ComputerClubDto.toDomain() = ComputerClubBranch(
    id = id,
    name = name,
    address = address,
    rating = rating
)

fun SeatDto.toDomain() = Seat(
    id = id,
    clubId = clubId,
    typeId = typeId,
    name = name,
    type = type,
    pricePerHour = pricePerHour,
    processor = processor,
    gpu = gpu,
    ram = ram,
    monitor = monitor,
    isActive = isActive
)

fun SeatTypeDto.toDomain() = SeatType(
    id = id,
    name = name,
    pricePerHour = pricePerHour,
    processor = processor,
    gpu = gpu,
    ram = ram,
    monitor = monitor
)

fun SeatLayoutDto.toDomain() = SeatLayout(
    id = id,
    seatId = seatId,
    label = label,
    room = room,
    x = x,
    y = y,
    width = width,
    height = height,
    color = color,
    displayText = displayText
)

fun MapObjectDto.toDomain() = MapObject(
    id = id,
    type = MapObjectType.valueOf(type.name),
    title = title,
    x = x,
    y = y,
    width = width,
    height = height,
    seatId = seatId
)

fun BookingDto.toDomain() = Booking(
    id = id,
    seatId = seatId,
    seatName = seatName,
    startTime = startTime,
    endTime = endTime,
    status = status,
    totalPrice = totalPrice
)
