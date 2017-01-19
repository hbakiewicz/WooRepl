/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weee2;

/**
 *
 * @author hbakiewicz
 */
public class wooOrderLine {
    
int id;
String subtotal;
String subtotal_tax;
String total;
String tax_level;
String price;
int quantity;
String tax_class;
String name;
int product_id;
String PcmTowId;
String sku;

    public wooOrderLine(int id, String total, String price, int quantity, String name, int product_id) {
        this.id = id;
        this.total = total;
        this.price = price;
        this.quantity = quantity;
        this.name = name;
        this.product_id = product_id;
    }

    public String getPcmTowId() {
        return PcmTowId;
    }

    public void setPcmTowId(String PcmTowId) {
        this.PcmTowId = PcmTowId;
    }
    

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    public String getSubtotal_tax() {
        return subtotal_tax;
    }

    public void setSubtotal_tax(String subtotal_tax) {
        this.subtotal_tax = subtotal_tax;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getTax_level() {
        return tax_level;
    }

    public void setTax_level(String tax_level) {
        this.tax_level = tax_level;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getTax_class() {
        return tax_class;
    }

    public void setTax_class(String tax_class) {
        this.tax_class = tax_class;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    
}
