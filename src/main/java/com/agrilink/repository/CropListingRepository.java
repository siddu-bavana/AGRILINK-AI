package com.agrilink.repository;
import com.agrilink.model.CropListing;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface CropListingRepository extends JpaRepository<CropListing,Long> { List<CropListing> findByFarmerId(Long farmerId); List<CropListing> findByCropNameIgnoreCaseAndStatus(String crop, CropListing.Status status); }
