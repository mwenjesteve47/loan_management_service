create table customers
(
    id            bigint auto_increment
        primary key,
    credit_score  int            null,
    email         varchar(255)   null,
    name          varchar(255)   null,
    phone_number  varchar(255)   null,
    active        int            null,
    date_created  datetime(6)    null,
    date_modified datetime(6)    null,
    first_name    varchar(255)   null,
    income        decimal(38, 2) null,
    last_name     varchar(255)   null,
    national_id   varchar(255)   null,
    currency      varchar(255)   null
);

create table customer_notification_preferences
(
    customer_preference_id    bigint auto_increment
        primary key,
    email_enabled             bit    not null,
    push_notification_enabled bit    not null,
    sms_enabled               bit    not null,
    customer_id               bigint not null,
    constraint UKqg96onpw5u43501qryynnd19k
        unique (customer_id),
    constraint FKjvhmcx2bndduq059e6qibma7a
        foreign key (customer_id) references customers (id)
);

create index customers_national_id_index
    on customers (national_id);

create index customers_phone_number_index
    on customers (phone_number);

create table loan_limits
(
    id              bigint auto_increment
        primary key,
    available_limit decimal(38, 2) null,
    credit_limit    decimal(38, 2) null,
    currency        varchar(255)   null,
    customer_id     bigint         not null,
    constraint UK8mel2yq3824ddros8iykg5thp
        unique (customer_id),
    constraint FKositk5jdr72mmxsfshiokgwrj
        foreign key (customer_id) references customers (id)
);

create table loan_products
(
    id            bigint auto_increment
        primary key,
    active        int                        null,
    date_created  datetime(6)                null,
    date_modified datetime(6)                null,
    description   varchar(255)               null,
    name          varchar(255)               null,
    tenure_type   enum ('FIXED', 'VARIABLE') null,
    tenure_unit   enum ('DAYS', 'MONTHS')    null,
    tenure_value  int                        null
);

create table fees
(
    id               bigint auto_increment
        primary key,
    active           int                                           null,
    amount           decimal(38, 2)                                null,
    calculation_type enum ('FIXED', 'PERCENTAGE')                  null,
    date_created     datetime(6)                                   null,
    date_modified    datetime(6)                                   null,
    days_after_due   int                                           null,
    fee_type         enum ('DAILY_FEE', 'LATE_FEE', 'SERVICE_FEE') null,
    loan_id          bigint                                        not null,
    constraint FK162ot8kb533m21m97ack9lb6i
        foreign key (loan_id) references loan_products (id)
);

create index loan_products_name_index
    on loan_products (name);

create table loans
(
    id                 bigint auto_increment
        primary key,
    amount             decimal(38, 2)                                                 null,
    amount_due         decimal(38, 2)                                                 null,
    billing_cycle_type enum ('CONSOLIDATED_DUE_DATE', 'INDIVIDUAL_DUE_DATE')          null,
    created_at         datetime(6)                                                    null,
    due_date           date                                                           null,
    loan_structure     enum ('INSTALLMENTS', 'LUMP_SUM')                              null,
    start_date         date                                                           null,
    state              enum ('CANCELLED', 'CLOSED', 'OPEN', 'OVERDUE', 'WRITTEN_OFF') null,
    updated_at         datetime(6)                                                    null,
    customer_id        bigint                                                         not null,
    loan_product_id    bigint                                                         not null,
    active             int                                                            null,
    constraint FK3s60kbg0a404doyf8ii6qwb9g
        foreign key (customer_id) references customers (id),
    constraint FKri0g402nb4pqka97jtyiyjmdu
        foreign key (loan_product_id) references loan_products (id)
);

create index loans_customer_id_index
    on loans (customer_id);

create index loans_loan_product_id_index
    on loans (loan_product_id);

create table repayment_transactions
(
    id           bigint auto_increment
        primary key,
    amount_paid  decimal(38, 2) null,
    payment_date datetime(6)    null,
    customer_id  bigint         null,
    loan_id      bigint         not null,
    constraint FKjgmmm4egtbo7yu0y4wtrsnf8b
        foreign key (customer_id) references customers (id),
    constraint FKtg4jg5rb6qtdycf7cjhasnxdx
        foreign key (loan_id) references loans (id)
);