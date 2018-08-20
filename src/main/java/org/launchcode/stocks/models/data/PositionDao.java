package org.launchcode.stocks.models.data;

import org.launchcode.stocks.models.Position;
import org.launchcode.stocks.models.PositionState;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface PositionDao extends CrudRepository<Position, Integer> {

    public List<Position> findBySymbol(String symbol);

    //@Query("SELECT p FROM Position p WHERE p.portfolio_id = :portfolioId")
    //public List<Position> findByPortfolio(@Param("portfolioId") int portfolioId);
    public List<Position> findByPortfolio_id(int portfolioId);

    @Query("SELECT DISTINCT p.symbol FROM Position p")
    public List<String> findSymbols();

    /**
    @Query("SELECT p FROM Position p WHERE p.portfolio_id = :portfolioId AND p.state <> :state ORDER BY p.priority ASC")
    public List<Position> findByPortfolioAndNotState(@Param("portfolioId") int portfolioId,
                                                     @Param("state") PositionState state);

     */

     @Query(value = "SELECT * FROM POSITION WHERE portfolio_id = :portfolioId AND STATE <> :state ORDER BY PRIORITY ASC",
     nativeQuery = true)
     public List<Position> findByPortfolioAndNotState(@Param("portfolioId") int portfolioId,
     @Param("state") String state);

    @Query(value = "SELECT * FROM POSITION WHERE portfolio_id = :portfolioId AND STATE = :state ORDER BY PRIORITY ASC",
            nativeQuery = true)
    public List<Position> findByPortfolioAndState(@Param("portfolioId") int portfolioId,
                                                  @Param("state") String state);


    public List<Position> findByPortfolio_idAndValidTrueAndStateNotOrderByPriorityAsc(int portfolioId, PositionState state);

}