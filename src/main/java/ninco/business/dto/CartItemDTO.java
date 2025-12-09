package ninco.business.dto;

import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * CartItemDTO representa un producto agregado al carrito de compras temporal.
 * Utiliza propiedades de JavaFX para facilitar la actualización automática de la tabla.
 */
public class CartItemDTO {
    private final ProductDTO product;
    private final SimpleStringProperty name;
    private final SimpleIntegerProperty quantity;
    private final SimpleFloatProperty price;
    private final SimpleFloatProperty subtotal;

    public CartItemDTO(ProductDTO product, int quantity) {
        this.product = product;
        this.name = new SimpleStringProperty(product.getName());
        this.quantity = new SimpleIntegerProperty(quantity);
        this.price = new SimpleFloatProperty(product.getPrice());
        this.subtotal = new SimpleFloatProperty(product.getPrice() * quantity);
    }

    public ProductDTO getProduct() { return product; }

    public String getName() { return name.get(); }
    public SimpleStringProperty nameProperty() { return name; }

    public int getQuantity() { return quantity.get(); }
    public void setQuantity(int qty) {
        this.quantity.set(qty);
        this.subtotal.set(qty * this.price.get());
    }
    public SimpleIntegerProperty quantityProperty() { return quantity; }

    public float getPrice() { return price.get(); }
    public SimpleFloatProperty priceProperty() { return price; }

    public float getSubtotal() { return subtotal.get(); }
    public SimpleFloatProperty subtotalProperty() { return subtotal; }
}