package com.luizlemetech.codetickets;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImportacaoRepository extends JpaRepository<Importacao, Long> {
}
