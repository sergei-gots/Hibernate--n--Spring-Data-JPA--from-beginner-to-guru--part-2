package guru.springframework.jdbc.repository;

import guru.springframework.jdbc.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Async;

import java.util.Optional;
import java.util.concurrent.Future;
import java.util.stream.Stream;

/**
 * Created by sergei on 18/02/2025
 */
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    Optional<Book> findByTitle(String title);

    @Nullable
    Book getByTitle(@Nullable String title);

    Book readByTitle(@Nullable String title);

    @Async
    Future<Stream<Book>> queryByTitle(String title);

    Stream<Book> findByTitleNotNull();
}
