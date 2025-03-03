package tn.esprit.market_3a33.Controllers;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentMethodCreateParams;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import tn.esprit.market_3a33.services.OrderService;

public class OrderDetailsModalController {
    @FXML private Label notificationLabel;
    @FXML private Label totalPriceLabel;
    @FXML private TextField cardNumberField;
    @FXML private TextField expMonthField;
    @FXML private TextField expYearField;
    @FXML private TextField cvcField;

    private double totalAmount;
    private int orderId; // Store the order ID
    private OrderService orderService = new OrderService();

    @FXML
    public void initialize() {
        // Add input filters
        addNumericFilter(expMonthField);
        addNumericFilter(expYearField);
        addNumericFilter(cvcField);
    }

    private void addNumericFilter(TextField field) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                field.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    public void setNotification(String notification) {
        notificationLabel.setText(notification);
        notificationLabel.setTextFill(Color.BLACK);

        String[] parts = notification.split("Total: ");
        if (parts.length > 1) {
            String amountStr = parts[1].replaceAll("[^\\d.]", "");
            totalAmount = Double.parseDouble(amountStr);
            totalPriceLabel.setText("Total: " + parts[1]);
        }
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId; // Set the order ID
    }

    @FXML
    private void handlePay() {
        try {
            if (!validateInputs()) return;

            // Replace with your test key from Stripe dashboard
            Stripe.apiKey = "sk_test_51QxnDTFWZeAFfqCkudE2TFt3kPgzJmlce0Ix1NwROvJ1NovjIbHPxCOQpB9N8V5PrAqTUZBwhjczhvD6TWYJSozH00Qx30caIL"; // Get from https://dashboard.stripe.com/test/apikeys

            PaymentMethodCreateParams cardParams = PaymentMethodCreateParams.builder()
                    .setType(PaymentMethodCreateParams.Type.CARD)
                    .setCard(PaymentMethodCreateParams.CardDetails.builder()
                            .setNumber(cardNumberField.getText().replaceAll("\\s+", ""))
                            .setExpMonth(Long.parseLong(expMonthField.getText()))
                            .setExpYear(Long.parseLong(expYearField.getText()))
                            .setCvc(cvcField.getText())
                            .build())
                    .build();

            PaymentMethod paymentMethod = PaymentMethod.create(cardParams);

            PaymentIntentCreateParams createParams = PaymentIntentCreateParams.builder()
                    .setAmount((long) (totalAmount * 100))
                    .setCurrency("usd")
                    .setPaymentMethod(paymentMethod.getId())
                    .setConfirm(true)
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(createParams);

            if ("succeeded".equals(paymentIntent.getStatus())) {
                showSuccess("Payment successful! Transaction ID: " + paymentIntent.getId());
                closeWindow();
            } else {
                showError("Payment failed: " + paymentIntent.getLastPaymentError().getMessage());
            }
        } catch (StripeException | NumberFormatException e) {
            showError("Payment error: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteOrder() {
        if (orderId <= 0) {
            showError("No order selected for deletion.");
            return;
        }

        // Delete the order and its associated order items
        orderService.deleteOrder(orderId);
        showSuccess("Order and associated items deleted successfully.");
        closeWindow();
    }

    private boolean validateInputs() {
        if (cardNumberField.getText().isBlank() ||
                expMonthField.getText().isBlank() ||
                expYearField.getText().isBlank() ||
                cvcField.getText().isBlank()) {
            showError("Please fill all payment fields");
            return false;
        }

        // Validate card number length
        String cardNumber = cardNumberField.getText().replaceAll("\\s+", "");
        if (cardNumber.length() != 16) {
            showError("Invalid card number (must be 16 digits)");
            return false;
        }

        // Validate expiration month
        try {
            int month = Integer.parseInt(expMonthField.getText());
            if (month < 1 || month > 12) {
                showError("Invalid expiration month (01-12)");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Invalid expiration month");
            return false;
        }

        return true;
    }

    private void showError(String message) {
        notificationLabel.setTextFill(Color.RED);
        notificationLabel.setText(message);
    }

    private void showSuccess(String message) {
        notificationLabel.setTextFill(Color.GREEN);
        notificationLabel.setText(message);
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        cardNumberField.getScene().getWindow().hide();
    }
}