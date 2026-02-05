-- user
create table if not exists users (
  id bigserial primary key,
  username text not null unique,
  email text not null unique,
  created_at timestamptz not null default now()
);
create index if not exists ix_users_created_at on users (created_at desc);


