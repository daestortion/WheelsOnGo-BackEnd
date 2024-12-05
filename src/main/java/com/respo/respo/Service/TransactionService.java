package com.respo.respo.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.respo.respo.Entity.TransactionEntity;
import com.respo.respo.Repository.TransactionRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    // Create a new transaction
    public TransactionEntity createTransaction(TransactionEntity transaction) {
        return transactionRepository.save(transaction);
    }

    // Get all transactions
    public List<TransactionEntity> getAllTransactions() {
        return transactionRepository.findAll();
    }

    // Get a specific transaction by ID
    public Optional<TransactionEntity> getTransactionById(int transactionId) {
        return transactionRepository.findById(transactionId);
    }

    // Delete a transaction by ID
    public void deleteTransaction(int transactionId) {
        transactionRepository.deleteById(transactionId);
    }

    // You can add more service methods as needed, e.g., for handling specific filters or logic
}
