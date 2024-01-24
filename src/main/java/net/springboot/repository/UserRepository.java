package net.springboot.repository;

import net.springboot.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.email = ?1")
    User findByEmail(String email);

    Page<User> findAll(Pageable pageable);

    Page<User> findByFirstNameContainingOrLastNameContainingOrEmailContaining(
            String firstName, String lastName, String email, Pageable pageable);

    Page<User> findAllByOrderByLastNameAscFirstNameAsc(Pageable pageable);

    Page<User> findAllByOrderByFirstNameAsc(Pageable pageable);

    Page<User> findAllByOrderByLastNameAsc(Pageable pageable);

    Page<User> findAllByOrderByEmailAsc(Pageable pageable);

    Page<User> findAllByOrderByRoleAsc(Pageable pageable);

}
