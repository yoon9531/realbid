package com.sy.auctionservice.domain;

public enum AuctionStatus {
    SCHEDULED, // 예정
    LIVE,      // 진행 중
    ENDED,     // 종료 (낙찰 성공)
    FAILED,    // 유찰 (낙찰 실패)
    CANCELED   // 취소
}