module com.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires lucene.core;
    requires lucene.queryparser;
    requires opencsv;
    requires org.jsoup;
    // requires lucene.analyzers.common;

    opens com.example to javafx.fxml;
    exports com.example;
}
