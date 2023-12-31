import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.converter.DoubleStringConverter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;
public class Controller implements Initializable{

    /*Initialising objects from the fxml file to make use of the layouts.*/
    @FXML
    private TableView<Product> tableViewInventory;

    @FXML
    private TableColumn<Product, Integer> productCode;

    @FXML
    private TableColumn<Product, String> productName;

    @FXML
    private TableColumn<Product, String> productType;

    @FXML
    private TableColumn<Product, Integer> quantity;

    @FXML
    private TableColumn<Product, Double> price;

    @FXML
    private TableColumn<Product, String> category;

    @FXML
    private TableColumn<Clothing, String> size;

    @FXML
    private TableColumn<Clothing, String> color;

    @FXML
    private Button buttonAdd;

    @FXML
    private ComboBox<String> comboboxProductType;

    @FXML
    private TextField textViewProductName;

    @FXML
    private TextField textFieldPrice;

    @FXML
    private Spinner<Integer> spinnerQuantity;

    @FXML
    private Label labelSize;

    @FXML
    private Label labelColor;

    @FXML
    private TextField textFieldSize, textFieldColor;
    
    @FXML
    private TextField textFieldCategory;

    @FXML
    private TextArea textAreaReport;

    DecimalFormat df=new DecimalFormat("#,###.00");


    private Product currentProduct = null;
    private final ObservableList<Product> products = FXCollections.observableArrayList();
    private final HashMap<Integer, Product> productsMap = new HashMap <>();

    /*Function to get the Products hashmap.*/
    public HashMap<Integer, Product> getProductsMap(){
        return this.productsMap;
    }

    /*Function to set the Products hashmap.*/
    public void setProductsMap(HashMap<Integer, Product> initialProductsMap){
        products.clear();
        productsMap.clear();
        productsMap.putAll(initialProductsMap);
        products.addAll(initialProductsMap.values());
        lastCode=productsMap.keySet().stream().max(Integer::compare).get();
    }

    /*initialize function that makes use of the fxml layout and makes all the components work. */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /*Setting Report Text Area Editable as false.*/
        textAreaReport.setEditable(false);
        /*Initialising the comboBox drop down to have the 2 options for the Products Type. */
        comboboxProductType.getItems().addAll(Consts.CLOTHING, Consts.ACCESSORIES);
        
