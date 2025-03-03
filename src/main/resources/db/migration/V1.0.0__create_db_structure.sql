create table accounts
(
    id         uuid         not null default gen_random_uuid(),
    username   varchar(128) not null,
    password   varchar(256) not null,
    created_at timestamp    not null default now(),
    updated_at timestamp    not null default now(),
    rules      jsonb        not null default '[]'::jsonb,
    host       varchar(128) not null,
    port       int          not null,
    protocol   varchar(128) not null,
    constraint pk_account primary key (id)
);

create index idx_accounts_username on accounts using btree (username);

create index idx_accounts_rules on accounts using gin (rules);
create index idx_accounts_rules_name on accounts using btree ((rules ->> 'name'));
create index idx_accounts_rules_action on accounts using btree ((rules ->> 'action'));
create index idx_accounts_rules_rule_id on accounts using btree ((rules ->> 'rule_id'));
