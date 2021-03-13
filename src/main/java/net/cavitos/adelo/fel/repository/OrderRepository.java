package net.cavitos.adelo.fel.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.cavitos.adelo.fel.domain.model.OrderDetail;

public class OrderRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderRepository.class);

    private static final String ORDER_INFORMATION_QUERY = 
      "SELECT OrderTransactions.OrderID, OrderTransactions.MenuItemID, OrderTransactions.MenuItemUnitPrice, OrderTransactions.Quantity, " +
        " OrderTransactions.ExtendedPrice, OrderTransactions.DiscountAmount, OrderTransactions.DiscountBasis, OrderTransactions.DiscountTaxable," +
        " OrderTransactions.TransactionStatus, MenuItems.MenuItemText, MenuItems.MenuItemDescription" +
        " FROM MenuItems INNER JOIN OrderTransactions ON MenuItems.MenuItemID = OrderTransactions.MenuItemID" +
        " WHERE OrderTransactions.OrderID = ?";

    private final String connectionString;

    public OrderRepository(String connectionString) {

        this.connectionString = connectionString;
    }

    public List<OrderDetail> getOrderDetails(long orderId) {

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        LOGGER.info("query database for orderId: {}", orderId);

        try {

            // connection = DriverManager.getConnection("jdbc:ucanaccess://D:/Adelo/Standard/adResDemo.mdb");
            connection = DriverManager.getConnection(connectionString);
            preparedStatement = connection.prepareStatement(ORDER_INFORMATION_QUERY);
            preparedStatement.setLong(1, orderId);

            resultSet = preparedStatement.executeQuery();

            List<OrderDetail> orderDetails = new ArrayList<OrderDetail>();
            while (resultSet.next()) {

                orderDetails.add(buildOrderDetail(resultSet));
            }

            LOGGER.info("success connection");
            LOGGER.info("found: {} details for orderId: {}", orderDetails.size(), orderId);
            return orderDetails;

        } catch (Exception exception) {

            LOGGER.error("can't get database connection - ", exception);
        } finally {
            if (Objects.nonNull(connection)) {
                
                try {
                    resultSet.close();
                    preparedStatement.close();
                    connection.close();
                } catch (Exception exception) {

                    LOGGER.error("can't close database - ", exception);
                }
            }
        }

        return Collections.emptyList();
    }

    // ------------------------------------------------------------------------------------------------------------

    private OrderDetail buildOrderDetail(ResultSet resultSet) throws SQLException {

        return OrderDetail.builder()
            .orderId(resultSet.getLong("OrderId"))
            .itemId(resultSet.getLong("MenuItemId"))
            .quantity(resultSet.getDouble("Quantity"))
            .unitPrice(resultSet.getDouble("MenuItemUnitPrice"))
            .discountAmount(resultSet.getDouble("DiscountAmount"))
            .discountTaxable(resultSet.getDouble("DiscountTaxable"))
            .itemText(resultSet.getString("MenuItemText"))
            .itemDescription(resultSet.getString("MenuItemDescription"))
            .build();
    }
    
}
