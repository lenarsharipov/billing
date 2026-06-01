package com.lenarsharipov.billing.common.constants;

public class CommonConstants {
    public static final String ACTIVATION_DATE_PATTERN = "yyyy-MM-dd";

    public static final String UUID_RFC_4122_PATTERN =
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$";

    public static final String X_IDEMPOTENCY_KEY_HEADER = "X-Idempotency-Key";
}
