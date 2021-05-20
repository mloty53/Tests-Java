package net.stawrul.services;

import net.stawrul.model.Book;
import net.stawrul.model.CD;
import net.stawrul.model.Movie;
import net.stawrul.model.Order;
import net.stawrul.services.exceptions.MoviesAndCDsException;
import net.stawrul.services.exceptions.NotExistException;
import net.stawrul.services.exceptions.OutOfStockException;
import net.stawrul.services.exceptions.ReduplicationException;
import net.stawrul.services.exceptions.OrderEmptyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Komponent (serwis) biznesowy do realizacji operacji na zamówieniach.
 */
@Service
public class OrdersService extends EntityService<Order> {

    //Instancja klasy EntityManger zostanie dostarczona przez framework Spring
    //(wstrzykiwanie zależności przez konstruktor).
    public OrdersService(EntityManager em) {

        //Order.class - klasa encyjna, na której będą wykonywane operacje
        //Order::getId - metoda klasy encyjnej do pobierania klucza głównego
        super(em, Order.class, Order::getId);
    }

    /**
     * Pobranie wszystkich zamówień z bazy danych.
     *
     * @return lista zamówień
     */
    public List<Order> findAll() {
        return em.createQuery("SELECT o FROM Order o", Order.class).getResultList();
    }

    /**
     * Złożenie zamówienia w sklepie.
     * <p>
     * Zamówienie jest akceptowane, jeśli wszystkie objęte nim produkty są dostępne (przynajmniej 1 sztuka). W wyniku
     * złożenia zamówienia liczba dostępnych sztuk produktów jest zmniejszana o jeden. Metoda działa w sposób
     * transakcyjny - zamówienie jest albo akceptowane w całości albo odrzucane w całości. W razie braku produktu
     * wyrzucany jest wyjątek OutOfStockException.
     *
     * @param order zamówienie do przetworzenia
     */
    @Transactional
    public void placeOrder(Order order) {
        double price=0;
        if(order==null)throw new OrderEmptyException();
        if (order.getCDs().size() == 0 && order.getMovies().size() == 0 && order.getBooks().size() == 0) throw new OrderEmptyException();
        if(order.getCDs().size() != 0 && order.getMovies().size() != 0){
            throw new MoviesAndCDsException();
        }

        List<Book> TMPBooks = new ArrayList<Book>();
        for (Book bookStub : order.getBooks()) {
            Book book = em.find(Book.class, bookStub.getId());

            if(book == null){
                String msg = "At the moment we don't have " + bookStub.getId().toString() + " in the shop.";
                throw new NotExistException(msg);
            }
            for(Book check : TMPBooks){
                if( book.getId() == check.getId() ){
                    throw new ReduplicationException();
                }
            }
            TMPBooks.add(book);
            if (book.getAmount() < 1) {
                //wyjątek z hierarchii RuntineException powoduje wycofanie transakcji (rollback)
                String msg = book.getId().toString() + "is out of stock";
                throw new OutOfStockException(msg);
            }else if(book.getAmount()!=0) {
                price += book.getPrice();
            }
        }

        List<CD> TMPCDs = new ArrayList<CD>();
        for (CD cdShelf : order.getCDs()) {
            CD cd = em.find(CD.class, cdShelf.getId());

            if(cd == null){
                String msg = "At the moment we don't have " + cdShelf.getId().toString() + " in our shop.";
                throw new NotExistException(msg);
            }
            for(CD check : TMPCDs){
                if( cd.getId() == check.getId() ){
                    throw new ReduplicationException();
                }
            }
            TMPCDs.add(cd);
            if (cd.getAmount() < 1) {
                String msg = cd.getId().toString() + "is out of stock";
                throw new OutOfStockException(msg);
            }
            price+=cd.getPrice();
        }

        List<Movie> TMPMovies = new ArrayList<Movie>();
        for (Movie movieCatalog : order.getMovies()) {
            Movie movie = em.find(Movie.class, movieCatalog.getId());

            if(movie == null){
                String msg = "At the moment we don't have " + movieCatalog.getId().toString() + " in our shop.";
                throw new NotExistException(msg);
            }
            for(Movie check : TMPMovies){
                if( movie.getId() == check.getId() ){
                    throw new ReduplicationException();
                }
            }
            TMPMovies.add(movie);
            if (movie.getAmount() < 1) {
                String msg = movie.getId().toString() + "is out of stock";
                throw new OutOfStockException(msg);
            }
            price+=movie.getPrice();
        }

        for(Book bookStub : order.getBooks()){
            Book book = em.find(Book.class, bookStub.getId());
            int newAmount = book.getAmount() - 1;
            book.setAmount(newAmount);
        }
        for(CD cdShelf : order.getCDs()){
            CD cd = em.find(CD.class, cdShelf.getId());
            int newAmount = cd.getAmount() - 1;
            cd.setAmount(newAmount);
        }
        for(Movie movieCatalog : order.getMovies()){
            Movie movie = em.find(Movie.class, movieCatalog.getId());
            int newAmount = movie.getAmount() - 1;
            movie.setAmount(newAmount);
        }
        save(order);
    }
}
