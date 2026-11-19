import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public record Expense(
        LocalDate date,
        BigDecimal amount,
        String category,
        boolean mandatory

) {
    public Expense {
        Objects.requireNonNull(date, "date is null");

        Objects.requireNonNull(amount, "amount is null");

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount must be greater than zero");
        }

        Objects.requireNonNull(category, "category is null");

        if (category.isBlank()) {
            throw new IllegalArgumentException("category is empty");
        }
    }
}
