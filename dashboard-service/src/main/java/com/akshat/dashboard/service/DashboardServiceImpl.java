package com.akshat.dashboard.service;

import com.akshat.dashboard.client.AuthClient;
import com.akshat.dashboard.client.CatalogClient;
import com.akshat.dashboard.client.OrderClient;
import com.akshat.dashboard.client.PaymentClient;
import com.akshat.dashboard.common.response.ApiResponse;
import com.akshat.dashboard.dto.AdminDashboardResponse;
import com.akshat.dashboard.dto.AdminOrderStatistics;
import com.akshat.dashboard.dto.AdminUserStatistics;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class DashboardServiceImpl implements DashboardService{

    private final AuthClient authClient;
    private final CatalogClient catalogClient;
    private final OrderClient orderClient;
    private final PaymentClient paymentClient;

    @Value("${internal.service-secret}")
    private String serviceSecret;

    /**
     * Retrieves all the data for admin dashboard
     *
     * <p> Fetches statistics via four Feign Client :
     * <ul>
     *      <li>{@link AuthClient}: Total users, sellers, and customers.</li>
     *      <li>{@link OrderClient}: Total, pending, and cancelled orders.</li>
     *      <li>{@link CatalogClient}: Total products.</li>
     *      <li>{@link PaymentClient}: Total revenue.</li>
     * </ul>
     *
     * <p> Combines the retrieved stats into single {@link AdminDashboardResponse} object.
     *
     * <p>The {@code serviceSecret}, injected from the
     * {@code internal.service-secret} configuration property,
     * is sent with each call for internal
     * service-to-service authentication.
     *
     * @return Aggregated dashboard statistics
    */
    @Override
    public AdminDashboardResponse getAdminDashboard() {

        ApiResponse<AdminUserStatistics> adminUserStatistics = authClient.getUserCount(serviceSecret);
        ApiResponse<AdminOrderStatistics> adminOrderStatistics = orderClient.getOrderCount(serviceSecret);
        ApiResponse<Long> totalProductsApiResponse = catalogClient.getTotalProducts(serviceSecret);
        ApiResponse<BigDecimal> totalRevenueApiResponse = paymentClient.getTotalRevenue(serviceSecret);

        long totalUsers = adminUserStatistics.data().totalUsers();
        long totalSellers = adminUserStatistics.data().totalSellers();
        long totalCustomers = adminUserStatistics.data().totalCustomers();

        long totalOrders = adminOrderStatistics.data().totalOrders();
        long pendingOrders = adminOrderStatistics.data().pendingOrders();
        long cancelledOrders = adminOrderStatistics.data().cancelledOrders();

        long totalProducts = totalProductsApiResponse.data();

        BigDecimal totalRevenue = totalRevenueApiResponse.data();

        return  new AdminDashboardResponse(
                totalUsers,
                totalSellers,
                totalCustomers,
                totalProducts,
                totalOrders,
                totalRevenue,
                pendingOrders,
                cancelledOrders);

    }
}
