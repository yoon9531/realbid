package com.sy.auctionservice.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class BidRequest {
    private Long bidderId;
    private BigDecimal amount;
}