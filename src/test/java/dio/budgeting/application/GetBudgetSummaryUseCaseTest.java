package dio.budgeting.application;

import dio.budgeting.domain.Category;
import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GetBudgetSummaryUseCaseTest {

    @Test
    void summarizesTransactionsByCategoryIncludingEmptyCategories() {
        TransactionRepository repository = new TransactionRepository() {
            @Override
            public Transaction save(Transaction transaction) {
                return transaction;
            }

            @Override
            public List<Transaction> findAllByCategory(Category category) {
                return findAll().stream().filter(transaction -> transaction.getCategory() == category).toList();
            }

            @Override
            public List<Transaction> findAll() {
                return List.of(
                        new Transaction("mercado", 1_250, Category.GROCERIES),
                        new Transaction("farmácia", 750, Category.PHARMA),
                        new Transaction("padaria", 300, Category.GROCERIES)
                );
            }
        };

        var summary = new GetBudgetSummaryUseCase(repository).execute();

        assertEquals(3, summary.size());
        assertEquals(new GetBudgetSummaryUseCase.BudgetSummary(Category.GROCERIES, 2, 1_550), summary.get(0));
        assertEquals(new GetBudgetSummaryUseCase.BudgetSummary(Category.PHARMA, 1, 750), summary.get(1));
        assertEquals(new GetBudgetSummaryUseCase.BudgetSummary(Category.AUTO, 0, 0), summary.get(2));
    }
}

