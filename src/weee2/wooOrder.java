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
public class wooOrder {

    int id;
    int order_number;
    String created_at;
    String updated_at;
    String completed_at;
    String status;
    String currency;
    String total;
    String subtotal;
    int total_line_items_quantity;
    String total_tax;
    String total_netto;
    String total_shipping;
    String total_discount;
    String shipping_methods;
    String payment_details;
    String method_id;
    String method_title;
    boolean paid;
    List<wooOrderLine> orderPoz = new ArrayList<>();
    String konitrid;
    String UzId;
    String MagId;
    boolean valid = true;
    String comment ;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    
    
    
    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
    
    

    public String getUzId() {
        return UzId;
    }

    public void setUzId(String UzId) {
        this.UzId = UzId;
    }

    public String getMagId() {
        return MagId;
    }

    public void setMagId(String MagId) {
        this.MagId = MagId;
    }

    public String getKonitrid() {
        return konitrid;
    }

    public void setKonitrid(String konitrid) {
        this.konitrid = konitrid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrder_number() {
        return order_number;
    }

    public void setOrder_number(int order_number) {
        this.order_number = order_number;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getCompleted_at() {
        return completed_at;
    }

    public void setCompleted_at(String completed_at) {
        this.completed_at = completed_at;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    public int getTotal_line_items_quantity() {
        return total_line_items_quantity;
    }

    public void setTotal_line_items_quantity(int total_line_items_quantity) {
        this.total_line_items_quantity = total_line_items_quantity;
    }

    public String getTotal_tax() {
        return total_tax;
    }

    public void setTotal_tax(String total_tax) {
        this.total_tax = total_tax;
    }

    public String getTotal_shipping() {
        return total_shipping;
    }

    public void setTotal_shipping(String total_shipping) {
        this.total_shipping = total_shipping;
    }

    public String getTotal_discount() {
        return total_discount;
    }

    public void setTotal_discount(String total_discount) {
        this.total_discount = total_discount;
    }

    public String getShipping_methods() {
        return shipping_methods;
    }

    public void setShipping_methods(String shipping_methods) {
        this.shipping_methods = shipping_methods;
    }

    public String getPayment_details() {
        return payment_details;
    }

    public void setPayment_details(String payment_details) {
        this.payment_details = payment_details;
    }

    public String getMethod_id() {
        return method_id;
    }

    public void setMethod_id(String method_id) {
        this.method_id = method_id;
    }

    public String getMethod_title() {
        return method_title;
    }

    public void setMethod_title(String method_title) {
        this.method_title = method_title;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public List<wooOrderLine> getOrderPoz() {
        return orderPoz;
    }

    public void setOrderPoz(List<wooOrderLine> orderPoz) {
        this.orderPoz = orderPoz;
    }

}
