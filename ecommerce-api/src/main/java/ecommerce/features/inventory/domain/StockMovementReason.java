package ecommerce.features.inventory.domain;

public enum StockMovementReason {


    // Manuales (Admin)
    PURCHASE,
    MANUAL_ADJUSTMENT,
    DAMAGED_GOODS,
    SUPPLIER_REFRESH,

    // Automáticos (Sistema - Futuras HUs)
    CUSTOMER_SALE,
    RESELLER_SALE,
    SALE_CANCELED


}
