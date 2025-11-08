package com.demoy.boardbox_catalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CatalogApplication {

    public static void main(String[] args) {
        var ctx = SpringApplication.run(CatalogApplication.class, args);
        System.out.println("DataSource beans: " + ctx.getBeanNamesForType(javax.sql.DataSource.class).length);
    }

}
