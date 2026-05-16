package com.gasagency.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gasagency.model.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    Product findByProductName(String productName);

}