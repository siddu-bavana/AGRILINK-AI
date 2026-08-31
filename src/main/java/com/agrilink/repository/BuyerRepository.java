package com.agrilink.repository;
import com.agrilink.model.Buyer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface BuyerRepository extends JpaRepository<Buyer,Long> { List<Buyer> findByVerifiedTrue(); }
