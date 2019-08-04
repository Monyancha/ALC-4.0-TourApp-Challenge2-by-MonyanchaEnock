package com.example.item;

public class ItemCategory {

    private String CategoryId;
    private String CategoryName;
    private String CategoryImageBig;
    private String CategoryTotal;

    public String getCategoryTotal() {
        return CategoryTotal;
    }

    public void setCategoryTotal(String categoryTotal) {
        CategoryTotal = categoryTotal;
    }

    public String getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(String CategoryId) {
        this.CategoryId = CategoryId;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String CategoryName) {
        this.CategoryName = CategoryName;
    }

    public String getCategoryImageBig() {
        return CategoryImageBig;
    }

    public void setCategoryImageBig(String CategoryImageBig) {
        this.CategoryImageBig = CategoryImageBig;
    }


}