        /*Formating the textField for Price and Spinner for  quantity.*/
        textFieldPrice.setTextFormatter(new TextFormatter<Double>(new DoubleStringConverter()));
        spinnerQuantity.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 9999, 1, 1));

        // Validating Price Textfield to take only Double values
        // Ref: https://stackoverflow.com/a/31043122/6013612
        DecimalFormat format = new DecimalFormat( "#.##" );
        textFieldPrice.setTextFormatter( new TextFormatter<>(c ->
        {
            if ( c.getControlNewText().isEmpty() )
                return c;

            ParsePosition parsePosition = new ParsePosition( 0 );
            Object object = format.parse( c.getControlNewText(), parsePosition );

            if ( object == null || parsePosition.getIndex() < c.getControlNewText().length() )
                return null;
            else
                return c;
        }));
        
        /*Since Size and Color is only the attribute of Clothing Class, 
        disabling the inut components when it is Accesories.*/
        comboboxProductType.valueProperty().addListener((ov, oldval, newval) -> {
            
            if(Consts.CLOTHING.equalsIgnoreCase(newval)){
                currentProduct = new Clothing();
                labelColor.setDisable(false);
                labelSize.setDisable(false);
                textFieldColor.setDisable(false);
                textFieldSize.setDisable(false);
            }else if(Consts.ACCESSORIES.equalsIgnoreCase(newval)){
                currentProduct = new Accessories();
                labelColor.setDisable(true);
                labelSize.setDisable(true);
                textFieldColor.setDisable(true);
                textFieldSize.setDisable(true);
            }
            
        });

        /*Setting the default for the dropdown to select the clothing.*/
        comboboxProductType.getSelectionModel().selectFirst();

        /*Creating object depending on the producType selected.*/
        if(comboboxProductType.getValue().equalsIgnoreCase(Consts.CLOTHING)){
            currentProduct = new Clothing();
        }else if(comboboxProductType.getValue().equalsIgnoreCase(Consts.ACCESSORIES)){
            currentProduct=new Accessories();
        }
        
        /*Setting the table view with products from the list that we got from the csv file. */
        tableViewInventory.setItems(products);
        /*Linking each column to the respective attribute to get the values in the table view. */
        productCode.setCellValueFactory(new PropertyValueFactory<>("productCode"));
        productName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        productType.setCellValueFactory(new PropertyValueFactory<>("productType"));
        quantity.setCellValueFactory(new PropertyValueFactory<>("inventoryCount"));
        price.setCellValueFactory(new PropertyValueFactory<>("pricePerUnit"));
        category.setCellValueFactory(new PropertyValueFactory<>("category"));
        size.setCellValueFactory(new PropertyValueFactory<>("size"));
        color.setCellValueFactory(new PropertyValueFactory<>("color"));

        /*When a selection on the table view is made the input components gets
         updated with the selected objects value for updating that product. */
        tableViewInventory.getSelectionModel().selectedItemProperty().addListener((observable, oldVal, newVal) -> {
            LogUtil.printLog("selected=> "+newVal);
            setCurrentProduct(newVal);
        });


    }

    /*Variable that saves the id values so that all the new added Products have uniques identifiers. */
    Integer lastCode=0;

    /*Function called when Add/Update button is clicked.*/
    @FXML
    private void addActionClicked(ActionEvent event){
        
        if(currentProduct != null ){
            System.out.println("Product doe :" + currentProduct.getProductCode());
        }
        if (currentProduct.getProductCode() != null ){
            LogUtil.printLog("Update Item");
            getAllFields(productsMap.get(currentProduct.getProductCode()));
            tableViewInventory.refresh();
            reportGeneration("Product Code "+currentProduct.getProductCode()+ " has been updated!\n"
            +productsMap.get(currentProduct.getProductCode()));
        }else {
            LogUtil.printLog("Add Item");
            if (comboboxProductType.getValue().equalsIgnoreCase(Consts.CLOTHING)) {
                currentProduct = new Clothing(++lastCode,
                        textViewProductName.getText(),
                        spinnerQuantity.getValue(),
                        Double.parseDouble(textFieldPrice.getText()),
                        textFieldSize.getText(),
                        textFieldColor.getText(),
                        textFieldCategory.getText());

            } else if (comboboxProductType.getValue().equalsIgnoreCase(Consts.ACCESSORIES)) {
                currentProduct = new Accessories(++lastCode,
                        textViewProductName.getText(),
                        spinnerQuantity.getValue(),
                        Double.parseDouble(textFieldPrice.getText()),
                        textFieldCategory.getText());
                
            }
            products.add(currentProduct);
            productsMap.put(currentProduct.getProductCode(), currentProduct);
            reportGeneration("New Product Added with Product Code: "+lastCode+"!\n"+currentProduct);
        }

        setCurrentProduct(null);
        writeToFile();
    }

    /*Function when the Reset button is clicked. */
    @FXML
    private void cancelActionClicked(ActionEvent event){
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setHeaderText("Are you sure you want to cancel?");
        alert.setTitle("Cancelling");
        alert.getButtonTypes().remove(0,2);
        alert.getButtonTypes().add(0, ButtonType.YES);
        alert.getButtonTypes().add(1, ButtonType.NO);
        Optional<ButtonType> confirmationResponse =  alert.showAndWait();
        if(confirmationResponse.get() == ButtonType.YES){
            setCurrentProduct(null);
            tableViewInventory.getSelectionModel().clearSelection();   
        }
        
    }

    /*Function when Generate Report Button is clicked. */
    @FXML
    private void generateReportActionClicked(ActionEvent event){
        reportGeneration("Generate Report");
    }

    private void writeToFile(){
        BufferedWriter fo = null;

        try {
            fo = new BufferedWriter(new FileWriter(Consts.FILENAME));
            for (Integer i : productsMap.keySet()) {
                fo.append(productsMap.get(i).toCSV());
            }

            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.printError("Exception occured.");
            LogUtil.printError(e.toString());
            if (fo!=null){
                try{
                    fo.close();
                }catch(Exception e2){
                    LogUtil.printError("Exception occured while cloing the bufferedwriter.");
                }
            }
        }
    }

    /*Report generation */
    private void reportGeneration(String addOrUpdate){
        String report=addOrUpdate +"\n------------------------------------------------\n";

        String[] categoryArray= new String[10];
        int[] productTypeArray=new int[2];//0 clothing, 1 Accesorries
        double[] productTypePriceArray=new double[2];
        int[] categoryCountInventory=new int[10];
        double[] categoryPrice=new double[10];
        boolean exists;
        int countCat=0;

        for (int i =0;i<products.size();i++) {
            exists=false;
            String cat=products.get(i).getCategory();
            /*Counting the productTypes in the inventory based on the object type*/
            if (products.get(i) instanceof Clothing) {
                productTypeArray[0] += products.get(i).getInventoryCount();
                productTypePriceArray[0] += (products.get(i).getInventoryCount()*products.get(i).getPricePerUnit());
            }else if (products.get(i) instanceof Accessories) {
                productTypeArray[1] += products.get(i).getInventoryCount();
                productTypePriceArray[1] += (products.get(i).getInventoryCount()*products.get(i).getPricePerUnit());
            }

            /*Displaying the list of items that are low in stock, inventory count is less than 3*/
            if(products.get(i).getInventoryCount()<4){
                report +="Low in Stock!";

                report+="\n\tProduct Code: "+products.get(i).getProductCode()
                        +"\n\tProduct Name: "+products.get(i).getProductName()
                        +"\n\tIn Stock: "+products.get(i).getInventoryCount();
                report += "\n\t****************\n";
            }

            /*Counting based on the category*/
            for(int j=0; j<categoryArray.length;j++){
                if(categoryArray[j]!=null && cat.equalsIgnoreCase(categoryArray[j])){
                    exists=true;
                    categoryCountInventory[j]+=products.get(i).getInventoryCount();
                    categoryPrice[j]+=(products.get(i).getInventoryCount()*products.get(i).getPricePerUnit());
                    break;
                }
            }
            if (!exists){
                categoryArray[countCat]=cat;
                categoryCountInventory[countCat]=products.get(i).getInventoryCount();
                categoryPrice[countCat]=products.get(i).getInventoryCount()*products.get(i).getPricePerUnit();
                countCat++;
            }
            
        }

        /*Sorting the category Array List*/
        for (int i = 0; i <countCat; i++) {

            for (int j = 0; j < categoryArray.length - i - 1; j++) {
                if (categoryArray[j+1]!= null && categoryArray[j].compareTo(categoryArray[j + 1])>0) {
                    String temp = categoryArray[j];
                    categoryArray[j] = categoryArray[j + 1];
                    categoryArray[j + 1] = temp;

                    int tempCount = categoryCountInventory[j];
                    categoryCountInventory[j] = categoryCountInventory[j + 1];
                    categoryCountInventory[j + 1] = tempCount;

                    double tempSubTotal = categoryPrice[j];
                    categoryPrice[j] = categoryPrice[j + 1];
                    categoryPrice[j + 1] = tempSubTotal;

                }
            }
        }
        report += "------------------------------------------------\n";

        /**Displaying the sorted Category and the count.**/
        for(int i=0; i<countCat;i++){
            if(categoryArray[i]!=null){
                report= report+"Category: "+categoryArray[i]
                        +"\n\t In Stock: "+categoryCountInventory[i]
                        +"\n\t Sub Total Cost: $"+df.format(categoryPrice[i])+"\n";
            }
        }

        report += "------------------------------------------------\n";
        report += "ProductType: Clothing \n\t In Stock: "+productTypeArray[0]+"\n\t Sub Total Cost: $"+df.format(productTypePriceArray[0])+"\n";
        report += "ProductType: Accessories \n\t In Stock: "+productTypeArray[1]+"\n\t Sub Total Cost: $"+df.format(productTypePriceArray[1])+"\n";
        report += "------------------------------------------------\n";
        report += "Total Cost: $"+df.format(productTypePriceArray[0]+productTypePriceArray[1])+"\n";
        textAreaReport.setText(report);

    }

    /*To set the currentProduct variable and the input componenets. */
    private void setCurrentProduct(Product selectedProduct) {
        if (selectedProduct!=null){
            System.out.println("SetCurrentProduct");
            setAllFields(selectedProduct);
            if(selectedProduct instanceof Clothing){
                currentProduct = new Clothing();
                ((Clothing)currentProduct).setSize(((Clothing)selectedProduct).getSize());
                ((Clothing)currentProduct).setColor(((Clothing)selectedProduct).getColor());
            }else if(selectedProduct instanceof Accessories){
                currentProduct = new Accessories();
            }
            currentProduct.setProductCode(selectedProduct.getProductCode());
            currentProduct.setProductName(selectedProduct.getProductName());
            currentProduct.setCategory(selectedProduct.getCategory());
            currentProduct.setInventoryCount(selectedProduct.getInventoryCount());
            currentProduct.setPricePerUnit(selectedProduct.getPricePerUnit());
            
            buttonAdd.setText("Update");
        } else {
            if(currentProduct instanceof Clothing){
                currentProduct= new Clothing();
                currentProduct.setProductCode(null);
                currentProduct.setProductName("");
                currentProduct.setCategory("");
                currentProduct.setInventoryCount(0);
                currentProduct.setPricePerUnit(0.0);
                ((Clothing)currentProduct).setSize("");
                ((Clothing)currentProduct).setColor("");
            } else if (currentProduct instanceof Accessories){
                currentProduct= new Accessories();
                currentProduct.setProductCode(null);
                currentProduct.setProductName("");
                currentProduct.setCategory("");
                currentProduct.setInventoryCount(0);
                currentProduct.setPricePerUnit(0.0);
            }
            buttonAdd.setText("Add");
            clearAllFields();
        }
    }

    void clearAllFields(){
        textViewProductName.setText("");
        textFieldCategory.setText("");
        textFieldColor.setText("");
        textFieldSize.setText("");
        textFieldPrice.setText("");
        spinnerQuantity.getValueFactory().setValue(1);
    }

    void setAllFields(Product p){
        if(p instanceof Clothing){
            textFieldColor.setText(((Clothing)p).getColor());
            textFieldSize.setText(((Clothing)p).getSize());
        }
        textViewProductName.setText(p.getProductName());
        comboboxProductType.setValue(p.getProductType());
        textFieldCategory.setText(p.getCategory());
        textFieldPrice.setText(p.getPricePerUnit().toString());
        spinnerQuantity.getValueFactory().setValue(p.getInventoryCount());
    }

    Product getAllFields(Product p){
        if(p instanceof Clothing){
            ((Clothing)p).setColor(textFieldColor.getText());
            ((Clothing)p).setSize(textFieldSize.getText());
        }
        p.setProductName(textViewProductName.getText());
        p.setProductType(comboboxProductType.getValue());
        p.setCategory(textFieldCategory.getText());
        p.setPricePerUnit(Double.parseDouble(textFieldPrice.getText()));
        p.setInventoryCount(spinnerQuantity.getValue());
        return p;
    }

    
}
