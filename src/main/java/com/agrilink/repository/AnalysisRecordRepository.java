package com.agrilink.repository;
import com.agrilink.model.AnalysisRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface AnalysisRecordRepository extends JpaRepository<AnalysisRecord,Long> { List<AnalysisRecord> findTop20ByUserIdOrderByCreatedAtDesc(Long userId); }
