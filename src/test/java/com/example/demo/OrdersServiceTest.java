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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class OrdersServiceTest {
    @Mock
    EntityManager em;

    @Test(expected = OutOfStockException.class)
    public void whenOrderedBookNotAvailable_placeOrderThrowsOutOfStockEx() {
        //Arrange
        Order order = new Order();
        Book book = new Book();
        book.setAmount(0);
        order.getBooks().add(book);
        Mockito.when(em.find(Book.class, book.getId())).thenReturn(book);
        OrdersService ordersService = new OrdersService(em);

        //Act
        ordersService.placeOrder(order);

        //Assert - exception expected
    }

    @Test(expected = NotExistException.class)
    public void whenOrderedBookNotExist_placeOrderThrowsNotExistEx() {
        Order order = new Order();
        Book book = new Book();
        order.getBooks().add(book);
        Mockito.when(em.find(Book.class, book.getId())).thenReturn(null);
        OrdersService ordersService = new OrdersService(em);

        //Act
        ordersService.placeOrder(order);

        //Assert - exception expected
        Mockito.verify(em,never()).persist(order);

    }

    @Test(expected = ReduplicationException.class)
    public void whenOrderedBookIsOrderedTwice_placeOrderThorwsReduplicationEx(){
        Order order = new Order();
        Book book = new Book();
        UUID id = UUID.randomUUID();
        book.setId(id);
        book.setAmount(5);
        book.setPrice(10.0);
        order.getBooks().add(book);
        order.getBooks().add(book);
        Mockito.when(em.find(Book.class, book.getId())).thenReturn(book);
        Mockito.when(em.find(Book.class, book.getId())).thenReturn(book);
        OrdersService ordersService = new OrdersService(em);

        //Act
        ordersService.placeOrder(order);

        //Assert - exception expected
    }

    @Test(expected = MoviesAndCDsException.class)
    public void whenMoviesAndCDsOrderedTogether_placeMoviesAndCDsEx(){
        //Arrange
        Order order = new Order();
        Movie movie = new Movie();
        CD cd = new CD();
        movie.setAmount(1);
        cd.setAmount(1);
        order.getMovies().add(movie);
        order.getCDs().add(cd);
        OrdersService ordersService = new OrdersService(em);

        //Act
        ordersService.placeOrder(order);

        //Assert - exception expected
    }

    @Test(expected = OrderEmptyException.class)
    public void whenOrderIsEmpty_placeOrderThrowsOrderEmpty() {
        // Arrange:
        Order order = new Order();
        OrdersService svc = new OrdersService(em);

        // Act:
        svc.placeOrder(order);

        // Assert - explode with OrderEmptyException!
    }

    @Test(expected = OrderEmptyException.class)
    public void whenOrderNull_placeOrderThrowsOrderEmpty() {
        // Arrange:
        OrdersService svc = new OrdersService(em);

        // Act:
        svc.placeOrder(null);

        // Assert - explode with OrderEmptyException!
    }

    @Test
    public void whenOrderedBookAvailable_placeOrderDecreasesAmountByOne() {
        // Arrange:
        Book book = new Book();
        book.setAmount(10);
        book.setPrice(10.0);

        Order order = new Order();
        order.getBooks().add(book);

        Mockito.when(em.find(Book.class, book.getId())).thenReturn(book);

        OrdersService ordersService = new OrdersService(em);

        // Act:
        ordersService.placeOrder(order);

        // Assert:
        assertEquals(9, (int)book.getAmount());
        Mockito.verify(em, times(1)).persist(order);
    }

    @Test
    public void validateTotalPriceCalculationInBooksService() {
        // Arrange:
        Order order = new Order();
        Book book1 = new Book();
        book1.setAmount(1);
        book1.setPrice(25.0);

        Book book2 = new Book();
        book2.setAmount(1);
        book2.setPrice(30.0);

        Mockito.when(em.find(Book.class, book1.getId())).thenReturn(book1);
        Mockito.when(em.find(Book.class, book2.getId())).thenReturn(book2);

        order.getBooks().add(book1);
        order.getBooks().add(book2);

        double result=order.getPrice(em);

        // Assert:
        assertEquals(result, 55.0 ,0.0);
    }
}
