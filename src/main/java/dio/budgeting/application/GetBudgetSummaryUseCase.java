package dio.budgeting.application;

import dio.budgeting.domain.Category;
import dio.budgeting.domain.TransactionRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class GetBudgetSummaryUseCase {
    private final TransactionRepository transactionRepository;

    public GetBudgetSummaryUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Tool(name = "get-budget-summary", description = "Resume gastos por categoria, com quantidade e total em centavos")
    public List<BudgetSummary> execute() {
        var transactions = transactionRepository.findAll();

        return Arrays.stream(Category.values())
                .map(category -> {
                    var categoryTransactions = transactions.stream()
                            .filter(transaction -> transaction.getCategory() == category)
                            .toList();
                    var totalAmount = categoryTransactions.stream()
                            .mapToLong(transaction -> transaction.getAmount())
                            .sum();
                    return new BudgetSummary(category, categoryTransactions.size(), totalAmount);
                })
                .toList();
    }

    public record BudgetSummary(Category category, int transactionCount, long totalAmount) {
    }
}

