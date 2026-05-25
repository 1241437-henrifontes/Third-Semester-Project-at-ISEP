package Repositories;

import Model.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository responsible for loading, validating, and storing product returns.
 */
public class ReturnRepository {
    private List<Return> returns = new ArrayList<>();
    private static ReturnRepository instance = new ReturnRepository();

    private ReturnRepository() {
    }

    /** Returns the ReturnRepository singleton. */
    public static ReturnRepository getInstance() {
        return instance;
    }

    /** Returns a copy of all stored returns. */
    public List<Return> getAllReturns() {
        return new ArrayList<>(returns);
    }

    /** Adds a return to the internal list. */
    public void addReturn(Return r) {
        returns.add(r);
    }

    /**
     * Loads returns from the CSV source named "returns", validating fields and formats.
     * <p>
     * Validations include: existing SKU, positive quantity, valid reason, timestamp, and an optional expiry date
     * accepted either as yyyy-MM-dd or yyyy-MM-ddTHH:mm:ss.
     *
     * @param itemRepository repository used to validate SKU existence
     */
    public void loadReturns(ItemRepository itemRepository) {
        returns = ReadFromCSV.readFile("returns", campos -> {
            if (campos.length < 5) {
                throw new IllegalArgumentException("Invalid return record: expected at least 5 columns but got " + campos.length);
            }

            String returnId = campos[0];
            String skuStr = campos[1];
            String qtyStr = campos[2];
            String reasonStr = campos[3];
            String tsStr = campos[4];
            String expiryStr = (campos.length >= 6) ? campos[5].trim() : "";

            if (skuStr == null || skuStr.isEmpty()) {
                throw new IllegalArgumentException("Missing SKU in returns.csv record for ReturnId '" + returnId + "'.");
            }
            if (!skuStr.matches("SKU\\d{4}")) {
                throw new IllegalArgumentException("Invalid SKU '" + skuStr + "' in returns.csv for ReturnId '" + returnId + "': expected 'SKU' followed by 4 digits.");
            }

            Item item = itemRepository.getItemBySKU(new SKU(skuStr));
            if (item == null) {
                throw new IllegalArgumentException("SKU not found: '" + skuStr + "' for ReturnId '" + returnId + "'.");
            }
            SKU sku = item.getSku();

            int quantity;
            try {
                quantity = Integer.parseInt(qtyStr);
            } catch (NumberFormatException nfe) {
                throw new IllegalArgumentException("Invalid quantity '" + qtyStr + "' for ReturnId '" + returnId + "': must be an integer.");
            }
            if (quantity <= 0) {
                throw new IllegalArgumentException("Invalid quantity '" + qtyStr + "' for ReturnId '" + returnId + "': must be > 0.");
            }

            ReturnReason reason;
            try {
                reason = ReturnReason.valueOf(reasonStr.toUpperCase().replace("-", "_"));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Unknown reason '" + reasonStr + "' for ReturnId '" + returnId + "'.");
            }

            LocalDateTime timestamp;
            try {
                timestamp = LocalDateTime.parse(tsStr);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid timestamp '" + tsStr + "' for ReturnId '" + returnId + "': expected ISO-8601 format yyyy-MM-ddTHH:mm:ss.");
            }

            LocalDateTime expiryDate = null;
            if (!expiryStr.isEmpty()) {
                try {
                    expiryDate = LocalDateTime.parse(expiryStr);
                } catch (DateTimeParseException e1) {
                    try {
                        LocalDate d = LocalDate.parse(expiryStr);
                        expiryDate = d.atStartOfDay();
                    } catch (DateTimeParseException e2) {
                        throw new IllegalArgumentException("Invalid expiryDate '" + expiryStr + "' for ReturnId '" + returnId + "': expected yyyy-MM-dd or yyyy-MM-ddTHH:mm:ss.");
                    }
                }
            }

            return new Return(returnId, sku, quantity, timestamp, expiryDate, reason);
        });

    }
}
