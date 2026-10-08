package com.cashbook.transactions;

import com.cashbook.auth.AuthService;
import com.cashbook.auth.users.User;
import com.cashbook.businessMember.BusinessAccessService;
import com.cashbook.cashbook.Cashbook;
import com.cashbook.cashbook.CashbookRepository;
import com.cashbook.category.Category;
import com.cashbook.category.CategoryRepository;
import com.cashbook.contacts.Contact;
import com.cashbook.contacts.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final CashbookRepository cashbookRepository;
    private final CategoryRepository categoryRepository;
    private final ContactRepository contactRepository;
    private final AuthService authService;
    private final BusinessAccessService businessAccessService;

    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Create a transaction.
     * OWNER and ACCOUNTANT are allowed.
     */
    public TransactionResponseDto create(TransactionRequestDto request) {

        Cashbook cashbook = getCashbook(request.getCashbookId());

        Long businessId = getBusinessId(cashbook);

        businessAccessService.verifyCanCreateTransaction(businessId);

        Category category = getCategoryForBusiness(
                request.getCategoryId(),
                cashbook
        );

        Contact contact = getContactForBusiness(
                request.getContactId(),
                cashbook
        );

        User currentUser = authService.getCurrentUser();

        BigDecimal previousBalance = getCurrentBalance(cashbook);

        BigDecimal runningBalance = calculateBalance(
                previousBalance,
                request.getType(),
                request.getAmount()
        );

        Transaction transaction = new Transaction();

        transaction.setTransactionNumber(generateTransactionNumber());
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setType(request.getType());
        transaction.setAmount(request.getAmount());
        transaction.setRunningBalance(runningBalance);
        transaction.setRemarks(request.getRemarks());
        transaction.setPaymentMode(request.getPaymentMode());
        transaction.setCashbook(cashbook);
        transaction.setCategory(category);
        transaction.setContact(contact);
        transaction.setCreatedBy(currentUser);

        Transaction saved = transactionRepository.save(transaction);

        return transactionMapper.toResponse(saved);
    }

    /**
     * Get all transactions the current user is permitted to view.
     * OWNER, ACCOUNTANT and VIEWER can view transactions.
     */
    @Transactional(readOnly = true)
    public List<TransactionResponseDto> getAll() {

        return transactionRepository.findAll()
                .stream()
                .filter(transaction ->
                        canViewBusiness(
                                getBusinessId(transaction.getCashbook())
                        )
                )
                .map(transactionMapper::toResponse)
                .toList();
    }

    /**
     * Get one transaction.
     * The user's business role must allow viewing.
     */
    @Transactional(readOnly = true)
    public TransactionResponseDto getById(Long id) {

        Transaction transaction = getTransaction(id);

        businessAccessService.verifyCanView(
                getBusinessId(transaction.getCashbook())
        );

        return transactionMapper.toResponse(transaction);
    }

    /**
     * Get transactions belonging to a particular cashbook.
     */
    @Transactional(readOnly = true)
    public List<TransactionResponseDto> getByCashbook(Long cashbookId) {

        Cashbook cashbook = getCashbook(cashbookId);

        businessAccessService.verifyCanView(getBusinessId(cashbook));

        return transactionRepository
                .findByCashbookIdOrderByTransactionDateDesc(cashbookId)
                .stream()
                .map(transactionMapper::toResponse)
                .toList();
    }

    /**
     * Update a transaction.
     * OWNER and ACCOUNTANT are allowed.
     */
    public TransactionResponseDto update(
            Long id,
            TransactionRequestDto request) {

        Transaction transaction = getTransaction(id);

        Cashbook oldCashbook = transaction.getCashbook();

        // Check permission for the transaction's existing business.
        businessAccessService.verifyCanUpdateTransaction(
                getBusinessId(oldCashbook)
        );

        Cashbook newCashbook = getCashbook(request.getCashbookId());

        // Also check permission for the destination business.
        businessAccessService.verifyCanUpdateTransaction(
                getBusinessId(newCashbook)
        );

        Category category = getCategoryForBusiness(
                request.getCategoryId(),
                newCashbook
        );

        Contact contact = getContactForBusiness(
                request.getContactId(),
                newCashbook
        );

        // Preserve the original creator.
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setType(request.getType());
        transaction.setAmount(request.getAmount());
        transaction.setRemarks(request.getRemarks());
        transaction.setPaymentMode(request.getPaymentMode());
        transaction.setCashbook(newCashbook);
        transaction.setCategory(category);
        transaction.setContact(contact);

        Transaction updated = transactionRepository.save(transaction);

        // Recalculate the balances of the affected cashbooks.
        recalculateBalances(oldCashbook);

        if (!oldCashbook.getId().equals(newCashbook.getId())) {
            recalculateBalances(newCashbook);
        }

        return transactionMapper.toResponse(updated);
    }

    /**
     * Delete a transaction.
     * Only OWNER is allowed.
     */
    public void delete(Long id) {

        Transaction transaction = getTransaction(id);

        Cashbook cashbook = transaction.getCashbook();

        businessAccessService.verifyCanDeleteTransaction(
                getBusinessId(cashbook)
        );

        transactionRepository.delete(transaction);
        transactionRepository.flush();

        recalculateBalances(cashbook);
    }

    private Cashbook getCashbook(Long cashbookId) {
        return cashbookRepository.findById(cashbookId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Cashbook not found"
                        )
                );
    }

    private Transaction getTransaction(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Transaction not found"
                        )
                );
    }

    /**
     * Get the business ID associated with a cashbook.
     */
    private Long getBusinessId(Cashbook cashbook) {

        if (cashbook.getBusiness() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cashbook is not associated with a business"
            );
        }

        return cashbook.getBusiness().getId();
    }

    /**
     * Used by getAll() to exclude businesses the user cannot view.
     * Only a 403 is treated as lack of access; other errors propagate.
     */
    private boolean canViewBusiness(Long businessId) {
        try {
            businessAccessService.verifyCanView(businessId);
            return true;

        } catch (ResponseStatusException exception) {
            System.err.println(
                    "Transaction viewing denied for businessId="
                            + businessId
                            + ", status="
                            + exception.getStatusCode()
                            + ", reason="
                            + exception.getReason()
            );

            if (exception.getStatusCode().value() == 403) {
                return false;
            }

            throw exception;
        }
    }



    private Category getCategoryForBusiness(
            Long categoryId,
            Cashbook cashbook) {

        if (categoryId == null) {
            return null;
        }

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Category not found"
                        )
                );

        if (category.getBusiness() == null
                || !category.getBusiness().getId()
                .equals(getBusinessId(cashbook))) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Category does not belong to this business"
            );
        }

        return category;
    }

    private Contact getContactForBusiness(
            Long contactId,
            Cashbook cashbook) {

        if (contactId == null) {
            return null;
        }

        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Contact not found"
                        )
                );

        if (contact.getBusiness() == null
                || !contact.getBusiness().getId()
                .equals(getBusinessId(cashbook))) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Contact does not belong to this business"
            );
        }

        return contact;
    }

    private BigDecimal calculateBalance(
            BigDecimal previousBalance,
            TransactionType type,
            BigDecimal amount) {

        if (type == TransactionType.CASH_IN) {
            return previousBalance.add(amount);
        }

        if (type == TransactionType.CASH_OUT) {
            return previousBalance.subtract(amount);
        }

        throw new IllegalArgumentException(
                "Unsupported transaction type"
        );
    }

    private BigDecimal getCurrentBalance(Cashbook cashbook) {

        return transactionRepository
                .findTopByCashbookOrderByIdDesc(cashbook)
                .map(Transaction::getRunningBalance)
                .orElse(
                        cashbook.getOpeningBalance() != null
                                ? cashbook.getOpeningBalance()
                                : BigDecimal.ZERO
                );
    }

    /**
     * Recalculate balances chronologically after an update or deletion.
     */
    private void recalculateBalances(Cashbook cashbook) {

        BigDecimal balance = cashbook.getOpeningBalance() != null
                ? cashbook.getOpeningBalance()
                : BigDecimal.ZERO;

        List<Transaction> transactions =
                transactionRepository
                        .findByCashbookIdOrderByTransactionDateDesc(
                                cashbook.getId()
                        );

        transactions = transactions.stream()
                .sorted(
                        Comparator
                                .comparing(Transaction::getTransactionDate)
                                .thenComparing(Transaction::getId)
                )
                .toList();

        for (Transaction transaction : transactions) {

            balance = calculateBalance(
                    balance,
                    transaction.getType(),
                    transaction.getAmount()
            );

            transaction.setRunningBalance(balance);
        }

        transactionRepository.saveAll(transactions);
    }

    private String generateTransactionNumber() {

        String transactionNumber;

        do {
            transactionNumber = String.format(
                    "TXN-%06d",
                    secureRandom.nextInt(1_000_000)
            );

        } while (
                transactionRepository
                        .existsByTransactionNumber(transactionNumber)
        );

        return transactionNumber;
    }
}
