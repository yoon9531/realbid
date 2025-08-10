package com.sy.auctionservice.repository;

import com.sy.auctionservice.domain.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BidRepository extends JpaRepository<Bid,Long> {
}
