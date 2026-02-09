-- 기능 요구사항(요약)
-- - 회원가입 / 로그인
-- - 게시글: 작성/수정/삭제/상세(댓글,좋아요,태그)/전체 조회
-- - 댓글: 작성/수정/삭제
-- - 태그: 게시글 작성/수정 시 함께 생성/연결(필요 시 삭제), 태그가 포함된 게시글 조회
-- - 팔로우: 팔로잉/언팔/팔로워&팔로잉 목록/카운트

-- users
create table if not exists users (
  id bigserial primary key,
  username text not null unique,
  email text not null unique,
  password_hash text not null,
  created_at timestamptz not null default now(),
  modified_at timestamptz not null default now()
);
create index if not exists ix_users_created_at on users (created_at desc);

-- posts
create table if not exists posts (
  id bigserial primary key,
  title text not null,
  content text not null,
  author_id bigint not null references users(id) on delete cascade,
  created_at timestamptz not null default now(),
  modified_at timestamptz not null default now(),
  likes_count int not null default 0
);
create index if not exists ix_posts_created_at on posts (created_at desc);
create index if not exists ix_posts_author_id on posts (author_id);

-- post_likes (게시글-유저 좋아요, 1유저=1좋아요)
create table if not exists post_likes (
  post_id bigint not null references posts(id) on delete cascade,
  user_id bigint not null references users(id) on delete cascade,
  created_at timestamptz not null default now(),
  primary key (post_id, user_id)
);
create index if not exists ix_post_likes_user_id on post_likes (user_id);

-- comments
create table if not exists comments (
  id bigserial primary key,
  content text not null,
  author_id bigint not null references users(id) on delete cascade,
  post_id bigint not null references posts(id) on delete cascade,
  created_at timestamptz not null default now(),
  modified_at timestamptz not null default now()
);
create index if not exists ix_comments_post_id_created_at on comments (post_id, created_at desc);

-- tags (태그 자체는 재사용 가능: name unique)
create table if not exists tags (
  id bigserial primary key,
  name text not null unique,
  created_at timestamptz not null default now()
);

-- post_tags (게시글-태그 M:N)
create table if not exists post_tags (
  post_id bigint not null references posts(id) on delete cascade,
  tag_id bigint not null references tags(id) on delete cascade,
  created_at timestamptz not null default now(),
  primary key (post_id, tag_id)
);
create index if not exists ix_post_tags_tag_id on post_tags (tag_id);

-- follows (유저-유저 팔로우 관계)
create table if not exists follows (
  follower_id bigint not null references users(id) on delete cascade,
  following_id bigint not null references users(id) on delete cascade,
  created_at timestamptz not null default now(),
  primary key (follower_id, following_id),
  constraint chk_follows_not_self check (follower_id <> following_id)
);
create index if not exists ix_follows_following_id on follows (following_id);