package com.ii.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ii.object.entity.Streamer;

public interface IStreamerRepository extends JpaRepository<Streamer, UUID> {

}
