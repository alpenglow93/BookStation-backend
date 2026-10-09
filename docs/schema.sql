-- BookStation 데이터베이스 스키마
-- PostgreSQL 16 + pgvector 기준

CREATE EXTENSION IF NOT EXISTS vector;

-- 구매 플랫폼
CREATE TABLE platform (
    id   BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL UNIQUE
);

-- 도서 카탈로그 (수집 도서 + 직접 등록 도서)
CREATE TABLE book (
    id         BIGSERIAL PRIMARY KEY,
    title      TEXT NOT NULL,
    author     TEXT,
    genre      TEXT,
    category   TEXT,                        -- 예: 로판 e북, 로판 웹소설
    synopsis   TEXT,                        -- 임베딩 원문
    cover_url  TEXT,
    source     TEXT NOT NULL,               -- RIDIBOOKS / MANUAL
    embedding  VECTOR,                      -- gemini-embedding-001 (768차원), 줄거리 임베딩
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

-- 내 서재
CREATE TABLE user_book (
    id           BIGSERIAL PRIMARY KEY,
    book_id      BIGINT NOT NULL REFERENCES book (id),
    platform_id  BIGINT REFERENCES platform (id),
    status       TEXT NOT NULL DEFAULT 'UNREAD',
    rating       SMALLINT,
    memo         TEXT,
    purchased_at DATE,
    created_at   TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT user_book_status_check
        CHECK (status IN ('WISHLIST', 'UNREAD', 'READING', 'COMPLETED', 'DROPPED')),
    CONSTRAINT user_book_rating_check
        CHECK (rating >= 1 AND rating <= 5),
    -- 같은 책을 같은 플랫폼으로 중복 등록 금지 (다른 플랫폼은 허용)
    CONSTRAINT uk_user_book_book_platform
        UNIQUE (book_id, platform_id)
);

-- 초기 데이터
INSERT INTO platform (name) VALUES
    ('리디북스'),
    ('카카오페이지'),
    ('네이버 시리즈');