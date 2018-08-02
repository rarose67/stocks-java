package org.launchcode.stocks.models.data;

import org.launchcode.stocks.models.Portfolio;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface PortfolioDao extends CrudRepository<Portfolio, Integer> {

    public List<Portfolio> findByName(String name);

    //public List<Portfolio> findByUser(int userId);

}