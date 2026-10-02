package com.celsius.domain.profile;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** AbstractProduct A: empaque térmico. La tarifa es fija más un valor por kilogramo. */
public interface Packaging {
    String name();
    String description();
    String rate();
    String capability();
    BigDecimal fixedFee();
    BigDecimal feePerKg();

    default BigDecimal fee(BigDecimal weightKg) {
        return fixedFee().add(weightKg.multiply(feePerKg())).setScale(0, RoundingMode.HALF_UP);
    }
}
