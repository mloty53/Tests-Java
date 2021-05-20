package com.example.demo;
import net.stawrul.model.Book;
import net.stawrul.model.CD;
import net.stawrul.model.Movie;
import net.stawrul.model.Order;
import net.stawrul.services.OrdersService;
import net.stawrul.services.exceptions.*;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import javax.persistence.EntityManager;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@SpringBootTest
class DemoApplicationTests {

	@Test
	void contextLoads() {
	}

}
