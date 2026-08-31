package com.agrilink.repository;
import com.agrilink.model.TransportRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface TransportRequestRepository extends JpaRepository<TransportRequest,Long> { List<TransportRequest> findByStatusAndDeliveryMarketIgnoreCase(TransportRequest.Status status,String market); }
