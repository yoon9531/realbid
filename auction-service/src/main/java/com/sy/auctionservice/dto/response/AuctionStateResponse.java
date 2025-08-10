package com.sy.auctionservice.dto.response;

import com.sy.auctionservice.domain.Auction;
import com.sy.auctionservice.domain.AuctionStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class AuctionStateResponse {
    private Long auctionId;
    private Long highestBidderId;
    private String highestBidderNickname; // UserClient를 통해 채워넣을 수 있음
    private BigDecimal highestBid;
    private LocalDateTime endTime;
    private AuctionStatus status;

    public static AuctionStateResponse from(Auction auction) {
        return AuctionStateResponse.builder()
                .auctionId(auction.getId())
                .highestBidderId(auction.getHighestBidderId())
                .highestBid(auction.getHighestBid())
                .endTime(auction.getEndTime())
                .status(auction.getStatus())
                .build();
    }
}