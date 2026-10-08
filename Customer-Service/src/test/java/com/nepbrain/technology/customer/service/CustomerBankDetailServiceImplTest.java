package com.nepbrain.technology.customer.service;

import com.nepbrain.technology.core.enums.StatusEnum;
import com.nepbrain.technology.core.exception.GlobalException;
import com.nepbrain.technology.core.payload.response.GlobalResponse;
import com.nepbrain.technology.customer.entity.CustomerBankDetailEntity;
import com.nepbrain.technology.customer.mapper.CustomerBankDetailMapper;
import com.nepbrain.technology.customer.payload.response.CustomerBankDetailResponse;
import com.nepbrain.technology.customer.repository.CustomerBankDetailRepository;
import com.nepbrain.technology.customer.repository.CustomerRepository;
import com.nepbrain.technology.customer.service.impl.CustomerBankDetailServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerBankDetailServiceImplTest {

    @Mock
    private CustomerBankDetailRepository bankDetailRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerBankDetailMapper bankDetailMapper;

    @InjectMocks
    private CustomerBankDetailServiceImpl service;

    @Test
    void changeStatus_setsInactive() {
        UUID id = UUID.randomUUID();
        CustomerBankDetailEntity entity = new CustomerBankDetailEntity();
        entity.setStatus(StatusEnum.ACTIVE);
        CustomerBankDetailResponse response = new CustomerBankDetailResponse();
        when(bankDetailRepository.findById(id)).thenReturn(Optional.of(entity));
        when(bankDetailRepository.save(entity)).thenReturn(entity);
        when(bankDetailMapper.toResponse(entity)).thenReturn(response);

        GlobalResponse result = service.changeStatus(id, StatusEnum.INACTIVE);

        assertThat(entity.getStatus()).isEqualTo(StatusEnum.INACTIVE);
        assertThat(result.getData()).isSameAs(response);
        verify(bankDetailRepository).save(entity);
    }

    @Test
    void changeStatus_missingRow_isBadRequest() {
        UUID id = UUID.randomUUID();
        when(bankDetailRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.changeStatus(id, StatusEnum.ACTIVE))
                .isInstanceOf(GlobalException.class)
                .extracting(ex -> ((GlobalException) ex).getHttpStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST);
        verify(bankDetailRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
