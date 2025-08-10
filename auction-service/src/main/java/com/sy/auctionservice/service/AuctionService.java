package com.sy.auctionservice.service;

import com.sy.auctionservice.client.UserClient;
import com.sy.auctionservice.domain.Auction;
import com.sy.auctionservice.dto.request.BidRequest;
import com.sy.auctionservice.dto.response.AuctionStateResponse;
import com.sy.auctionservice.repository.AuctionRepository;
import com.sy.auctionservice.repository.BidRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuctionService {
    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final UserClient userClient; // Feign 클라이언트

    @Transactional
    public AuctionStateResponse processBid(Long auctionId, BidRequest bidRequest) {
        // 동시성 문제를 해결하기 위해 낙관적 잠금 사용
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new RuntimeException("경매를 찾을 수 없습니다."));

        // 1. 입찰 유효성 검증
        if (bidRequest.getAmount().compareTo(auction.getHighestBid()) <= 0) {
            throw new RuntimeException("현재 최고가보다 높은 금액을 입찰해야 합니다.");
        }
        // Feign Client를 이용해 사용자 서비스에서 입찰자 정보 확인 (예: 잔액)
        // UserDto user = userClient.getUser(bidRequest.getBidderId()); ...

        // 2. 데이터 업데이트
        auction.setHighestBid(bidRequest.getAmount());
        auction.setHighestBidderId(bidRequest.getBidderId());
        auctionRepository.save(auction); // version이 자동으로 증가

        bidRepository.save(new Bid(auction, bidRequest.getBidderId(), bidRequest.getAmount()));

        // 3. 방송할 최신 경매 상태 DTO 생성 및 반환
        return new AuctionStateResponse(auction.getHighestBid(), ...);
    }
}
