package com.skillswap.exchangeservice.repository;

import com.skillswap.exchangeservice.entity.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExchangeRepository extends JpaRepository<Exchange, Long> {

    // Find all exchanges where the user is either the requester OR the provider
    List<Exchange> findByRequesterIdOrProviderId(Long requesterId, Long providerId);
}