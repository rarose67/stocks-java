package org.launchcode.stocks.models.data;

import org.launchcode.stocks.models.Member;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface MemberDao extends CrudRepository<Member, Integer> {

    public List<Member> findByUsername(String username);

}
