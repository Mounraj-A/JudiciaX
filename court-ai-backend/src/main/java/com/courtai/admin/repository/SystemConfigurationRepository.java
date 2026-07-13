package com.courtai.admin.repository;

import com.courtai.admin.entity.SystemConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemConfigurationRepository extends JpaRepository<SystemConfiguration, Long> {

    Optional<SystemConfiguration> findByConfigKeyAndIsDeletedFalse(String configKey);

    Optional<SystemConfiguration> findByUuidAndIsDeletedFalse(String uuid);

    Page<SystemConfiguration> findByCategoryAndIsDeletedFalse(String category, Pageable pageable);

    boolean existsByConfigKey(String configKey);
}
