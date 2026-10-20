package com.bloodlink.aspect;

import com.bloodlink.controller.DonorController;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:aop-test",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ApiTimingAspectTest {
    @Autowired
    private DonorController donorController;

    @Test
    void controllerIsWrappedByTheTimingAspect() {
        assertThat(AopUtils.isAopProxy(donorController)).isTrue();
    }
}
