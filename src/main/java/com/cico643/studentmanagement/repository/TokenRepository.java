package com.cico643.studentmanagement.repository;

import com.cico643.studentmanagement.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {
    @Query(value = """
        SELECT t FROM Token t inner join User u on t.user.id = u.id
        WHERE u.id = :id and (t.expired = false or t.revoked = false)
    """)
    List<Token> findAllValidTokenByUser(String id);

    Optional<Token> findByToken(String token);
}
