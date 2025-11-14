package ninco.business.dto;

import ninco.business.rules.Validator;
import ninco.common.InvalidFieldException;

import java.time.LocalDateTime;

public class StockDTO implements Record {
    private final int product_id;
    private final int store_id;
    private final String product_name;
    private final String store_name;
    private final int quantity;
    private final LocalDateTime createdAt;

    /**
     * Constructor for creating a ProductDTO object with all fields from the database.
     *
     * @param product_id        The unique identifier of the product
     * @param store_id          The unique identifier of the store
     * @param product_name      The name of the product
     * @param store_name        The name of the store
     * @param quantity          The quantity of every product
     * @throws InvalidFieldException if any of the fields are invalid
     */
    public StockDTO(
            int product_id,
            int store_id,
            String product_name,
            String store_name,
            int quantity,
            LocalDateTime createdAt
    ) throws InvalidFieldException {
        this.product_id = product_id;
        this.store_id = store_id;
        this.product_name = Validator.getValidName(product_name, "product-name", 3, 128);
        this.store_name = Validator.getValidName(store_name, "store-name", 3, 128);
        this.quantity = quantity;
        this.createdAt = createdAt;
    }

    public int getProduct_id() {
        return product_id;
    }

    public int getStore_id() {
        return store_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public String getStore_name() {
        return store_name;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
