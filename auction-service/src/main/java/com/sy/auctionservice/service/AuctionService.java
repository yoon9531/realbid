package com.sy.auctionservice.service;

import com.sy.auctionservice.client.UserClient;
import com.sy.auctionservice.domain.Auction;
import com.sy.auctionservice.domain.AuctionStatus;
import com.sy.auctionservice.domain.Bid;
import com.sy.auctionservice.dto.request.BidRequest;
import com.sy.auctionservice.dto.response.AuctionStateResponse;
import com.sy.auctionservice.event.BidPlacedEvent;
import com.sy.auctionservice.kafka.producer.AuctionEventProducer;
import com.sy.auctionservice.repository.AuctionRepository;
import com.sy.auctionservice.repository.BidRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final UserClient userClient; // Feign 클라이언트 (사용자 정보 확인용)
    private final AuctionEventProducer auctionEventProducer;

    /**
     * 실시간 입찰을 처리하는 핵심 메서드
     */
    @Transactional
    public AuctionStateResponse processBid(Long auctionId, BidRequest bidRequest) {
        try {
            // --- 1. 경매 정보 조회 ---
            // 여러 사용자의 동시 입찰로 인한 데이터 부정합을 막기 위해 낙관적 잠금을 사용합니다.
            // Auction 엔티티에 @Version 어노테이션이 필요합니다.
            Auction auction = auctionRepository.findById(auctionId)
                    .orElseThrow(() -> new EntityNotFoundException("해당 경매를 찾을 수 없습니다: " + auctionId));

            // --- 2. 입찰 유효성 검증 ---
            validateBid(auction, bidRequest);

            // --- 3. 경매 정보 업데이트 ---
            auction.updateHighestBid(bidRequest.getBidderId(), bidRequest.getAmount());

            // --- 4. 입찰 기록 저장 ---
            bidRepository.save(new Bid(auction, bidRequest.getBidderId(), bidRequest.getAmount()));

            // --- 5. 카프카에 입찰 이벤트 발행 ---
            // DB 트랜잭션이 성공적으로 커밋된 후에 이벤트를 발행하는 것이 더 안정적입니다.
            // (트랜잭션 이벤트 리스너 @TransactionalEventListener 사용 고려)
            auctionEventProducer.sendBidPlacedEvent(
                    new BidPlacedEvent(auctionId, bidRequest.getBidderId(), bidRequest.getAmount(), LocalDateTime.now())
            );

            log.info("Bid processed successfully for auctionId: {}, new highest bid: {}", auctionId, bidRequest.getAmount());

            // --- 6. 클라이언트에 브로드캐스팅할 최신 상태 반환 ---
            return AuctionStateResponse.from(auction);

        } catch (ObjectOptimisticLockingFailureException e) {
            // 동시 입찰 경합이 발생한 경우! (다른 사람이 먼저 입찰)
            // 이 예외는 ControllerAdvice에서 처리하여 사용자에게 "이미 더 높은 가격으로 입찰되었습니다." 와 같은 메시지를 보내야 합니다.
            log.warn("Optimistic lock conflict for auctionId: {}", auctionId);
            throw new IllegalStateException("입찰 경합이 발생했습니다. 다시 시도해주세요.");
        }
    }

    /**
     * Saga: 결제 성공 시 경매를 최종 종료 처리하는 메서드
     */
    @Transactional
    public void finalizeAuction(Long auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new EntityNotFoundException("해당 경매를 찾을 수 없습니다: " + auctionId));

        auction.changeStatus(AuctionStatus.ENDED);
        log.info("Auction finalized: {}", auctionId);
    }

    /**
     * Saga: 결제 실패 시 보상 트랜잭션을 처리하는 메서드
     */
    @Transactional
    public void compensateAuction(Long auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new EntityNotFoundException("해당 경매를 찾을 수 없습니다: " + auctionId));

        // 정책에 따라 유찰(FAILED) 처리하거나, 재경매(LIVE) 상태로 돌릴 수 있습니다.
        auction.changeStatus(AuctionStatus.FAILED);
        log.warn("Auction compensation triggered, status set to FAILED: {}", auctionId);
    }

    // 입찰 유효성을 검증하는 private 헬퍼 메서드
    private void validateBid(Auction auction, BidRequest bidRequest) {
        if (auction.getStatus() != AuctionStatus.LIVE) {
            throw new IllegalStateException("현재 진행 중인 경매가 아닙니다.");
        }
        if (bidRequest.getAmount().compareTo(auction.getHighestBid()) <= 0) {
            throw new IllegalArgumentException("현재 최고가보다 높은 금액을 입찰해야 합니다.");
        }
        // 자기 자신(판매자)의 경매에 입찰하는 것을 막는 로직 (구현 필요)
        // if (auction.getSellerId().equals(bidRequest.getBidderId())) { ... }
    }
}