/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package weee2;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hbakiewicz
 */
public class obj_product {

    product_details product;
    List<product_details> products = new ArrayList<>();

    public product_details getProduct() {
        return product;
    }

    public void setProduct(product_details product) {
        this.product = product;
    }

    public List<product_details> getProducts() {
        return products;
    }

    public void setProducts(List<product_details> products) {
        this.products = products;
    }
    
    

}
