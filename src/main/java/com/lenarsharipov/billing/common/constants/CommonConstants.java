package com.lenarsharipov.billing.common.constants;

public class CommonConstants {
    public static final String ACTIVATION_DATE_PATTERN = "yyyy-MM-dd";

    public static final String EXCHANGE_NAME = "subscription.events";
    public static final String INVOICE_QUEUE = "billing.usercache.invoice.queue";
    public static final String DEACTIVATED_QUEUE = "billing.usercache.sub-deactivated.queue";
    public static final String ROUTING_INVOICE_CREATED = "invoice.created";
    public static final String ROUTING_SUB_DEACTIVATED = "subscription.deactivated";

    public static final String CACHE_USER_INVOICES_KEY_PREFIX = "cache:user_invoices:";
    public static final String CACHE_ACTIVE_SUBSCRIPTIONS_KEY_PREFIX = "cache:active_subs";

    public static final String TEN_SECONDS = "10s";
    public static final String TEN_MINUTES = "10m";
    public static final String FIVE_MINUTES = "5m";
    public static final String FORTY_FIVE_MINUTES = "45m";

    public static final String MONTHLY_BILLING_LOCK_NAME = "monthlyBillingLock";
    public static final String OUTBOX_RETRY_LOCK_NAME = "outboxRetryLock";

    public static final long TWENTY_FOUR_HOURS = 86_400;
}
