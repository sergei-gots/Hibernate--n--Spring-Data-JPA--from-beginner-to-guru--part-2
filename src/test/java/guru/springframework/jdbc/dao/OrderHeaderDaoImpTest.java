package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Address;
import guru.springframework.jdbc.domain.OrderHeader;
import guru.springframework.jdbc.enumeration.OrderStatus;
import guru.springframework.jdbc.repository.OrderHeaderRepository;
import jakarta.persistence.EntityNotFoundException;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Created by sergei on 27/02/2025
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan("guru.springframework.jdbc.dao")
public class OrderHeaderDaoImpTest {

    @Autowired
    OrderHeaderRepository orderHeaderRepository;

    OrderHeaderDao orderHeaderDao;

    @BeforeEach
    public void setUp() {
        orderHeaderDao = new OrderHeaderDaoImpl(orderHeaderRepository);
    }

    @Test
    public void testEquals() {

        String customer = RandomString.make(7);

        OrderHeader orderHeader1 = new OrderHeader();
        orderHeader1.setCustomer(customer);

        OrderHeader orderHeader2 = new OrderHeader();
        orderHeader2.setCustomer(customer);

        assertThat(orderHeader1).isEqualTo(orderHeader2);
    }

    @Test
    public void testNotEquals() {

        OrderHeader orderHeader1 = new OrderHeader();
        orderHeader1.setCustomer(RandomString.make(7));

        OrderHeader orderHeader2 = new OrderHeader();
        orderHeader2.setCustomer(RandomString.make(8));

        assertThat(orderHeader1).isNotEqualTo(orderHeader2);
    }

    @Test
    public void testSave() {

        OrderHeader orderHeader = new OrderHeader();
        orderHeader.setCustomer("Customer#" + RandomString.make(10));
        Address address = new Address();
        address.setAddress("37 West Avenue");
        address.setCity("South Park");
        address.setState("CA");
        address.setZipCode("322233");

        orderHeader.setShippingAddress(address);
        orderHeader.setBillingAddress(address);

        OrderHeader saved = orderHeaderDao.save(orderHeader);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCustomer()).isEqualTo(orderHeader.getCustomer());

        assertNotNull(saved.getCreatedDate());

    }

    @Test
    public void testGetById() {

        OrderHeader orderHeader = new OrderHeader();
        orderHeader.setCustomer("CustomerName##");
        OrderHeader saved = orderHeaderRepository.save(orderHeader);

        OrderHeader fetched = orderHeaderDao.getById(saved.getId());

        assertThat(fetched).isNotNull();
        assertThat(fetched).isEqualTo(saved);
    }

    @Test
    public void testGetById_whenNotExists_thenThrows() {

        assertThrows(EntityNotFoundException.class, () -> orderHeaderDao.getById(Long.MAX_VALUE));
    }

    @Test
    public void TestFindAll() {

        int pageSize = 10;

        Pageable pageable = PageRequest.of(1, pageSize);

        Page<OrderHeader> orderHeadersPage = orderHeaderDao.findAll(pageable);

        assertThat(orderHeadersPage).isNotNull();

        List<OrderHeader> orderHeaders = orderHeadersPage.getContent();

        assertThat(orderHeaders.size()).isGreaterThanOrEqualTo(0);
        assertThat(orderHeaders.size()).isLessThanOrEqualTo(pageSize);
    }

    @Test
    public void testUpdate() {
        OrderHeader orderHeader = new OrderHeader();
        orderHeader.setCustomer("Customer#1" + RandomString.make(10));

        orderHeader.setCustomer("Customer#" + RandomString.make(10));
        Address address = new Address();
        address.setAddress("37 West Avenue");
        address.setCity("South Park");
        address.setState("CA");
        address.setZipCode("322233");
        orderHeader.setBillingAddress(address);


        OrderHeader persisted = orderHeaderDao.save(orderHeader);

        orderHeader.setCustomer("Customer#2" + RandomString.make(10));
        orderHeader.setOrderStatus(OrderStatus.DELIVERED);

        OrderHeader updated = orderHeaderDao.update(persisted);

        assertThat(updated).isNotNull();
        assertThat(updated).isEqualTo(persisted);
        assertThat(updated.getOrderStatus()).isEqualTo(persisted.getOrderStatus());

    }

    @Test
    public void testDeleteById() {

        OrderHeader orderHeader = new OrderHeader();
        orderHeader.setCustomer("Customer#" + RandomString.make(10));

        OrderHeader saved = orderHeaderDao.save(orderHeader);

        orderHeaderDao.deleteById(saved.getId());

        assertThrows(EntityNotFoundException.class, () -> orderHeaderDao.getById(saved.getId()));
    }

    @Test
    public void testGetByCustomerName() {

        String customerName = "Customer#" + RandomString.make(10);

        OrderHeader orderHeader = new OrderHeader();
        orderHeader.setCustomer(customerName);

        OrderHeader saved = orderHeaderDao.save(orderHeader);

        OrderHeader fetched = orderHeaderDao.findOrderHeaderByCustomer(customerName);

        assertThat(fetched).isNotNull();
        assertThat(fetched.getId()).isEqualTo(saved.getId());
        assertThat(fetched.getCustomer()).isEqualTo(customerName);
    }

    @Test
    public void testGetByCustomerName_whenNotExists_thenThrows() {

        String customerName = "Customer#that#is#not#in#db" + RandomString.make(10);

        assertThrows(EntityNotFoundException.class, () -> orderHeaderDao.findOrderHeaderByCustomer(customerName));

    }

}