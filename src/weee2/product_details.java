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
public class product_details {
    int stock_quantity;
    int id;

    public int getStock_quantity() {
        return stock_quantity;
    }

    public void setStock_quantity(int stock_quantity) {
        this.stock_quantity = stock_quantity;
    }

    public product_details(int stock_quantity) {
        this.stock_quantity = stock_quantity;
    }

    public product_details(int stock_quantity, int id) {
        this.stock_quantity = stock_quantity;
        this.id = id;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

  
    
    
    
}
