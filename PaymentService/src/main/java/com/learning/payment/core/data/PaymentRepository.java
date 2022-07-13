package com.learning.payment.core.data;

import com.learning.payment.core.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentEntity, String> {

    PaymentEntity findByPaymentId(String PsaymentId);
}
