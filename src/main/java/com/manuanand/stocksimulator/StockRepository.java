package com.manuanand.stocksimulator;

import org.springframework.data.repository.CrudRepository;

import com.manuanand.stocksimulator.Stock;

// This will be AUTO IMPLEMENTED by Spring into a Bean called nodeRepository
// CRUD refers Create, Read, Update, Delete

public interface StockRepository extends CrudRepository<Stock, Integer> {

}
