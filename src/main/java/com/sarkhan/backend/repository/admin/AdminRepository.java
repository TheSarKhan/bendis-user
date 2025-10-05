package com.sarkhan.backend.repository.admin;

import com.sarkhan.backend.model.admin.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
}
