package com.skillswap.chatservice.repository;

import com.skillswap.chatservice.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // Find all messages for a specific exchange, ordered by time
    List<Message> findByExchangeIdOrderByTimestampAsc(Long exchangeId);
}