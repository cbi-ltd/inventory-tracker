package org.inventory_tracker.entity.security;

import org.inventory_tracker.entity.Merchant;

public class MerchantContext {

    private static final ThreadLocal<Merchant> CURRENT_MERCHANT = new ThreadLocal<>();

    private MerchantContext() {
    }

    public static void setCurrentMerchant(Merchant merchant) {
        CURRENT_MERCHANT.set(merchant);
    }

    public static Merchant getCurrentMerchant() {
        return CURRENT_MERCHANT.get();
    }

    public static void clear() {
        CURRENT_MERCHANT.remove();
    }
}
