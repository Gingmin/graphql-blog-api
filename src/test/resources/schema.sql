-- Test bootstrap schema (PostgreSQL)
-- 목적: @SpringBootTest 실행 시 로컬 테스트 DB에 필요한 테이블을 항상 확보

create table if not exists users (
  id bigserial primary key,
  username text not null unique,
  email text not null unique,
  password_hash text not null,
  created_at timestamptz not null default now(),
  modified_at timestamptz not null default now()
);
create index if not exists ix_users_created_at on users (created_at desc);

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

create table if not exists comments (
  id bigserial primary key,
  content text not null,
  author_id bigint not null references users(id) on delete cascade,
  post_id bigint not null references posts(id) on delete cascade,
  created_at timestamptz not null default now(),
  modified_at timestamptz not null default now()
);
create index if not exists ix_comments_post_id_created_at on comments (post_id, created_at desc);

create table if not exists tags (
  id bigserial primary key,
  name text not null unique,
  created_at timestamptz not null default now()
);

create table if not exists post_tags (
  post_id bigint not null references posts(id) on delete cascade,
  tag_id bigint not null references tags(id) on delete cascade,
  created_at timestamptz not null default now(),
  primary key (post_id, tag_id)
);
create index if not exists ix_post_tags_tag_id on post_tags (tag_id);

create table if not exists follows (
  follower_id bigint not null references users(id) on delete cascade,
  following_id bigint not null references users(id) on delete cascade,
  created_at timestamptz not null default now(),
  primary key (follower_id, following_id),
  constraint chk_follows_not_self check (follower_id <> following_id)
);
create index if not exists ix_follows_following_id on follows (following_id);

