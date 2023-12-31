public class Product{
    private Integer productCode;
    private String productName;
    private String productType;
    private Integer inventoryCount;
    private Double pricePerUnit;
    protected String category;

    Product(){

    }

    Product(String productType){

        setProductType(productType);

    }

    Product(Integer code, String name, int inventory, double price, String productType, String category){
        setProductCode(code);
        setCategory(category);
        setProductName(name);
        setInventoryCount(inventory);
        setPricePerUnit(price);
        setProductType(productType);
    }

    public Integer getProductCode() {
        return productCode;
    }

    public void setProductCode(Integer productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public Integer getInventoryCount() {
        return inventoryCount;
    }

    public void setInventoryCount(Integer inventoryCount) {
        this.inventoryCount = inventoryCount;
    }

    public Double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(Double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String toCSV () {
        String str;
        str = getProductCode() + "," + getProductName() + "," + getInventoryCount() 
         + "," + getPricePerUnit()
         + "," + getCategory()
         + ",,," ;
        return str;
    }

    public String toString() {
        String str;
        str = "\tProduct Code: "+getProductCode() + 
        "\n\tProduct Name: " + getProductName() + 
        "\n\tIn Stock: " + getInventoryCount() +
        "\n\tPrice: " + getPricePerUnit() +
        "\n\tCategory: " + getCategory() 
        ;
        return str;
    }
    
}
