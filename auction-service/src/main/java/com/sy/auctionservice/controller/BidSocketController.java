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
    private final SimpMessagingTemplate messagingTemplate; // 특정 토픽으로 메시지를 보내는 도구

    @MessageMapping("/auctions/{auctionId}/bid") // 클라이언트가 /app/auctions/{id}/bid 로 메시지를 보냄
    public void handleBid(@DestinationVariable Long auctionId, BidRequest bidRequest) {
        // 1. 서비스에 입찰 처리 위임
        AuctionStateResponse updatedAuctionState = auctionService.processBid(auctionId, bidRequest);

        // 2. 처리 결과를 해당 경매방을 구독 중인 모든 클라이언트에게 전송
        messagingTemplate.convertAndSend("/topic/auctions/" + auctionId, updatedAuctionState);
    }
}
