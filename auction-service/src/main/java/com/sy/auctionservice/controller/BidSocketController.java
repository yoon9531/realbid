package com.sy.auctionservice.controller;

import com.sy.auctionservice.dto.request.BidRequest;
import com.sy.auctionservice.dto.response.AuctionStateResponse;
import com.sy.auctionservice.service.AuctionService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class BidSocketController {
    private final AuctionService auctionService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/auctions/{auctionId}/bid")
    public void handleBid(@DestinationVariable Long auctionId, BidRequest bidRequest) {
        AuctionStateResponse updatedAuctionState = auctionService.processBid(auctionId, bidRequest);

        messagingTemplate.convertAndSend("/topic/auctions/" + auctionId, updatedAuctionState);
    }
}
