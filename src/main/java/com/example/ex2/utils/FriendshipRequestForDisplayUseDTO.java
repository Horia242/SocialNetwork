package com.example.ex2.utils;

import ro.ubbcluj.map.model.FriendshipRequestDTO;
import ro.ubbcluj.map.model.FriendshipRequestStatus;

import java.time.LocalDate;

public class FriendshipRequestForDisplayUseDTO<userID> {
    String name;
    String surname;
    FriendshipRequestStatus status;
    LocalDate date;
    FriendshipRequestDTO friendshipRequestDTO;

    public FriendshipRequestForDisplayUseDTO(String name, String surname, FriendshipRequestStatus status, LocalDate date,FriendshipRequestDTO friendshipRequestDTO) {
        this.name = name;
        this.surname = surname;
        this.status = status;
        this.date = date;
        this.friendshipRequestDTO = friendshipRequestDTO;
    }

    public FriendshipRequestDTO getFriendshipRequestDTO() {
        return friendshipRequestDTO;
    }

    public void setFriendshipRequestDTO(FriendshipRequestDTO friendshipRequestDTO) {
        this.friendshipRequestDTO = friendshipRequestDTO;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public FriendshipRequestStatus getStatus() {
        return status;
    }

    public void setStatus(FriendshipRequestStatus status) {
        this.status = status;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
