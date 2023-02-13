package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.validators.interfaces.ValidCategoryConstraint;

public class SellNftForm extends PriceForm {
    @ValidCategoryConstraint(errorMessageTemplate = "InvalidCategoryException.message")
    private String category;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}
