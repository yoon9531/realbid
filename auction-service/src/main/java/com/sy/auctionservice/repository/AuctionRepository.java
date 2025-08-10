package com.sy.auctionservice.repository;

import com.sy.auctionservice.domain.Auction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionRepository extends JpaRepository<Auction,Long> {
}
